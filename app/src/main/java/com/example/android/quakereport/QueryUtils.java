package com.example.android.quakereport;

import android.app.usage.UsageEvents;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * 加入 fetchEarthquakeData() 辅助方法，该方法将所有步骤连接在一起，
     * 即创建 URL、发送请求、处理响应。由于这是 EarthquakeAsyncTask
     * 需要交互的唯一 "public" QueryUtils 方法，
     * 因此将 QueryUtils 中所有其他辅助方法设置为 "private"。
     *
     * 查询 USGS 数据集并返回 {@link Earthquake} 对象的列表。
     */
    public static List<Earthquake> fetchEarthquakeData(String requestUrl) {
        Log.i(LOG_TAG,"TEST: fetchEarthquakeData() called");

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<Earthquake> earthquakes = extractFeatureFromJson(jsonResponse);

        // Return the {@link Event}
        return earthquakes;
    }

//    /**
//     * Sample JSON response for a USGS query
//     */
//    private static final String SAMPLE_JSON_RESPONSE =

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<Earthquake> extractFeatureFromJson(String jsonResponse) {

        //如果JSON字符串为空或者null，将提早返回
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        List<Earthquake> earthquakes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        // 尝试解析 JSON 响应字符串。如果格式化 JSON 的方式存在问题，
        // 则将抛出 JSONException 异常对象。
        // 捕获该异常以便应用不会崩溃，并将错误消息打印到日志中。
        try {
            // build up a list of Earthquake objects with the corresponding data.
            // 通过 JSON 响应字符串创建 JSONObject
            JSONObject root = new JSONObject(jsonResponse);

            //Get JSON Array node
            // 提取与名为 "features" 的键关联的 JSONArray，
            // 该键表示特征（或地震）列表。
            JSONArray earthquakeArray = root.getJSONArray("features");

            //Looping through all features
            // 针对 earthquakeArray 中的每个地震，创建 {@link Earthquake} 对象
            for (int i = 0; i < earthquakeArray.length(); i++) {

                // 获取地震列表中位置 i 处的单一地震
                JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);

                // 针对给定地震，提取与名为 "properties" 的键关联的 JSONObject，
                // 该键表示该地震所有属性的列表。
                JSONObject properties = currentEarthquake.getJSONObject("properties");

                // 提取名为 "mag" 的键的值
                double mag = properties.getDouble("mag");

                // 提取名为 "place" 的键的值
                String location = properties.getString("place");

                // 提取名为 "time" 的键的值
                long time = properties.getLong("time");

                // 提取名为 "url" 的键的值
                String url = properties.getString("url");

                // 使用 JSON 响应中的震级、位置、时间和 url，
                // 创建新的 {@link Earthquake} 对象。
                Earthquake earthquake = new Earthquake(mag, location, time, url);

                // 将该新 {@link Earthquake} 添加到地震列表。
                earthquakes.add(earthquake);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            // 在 "try" 块中执行上述任一语句时若系统抛出错误，
            // 则在此处捕获异常，以便应用不会崩溃。在日志消息中打印
            // 来自异常的消息。
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // 关闭输入流可能会抛出 IOException，
                // 这就是makeHttpRequest(URL url) 方法签名指定可能抛出 IOException 的原因。
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}