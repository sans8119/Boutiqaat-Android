package com.boutiqaat.android.boutiqaat.datasource.db.datamodel;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.databinding.PropertyChangeRegistry;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.boutiqaat.android.boutiqaat.BR;
import com.boutiqaat.android.boutiqaat.datasource.db.Converters;
import com.google.gson.annotations.SerializedName;

import timber.log.Timber;

/**
 * This the child of ServerData in the json returned by instagram resp api. This class also defines the table structure for
 * storing data obtained from cloud to the local database. Hence this enables us the offline stratergy.
 */

@Entity(tableName = "table_instagram_users_self_media_recent")
public class UsersRecentMediaModel implements android.databinding.Observable, DataModel {
    public String id;
    @TypeConverters(Converters.class)
    public ImageModel images;
    @NonNull
    @PrimaryKey
    @SerializedName("created_time")
    public String createdTime;

    @TypeConverters(Converters.class)
    public CaptionModel caption;

    @Ignore
    public transient Bitmap thumbNailImg;


    @Ignore
    public transient PropertyChangeRegistry registry = new PropertyChangeRegistry();


    public UsersRecentMediaModel() {
    }

    @BindingAdapter("thumbNailImg")
    public static void setImageBitmap(ImageView imageView, Bitmap bitmap) {
        Timber.i("@@@@@--------setImageBitmap------->" + bitmap + ", " + imageView);
        if (bitmap == null) {
            imageView.setImageDrawable(null);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        registry.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        registry.remove(callback);
    }

    @Bindable
    public Bitmap getThumbNailImg() {
        return thumbNailImg;
    }

    public void setThumbNailImg(Bitmap thumbNailImg) {
        this.thumbNailImg = thumbNailImg;
        registry.notifyChange(this, BR.thumbNailImg);
        Timber.i("Image bitmap obtained:" + thumbNailImg.getByteCount() + ";Creation time of image in server:" + createdTime);
    }

    @Override
    public String toString() {
        return "UsersRecentMediaModel{" +
                "id='" + id + '\'' +
                ", images=" + images +
                ", createdtime='" + createdTime + '\'' +
                ", thumbNailImg='" + thumbNailImg + '\'' +
                '}';
    }
}
