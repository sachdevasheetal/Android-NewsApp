package com.example.pluralsight.ui.home;

import android.os.Parcel;
import android.os.Parcelable;

public class News_Model implements Parcelable {
    private String url;
    private String description;
    private String Title;
    private String image_url;
    private String section;
    private String time;
    private String select;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;


    protected News_Model(Parcel in) {
        url = in.readString();
        description = in.readString();
        Title = in.readString();
        image_url = in.readString();
        section = in.readString();
        time = in.readString();
        select = in.readString();
        id = in.readString();
    }

    public static final Creator<News_Model> CREATOR = new Creator<News_Model>() {
        @Override
        public News_Model createFromParcel(Parcel in) {
            return new News_Model(in);
        }

        @Override
        public News_Model[] newArray(int size) {
            return new News_Model[size];
        }
    };

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public News_Model(String url, String description, String title, String image_url, String section, String time, String select, String id) {
        this.url = url;
        this.description = description;
        this.Title = title;
        this.image_url = image_url;
        this.section = section;
        this.time = time;
        this.select = select;
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return Title;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getSection() {
        return section;
    }

    public String getTime() {
        return time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(url);
    dest.writeString(description);
    dest.writeString(Title);
    dest.writeString(image_url);
    dest.writeString(section);
    dest.writeString(time);
    dest.writeString(select);
    dest.writeString(id);
    }
}
