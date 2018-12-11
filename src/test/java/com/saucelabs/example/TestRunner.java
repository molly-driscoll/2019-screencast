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

//@ExtendedCucumberOptions(
//        customTemplatesPath = "/Users/bmeyer/github/billmeyer/saucedemo-java-cucumber/src/test/resources/templates/templates.json",
//        // Location of the JSON report...
//        jsonReport = "target/cucumber-report/cucumber.json",
//        // Location of the json usage report...
//        jsonUsageReport = "target/cucumber-report/cucumber-usage.json.save",
//        // Location to write the reports...
//        outputFolder = "target/extendedcucumber", retryCount = 3,
//        // We want the usage report...
//        usageReport = true,
//        // We want the overview report...
//        overviewReport = true,
//        // We want the overview charts report...
//        overviewChartsReport = true,
//        // We want the detailed report...
//        detailedReport = true,
//        // We want the detailed aggregate report...
//        detailedAggregatedReport = true,
//        // We want the coverage report...
//        coverageReport = true,
//        // No PDF output...
//        toPDF = false,
//        // We want the breakdown report...
//        breakdownReport = true,
//        // We want the feature map report...
//        featureMapReport = true,
//        //
//        featureOverviewChart = true,
//        //
//        knownErrorsReport = false,
//        //
//        consolidatedReport = true,
//        //
//        systemInfoReport = true,
//        //
//        benchmarkReport = true
////        excludeCoverageTags = {"@flaky" },
////        includeCoverageTags = {"@passed" },
//)
@CucumberOptions(
        // Features
        features = "src/test/resources/features",
        // Glue
        glue = {"com/saucelabs/example/stepdefs"},
//        tags = {"@Regression1"},
//        tags = {"@SignOn"},
//          tags = {"@Orders"},
//        tags = {"@Orders,@SignOn"},
        snippets = SnippetType.CAMELCASE,
        // Plugins
        plugin = {
                // Cucumber report location
                "json:target/cucumber-report/cucumber.json",
//            "rerun:target/cucumber-reports/rerun.txt",
                "usage:target/cucumber-report/cucumber-usage.json",
//            "pretty:target/pretty",
//            "progress:target/progress",
//            "timeline:target/timeline",
//            "testng:target/testng",
                "html:target/cucumber-html-report",
//            "com.saucelabs.cucumber.ExtentReportsFormatter:target/my-extent-reports"
//                "com.vimalselvam.cucumber.listener.ExtentCucumberFormatter:target/extent-reports"
}
)
public class TestRunner
{
    private TestNGCucumberRunner testNGCucumberRunner;
//    private ExtendedRuntimeOptions[] extendedOptions;
//    private Class<?> clazz;

//    private void runPredefinedMethods(Class<?> annotation)
//    throws Exception
//    {
//        Method[] methodList = this.clazz.getMethods();
//        for (Method method : methodList)
//        {
//            Annotation[] annotations = method.getAnnotations();
//            for (Annotation item : annotations)
//            {
//                if (item.annotationType().equals(annotation))
//                {
//                    method.invoke(this);
//                    break;
//                }
//            }
//        }
//    }

    @BeforeClass(alwaysRun = true)
    public void setUpClass()
    throws Exception
    {
        testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());

//        this.clazz = this.getClass();
//        try
//        {
//            extendedOptions = ExtendedRuntimeOptions.init(clazz);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        clazz = this.getClass();
//        try
//        {
//            runPredefinedMethods(BeforeSuite.class);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
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

//        try
//        {
//            runPredefinedMethods(AfterSuite.class);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        for (ExtendedRuntimeOptions extendedOption : extendedOptions)
//        {
//            ReportRunner.run(extendedOption);
//        }
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
//
//    @Test
//    public void testCustomFormatTemplate()
//    throws Exception
//    {
//        ExtendedCucumber runner = new ExtendedCucumber(this.getClass());
//        RunNotifier notifier = new RunNotifier();
//        runner.run(notifier);
//    }
//
//    @Test
//    public void testCustomFormatTemplateTestNGClass()
//    throws Exception
//    {
//        TestListenerAdapter tla = new TestListenerAdapter();
//        TestNG testng = new TestNG();
//        testng.setTestClasses(new Class[]{TestRunner.class});
//        testng.addListener(tla);
////        testng.run();
//
//        try
//        {
//            runPredefinedMethods(AfterSuite.class);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        for (ExtendedRuntimeOptions extendedOption : extendedOptions)
//        {
//            ReportRunner.run(extendedOption);
//        }
//    }
}
