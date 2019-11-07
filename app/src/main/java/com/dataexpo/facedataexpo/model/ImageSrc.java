package com.dataexpo.facedataexpo.model;

public class ImageSrc {
    private int imageId;
    private int imageSrcId;
    private String uri;
    private String info;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getImageSrcId() {
        return imageSrcId;
    }

    public void setImageSrcId(int imageSrcId) {
        this.imageSrcId = imageSrcId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
