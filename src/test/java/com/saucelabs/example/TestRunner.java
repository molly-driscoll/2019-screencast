package com.saucelabs.example;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.testng.CucumberFeatureWrapper;
import cucumber.api.testng.PickleEventWrapper;
import cucumber.api.testng.TestNGCucumberRunner;
import gherkin.events.PickleEvent;
import gherkin.pickles.Pickle;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com/saucelabs/example/stepdefs"},
//        tags = {"@Regression1"},
        tags = {"@Orders"},
        snippets = SnippetType.CAMELCASE,
        plugin = {
            "json:target/cucumber-reports/cucumber.json",
            "rerun:target/cucumber-reports/rerun.txt",
            "usage:target/cucumber-reports/cucumber-usage.json"
        })
public class TestRunner
{
    private TestNGCucumberRunner testNGCucumberRunner;

    @BeforeClass(alwaysRun = true)
    public void setUpClass()
    throws Exception
    {
        testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
    }

    @Test(groups = "cucumber", description = "Runs Cucumber Scenarios", dataProvider = "scenarios")
    public void feature(PickleEventWrapper pickleWrapper, CucumberFeatureWrapper cucumberFeatureWrapper)
            throws Throwable
    {
        PickleEvent event = pickleWrapper.getPickleEvent();
        Pickle pickle = event.pickle;
        System.out.printf("Running scenario: %s\n", pickle.getName());

        // the 'featureWrapper' parameter solely exists to display the feature file in a test report
        testNGCucumberRunner.runScenario(event);
    }

    @DataProvider
    public Object[][] scenarios()
    {
        return testNGCucumberRunner.provideScenarios();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass()
    throws Exception
    {
        testNGCucumberRunner.finish();
    }
}
