package com.simbacode.pesapal.testsuits;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.simbacode.pesapal.testcases.PostRequestTest;

@RunWith(Suite.class)
@SuiteClasses({ PostRequestTest.class })
public class MobipaymentsTest {
}
