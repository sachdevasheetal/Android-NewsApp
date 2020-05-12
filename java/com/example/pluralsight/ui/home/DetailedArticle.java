package com.example.pluralsight.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.pluralsight.R;
import com.example.pluralsight.ui.favorites.SharedPreference;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DetailedArticle extends AppCompatActivity {
    TextView textViewAbstract;
    TextView textNewsTitle,textViewSection,textViewDate,textViewLink,fetch;
    ImageView imageViewDetailed;
    News_Model movie;
    private ShareActionProvider mShareActionProvider;
    private Menu menu;
    ProgressBar spinner;
    SharedPreference sharedPreference=new SharedPreference();
    News_Model store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_article);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
         movie = (News_Model) getIntent().getParcelableExtra("obj");
          store=new News_Model(movie.getUrl(),movie.getDescription(),movie.getTitle(),movie.getImage_url(),movie.getSection(),movie.getTime(),movie.getSelect(),movie.getId());
        textViewAbstract=(TextView) findViewById(R.id.textViewAbstract);
        textViewSection=(TextView) findViewById(R.id.textViewSection);
        textViewDate=(TextView)findViewById(R.id.textViewDate);
        textNewsTitle=(TextView)findViewById(R.id.textNewsTitle);
        imageViewDetailed=(ImageView)findViewById(R.id.imageViewDetailed);
        textViewLink=(TextView)findViewById(R.id.textViewLink);
        new AsyncTask().execute("https://homework8-273123.appspot.com/api/article/guard?web_url="+movie.getId());

    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem switchButton = menu.findItem(R.id.mFav);
        if(checkFavoriteItem(movie)){
            switchButton.setIcon(ContextCompat.getDrawable(this, R.mipmap.baseline_bookmark_black_48dp_2x));
            switchButton.setChecked(true);
        }else{
            switchButton.setIcon(ContextCompat.getDrawable(this, R.mipmap.baseline_bookmark_border_black_48dp_2x));
            switchButton.setChecked(false);
        }
        return super.onPrepareOptionsMenu(menu);

    }
    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        getMenuInflater().inflate(R.menu.detailed_menu, menu);
        getSupportActionBar().setTitle(movie.getTitle());
        return super.onCreateOptionsMenu(menu);
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        MenuItem switchButton = menu.findItem(R.id.mFav);
        if(checkFavoriteItem(movie)){
            switchButton.setIcon(ContextCompat.getDrawable(this, R.mipmap.baseline_bookmark_black_48dp_2x));
            switchButton.setChecked(true);
        }else{
            switchButton.setIcon(ContextCompat.getDrawable(this, R.mipmap.baseline_bookmark_border_black_48dp_2x));
            switchButton.setChecked(false);
        }

        switch (item.getItemId()) {

            case R.id.mShare:
                String hashtags="CSCI571NewsSearch";
                String url=movie.getUrl();
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
                    tweetUrl.append(urlEncode(hashtags));
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(str));
                List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
                for (ResolveInfo info : matches) {
                    if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                        intent.setPackage(info.activityInfo.packageName);
                    }
                }
                startActivity(intent);
                break;
            case R.id.mFav:
                if (!menu.findItem(R.id.mFav).isChecked()) {
                    sharedPreference.addFavorite(this, store);
                    Toast.makeText(this,
                            "\""+movie.getTitle()+"\" was added to Bookmarks",
                            Toast.LENGTH_SHORT).show();
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.mipmap.baseline_bookmark_black_48dp_2x));
                    menu.findItem(R.id.mFav).setChecked(true);

                } else {
                    sharedPreference.removeFavorite(this, store);
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.mipmap.baseline_bookmark_border_black_48dp_2x));
                    Toast.makeText(this,
                            "\""+movie.getTitle()+"\" was removed from favorites",
                            Toast.LENGTH_SHORT).show();
                    menu.findItem(R.id.mFav).setChecked(false);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    public boolean checkFavoriteItem(News_Model checkProduct) {
        boolean check = false;
        List<News_Model> favorites = sharedPreference.getFavorites(this);
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
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public  String capitalize(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    public class AsyncTask extends android.os.AsyncTask<String,Void,String>
    {
        ProgressBar spinner=(ProgressBar)findViewById(R.id.progressBar);
        TextView fetch=(TextView)findViewById(R.id.fetch);
        CardView card=(CardView)findViewById(R.id.card);
        @SuppressLint("ResourceAsColor")
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONArray jObj = null;

            try {
                jObj = new JSONArray(s);
                String url=jObj.getJSONObject(0).getString("web_url").toString();
                Log.d("PRINTING URl",url);
                 spinner.setVisibility(View.GONE);
                 fetch.setVisibility(View.GONE);
                 card.setVisibility(View.VISIBLE);
                textViewLink.setText(Html.fromHtml("<a href=\""+url+"\">View Full Article</a>"));
                textViewLink.setMovementMethod(LinkMovementMethod.getInstance());
                String image_url=new JSONArray(jObj.getJSONObject(0).getString("multimedia")).getJSONObject(0).getString("url").toString();
                Spanned text = Html.fromHtml(jObj.getJSONObject(0).getString("abstract").toString());
                ForegroundColorSpan spans[] = text.getSpans(0, text.length(),
                        ForegroundColorSpan.class);
                if (spans.length > 0) {
                    textViewAbstract.setLinkTextColor(spans[0].getForegroundColor());
                }
                textViewAbstract.setText(text);
                String section=jObj.getJSONObject(0).getString("section").toString();
                section=capitalize(section);
                textViewSection.setText(section);
                textNewsTitle.setText(movie.getTitle());
                Picasso.with(getApplicationContext()).load(image_url).resize(600, 200)
                        .centerInside().into(imageViewDetailed);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
                DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

                String time=jObj.getJSONObject(0).getString("pub_date");
                LocalDateTime dateTime = LocalDateTime.parse(time, formatter2);
                ZonedDateTime date11 = dateTime.atZone( ZoneId.of("Zulu"));
                ZonedDateTime finaldate = date11.withZoneSameInstant( ZoneId.of( "America/Los_Angeles" ) );
                dateTime.format(formatter);
                textViewDate.setText(formatter.format(finaldate));

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
