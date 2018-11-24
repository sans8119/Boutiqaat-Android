package com.boutiqaat.android.boutiqaat.datasource.db;

import android.arch.persistence.room.TypeConverter;

import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.CaptionModel;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.ImageData;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.ImageModel;

/**
 * Used by Room to store ImageModel data in the table and to retrieve ImageModel data.
 */

public final class Converters {

    @TypeConverter
    public static String fromFields(ImageModel images) {
        return images.thumbnail.url;
    }

    @TypeConverter
    public static ImageModel tooField(String fieldsStr) {
        ImageModel images = new ImageModel();
        images.thumbnail = new ImageData();
        images.thumbnail.url = fieldsStr;
        return images;
    }

    @TypeConverter
    public static String fromFields(CaptionModel captionModel) {
        return captionModel.text;
    }

    @TypeConverter
    public static CaptionModel tooFieldCaption(String fieldsStr) {
        CaptionModel captionModel = new CaptionModel();
        captionModel.text = fieldsStr;
        return captionModel;
    }
}
