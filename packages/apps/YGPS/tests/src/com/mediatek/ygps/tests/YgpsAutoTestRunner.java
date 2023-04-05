
package com.mediatek.ygps.tests;

import junit.framework.TestSuite;
import android.test.InstrumentationTestRunner;

public class YgpsAutoTestRunner extends InstrumentationTestRunner {
    @Override
    public TestSuite getAllTests() {
        TestSuite tests = new TestSuite();
        tests.addTestSuite(YgpsAutoTest.class);
        return tests;
    }
}
