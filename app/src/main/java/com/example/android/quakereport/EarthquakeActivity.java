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

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    //作为静态常量存储是因为这个Activity外，没有其他类需要引用它
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=5&limit=10";
    private QuakeInfoAdapter mQuakeInfoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new {@link ArrayAdapter} of earthquakes
        mQuakeInfoAdapter = new QuakeInfoAdapter(this, new ArrayList<Earthquake>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mQuakeInfoAdapter);

        // 启动 AsyncTask 以获取地震数据
        EarthQuakeAsyncTask earthQuakeAsyncTask = new EarthQuakeAsyncTask();
        earthQuakeAsyncTask.execute(USGS_REQUEST_URL);

        //https://classroom.udacity.com/courses/ud843/lessons/1335cf7d-bb4f-48c6-8503-f14b127d2abc/concepts/9e3938ea-8018-42ba-8753-1f4b4f5f29c8
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

    }

    private class EarthQuakeAsyncTask extends AsyncTask<String, Void, List<Earthquake>> {

        /**
         * 此方法在后台线程上运行并执行网络请求。
         * 我们不能够通过后台线程更新 UI，因此我们返回
         * {@link Earthquake} 的列表作为结果。
         */
        @Override
        protected List<Earthquake> doInBackground(String... urls) {
            // 如果不存在任何 URL 或第一个 URL 为空，切勿执行请求。
            //自己写的时候忘了这个
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            return QueryUtils.fetchEarthquakeData(urls[0]);
        }

        /**
         * 后台工作完成后，此方法会在主 UI 线程上
         * 运行。此方法接收 doInBackground() 方法的返回值
         * 作为输入。首先，我们将清理适配器，除去先前 USGS 查询的地震
         * 数据。然后，我们使用新地震列表更新适配器，
         * 这将触发 ListView 重新填充其列表项。
         */
        @Override
        protected void onPostExecute(List<Earthquake> earthquakes) {
            // 清除之前地震数据的适配器
            mQuakeInfoAdapter.clear();

            // 如果存在 {@link Earthquake} 的有效列表，则将其添加到适配器的
            // 数据集。这将触发 ListView 执行更新。
            if (earthquakes != null && !earthquakes.isEmpty()) {
                mQuakeInfoAdapter.addAll(earthquakes);
            }
        }
    }


}
