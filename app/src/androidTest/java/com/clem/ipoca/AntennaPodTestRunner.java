package com.clem.ipoca;

import android.test.InstrumentationTestRunner;
import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.TestSuite;

public class AntennaPodTestRunner extends InstrumentationTestRunner {

    @Override
    public TestSuite getAllTests() {
        return new TestSuiteBuilder(AntennaPodTestRunner.class)
                .includeAllPackagesUnderHere()
                .excludePackages("de.test.antennapod.gpodnet")
                .build();
    }

}
