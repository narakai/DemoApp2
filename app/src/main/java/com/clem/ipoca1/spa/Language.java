package com.clem.ipoca1.spa;

import com.clem.ipoca1.PodcastApp;

import java.util.Locale;

public class Language {

    public static final String ZH = "ZH";
    public static final String EN = "EN";

    public static String systemLanguage() {
        if (PodcastApp.getContext() != null) {
            Locale locale = PodcastApp.getContext().getResources().getConfiguration().locale;
            String language = locale.getLanguage();
            if (language.endsWith("zh")) {
                return ZH;
            }
            return EN;
        } else {
            return ZH;
        }
    }

    public static boolean isZH() {
        Locale locale = PodcastApp.getContext().getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return language.toLowerCase().endsWith("zh") && locale.getCountry().toLowerCase().endsWith("cn");
    }

}
