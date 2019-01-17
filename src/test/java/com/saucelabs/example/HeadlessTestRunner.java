package com.saucelabs.example;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.testng.TestNGCucumberRunner;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

// @formatter:off
@CucumberOptions(
    // Features
    features = "src/test/resources/features",

    // Glue
    glue = {"com/saucelabs/example/stepdefs"},
    snippets = SnippetType.CAMELCASE,

    // Plugins
    plugin = {
        // Cucumber report location
        "json:target/cucumber-report/cucumber.json",
        "usage:target/cucumber-report/cucumber-usage.json",
        "html:target/cucumber-html-report"
    }
)
// @formatter:on
public class HeadlessTestRunner extends AbstractTestRunner
{
    @Parameters({"browser", "version", "platform"})
    @BeforeClass(alwaysRun = false)
    public void setUpHeadlessProfile(@Optional String browser, String version, String platform)
    {
        testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());

        TestPlatform.Builder builder = new TestPlatform.Builder();

        // @formatter:off
        TestPlatform tp = builder
                .browser(Browser.valueOf(browser))
                .browserVersion(version)
                .platformName(platform)
                .platformContainer(PlatformContainer.HEADLESS)
                .build();
        // @formatter:on

        Util.setTestPlatform(tp);
    }
}
