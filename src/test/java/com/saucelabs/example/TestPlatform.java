package com.saucelabs.example;

public class TestPlatform
{
    private Browser browser;
    private String browserVersion;
    private String platformName;
    private String platformVersion;
    private PlatformContainer platformContainer;
    private String deviceName;
    private DataCenter dataCenter;

    private TestPlatform()
    {
    }

    public static class Builder
    {
        private Browser browser;
        private String browserVersion;
        private String platformName;
        private String platformVersion;
        private PlatformContainer platformContainer;
        private String deviceName;
        private DataCenter dataCenter;

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

        public Builder dataCenter(DataCenter dataCenter)
        {
            this.dataCenter = dataCenter;
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

        public Builder platformContainer(PlatformContainer platformContainer)
        {
            this.platformContainer = platformContainer;
            return this;
        }

        public TestPlatform build()
        {
            TestPlatform tp = new TestPlatform();
            tp.browser = browser;
            tp.browserVersion = browserVersion;
            tp.platformName = platformName;
            tp.platformVersion = platformVersion;
            tp.platformContainer = platformContainer;
            tp.dataCenter = dataCenter;
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

    public PlatformContainer getPlatformContainer() { return platformContainer; }

    public DataCenter getDataCenter() { return dataCenter; }

    public String getDeviceName()
    {
        return deviceName;
    }
}
