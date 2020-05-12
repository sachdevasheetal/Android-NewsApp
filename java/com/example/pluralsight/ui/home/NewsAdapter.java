package com.example.pluralsight.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pluralsight.R;
import com.example.pluralsight.ui.favorites.SharedPreference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsView> {

    private Context mCtx;
    private List<News_Model> newsList;
    private OnNewsClickListner mOnNewsClickListner;
    private OnfavClickListner mOnfavClickListner;
    private OnNewsLongClickListner mOnNewsLongClickListner;

    SharedPreference sharedPreference=new SharedPreference();

    public NewsAdapter(Context mCtx, List<News_Model> newsList,OnNewsClickListner mOnNewsClickListner,OnfavClickListner mOnfavClickListner,OnNewsLongClickListner mOnNewsLongClickListner) {
        this.mCtx = mCtx;
        this.newsList = newsList;
        this.mOnNewsClickListner=mOnNewsClickListner;
        this.mOnfavClickListner=mOnfavClickListner;
        this.mOnNewsLongClickListner=mOnNewsLongClickListner;

    }

    @NonNull
    @Override
    public NewsView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(mCtx);
        View view=inflater.inflate(R.layout.news_list,null);
        NewsView holder=new NewsView(view,mOnNewsClickListner,mOnfavClickListner,mOnNewsLongClickListner);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsView holder, int position) {
        News_Model news_model = newsList.get(position);
        holder.textViewTitle.setText(news_model.getTitle());
        holder.textViewSection.setText(news_model.getSection());
        holder.textViewTime.setText(news_model.getTime());
        Log.d("CHECKING", Boolean.toString(checkFavoriteItem(newsList.get(position))));
        if (checkFavoriteItem(newsList.get(position))) {
            holder.favView.setImageResource(R.mipmap.baseline_bookmark_black_36dp);
            holder.favView.setTag("red");
        } else {
            holder.favView.setImageResource(R.mipmap.baseline_bookmark_border_black_36dp);
            holder.favView.setTag("grey");
        }
        Picasso.with(mCtx).load(news_model.getImage_url()).resize(600, 200)
                .centerInside().into(holder.imageViewNews);
        //holder.textViewTime.setText(news_model.getTime());
    }

     boolean checkFavoriteItem(News_Model checkProduct) {
        boolean check = false;
        List<News_Model> favorites = sharedPreference.getFavorites(mCtx);
        if (favorites != null) {
            for (News_Model product : favorites) {
                //Log.d("MATCHING 1",checkProduct.toString());
                //Log.d("MATCHING 2",product.toString());
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

    class NewsView extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener  {
        ImageView imageViewNews,favView;
        TextView textViewTitle,textViewDescription,textViewTime,textViewSection;
        OnNewsClickListner onNewsClickListner;
        OnfavClickListner onfavClickListner;
        OnNewsLongClickListner mOnNewsLongClickListner;
         NewsView(@NonNull View itemView,OnNewsClickListner onNewsClickListner,OnfavClickListner onfavClickListner,OnNewsLongClickListner mOnNewsLongClickListner) {
            super(itemView);
            this.onNewsClickListner=onNewsClickListner;
            this.onfavClickListner=onfavClickListner;
            this.mOnNewsLongClickListner=mOnNewsLongClickListner;

            textViewTitle=itemView.findViewById(R.id.textViewTitleNews);
            textViewSection = itemView.findViewById(R.id.textViewSection);
            imageViewNews=itemView.findViewById(R.id.imageViewNews);
            textViewTime=itemView.findViewById(R.id.textViewShortDesc);
            favView=itemView.findViewById(R.id.imageViewFav);

            favView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            //favView.OnItemLongClickListener(this);
            itemView.setOnClickListener(this);


        }


        @Override
        public void onClick(View v) {

            if(v.getId()==R.id.imageViewFav)
            {
                onfavClickListner.onFavClick(getAdapterPosition());
                String tag = favView.getTag().toString();
                if (tag.equalsIgnoreCase("grey")) {
                    sharedPreference.addFavorite(mCtx, newsList.get(getAdapterPosition()));
                    Toast.makeText(mCtx,
                            "\""+newsList.get(getAdapterPosition()).getTitle()+"\" was added to Bookmarks",
                            Toast.LENGTH_SHORT).show();

                    favView.setTag("red");
                    favView.setImageResource(R.mipmap.baseline_bookmark_black_36dp);
                } else {
                    sharedPreference.removeFavorite(mCtx, newsList.get(getAdapterPosition()));
                    favView.setTag("grey");
                    favView.setImageResource(R.mipmap.baseline_bookmark_border_black_36dp);
                    Toast.makeText(mCtx,
                            "\""+newsList.get(getAdapterPosition()).getTitle()+"\" was removed from favorites",
                            Toast.LENGTH_SHORT).show();
                }
            }

            else
            {onNewsClickListner.onNewsClick(getAdapterPosition());}

        }

        @Override
        public boolean onLongClick(View v) {

            return mOnNewsLongClickListner.onNewsLongClick(getAdapterPosition());
            //return true;
        }
    }
    public interface OnNewsClickListner
    {
        void onNewsClick(int position);
    }
    public interface OnNewsLongClickListner
    {
        boolean onNewsLongClick(int position);
    }
    public interface OnfavClickListner
    {
        void onFavClick(int position);
    }


}
