package com.example.pluralsight.ui.bookmark;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Constraints;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pluralsight.R;
import com.example.pluralsight.ui.favorites.SharedPreference;
import com.example.pluralsight.ui.home.DetailedArticle;
import com.example.pluralsight.ui.home.News_Model;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BookmarkFragment extends Fragment implements BookmarkAdapter.OnBookNewsClickListner,BookmarkAdapter.OnBookfavClickListner,BookmarkAdapter.OnNewsLongClickListnerBookmark{
    private BookmarkViewModel bookmarkViewModel;
    private BookmarkAdapter bookmarkAdapter;
    RecyclerView recyclerView;
    TextView text;
    public List<News_Model> list;

    SharedPreference sharedPreference=new SharedPreference();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        bookmarkViewModel =
                ViewModelProviders.of(this).get(BookmarkViewModel.class);
        View root = inflater.inflate(R.layout.fragment_bookmark, container, false);

        bookmarkViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        list= new ArrayList<>();
        recyclerView=(RecyclerView)root.findViewById(R.id.book_recycler);
        text=(TextView)root.findViewById(R.id.empty);
        list=sharedPreference.getFavorites(getActivity());
        if(list==null||list.size()==0)
            Log.d("Finally Empty","Empty");


        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);

        recyclerView.addItemDecoration(dividerItemDecoration);
        if(list==null||list.size()==0) {
            recyclerView.setVisibility(View.GONE);
            text.setVisibility(View.VISIBLE);
        }
        else {
            bookmarkAdapter = new BookmarkAdapter(getActivity(),list,this,this,this);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
            recyclerView.setHasFixedSize(true);
            recyclerView.setVisibility(View.VISIBLE);
            text.setVisibility(View.GONE);
            recyclerView.setAdapter(bookmarkAdapter);

        }

        return root;
    }

    @Override
    public void onFavClick(int position) {

        String title=list.get(position).getTitle();
        list.remove(position);

        if(list.size()==0) {
            recyclerView.setVisibility(View.GONE);
            text.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(),
                    "\""+title+"\" was removed from favorites",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            text.setVisibility(View.GONE);
            Toast.makeText(getActivity(),
                    "\""+title+"\" was removed from favorites",
                    Toast.LENGTH_SHORT).show();
            bookmarkAdapter.notifyItemRemoved(position);

        }
    }
    @Override
    public void onResume() {
        super.onResume();


        SharedPreference sharedPreference=new SharedPreference();
        list=sharedPreference.getFavorites(getActivity());

        if(bookmarkAdapter!=null)
        {
            list=sharedPreference.getFavorites(getActivity());
            bookmarkAdapter = new BookmarkAdapter(getActivity(),list,this,this,this);

            if(list==null||list.size()==0) {
                recyclerView.setVisibility(View.GONE);
                text.setVisibility(View.VISIBLE);
            }
            else {
                recyclerView.setVisibility(View.VISIBLE);
                text.setVisibility(View.GONE);
                recyclerView.setAdapter(bookmarkAdapter);

            }
        }

    }
    @Override
    public void onNewsClick(int position) {
        Intent intent=new Intent(getActivity(), DetailedArticle.class);
        intent.putExtra("obj", (Parcelable) list.get(position));
        startActivity(intent);
    }

    @Override
    public boolean onNewsLongClickBookmark(final int position) {
        Log.d("CALLING FAV","FAV");
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
        Log.d("TESTING",list.get(position).getImage_url());

        String uri=list.get(position).getImage_url().toString();
        Picasso.with(getContext()).load(uri).resize(600, 200)
                .centerInside().into((ImageView) layout2.findViewById(R.id.imageView4));
        ((TextView)layout2.findViewById(R.id.textView5)).setText(list.get(position).getTitle());
        ImageButton buttonClickFav,buttonClickShare;
        buttonClickFav = (ImageButton) dialog.findViewById(R.id.imageButton);
        buttonClickFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hashtags="CSCI571NewsSearch";

                String url=list.get(position).getUrl();
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
        if (checkFavoriteItem(list.get(position))) {
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
                    sharedPreference.addFavorite(getActivity(), list.get(position));
                    Toast.makeText(getActivity(),
                            "\""+list.get(position).getTitle()+"\" was added to Bookmarks",
                            Toast.LENGTH_SHORT).show();

                    btn.setTag("red");
                    btn.setImageResource(R.mipmap.baseline_bookmark_black_36dp);
                } else {
                    sharedPreference.removeFavorite(getActivity(), list.get(position));
                    btn.setTag("grey");
                    btn.setImageResource(R.mipmap.baseline_bookmark_border_black_36dp);
                    Toast.makeText(getActivity(),
                            "\""+list.get(position).getTitle()+"\" was removed from favorites",
                            Toast.LENGTH_SHORT).show();
                }
                list.remove(position);


                if(list.size()==0) {
                    recyclerView.setVisibility(View.GONE);
                    text.setVisibility(View.VISIBLE);
                }
                else {
                    recyclerView.setVisibility(View.VISIBLE);
                    text.setVisibility(View.GONE);
                    bookmarkAdapter.notifyItemRemoved(position);

                }
                dialog.dismiss();
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

