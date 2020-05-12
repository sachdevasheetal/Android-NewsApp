package com.example.pluralsight.ui.notifications;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.pluralsight.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private LineChart lineChart;
    List<Entry> entries1;
    String init="Coronavirus";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);

        final View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        lineChart = (LineChart)root.findViewById(R.id.lineChart);
       entries1 = new ArrayList<>();
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
             //   textView.setText(s);

            }
        });

        final EditText edit_txt = (EditText) root.findViewById(R.id.editText);
        //
        //http://10.0.2.2:5000/trends?web_url=coronavirus
       new NotificationsFragment.AsyncTask(root).execute("https://homework8-273123.appspot.com/trends?web_url=coronavirus");

        edit_txt.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE||(event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                    init=edit_txt.getText().toString();

                    new NotificationsFragment.AsyncTask(root).execute("https://homework8-273123.appspot.com/trends?web_url="+init);
                    return true;
                }
                return false;
            }
        });

        return root;
    }

    public class AsyncTask extends android.os.AsyncTask<String,Void,String>
    {
        private View root;

        public AsyncTask( View root) {

            this.root = root;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONArray jObj = null;

            try {
                jObj = new JSONArray(s);

                 List<Integer> plot= new ArrayList<>();
                for(int i=0;i<jObj.length();i++)
                {
                    plot.add(Integer.parseInt(jObj.get(i).toString()));
                }
                entries1 = new ArrayList<>();
                for(int i=0;i<plot.size();i++)
                {
                    entries1.add(new Entry(i,plot.get(i)));
                }
                LineDataSet dataSet1 = new LineDataSet(entries1, "Trending chart for "+init);
                dataSet1.setLineWidth(1f);
                dataSet1.setDrawCircleHole(false);
                dataSet1.setCircleSize(3f);
                dataSet1.setColor(Color.parseColor("#6200EE"));
                dataSet1.setFillColor(Color.parseColor("#6200EE"));
                dataSet1.setCircleColor(Color.parseColor("#6200EE"));
                dataSet1.setValueTextColor(Color.parseColor("#6200EE"));
                dataSet1.setValueTextSize(10);
                List<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(dataSet1);
                LineData data = new LineData(dataSets);
                LineChart chart = (LineChart) root.findViewById(R.id.lineChart);
                Legend l = chart.getLegend();
                l.setTextSize(15);
                l.setFormSize(15);
                chart.setData(data);
                chart.getXAxis().setDrawGridLines(false);
                chart.getAxisLeft().setDrawGridLines(false);
                YAxis leftYAxis = chart.getAxisLeft();
                leftYAxis.setDrawAxisLine(false);
                leftYAxis.setDrawLabels(true);
                leftYAxis.setTextSize(12);
                leftYAxis.setAxisLineColor(Color.BLUE);
                chart.getAxisRight().setDrawGridLines(false);
                chart.getAxisRight().setTextSize(12);
                chart.getAxisRight().setAxisLineWidth(1);
                chart.getXAxis().setAxisLineWidth(1);
                chart.getXAxis().setTextSize(12);
                chart.invalidate();

            } catch (JSONException e) {

                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                return getData(strings[0]);
            }
            catch(IOException ex)
            {
                return "Network Error";
            }
        }
        private String getData(String urlPath) throws IOException
        {
            StringBuilder result=new StringBuilder();
            BufferedReader bufferedReader=null;

            try
            {
                URL url=new URL(urlPath);
                Log.d("URL",url.toString());
                HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.connect();

                InputStream inputStream=urlConnection.getInputStream();
                bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line=bufferedReader.readLine())!=null)
                {

                    result.append(line).append("\n");

                }
            }
            finally {
                if(bufferedReader!=null)
                    bufferedReader.close();
            }
            return result.toString();
        }
    }
}
