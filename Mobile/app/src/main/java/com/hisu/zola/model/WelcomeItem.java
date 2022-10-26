package com.hisu.zola.model;

public class WelcomeItem {

    private int featureImage;
    private String featureName;
    private String featureDesc;
    private boolean isGetStartedScreen;

    public WelcomeItem() {
    }

    public WelcomeItem(int featureImage, String featureName, String featureDesc, boolean isGetStartedScreen) {
        this.featureImage = featureImage;
        this.featureName = featureName;
        this.featureDesc = featureDesc;
        this.isGetStartedScreen = isGetStartedScreen;
    }

    public int getFeatureImage() {
        return featureImage;
    }

    public void setFeatureImage(int featureImage) {
        this.featureImage = featureImage;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getFeatureDesc() {
        return featureDesc;
    }

    public void setFeatureDesc(String featureDesc) {
        this.featureDesc = featureDesc;
    }

    public boolean isGetStartedScreen() {
        return isGetStartedScreen;
    }

    public void setGetStartedScreen(boolean getStartedScreen) {
        isGetStartedScreen = getStartedScreen;
    }
}