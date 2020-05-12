package com.example.pluralsight.ui.favorites;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.pluralsight.ui.home.News_Model;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SharedPreference {
    public static final String PREFS_NAME = "NEWS_APP";
    public static final String FAVORITES = "Article_Favorite";

    public SharedPreference() {
        super();
    }
    // This four methods are used for maintaining favorites.
    public void saveFavorites(Context context, List<News_Model> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);
        Log.d("FAVORITES",jsonFavorites);
        editor.putString(FAVORITES, jsonFavorites);

        editor.commit();
//        SharedPreferences settings;
//        SharedPreferences.Editor editor;
//        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        editor = settings.edit();
//
//        GsonBuilder builder = new GsonBuilder();
//        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
//        builder.excludeFieldsWithoutExposeAnnotation();
//        Gson sExposeGson = builder.create();
//        String jsonFavorites = sExposeGson.toJson(favorites);
//        editor.putString(FAVORITES, jsonFavorites);
//        editor.commit();
    }
    public void addFavorite(Context context, News_Model product) {
        List<News_Model> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<News_Model>();

        Log.d("ADDING",Integer.toString(favorites.size()));
        favorites.add(product);
        saveFavorites(context, favorites);
    }

    public void removeFavorite(Context context, News_Model product) {
        ArrayList<News_Model> favorites = getFavorites(context);
        //List<News_Model> favorites=getFavorites(context);
        if (favorites != null) {
            for (News_Model product1 : favorites) {
                Log.d("MATCHING 1",product1.getTitle());
                Log.d("MATCHING 2",product.getTitle());
                if (product1.getTitle().equals(product.getTitle())) {
                    //check = true;
                    favorites.remove(product1);
                    break;
                }
            }



            //favorites.remove(product);
            //Log.d("REMOVING",Integer.toString(favorites.size()));
            Log.d("REMOVING",product.toString());
            saveFavorites(context, favorites);
        }
    }

    public ArrayList<News_Model> getFavorites(Context context) {
       SharedPreferences settings;
      //  SharedPreferences.Editor editor;
//
//        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
  //      editor = settings.edit();
    //    editor.clear();
//
      //  editor.commit();


        List<News_Model> favorites;



        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);

            Gson gson = new Gson();
            News_Model[] favoriteItems = gson.fromJson(jsonFavorites, News_Model[].class);

            favorites = Arrays.asList(favoriteItems);

            favorites = new ArrayList<News_Model>(favorites);
            //Log.d("FAVORITES",Integer.toString(favoriteItems.s));
        } else
            return null;

        return (ArrayList<News_Model>) favorites;
    }
}