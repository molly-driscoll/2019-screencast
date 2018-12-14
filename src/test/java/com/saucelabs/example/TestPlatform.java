package com.saucelabs.example;

import com.saucelabs.Browser;

public class TestPlatform
{
    public Browser browser;
    public String version;
    public String platform;

    public TestPlatform(String browserString, String version, String platform)
    {
        this.browser = Browser.valueOf(browserString);
        this.version = version;
        this.platform = platform;
    }
}
