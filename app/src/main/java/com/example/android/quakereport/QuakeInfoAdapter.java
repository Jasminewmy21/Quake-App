package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class QuakeInfoAdapter extends ArrayAdapter<Earthquake> {

    private static final String LOG_TAT = QuakeInfoAdapter.class.getSimpleName();

    private static final String LOCATION_SEPARATOR = "of";
    private static final int LOCATION_SEPARATOR_COUNT = 2;

    public QuakeInfoAdapter(Context context, ArrayList<Earthquake> quakeInfoItems) {
        super(context, 0, quakeInfoItems);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Earthquake currentQuackInfo = getItem(position);

        TextView mag = (TextView) listItemView.findViewById(R.id.mag);
        mag.setText(formatMag(currentQuackInfo.getMag()));

        // 为震级圆圈设置正确的背景颜色。
        // 从 TextView 获取背景，该背景是一个 GradientDrawable。
        GradientDrawable magnitudeCircle = (GradientDrawable) mag.getBackground();

        // 根据当前的地震震级获取相应的背景颜色
        int magColor = getMagnitudeColor(currentQuackInfo.getMag());

        // 设置震级圆圈的颜色
        magnitudeCircle.setColor(magColor);

        //place相关
        String currentLocation = currentQuackInfo.getLocation();

        int index = currentLocation.indexOf(LOCATION_SEPARATOR);
        String locationForDisplay = currentLocation;
        String distanceForDisplay = getContext().getString(R.string.near_the);
        if (index != -1) {
            distanceForDisplay = currentLocation.substring(0, index + LOCATION_SEPARATOR_COUNT);
            locationForDisplay = currentLocation.substring(index + LOCATION_SEPARATOR_COUNT + 1);
        }

        /**课程方法*/
//        if (currentLocation.contains(LOCATION_SEPARATOR)) {
//            String[] parts = currentLocation.split(LOCATION_SEPARATOR);
//            distanceForDisplay = parts[0] + LOCATION_SEPARATOR;
//            locationForDisplay = parts[1];
//        } else {
//            distanceForDisplay = getContext().getString(R.string.near_the);
//            locationForDisplay = currentLocation;
//        }
        TextView location = (TextView) listItemView.findViewById(R.id.location);
        location.setText(locationForDisplay);

        TextView distance = (TextView) listItemView.findViewById(R.id.distance);
        distance.setText(distanceForDisplay);

        //time相关
        TextView date = (TextView) listItemView.findViewById(R.id.date);
        //调用Date构造函数将 以毫秒为单位的时间转换为Date对象
        Date dateObject = new Date(currentQuackInfo.getDate());
        //初始化SimpleDateFormat实例，并将其配置为指定格式提供更易懂的表示
        String dateToDisplay = formatDate(dateObject);
        date.setText(dateToDisplay);
        TextView time = (TextView) listItemView.findViewById(R.id.time);
        String timeForDisplay = formatTime(dateObject);
        time.setText(timeForDisplay);

        return listItemView;
    }

    private int getMagnitudeColor(double mag) {
        int magColorResourceId;
        int magFloor = (int) Math.floor(mag);
        switch (magFloor) {
            case 0:
            case 1:
                magColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magColorResourceId = R.color.magnitude9;
                break;
            default:
                magColorResourceId = R.color.magnitude10plus;
                break;
        }
        //关于颜色值的注意事项：
        //在 Java 代码中，可以参考 使用颜色资源 ID
        // （如 R.color.magnitude1、R.color.magnitude2）
        // 在 colors.xml 文件中定义的颜色。尽管仍需将颜色 资源 ID 转换为颜色整数值。示例：
        return ContextCompat.getColor(getContext(), magColorResourceId);
    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormatter.format(dateObject);
    }

    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
        return timeFormatter.format(dateObject);
    }

    private String formatMag(double mag) {
        DecimalFormat decimalFormatter = new DecimalFormat("0.0");
        return decimalFormatter.format(mag);
    }

}
