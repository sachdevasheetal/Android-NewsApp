package com.example.pluralsight.ui.bookmark;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pluralsight.R;
import com.example.pluralsight.ui.favorites.SharedPreference;
import com.example.pluralsight.ui.home.News_Model;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.MyViewHolder> implements View.OnClickListener{

    private Context mContext;
    private List<News_Model> newsList;
    private OnBookNewsClickListner mOnNewsClickListner;
    private OnBookfavClickListner mOnfavClickListner;
    private OnNewsLongClickListnerBookmark mOnNewsLongClickListnerBookmark;


    SharedPreference sharedPreference=new SharedPreference();
    public BookmarkAdapter(Context mContext, List<News_Model> newsList, OnBookNewsClickListner mOnNewsClickListner, OnBookfavClickListner mOnfavClickListner,OnNewsLongClickListnerBookmark mOnNewsLongClickListnerBookmark) {


        this.mContext = mContext;
        this.newsList = newsList;
        this.mOnNewsClickListner = mOnNewsClickListner;
        this.mOnfavClickListner = mOnfavClickListner;
        this.mOnNewsLongClickListnerBookmark=mOnNewsLongClickListnerBookmark;
        if(newsList.size()==0)
            Log.d("EMPTYCONST","EMPTY");

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("viewType",Integer.toString(viewType));
        if(viewType==0)
            Log.d("EMPTY","EMPTY");

        LayoutInflater inflater= LayoutInflater.from(mContext);
        Log.d("CHECKING","CALLING ON CREATEA");
        View view=inflater.inflate(R.layout.bookmark_card,parent,false);
        MyViewHolder holder=new MyViewHolder(view,mOnNewsClickListner,mOnfavClickListner,mOnNewsLongClickListnerBookmark);
        return holder;

    }



    @Override
    public int getItemViewType(int position) {
        Log.d("EMPTY getItem","EMPTY");
        if(newsList.size()==0)
            return 0;
        else
            return 1;

    }
    public  String capitalize(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.book_title.setText(newsList.get(position).getTitle());
        String section=newsList.get(position).getSection();
        section=capitalize(section);
        holder.book_section.setText(section);

        Log.d("TITLE IN BOOK",newsList.get(position).getSection());

        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String time=newsList.get(position).getSelect();
        LocalDateTime dateTime = LocalDateTime.parse(time, formatter2);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM");

        ZonedDateTime date11 = dateTime.atZone( ZoneId.of("Zulu"));
        ZonedDateTime finaldate = date11.withZoneSameInstant( ZoneId.of( "America/Los_Angeles" ) );


        dateTime.format(formatter);

        holder.book_date.setText( formatter.format(finaldate));

        if (checkFavoriteItem(newsList.get(position))) {
            holder.favView.setImageResource(R.mipmap.baseline_bookmark_black_36dp);
            holder.favView.setTag("red");

        } else {
            holder.favView.setImageResource(R.mipmap.baseline_bookmark_border_black_36dp);
            holder.favView.setTag("grey");

        }
        Picasso.with(mContext).load(newsList.get(position).getImage_url()).resize(600, 200)
                .centerInside().into(holder.imageViewNews);
    }

    public boolean checkFavoriteItem(News_Model checkProduct) {
        boolean check = false;
        List<News_Model> favorites = sharedPreference.getFavorites(mContext);
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
    public int getItemCount() {
        return newsList.size();
    }

    @Override
    public void onClick(View v) {

    }

    public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        ImageView imageViewNews,favView;
        TextView book_title,book_date,book_section;
        OnBookNewsClickListner onNewsClickListner;
        OnBookfavClickListner onfavClickListner;
        OnNewsLongClickListnerBookmark mOnNewsLongClickListnerBookmark;

        public MyViewHolder(@NonNull View itemView, OnBookNewsClickListner onNewsClickListner, OnBookfavClickListner onfavClickListner,OnNewsLongClickListnerBookmark mOnNewsLongClickListnerBookmark) {
            super(itemView);
            Log.d("EMPTY view","EMPTY");
            this.onNewsClickListner = onNewsClickListner;
            this.onfavClickListner = onfavClickListner;
            this.mOnNewsLongClickListnerBookmark=mOnNewsLongClickListnerBookmark;
            imageViewNews=itemView.findViewById(R.id.book_img);
            favView=itemView.findViewById(R.id.book_fav);
            book_title=itemView.findViewById(R.id.book_title);
            book_date=itemView.findViewById(R.id.book_date);
            book_section=itemView.findViewById(R.id.book_section);
            favView.setOnClickListener(this);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.book_fav)
            {


                String tag = favView.getTag().toString();
                sharedPreference.removeFavorite(mContext, newsList.get(getAdapterPosition()));
                onfavClickListner.onFavClick(getAdapterPosition());

            }

            else
            {onNewsClickListner.onNewsClick(getAdapterPosition());}
        }

        @Override
        public boolean onLongClick(View v) {
            return mOnNewsLongClickListnerBookmark.onNewsLongClickBookmark(getAdapterPosition());
        }
    }
    public interface OnBookfavClickListner
    {
        void onFavClick(int position);
    }
    public interface OnBookNewsClickListner
    {
        void onNewsClick(int position);
    }
    public interface OnNewsLongClickListnerBookmark
    {
        boolean onNewsLongClickBookmark(int position);
    }
}
