package com.example.pluralsight.ui.dashboard;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Constraints;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.pluralsight.R;
import com.example.pluralsight.ui.favorites.SharedPreference;
import com.example.pluralsight.ui.home.DetailedArticle;
import com.example.pluralsight.ui.home.NewsAdapter;
import com.example.pluralsight.ui.home.News_Model;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
public class DashboardFragment extends Fragment {

    private Toolbar mToolbar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
         View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        PagerAdapter mAdapter = new PagerAdapter(((AppCompatActivity) getActivity()).getSupportFragmentManager());
        TabLayout mTabLayout = (TabLayout) root.findViewById(R.id.tabLayout);
        ViewPager mPager = (ViewPager) root.findViewById(R.id.viewPager);
        mPager.setAdapter(mAdapter);
        mTabLayout.setTabsFromPagerAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mPager);

        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }


    public  static class MyFragment extends Fragment {

        private static final String ARG_PAGE = "arg_page";
        private  SwipeRefreshLayout swipeNews;
        RecyclerView recyclerView;
        List<News_Model> listNews;
        View root;
        String section="";
        com.example.pluralsight.ui.home.NewsAdapter newsAdapter;
        public MyFragment()
        {

        }

        @Override
        public void onResume() {
            super.onResume();
            if(newsAdapter!=null)
            newsAdapter.notifyDataSetChanged();
        }

        public class StartAsyncTask extends AsyncTask<String, Void, String> implements com.example.pluralsight.ui.home.NewsAdapter.OnNewsClickListner, com.example.pluralsight.ui.home.NewsAdapter.OnfavClickListner, NewsAdapter.OnNewsLongClickListner
        {
            SharedPreference sharedPreference = new SharedPreference();
            @Override
            public void onNewsClick(int position) {

                Intent intent=new Intent(this.mContext, DetailedArticle.class);
                intent.putExtra("obj", (Parcelable) listNews.get(position));
                startActivity(intent);
                Log.d("CLICKED","CLICKED NEWS "+position);
                Log.d("CLICKED","CLICKED NEWS "+position);

            }
            private Context mContext;
            private View root;
            ProgressBar spinner;
            TextView fet;

            StartAsyncTask(Context mContext, View root) {
                this.mContext = mContext;
                this.root = root;
                this.spinner=(ProgressBar)root.findViewById(R.id.progressBar);
                this.fet=(TextView)root.findViewById(R.id.fetch1) ;
                listNews = new ArrayList<>();
            }


            @Override
            protected void onPreExecute() {

                super.onPreExecute();

            }
            String capitalize(String str) {
                if(str == null || str.isEmpty()) {
                    return str;
                }

                return str.substring(0, 1).toUpperCase() + str.substring(1);
            }
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSONArray jObj = null;

                try {
                    jObj = new JSONArray(s);
                    Log.d("SHEETAL",jObj.toString());
                    for(int i=0;i<jObj.length();i++)
                    {
                        LocalDateTime ldt = LocalDateTime.now();            //Local date time

                        ZoneId zoneId = ZoneId.of( "UTC" );        //Zone information
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
                        ZonedDateTime current = ZonedDateTime.now(zoneId);
                        ZonedDateTime current1 = ZonedDateTime.parse(current.format(formatter));
                        Log.d("CURRENTTime",current.format(formatter).toString());

                        String url,details,title,section,image_url,id,time;
                        url=jObj.getJSONObject(i).getString("url").toString();
                        title=jObj.getJSONObject(i).getString("title").toString();
                        details=jObj.getJSONObject(i).getString("abstract").toString();
                        image_url=new JSONArray(jObj.getJSONObject(i).getString("multimedia")).getJSONObject(0).getString("url").toString();
                        section=jObj.getJSONObject(i).getString("section").toString();
                        section=capitalize(section);
                        id=jObj.getJSONObject(i).getString("id").toString();
                        time=jObj.getJSONObject(i).getString("published_date").toString();

                        LocalDateTime dateTime = LocalDateTime.parse(time, formatter);
                        ZonedDateTime pubTime = dateTime.atZone( zoneId );
                        ZonedDateTime pubTime1 = ZonedDateTime.parse(pubTime.format(formatter));
                        Log.d("DATETIME",pubTime1.toString());
                        Duration duration = Duration.between(pubTime1,current1);
                        long d=duration.toDays();
                        long h=duration.toHours();
                        long m=duration.toMinutes();
                        long sec=duration.getSeconds();
                        String send="";
                        if(d>0)
                            send= Long.toString(d) +"d ago";
                        else if(h>0)
                            send= Long.toString(h) +"h ago";
                        else if(m>0)
                            send= Long.toString(m) +"m ago";
                        else if(sec>=0)
                            send= Long.toString(sec) +"s ago";
                        else send=Integer.toString(0)+"s ago";

                        listNews.add(new News_Model(url,details,title,image_url,section,send,time, id));
                    }
                   swipeNews.setRefreshing(false);

                } catch (JSONException e) {


                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                }
                newsAdapter=new NewsAdapter(getActivity(),listNews,this,this,this);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(newsAdapter);
                spinner.setVisibility(View.GONE);

                fet.setVisibility(View.GONE);
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

            @Override
            public void onFavClick(int position) {

                Log.d("CLICKED","CLICKED FAVS "+position);
            }

            @Override
            public boolean onNewsLongClick(final int position) {

                Log.d("CLICKED","CLICKED FAVS "+position);
                final Dialog dialog = new Dialog(getActivity());
                // Include dialog.xml file
                LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                @SuppressLint("InflateParams") final View layout2 = layoutInflater.inflate(R.layout.dialog,null);
                dialog.setContentView(layout2);
                // Set dialog title
                dialog.setTitle("Custom Dialog");
                dialog.getWindow().setLayout(1200, Constraints.LayoutParams.WRAP_CONTENT);
                Log.d("TESTING",listNews.get(position).getImage_url());

                String uri=listNews.get(position).getImage_url().toString();
                Picasso.with(getContext()).load(uri).resize(600, 200)
                        .centerInside().into((ImageView) layout2.findViewById(R.id.imageView4));
                ((TextView)layout2.findViewById(R.id.textView5)).setText(listNews.get(position).getTitle());
                ImageButton buttonClickFav,buttonClickShare;
                buttonClickFav = (ImageButton) dialog.findViewById(R.id.imageButton);
                buttonClickFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String hashtags="CSCI571NewsSearch";

                        String url=listNews.get(position).getUrl();
                        String text="Check out this link:";

                        StringBuilder tweetUrl = new StringBuilder("https://twitter.com/intent/tweet?text=");
                        tweetUrl.append(TextUtils.isEmpty(text) ? urlEncode(" ") : urlEncode(text));
                        String str="https://twitter.com/intent/tweet?text="+text+"&url="+url+"&hashtags=CSCI571NewsSearch";
                        if (!TextUtils.isEmpty(url)) {
                            tweetUrl.append("&url=");
                            tweetUrl.append(urlEncode(url));
                        }
                        if (!TextUtils.isEmpty(hashtags)) {
                            tweetUrl.append("&hastags=");
                            tweetUrl.append(urlEncode(hashtags));
                        }
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(str));
                        List<ResolveInfo> matches = getActivity().getPackageManager().queryIntentActivities(intent, 0);
                        for (ResolveInfo info : matches) {
                            if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                                intent.setPackage(info.activityInfo.packageName);
                            }
                        }
                        startActivity(intent);
                    }
                });

                buttonClickShare=(ImageButton) dialog.findViewById(R.id.imageButton2);
                if (checkFavoriteItem(listNews.get(position))) {
                    buttonClickShare.setImageResource(R.mipmap.baseline_bookmark_black_36dp);
                    buttonClickShare.setTag("red");

                } else {
                    buttonClickShare.setImageResource(R.mipmap.baseline_bookmark_border_black_36dp);
                    buttonClickShare.setTag("grey");

                }
                buttonClickShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("NEWSSEARCH","NEWSSEARCH");
                        ImageButton btn = (ImageButton) layout2.findViewById(R.id.imageButton2);

                        String tag = btn.getTag().toString();
                        if (tag.equalsIgnoreCase("grey")) {
                            sharedPreference.addFavorite(getActivity(), listNews.get(position));
                            Toast.makeText(getActivity(),
                                    "\""+listNews.get(position).getTitle()+"\" was added to Bookmarks",
                                    Toast.LENGTH_SHORT).show();

                            btn.setTag("red");
                            btn.setImageResource(R.mipmap.baseline_bookmark_black_36dp);
                        } else {
                            sharedPreference.removeFavorite(getActivity(), listNews.get(position));
                            btn.setTag("grey");
                            btn.setImageResource(R.mipmap.baseline_bookmark_border_black_36dp);
                            Toast.makeText(getActivity(),
                                    "\""+listNews.get(position).getTitle()+"\" was removed from favorites",
                                    Toast.LENGTH_SHORT).show();
                        }
                        newsAdapter.notifyDataSetChanged();
                    }
                });
                dialog.show();
                return true;
            }
        }
        public boolean checkFavoriteItem(News_Model checkProduct) {
            SharedPreference sharedPreference = new SharedPreference();
            boolean check = false;
            List<News_Model> favorites = sharedPreference.getFavorites(getActivity());
            if (favorites != null) {
                for (News_Model product : favorites) {

                    if (product.getTitle().equals(checkProduct.getTitle())) {
                        check = true;
                        break;
                    }
                }
            }
            return check;
        }
        public  String urlEncode(String s) {
            try {
                return URLEncoder.encode(s, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Log.wtf("wtf", "UTF-8 should always be supported", e);
                throw new RuntimeException("URLEncoder.encode() failed for " + s);
            }
        }
        public static MyFragment newInstance(int pageNumber)
        {
            Log.d("INSTANCE","INSTANCE");
            MyFragment myFragment = new MyFragment();
            Bundle arguments =new Bundle();
            arguments.putInt(ARG_PAGE,pageNumber);
            myFragment.setArguments(arguments);
            return myFragment;
        }

        @Override
        public void onViewStateRestored(@Nullable Bundle savedInstanceState) {

            super.onViewStateRestored(savedInstanceState);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

            super.onViewCreated(view, savedInstanceState);
            Log.d("onViewStateRestored","onViewStateRestored");
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            Log.d("NEW TAB","FRAGMENT");
            Log.d("INSTANCE 1","INSTANCE");
            Bundle arguments =getArguments();
            final int pageNumber=arguments.getInt(ARG_PAGE);
            TextView myText =new TextView(getActivity());

            myText.setText("I am in tab"+pageNumber);
            myText.setGravity(Gravity.CENTER);
             root = inflater.inflate(R.layout.fragment_headlines, container, false);
            TextView fetch;
            fetch=(TextView)root.findViewById(R.id.fetch1);
            ProgressBar spinner=(ProgressBar)root.findViewById(R.id.progressBar);
            swipeNews = (SwipeRefreshLayout) root.findViewById(R.id.swipe);
            listNews = new ArrayList<>();

            if(pageNumber==0)
                section= "world";
            else if(pageNumber==1)
                section= "business";
            else if(pageNumber==2)
                section= "politics";
            else if(pageNumber==3)
                section= "sport";
            else if(pageNumber==4)
                section= "technology";
            else if(pageNumber==5)
                section="science";
            recyclerView=(RecyclerView)root.findViewById(R.id.recyler_news);

            new MyFragment.StartAsyncTask(getActivity(),root).execute("https://homework8-273123.appspot.com/api/guard?web_url="+section);

            swipeNews.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    listNews = new ArrayList<>();

                    if(pageNumber==0)
                        section= "world";
                    else if(pageNumber==1)
                        section= "business";
                    else if(pageNumber==2)
                        section= "politics";
                    else if(pageNumber==3)
                        section= "sport";
                    else if(pageNumber==4)
                        section= "technology";
                    else if(pageNumber==5)
                        section="science";

                    new MyFragment.StartAsyncTask(getActivity(),root).execute("https://homework8-273123.appspot.com/api/guard?web_url="+section);
                }
            });

            return root;

        }


    }


}

class PagerAdapter extends FragmentStatePagerAdapter
{

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        DashboardFragment.MyFragment myFragment= DashboardFragment.MyFragment.newInstance(position);

        return myFragment;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public int getItemPosition(Object object){
        return POSITION_NONE;
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0)
            return "World";
        else if(position==1)
            return "Business";
        else if(position==2)
            return "Politics";
        else if(position==3)
            return "Sports";
        else if(position==4)
            return "Technology";
        else if(position==5)
            return "Science";
        return "TAB " + position;
    }
}
