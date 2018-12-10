package com.saucelabs.example;

import com.github.mkolisnyk.cucumber.runner.AfterSuite;
import com.github.mkolisnyk.cucumber.runner.BeforeSuite;
import com.github.mkolisnyk.cucumber.runner.ExtendedCucumberOptions;
import com.github.mkolisnyk.cucumber.runner.ReportRunner;
import com.github.mkolisnyk.cucumber.runner.runtime.ExtendedRuntimeOptions;
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@ExtendedCucumberOptions(
        // Location of the JSON report...
        jsonReport = "target/cucumber-report/cucumber.json",
        // Location of the json usage report...
        jsonUsageReport = "target/cucumber-report/cucumber-usage.json",
        // Location to write the reports...
        outputFolder = "target/extendedcucumber", retryCount = 3,
        // We want the usage report...
        usageReport = true,
        // We want the overview report...
        overviewReport = true,
        // We want the overview charts report...
        overviewChartsReport = true,
        // We want the detailed report...
        detailedReport = true,
        // We want the detailed aggregate report...
        detailedAggregatedReport = true,
        // We want the coverage report...
        coverageReport = true,
        // No PDF output...
        toPDF = false,
        // We want the breakdown report...
        breakdownReport = true,
        // We want the feature map report...
        featureMapReport = true, featureOverviewChart = true, knownErrorsReport = true, consolidatedReport = true, systemInfoReport = true, benchmarkReport = true
//        excludeCoverageTags = {"@flaky" },
//        includeCoverageTags = {"@passed" },
)
@CucumberOptions(features = "src/test/resources/features", glue = {"com/saucelabs/example/stepdefs"},
//        tags = {"@Regression1"},
//        tags = {"@SignOn"},
//          tags = {"@Orders"},
//        tags = {"@Orders,@SignOn"},
        snippets = SnippetType.CAMELCASE, plugin = {"json:target/cucumber-report/cucumber.json",
//            "rerun:target/cucumber-reports/rerun.txt",
        "usage:target/cucumber-report/cucumber-usage.json",
//            "pretty:target/pretty",
//            "progress:target/progress",
//            "timeline:target/timeline",
//            "usage:target/usage",
//            "testng:target/testng",
        "html:target/cucumber-html-report"
//            "com.saucelabs.cucumber.ExtentReportsFormatter:target/myextentreports"
})
public class TestRunner
{
    private TestNGCucumberRunner testNGCucumberRunner;
    private ExtendedRuntimeOptions[] extendedOptions;
    private Class<?> clazz;

    private void runPredefinedMethods(Class<?> annotation)
    throws Exception
    {
        Method[] methodList = this.clazz.getMethods();
        for (Method method : methodList)
        {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation item : annotations)
            {
                if (item.annotationType().equals(annotation))
                {
                    method.invoke(this);
                    break;
                }
            }
        }
    }

    @BeforeClass(alwaysRun = true)
    public void setUpClass()
    throws Exception
    {
        testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());

        this.clazz = this.getClass();
        try
        {
            extendedOptions = ExtendedRuntimeOptions.init(clazz);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        clazz = this.getClass();
        try
        {
            runPredefinedMethods(BeforeSuite.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass()
    throws Exception
    {
        if (testNGCucumberRunner == null)
        {
            return;
        }

        testNGCucumberRunner.finish();

        try
        {
            runPredefinedMethods(AfterSuite.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        for (ExtendedRuntimeOptions extendedOption : extendedOptions)
        {
            ReportRunner.run(extendedOption);
        }
    }

//    @BeforeClass(alwaysRun = true)
//    public void setUpClass()
//    throws Exception
//    {
//        testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
//    }

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
        if (testNGCucumberRunner == null)
        {
            return null;
        }

        return testNGCucumberRunner.provideScenarios();
    }

//    @AfterClass(alwaysRun = true)
//    public void tearDownClass()
//    throws Exception
//    {
//        if (testNGCucumberRunner == null)
//        {
//            return;
//        }
//
//        testNGCucumberRunner.finish();
//    }
}
