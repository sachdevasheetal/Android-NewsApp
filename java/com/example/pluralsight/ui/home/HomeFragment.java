package com.example.pluralsight.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
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
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.pluralsight.R;
import com.example.pluralsight.ui.RoundedTransformation;
import com.example.pluralsight.ui.favorites.SharedPreference;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.Locale;
import java.util.Objects;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private List<News_Model> listNews;
    private TextView textViewCity, textViewTemp, textViewState, textViewWeather,fetch;
    private ImageView imageView1;
    public static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private SwipeRefreshLayout swipeNews;
    private LocationManager locationManager;
    private GoogleApiClient mGoogleApiClient;
    private final int PERMISSION_LOCATION = 111;
    private String cityName, stateName, weather, temp,cityNameURI;
    private SharedPreference sharedPreference = new SharedPreference();
    private String provider;

    View root;
    private Bitmap mBitmap;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar2);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });

        swipeNews = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh);

        swipeNews.setRefreshing(false);
        swipeNews.setOnRefreshListener(this);
        textViewCity = (TextView) root.findViewById(R.id.textViewCity);
        textViewTemp = (TextView) root.findViewById(R.id.textViewTemp);
        textViewState = (TextView) root.findViewById(R.id.textViewState);
        textViewWeather = (TextView) root.findViewById(R.id.textViewWeather);
        recyclerView=(RecyclerView)root.findViewById(R.id.recyler_news);
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        //dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.line_divider));
         recyclerView.addItemDecoration(dividerItemDecoration);

        fetch = (TextView) root.findViewById(R.id.fetch);
        imageView1 = (ImageView) root.findViewById(R.id.imageView1);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        { mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API).enableAutoManage(getActivity(), this)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();}
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // return TODO;
        }
        Location location;
        if(provider!=null)
        {location = locationManager.getLastKnownLocation(provider);

        }

        return root;
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude;
        double longitude;
        if(location!=null)

        {
            longitude =location.getLongitude();
        latitude =location.getLatitude();}
        else
        {
            longitude =-118.283;
            latitude =34.0266;
        }
        Log.d("Longitude",Double.toString(longitude));
        Log.d("Latitude",Double.toString(latitude));
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        cityName = addresses.get(0).getLocality();

        stateName = addresses.get(0).getAdminArea();

        String countryName = addresses.get(0).getCountryName();;

        try {
            cityNameURI=URLEncoder.encode(cityName, "UTF-8").replace("+", "%20").toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        swipeNews.post(new Runnable() {
            @Override
            public void run() {
                //
                String AppId="";//Get your openweathermap AppId
                new WeatherAsyncTask(root).execute("https://api.openweathermap.org/data/2.5/weather?q="+cityNameURI+"&units=metric&appid="+AppId);

                listNews = new ArrayList<>();

            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            Log.d("CURRENTLOCATION 2","CURRENT");
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_LOCATION);

        }
        else
        {
            getCurrentLocation();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(newsAdapter!=null)
            newsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("CURRENTLOCATION 3","CURRENT");
        if(requestCode== PERMISSION_LOCATION && grantResults.length>0)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            { Log.d("CURRENTLOCATION 1","CURRENT");
                getCurrentLocation();}
            else
                Toast.makeText(getActivity(),"PERMSSION DENIED",Toast.LENGTH_LONG).show();
        }
    }

    private void getCurrentLocation() {
        //LocationRe
        Log.d("CURRENTLOCATION","CURRENT");
        try {
            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);
            onLocationChanged(location);

        } catch (SecurityException exception) {

        }
    }

    @Override
    public void onRefresh() {
        new StartAsyncTask(getActivity(),root).execute("https://homework8-273123.appspot.com/api/latest");
        listNews = new ArrayList<>();

    }

    public class WeatherAsyncTask extends AsyncTask<String,Void,String>
    {
        private Context mContext;
        private View root;
        ProgressBar spinner;
        public WeatherAsyncTask( View root) {
            this.root = root;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject jObj = null;

            try {
                jObj = new JSONObject(s);
                weather=new JSONArray(jObj.getString("weather")).getJSONObject(0).getString("main").toString();
                temp=new JSONObject(jObj.getString("main")).getString("temp").toString();
                float f=Float.parseFloat(temp);
                textViewCity.setText(cityName);
                textViewTemp.setText((int)f+" \u2103");
                textViewState.setText(stateName);
                textViewWeather.setText(weather);
                int img;
                if(weather.equalsIgnoreCase("clear"))
                    img=R.mipmap.clear_weather;
                else if(weather.equalsIgnoreCase("rain")||weather.equalsIgnoreCase("drizzle"))
                    img=R.mipmap.rainy_weather;
                else if(weather.equalsIgnoreCase("clouds"))
                    img=R.mipmap.cloudy_weather;
                else if(weather.equalsIgnoreCase("snow"))
                    img=R.mipmap.snowy_weather;
                else if(weather.equalsIgnoreCase("thunderstorm"))
                    img=R.mipmap.thunder_weather;
                else
                    img=R.mipmap.sunny_weather;


                Picasso.with(getContext()).load(img).transform(new RoundedTransformation(10, 4)).resize(600, 200)
                        .centerInside().into(imageView1);
                new StartAsyncTask(getActivity(),root).execute("https://homework8-273123.appspot.com/api/latest");
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
    public class StartAsyncTask extends AsyncTask<String, Void, String> implements NewsAdapter.OnNewsClickListner,NewsAdapter.OnfavClickListner,NewsAdapter.OnNewsLongClickListner
    {
        @Override
        public void onNewsClick(int position) {
            Intent intent=new Intent(this.mContext,DetailedArticle.class);
            intent.putExtra("obj", (Parcelable) listNews.get(position));
            startActivity(intent);

        }
        private Context mContext;
        private View root;
        ProgressBar spinner;
        public StartAsyncTask(Context mContext, View root) {
            this.mContext = mContext;
            this.root = root;
            this.spinner=(ProgressBar)root.findViewById(R.id.progressBar);
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
                    url=jObj.getJSONObject(i).getString("url").toString();
                    title=jObj.getJSONObject(i).getString("title").toString();
                    details=jObj.getJSONObject(i).getString("abstract").toString();
                    image_url=new JSONArray(jObj.getJSONObject(i).getString("multimedia")).getJSONObject(0).getString("url").toString();
                    section=jObj.getJSONObject(i).getString("section").toString();
                    section=capitalize(section);
                    //section="testingtestingtestingwwwwwwwwwww";
                    id=jObj.getJSONObject(i).getString("id").toString();
                    time=jObj.getJSONObject(i).getString("published_date").toString();
                    LocalDateTime dateTime = LocalDateTime.parse(time, formatter);
                    ZonedDateTime pubTime = dateTime.atZone( zoneId );
                    ZonedDateTime pubTime1 = ZonedDateTime.parse(pubTime.format(formatter));
                    Duration duration = Duration.between(pubTime1,current1);
                    Long d=duration.toDays();
                    Long h=duration.toHours();
                    Long m=duration.toMinutes();
                    Long sec=duration.getSeconds();
                    String send="";
                    int test=0;
                    if(d>0)
                        send=d.toString()+"d ago";
                    else if(h>0)
                        send=h.toString()+"h ago";
                    else if(m>0)
                        send=m.toString()+"m ago";
                    else if(sec>=0)
                        send=sec.toString()+"s ago";
                    else if(sec<0)
                        send=Integer.toString(test)+"s ago";

                    listNews.add(new News_Model(url,details,title,image_url,section,send,time, id));
                }
                swipeNews.setRefreshing(false);
            } catch (JSONException e) {


                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }

            spinner.setVisibility(View.GONE);

            fetch.setVisibility(View.GONE);
            newsAdapter=new NewsAdapter(getActivity(),listNews,this,this,this);

            recyclerView.setHasFixedSize(true);

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(newsAdapter);
            recyclerView.setNestedScrollingEnabled(false);

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
            final Dialog dialog = new Dialog(Objects.requireNonNull(getActivity()));
            // Include dialog.xml file
            LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            assert layoutInflater != null;
            @SuppressLint("InflateParams") final View layout2 = layoutInflater.inflate(R.layout.dialog,null);
            dialog.setContentView(layout2);
            // Set dialog title
            dialog.setTitle("Custom Dialog");
            dialog.getWindow().setLayout(1200, Constraints.LayoutParams.WRAP_CONTENT);
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

        public boolean checkFavoriteItem(News_Model checkProduct) {
            boolean check = false;
            List<News_Model> favorites = sharedPreference.getFavorites(Objects.requireNonNull(getActivity()));
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
