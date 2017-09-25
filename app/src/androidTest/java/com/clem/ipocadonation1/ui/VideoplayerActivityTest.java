package com.clem.ipocadonation1.ui;

import android.test.ActivityInstrumentationTestCase2;

import com.clem.ipocadonation1.activity.VideoplayerActivity;
import com.robotium.solo.Solo;

/**
 * Test class for VideoplayerActivity
 */
public class VideoplayerActivityTest extends ActivityInstrumentationTestCase2<VideoplayerActivity> {

    private Solo solo;

    public VideoplayerActivityTest() {
        super(VideoplayerActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
    }

    /**
     * Test if activity can be started.
     */
    public void testStartActivity() throws Exception {
        solo.waitForActivity(VideoplayerActivity.class);
    }
}
