package com.saucelabs.example;

import com.saucelabs.Browser;

public class TestPlatform
{
    private Browser browser;
    private String browserVersion;
    private String platformName;
    private String platformVersion;
    private String deviceName;

    private TestPlatform()
    {
    }

    public static class Builder
    {
        private Browser browser;
        private String browserVersion;
        private String platformName;
        private String platformVersion;
        private String deviceName;

        public Builder()
        {

        }

        public Builder browser(Browser browser)
        {
            this.browser = browser;
            return this;
        }

        public Builder browserVersion(String browserVersion)
        {
            this.browserVersion = browserVersion;
            return this;
        }

        public Builder deviceName(String deviceName)
        {
            this.deviceName = deviceName;
            return this;
        }

        public Builder platformName(String platformName)
        {
            this.platformName = platformName;
            return this;
        }

        public Builder platformVersion(String platformVersion)
        {
            this.platformVersion = platformVersion;
            return this;
        }

        public TestPlatform build()
        {
            TestPlatform tp = new TestPlatform();
            tp.browser = browser;
            tp.browserVersion = browserVersion;
            tp.platformName = platformName;
            tp.platformVersion = platformVersion;
            tp.deviceName = deviceName;
            return tp;
        }
    }

    public Browser getBrowser()
    {
        return browser;
    }

    public String getBrowserVersion()
    {
        return browserVersion;
    }

    public String getPlatformName()
    {
        return platformName;
    }

    public String getPlatformVersion()
    {
        return platformVersion;
    }

    public String getDeviceName()
    {
        return deviceName;
    }
}
