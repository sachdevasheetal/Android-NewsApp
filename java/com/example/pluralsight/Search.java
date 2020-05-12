package com.example.pluralsight;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Constraints;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.pluralsight.ui.favorites.SharedPreference;
import com.example.pluralsight.ui.home.DetailedArticle;
import com.example.pluralsight.ui.home.NewsAdapter;
import com.example.pluralsight.ui.home.News_Model;
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
import java.util.Objects;

public class Search extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    RecyclerView recyclerView;
    NewsAdapter newsAdapter;
    List<News_Model> listNews;
    private Menu menu;
    private SwipeRefreshLayout swipeNews;
    String message;
    SharedPreference sharedPreference = new SharedPreference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LayoutInflater inflator = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ResourceType") ViewGroup container = findViewById(R.layout.activity_search);
        listNews = new ArrayList<>();
        Intent intent = getIntent();
        message = intent.getStringExtra("obj");
        getSupportActionBar().setTitle("Search results for "+message);
        swipeNews = (SwipeRefreshLayout) findViewById(R.id.swipe_search);
        swipeNews.setRefreshing(false);
        swipeNews.setOnRefreshListener(this);
        recyclerView=(RecyclerView)findViewById(R.id.recyler_news_1);
        DividerItemDecoration dividerItemDecoration;
        dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        new Search.StartAsyncTask(this).execute("https://homework8-273123.appspot.com/api/search/guard?web_url="+message);

    }
    @Override
    public void onResume() {
        super.onResume();
        if(newsAdapter!=null)
            newsAdapter.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        new Search.StartAsyncTask(this).execute("https://homework8-273123.appspot.com/api/search/guard?web_url="+message);
    }


    public class StartAsyncTask extends AsyncTask<String, Void, String> implements NewsAdapter.OnNewsClickListner,NewsAdapter.OnfavClickListner,NewsAdapter.OnNewsLongClickListner
    {
        @Override
        public void onNewsClick(int position) {
            Intent intent=new Intent(this.mContext, DetailedArticle.class);
            intent.putExtra("obj", (Parcelable) listNews.get(position));
            startActivity(intent);
            Log.d("CLICKED","CLICKED NEWS "+position);

        }
        private Context mContext;
        ProgressBar spinner;

        TextView fetch;
        public StartAsyncTask(Context mContext) {
            this.mContext = mContext;
            //this.root = root;
            this.spinner=(ProgressBar)findViewById(R.id.progressBar_1);
            this.fetch=(TextView)findViewById(R.id.fetch);
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        public  String capitalize(String str) {
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
                for(int i=0;i<jObj.length();i++)
                {
                    LocalDateTime ldt = LocalDateTime.now();            //Local date time

                    ZoneId zoneId = ZoneId.of( "UTC" );        //Zone information
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    ZonedDateTime current = ZonedDateTime.now(zoneId);
                    ZonedDateTime current1 = ZonedDateTime.parse(current.format(formatter));

                    String url,details,title,section,image_url,id,time;
                    url=jObj.getJSONObject(i).getString("web_url").toString();
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
;
                    Duration duration = Duration.between(pubTime1,current1);
                    Long d=duration.toDays();
                    Long h=duration.toHours();
                    Long m=duration.toMinutes();
                    Long sec=duration.getSeconds();
                    String send="";
                    if(d>0)
                        send=d.toString()+"d ago";
                    else if(h>0)
                        send=h.toString()+"h ago";
                    else if(m>0)
                        send=m.toString()+"m ago";
                    else if(sec>=0)
                        send=sec.toString()+"s ago";
                    else if(sec<0)
                        send=Integer.toString(0)+"s ago";
                    listNews.add(new News_Model(url,details,title,image_url,section,send,time, id));
                }

                //sLog.d("SIZE",JObj.length.toString());
            } catch (JSONException e) {


                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }


            Log.d("SHEETAL12",listNews.toString());

            spinner.setVisibility(View.GONE);
            fetch.setVisibility(View.GONE);
            swipeNews.setRefreshing(false);
            newsAdapter=new NewsAdapter(mContext,listNews,this,this,this);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            recyclerView.setAdapter(newsAdapter);
            newsAdapter.notifyDataSetChanged();
            Log.d("SHEETAL123",listNews.toString());
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

        }

        @Override
        public boolean onNewsLongClick(final int position) {
            final Dialog dialog = new Dialog(Objects.requireNonNull(mContext));
            // Include dialog.xml file
            LayoutInflater layoutInflater = (LayoutInflater)
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            assert layoutInflater != null;
            @SuppressLint("InflateParams") final View layout2 = layoutInflater.inflate(R.layout.dialog,null);
            dialog.setContentView(layout2);
            // Set dialog title
            dialog.setTitle("Custom Dialog");
            dialog.getWindow().setLayout(1200, Constraints.LayoutParams.WRAP_CONTENT);
            Log.d("TESTING",listNews.get(position).getImage_url());

            String uri=listNews.get(position).getImage_url().toString();
            Picasso.with(mContext).load(uri).resize(600, 200)
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
                    String str="https://twitter.com/intent/tweet?text="+text+"&url="+url+"&hashtags=CSCI571NewsSearch";
                    tweetUrl.append(TextUtils.isEmpty(text) ? urlEncode(" ") : urlEncode(text));
                    if (!TextUtils.isEmpty(url)) {
                        tweetUrl.append("&url=");
                        tweetUrl.append(urlEncode(url));
                    }
                    if (!TextUtils.isEmpty(hashtags)) {
                        tweetUrl.append("&hastags=");
                        tweetUrl.append(hashtags);
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(str));
                    List<ResolveInfo> matches = mContext.getPackageManager().queryIntentActivities(intent, 0);
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
                    ImageButton btn = (ImageButton) layout2.findViewById(R.id.imageButton2);
                    String tag = btn.getTag().toString();
                    if (tag.equalsIgnoreCase("grey")) {
                        sharedPreference.addFavorite(mContext, listNews.get(position));
                        Toast.makeText(mContext,
                                "\""+listNews.get(position).getTitle()+"\" was added to Bookmarks",
                                Toast.LENGTH_SHORT).show();

                        btn.setTag("red");
                        btn.setImageResource(R.mipmap.baseline_bookmark_black_36dp);
                    } else {
                        sharedPreference.removeFavorite(mContext, listNews.get(position));
                        btn.setTag("grey");
                        btn.setImageResource(R.mipmap.baseline_bookmark_border_black_36dp);
                        Toast.makeText(mContext,
                                "\""+listNews.get(position).getTitle()+"\" was removed from favorites",
                                Toast.LENGTH_SHORT).show();
                    }
                    newsAdapter.notifyDataSetChanged();
                }
            });
            dialog.show();


            return true;
        }
        public boolean checkFavoriteItem(News_Model checkProduct) {
            boolean check = false;
            List<News_Model> favorites = sharedPreference.getFavorites(Objects.requireNonNull(mContext));
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
    }
}
