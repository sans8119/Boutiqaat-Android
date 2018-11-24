package com.boutiqaat.android.boutiqaat.datasource.db.datamodel;

/**
 * Instagram rest api returns url's of images. This information is captured in this class.
 */
public class ImageData implements DataModel {
    public String url;

    @Override
    public String toString() {
        return "ImageData{" +
                "url='" + url + '\'' +
                '}';
    }
}
