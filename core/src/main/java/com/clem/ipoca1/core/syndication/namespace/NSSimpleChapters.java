package com.clem.ipoca1.core.syndication.namespace;

import android.util.Log;

import com.clem.ipoca1.core.feed.FeedItem;
import com.clem.ipoca1.core.feed.SimpleChapter;
import com.clem.ipoca1.core.syndication.handler.HandlerState;
import com.clem.ipoca1.core.util.DateUtils;

import org.xml.sax.Attributes;

import java.util.ArrayList;

public class NSSimpleChapters extends Namespace {
    private static final String TAG = "NSSimpleChapters";

    public static final String NSTAG = "psc|sc";
    public static final String NSURI = "http://podlove.org/simple-chapters";

    public static final String CHAPTERS = "chapters";
    public static final String CHAPTER = "chapter";
    public static final String START = "start";
    public static final String TITLE = "title";
    public static final String HREF = "href";

    @Override
    public SyndElement handleElementStart(String localName, HandlerState state,
                                          Attributes attributes) {
        FeedItem currentItem = state.getCurrentItem();
        if(currentItem != null) {
            if (localName.equals(CHAPTERS)) {
                currentItem.setChapters(new ArrayList<>());
            } else if (localName.equals(CHAPTER)) {
                try {
                    long start = DateUtils.parseTimeString(attributes.getValue(START));
                    String title = attributes.getValue(TITLE);
                    String link = attributes.getValue(HREF);
                    SimpleChapter chapter = new SimpleChapter(start, title, currentItem, link);
                    currentItem.getChapters().add(chapter);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Unable to read chapter", e);
                }
            }
        }
        return new SyndElement(localName, this);
    }

    @Override
    public void handleElementEnd(String localName, HandlerState state) {
    }

}
