package com.hisu.zola.model;

public class StarterSliderItem {
    private int coverImageID;
    private String feature;
    private String featureDesc;

    public StarterSliderItem() {
    }

    public StarterSliderItem(int coverImageID, String feature, String featureDesc) {
        this.coverImageID = coverImageID;
        this.feature = feature;
        this.featureDesc = featureDesc;
    }

    public int getCoverImageID() {
        return coverImageID;
    }

    public void setCoverImageID(int coverImageID) {
        this.coverImageID = coverImageID;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getFeatureDesc() {
        return featureDesc;
    }

    public void setFeatureDesc(String featureDesc) {
        this.featureDesc = featureDesc;
    }
}