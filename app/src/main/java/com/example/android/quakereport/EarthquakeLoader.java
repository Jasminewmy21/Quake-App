package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * 为定义 EarthquakeLoader 类，我们扩展 AsyncTaskLoader
 * 并指定 List 作为泛型参数，该参数说明了 预期加载的数据类型。
 * 在本例中，loader 正在 加载 Earthquake 对象的列表。
 * 然后，我们将获取 构造函数和 loadInBackground() 中的字符串 URL，
 * 我们将返回 EarthquakeAsyncTask，执行与在 doInBackground 中完全相同的操作。
 * 重要信息：请注意，我们还会覆盖 onStartLoading() 方法 来调用 forceLoad()，
 * 必须执行该步骤才能实际触发 loadInBackground() 方法的执行。
 */
public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    private static final String LOG_TAG = EarthquakeLoader.class.getName();

    private String mUrl;

    /**
     * 构建新 {@link EarthquakeLoader}。
     * @param context 上下文
     * @param url 要从中加载数据的url
     */
    public EarthquakeLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }


    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG,"TEST: onStartLoading() called");

        forceLoad();
    }

    /**
     * 位于后台线程
     * @return
     */
    @Override
    public List<Earthquake> loadInBackground() {
        Log.i(LOG_TAG,"TEST: loadInBackground() called");

        if (mUrl == null) {
            return null;
        }

        return QueryUtils.fetchEarthquakeData(mUrl);
    }
}
