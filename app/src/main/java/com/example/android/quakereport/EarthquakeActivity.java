/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

//https://classroom.udacity.com/courses/ud843/lessons/1335cf7d-bb4f-48c6-8503-f14b127d2abc/concepts/9e3938ea-8018-42ba-8753-1f4b4f5f29c8

/**
 * 在我们的活动中实现 LoaderCallbacks 有些复杂。
 * 首先，我们需要用于实现 LoaderCallbacks 接口的 EarthquakeActivity ，
 * 以及用于指定 loader 返回内容（本例中为 Earthquake）的泛型参数。
 */
public class EarthquakeActivity extends AppCompatActivity implements LoaderCallbacks<List<Earthquake>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    private QuakeInfoAdapter mQuakeInfoAdapter;

    //作为静态常量存储是因为这个Activity外，没有其他类需要引用它
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=5&limit=10";

    /**
     * 地震 loader ID 的常量值。我们可选择任意整数。
     * 仅当使用多个 loader 时该设置才起作用。
     * <p>
     * 首先，我们需要为 loader 指定 ID。仅当我们在同一活动中使用 多个 loader 时才真正相关。
     * 我们可选择任意整数，因此我们选择数字 1。
     */
    private static final int EARTHQUAKE_LOADER_ID = 1;

    private TextView mEmptyTextView;

    private View mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "TEST: Earthquake activity onCreate() called...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        mEmptyTextView = (TextView) findViewById(R.id.empty_view);
        earthquakeListView.setEmptyView(mEmptyTextView);

        mProgressBar = findViewById(R.id.loading_spinner);

        // Create a new {@link ArrayAdapter} of earthquakes
        mQuakeInfoAdapter = new QuakeInfoAdapter(this, new ArrayList<Earthquake>());
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mQuakeInfoAdapter);
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // 查找单击的当前地震
                Earthquake currentEarthquake = mQuakeInfoAdapter.getItem(position);

                // 将字符串 URL 转换为 URI 对象（以传递至 Intent 中 constructor)
                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());

                // 创建一个新的 Intent 以查看地震 URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // 发送 Intent 以启动新活动
                startActivity(websiteIntent);
            }
        });

        //Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            /**
             * 最后，要检索地震，需要获取Loader Manager并告知Loader Manager使用指定 ID 初始化 loader，
             * 第二个参数可用于传递一系列附加信息。
             * 第三个参数是应接收LoaderCallbacks(以及加载完成时的数据)的对象,也就是本活动将执行的操作。
             * 此代码将进入EarthquakeActivity 的 onCreate() 方法，以便打开应用时可初始化loader。
             */
            LoaderManager loaderManager = getLoaderManager();

            /**
             * 初始化 loader。传递上面定义的整数 ID 常量并为为捆绑
             * 传递 null。为 LoaderCallbacks 参数（由于
             * 此活动实现了 LoaderCallbacks 接口而有效）传递此活动。
             */
            Log.i(LOG_TAG, "TEST: calling initLoader()...");
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            // Update empty state with no connection error message
            mProgressBar.setVisibility(View.GONE);
            mEmptyTextView.setText(R.string.no_internet_connection);
        }

    }

    /**
     * 覆盖 EarthquakeActivity.java 中的一些方法以使用 菜单，然后在用户单击菜单项时作出响应
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 我们需要 onCreateLoader()，前提是 LoaderManager已确定具有我们指定的 ID 的 loader 当前未运行，
     * 因此我们需要新建一个。
     *
     * @param i
     * @param bundle
     * @return Loader<List < Earthquake>>
     */
    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {
        Log.i(LOG_TAG, "TEST: onCreateLoader() called");

        // 为给定 URL 创建新 loader
        return new EarthquakeLoader(this, USGS_REQUEST_URL);
    }

    /**
     * 我们需要 onLoadFinished(),我们将在其中执行与在 onPostExecute() 中完全相同的操作，
     * 并使用地震数据更新我们的UI,通过更新适配器中的数据集。
     *
     * @param loader
     * @param earthquakes
     */
    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {
        Log.i(LOG_TAG, "TEST: onLoadFinished() called");

        // 因数据已加载，隐藏加载指示符
        mProgressBar.setVisibility(View.GONE);

        /**
         * 为避免首次启动应用时屏幕中闪现“未发现地震。(No earthquakes found.)”消息，
         * 我们可将空状态 TextView 留空， 直至完成第一次加载。
         * 在 onLoadFinished 回调 方法中，我们可将文本设置为字符串“No earthquakes found”
         * 由于此操作的代价在承受范围内，所以 可在每次 loader 完成时设置此文本。
         * 经过权衡之后，如此操作能获得更好的用户体验。
         */
        mEmptyTextView.setText(R.string.no_earthquakes);

        // 清除之前地震数据的适配器
        mQuakeInfoAdapter.clear();

        // 如果存在 {@link Earthquake} 的有效列表，则将其添加到适配器的
        // 数据集。这将触发 ListView 执行更新。
        if (earthquakes != null && !earthquakes.isEmpty()) {
            mQuakeInfoAdapter.addAll(earthquakes);
        }
    }

    /**
     * 我们需要 onLoaderReset()，系统会通知 我们，loader 的数据不再有效。
     * 实际上， 简单的 loader 不会出现这样的情况，
     * 正确的做法是 通过清理适配器的数据集 移除 UI 中的所有地震数据
     *
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        Log.i(LOG_TAG, "TEST: onLoaderReset() called");

        // 重置 Loader，以便能够清除现有数据。
        mQuakeInfoAdapter.clear();
    }
}
