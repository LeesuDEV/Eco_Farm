package com.example.proto;

public class BrightnessConverter {
    static String brightnessConvert(String brightness) {
        double doubleBrightness = Double.parseDouble(brightness);
        return String.valueOf ((int)(doubleBrightness * 100));
    }

    static String brightnessDeConvert(String brightness) {
        double doubleBrightness = Double.parseDouble(brightness);
        return String.valueOf(doubleBrightness / 100);
    }
}
