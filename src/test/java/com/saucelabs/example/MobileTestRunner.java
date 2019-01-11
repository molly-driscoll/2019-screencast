package com.saucelabs.example;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.testng.TestNGCucumberRunner;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

@CucumberOptions(
        // Features
        features = "src/test/resources/features",
        // Glue
        glue = {"com/saucelabs/example/stepdefs"}, snippets = SnippetType.CAMELCASE,
        // Plugins
        plugin = {
                // Cucumber report location
                "json:target/cucumber-report/cucumber.json", "usage:target/cucumber-report/cucumber-usage.json",
                "html:target/cucumber-html-report",})
public class MobileTestRunner extends AbstractTestRunner
{
//    private TestNGCucumberRunner testNGCucumberRunner;

    @Parameters({"deviceName", "platform", "platformVersion"})
    @BeforeClass(alwaysRun = true)
    public void setUpMobileProfile(String deviceName, String platform, String platformVersion)
    {
        testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());

        TestPlatform.Builder builder = new TestPlatform.Builder();

        TestPlatform tp = builder.deviceName(deviceName).platformName(platform).platformVersion(
                platformVersion).build();
        Util.setTestPlatform(tp);
    }

//    @AfterClass(alwaysRun = true)
//    public void tearDownClass()
//    {
//        if (testNGCucumberRunner == null)
//        {
//            return;
//        }
//
//        testNGCucumberRunner.finish();
//
//    }
//
//    @Test(groups = "cucumber", description = "Runs Cucumber Scenarios", dataProvider = "scenarios")
//    public void feature(PickleEventWrapper pickleWrapper, CucumberFeatureWrapper cucumberFeatureWrapper)
//    throws Throwable
//    {
//        PickleEvent event = pickleWrapper.getPickleEvent();
//        Pickle pickle = event.pickle;
//        System.out.printf("Running scenario: %s\n", pickle.getName());
//
//        // the 'featureWrapper' parameter solely exists to display the feature file in a test report
//        testNGCucumberRunner.runScenario(event);
//    }
//
//    @DataProvider
//    public Object[][] scenarios()
//    {
//        if (testNGCucumberRunner == null)
//        {
//            return null;
//        }
//
//        return testNGCucumberRunner.provideScenarios();
//    }
}
