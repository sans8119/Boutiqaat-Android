package com.boutiqaat.android.boutiqaat.datasource.db.datamodel;

import java.io.Serializable;

/**
 * This represents one of the internal sub tags of json returned by instagram rest api.
 */
public class ImageModel implements Serializable, DataModel {
    public ImageData thumbnail;
    public String createdTime;

    @Override
    public String toString() {
        return "ImageModel{" +
                "thumbnail=" + thumbnail +

                '}';
    }

}
