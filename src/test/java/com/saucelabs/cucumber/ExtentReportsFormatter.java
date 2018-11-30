package com.saucelabs.cucumber;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.gherkin.model.And;
import com.aventstack.extentreports.gherkin.model.Given;
import com.aventstack.extentreports.gherkin.model.Then;
import com.aventstack.extentreports.gherkin.model.When;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import cucumber.api.HookTestStep;
import cucumber.api.PickleStepTestStep;
import cucumber.api.Result;
import cucumber.api.TestCase;
import cucumber.api.event.EmbedEvent;
import cucumber.api.event.EventHandler;
import cucumber.api.event.EventListener;
import cucumber.api.event.EventPublisher;
import cucumber.api.event.TestCaseFinished;
import cucumber.api.event.TestCaseStarted;
import cucumber.api.event.TestRunFinished;
import cucumber.api.event.TestRunStarted;
import cucumber.api.event.TestSourceRead;
import cucumber.api.event.TestStepFinished;
import cucumber.api.event.TestStepStarted;
import cucumber.api.event.WriteEvent;
import cucumber.api.formatter.NiceAppendable;
import cucumber.runtime.CucumberException;
import cucumber.runtime.io.URLOutputStream;
import gherkin.ast.Background;
import gherkin.ast.DataTable;
import gherkin.ast.DocString;
import gherkin.ast.Examples;
import gherkin.ast.Feature;
import gherkin.ast.Node;
import gherkin.ast.ScenarioDefinition;
import gherkin.ast.ScenarioOutline;
import gherkin.ast.Step;
import gherkin.ast.TableCell;
import gherkin.ast.TableRow;
import gherkin.ast.Tag;
import gherkin.deps.com.google.gson.Gson;
import gherkin.deps.com.google.gson.GsonBuilder;
import gherkin.pickles.Argument;
import gherkin.pickles.PickleCell;
import gherkin.pickles.PickleRow;
import gherkin.pickles.PickleString;
import gherkin.pickles.PickleTable;
import gherkin.pickles.PickleTag;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtentReportsFormatter implements EventListener
{
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String JS_FORMATTER_VAR = "formatter";
    private static final String JS_REPORT_FILENAME = "report.js";
//    private static final String[] TEXT_ASSETS = new String[]{"/cucumber/formatter/formatter.js", "/cucumber/formatter/index.html", "/cucumber/formatter/jquery-1.8.2.min.js", "/cucumber/formatter/style.css"};
//    private static final Map<String, String> MIME_TYPES_EXTENSIONS = new HashMap<String, String>()
//    {
//        {
//            put("image/bmp", "bmp");
//            put("image/gif", "gif");
//            put("image/jpeg", "jpg");
//            put("image/png", "png");
//            put("image/svg+xml", "svg");
//            put("video/ogg", "ogg");
//        }
//    };

    private NiceAppendable jsOut;

    private final TestSourcesModel testSources = new TestSourcesModel();

    private boolean firstFeature = true;
    private String currentFeatureFile;
    private Map<String, Object> currentTestCaseMap;
    private Map<String, Object> currentFeatureMap;
    private Map<String, Object> currentScenarioOutlineMap;
    private Map<String, Object> currentExamplesMap;
    private Map<String, Object> currentBackgroundMap;
    private Map<String, Object> currentTestStepMap;
    private Map<String, Object> currentMatchMap;
    private Map<String, Object> currentResultMap;
    private Map<String, Object> currentStepMap;
    private Map<String, Object> currentScenarioMap;
    private ScenarioOutline currentScenarioOutline;
    private Examples currentExamples;
    private Calendar testStartTime;
    private long currentTestTime;
    private int embeddedIndex;

    private URL htmlReportDir;

    // ExtentReports
    ExtentReports extentReports;
    ExtentHtmlReporter extentHtmlReporter;
    ExtentTest etFeature;
    ExtentTest etScenarioOutline;
    ExtentTest etCurrentStep;

    private EventHandler<TestRunStarted> testRunStartedHandler = event -> {
        System.out.printf(">>> TestRunStarted Event\n");

        testStartTime = Calendar.getInstance();
        currentTestTime = testStartTime.getTimeInMillis();
    };

    private EventHandler<TestSourceRead> testSourceReadHandler = event -> {
        System.out.printf(">>> TestSourceRead Event, uri: %s\n", event.uri);
        testSources.addTestSourceReadEvent(event.uri, event);
    };

    private EventHandler<TestCaseStarted> testCaseStartedHandler = event -> {
        System.out.printf(">>> TestCaseStarted Event, test case: %s\n", event.testCase.getName());

        if (firstFeature)
        {
            jsOut.append("$(document).ready(function() {").append("var ").append(JS_FORMATTER_VAR).append(" = new CucumberHTML.DOMFormatter($('.cucumber-report'));");
            firstFeature = false;
        }

        doStartOfFeature(event.testCase);
        doScenarioOutline(event.testCase);
        currentTestCaseMap = createTestCase(event.testCase);

        if (testSources.hasBackground(currentFeatureFile, event.testCase.getLine()))
        {
            currentBackgroundMap = createBackground(event.testCase);
            jsFunctionCall("background", currentBackgroundMap);
        }
        else
        {
            currentScenarioMap = currentTestCaseMap;
            jsFunctionCall("scenario", currentScenarioMap);
            currentTestCaseMap = null;
        }

    };

    private EventHandler<TestStepStarted> testStepStartedHandler = event -> {
        System.out.printf(">>> TestStepStarted Event\n");

        if (event.testStep instanceof PickleStepTestStep)
        {
            PickleStepTestStep testStep = (PickleStepTestStep) event.testStep;
            if (isFirstStepAfterBackground(testStep))
            {
                currentScenarioMap = currentTestCaseMap;
                jsFunctionCall("scenario", currentScenarioMap);
                currentTestCaseMap = null;
            }

            currentStepMap = createTestStep(testStep);
            jsFunctionCall("step", currentStepMap);

            currentMatchMap = createMatchMap((PickleStepTestStep) event.testStep);
            jsFunctionCall("match", currentMatchMap);
        }
    };

    private EventHandler<TestStepFinished> testStepFinishedHandler = event -> {
        System.out.printf(">>> TestStepFinished Event\n");

        if (event.testStep instanceof PickleStepTestStep)
        {
            currentResultMap = createResultMap(event.result);
            jsFunctionCall("result", currentResultMap);

            int duration = (int)((Long)currentResultMap.get("duration") / 1E6);
            Date startTime = new Date(currentTestTime);
            Date endTime = new Date(currentTestTime + duration);

            currentTestTime += duration;

            etCurrentStep.getModel().setStartTime(startTime);
            etCurrentStep.getModel().setEndTime(endTime);

            switch((String)currentResultMap.get("status"))
            {
                case "passed":
                    etCurrentStep.pass("passed");
                    break;

                case "failed":
                    etCurrentStep.fail("failed");
                    break;
            }
        }
        else if (event.testStep instanceof HookTestStep)
        {
            HookTestStep hookTestStep = (HookTestStep) event.testStep;
            currentResultMap = createResultMap(event.result);
            jsFunctionCall(hookTestStep.getHookType().toString(), currentResultMap);
        }
        else
        {
            throw new IllegalStateException();
        }
    };

    private EventHandler<EmbedEvent> embedEventHandler = event -> {
        System.out.printf(">>> EmbedEvent Event\n");

//        String mimeType = event.mimeType;
//        if (mimeType.startsWith("text/"))
//        {
//            // just pass straight to the formatter to output in the html
//            jsFunctionCall("embedding", mimeType, new String(event.data));
//        }
//        else
//        {
//            // Creating a file instead of using data urls to not clutter the js file
//            String extension = MIME_TYPES_EXTENSIONS.get(mimeType);
//            if (extension != null)
//            {
//                StringBuilder fileName = new StringBuilder("embedded").append(embeddedIndex++).append(".").append(extension);
//                writeBytesToURL(event.data, toUrl(fileName.toString()));
//                jsFunctionCall("embedding", mimeType, fileName);
//            }
//        }
    };

    private EventHandler<WriteEvent> writeEventhandler = event -> {
        System.out.printf(">>> WriteEvent Event\n");
        jsFunctionCall("write", event.text);
    };

    private EventHandler<TestCaseFinished> testCaseFinishedHandler = event -> {
        System.out.printf(">>> TestCaseFinished Event\n");

//        etFeature = extentReports.createTest(com.aventstack.extentreports.gherkin.model.Feature.class, (String) currentTestCaseMap.get("name"), (String) currentTestCaseMap.get("description"));

//        Result result = event.result;
//        if(result.getStatus() == Result.Type.PASSED)
//            etFeature.pass("pass");
//        else
//            etFeature.fail("fail");
    };

    private EventHandler<TestRunFinished> testRunFinishedHandler = event -> {
        System.out.printf(">>> TestRunFinished Event\n");

        extentReports.flush();
        if (!firstFeature)
        {
            jsOut.append("});");
//            copyReportFiles();
        }
        jsOut.close();
    };

    public ExtentReportsFormatter(URL htmlReportDir)
    {
        this(htmlReportDir, createJsOut(htmlReportDir));
    }

    public ExtentReportsFormatter(URL htmlReportDir, NiceAppendable jsOut)
    {
        this.htmlReportDir = htmlReportDir;
        this.jsOut = jsOut;

        extentReports = new ExtentReports();

//        OutputStream os = htmlReportDir.openStream();
        File outputDir = new File("target/extentreports");
        if (outputDir.exists() == false)
        {
            outputDir.mkdir();
        }

        extentHtmlReporter = new ExtentHtmlReporter("target/extentreports/extentReports.html");
        extentHtmlReporter.config().setTheme(Theme.STANDARD);

        extentReports.attachReporter(extentHtmlReporter);
        extentReports.setSystemInfo("HostName", "AventStack-PC");

//        extentReports.createTest("MyFirstTest").pass("details");
//        extentReports.createTest("MySecondTest", "Some Test Description").pass("details");
//
//        // feature
//        ExtentTest feature = extentReports.createTest(com.aventstack.extentreports.gherkin.model.Feature.class, "Refund item");
//
//        // scenario
//        ExtentTest scenario = feature.createNode(Scenario.class, "Jeff returns a faulty microwave");
//        scenario.createNode(Given.class, "Jeff has bought a microwave for $100").pass("pass");
//        scenario.createNode(And.class, "he has a receipt").pass("pass");
//        scenario.createNode(When.class, "he returns the microwave").pass("pass");
//        scenario.createNode(Then.class, "Jeff should be refunded $100").fail("fail");
//        extentReports.flush();
    }

    /**
     * Set the event publisher. The plugin can register event listeners with the publisher.
     *
     * @param publisher the event publisher
     */
    @Override
    public void setEventPublisher(EventPublisher publisher)
    {
        // The first event sent
        publisher.registerHandlerFor(TestRunStarted.class, testRunStartedHandler);

        // Sent for each feature file read, contains the feature file source
        publisher.registerHandlerFor(TestSourceRead.class, testSourceReadHandler);

        // Sent before starting the execution of a Test Case(/Pickle/Scenario), contains the Test Case
        publisher.registerHandlerFor(TestCaseStarted.class, testCaseStartedHandler);

        // Sent before starting the execution of a Test Step, contains the Test Step
        publisher.registerHandlerFor(TestStepStarted.class, testStepStartedHandler);

        // Sent after the execution of a Test Step, contains the Test Step and its Result
        publisher.registerHandlerFor(TestStepFinished.class, testStepFinishedHandler);

        //  TestCaseFinished} - sent after the execution of a Test Case(/Pickle/Scenario), contains the Test Case and its Result.
        publisher.registerHandlerFor(TestCaseFinished.class, testCaseFinishedHandler);

        // Calling scenario.embed in a hook triggers this event
        publisher.registerHandlerFor(EmbedEvent.class, embedEventHandler);

        // Calling scenario.write in a hook triggers this event
        publisher.registerHandlerFor(WriteEvent.class, writeEventhandler);

        // The last event sent
        publisher.registerHandlerFor(TestRunFinished.class, testRunFinishedHandler);
    }

    private void doStartOfFeature(TestCase testCase)
    {
        System.out.printf(">>> doStartOfFeature\n");

        if (currentFeatureFile == null || !currentFeatureFile.equals(testCase.getUri()))
        {
            currentFeatureFile = testCase.getUri();
            jsFunctionCall("uri", currentFeatureFile);
            currentFeatureMap = createFeature(testCase);
            jsFunctionCall("feature", currentFeatureMap);
            etFeature = extentReports.createTest(com.aventstack.extentreports.gherkin.model.Feature.class,
                    (String) currentFeatureMap.get("name"), (String) currentFeatureMap.get("description"));

        }
    }

    private void doScenarioOutline(TestCase testCase)
    {
        System.out.printf(">>> doScenarioOutline\n");

        com.saucelabs.cucumber.TestSourcesModel.AstNode astNode = testSources.getAstNode(currentFeatureFile, testCase.getLine());
        if (com.saucelabs.cucumber.TestSourcesModel.isScenarioOutlineScenario(astNode))
        {
            ScenarioOutline scenarioOutline = (ScenarioOutline) com.saucelabs.cucumber.TestSourcesModel.getScenarioDefinition(astNode);
            if (currentScenarioOutline == null || !currentScenarioOutline.equals(scenarioOutline))
            {
                currentScenarioOutline = scenarioOutline;
                currentScenarioOutlineMap = createScenarioOutline(currentScenarioOutline);
                jsFunctionCall("scenarioOutline", currentScenarioOutlineMap);
                etScenarioOutline = etFeature.createNode((String)currentScenarioOutlineMap.get("name"));

                addOutlineStepsToReport(scenarioOutline);
            }

            Examples examples = (Examples) astNode.parent.node;
            if (currentExamples == null || !currentExamples.equals(examples))
            {
                currentExamples = examples;
                currentExamplesMap = createExamples(currentExamples);
                jsFunctionCall("examples", currentExamplesMap);
            }
        }
        else
        {
            currentScenarioOutline = null;
            currentExamples = null;
        }
    }

    private Map<String, Object> createFeature(TestCase testCase)
    {
        System.out.printf(">>> createFeature\n");

        Map<String, Object> featureMap = new HashMap<String, Object>();
        Feature feature = testSources.getFeature(testCase.getUri());
        if (feature != null)
        {
            featureMap.put("keyword", feature.getKeyword());
            featureMap.put("name", feature.getName());
            featureMap.put("description", feature.getDescription() != null ? feature.getDescription() : "");
            if (!feature.getTags().isEmpty())
            {
                featureMap.put("tags", createTagList(feature.getTags()));
            }
        }
        return featureMap;
    }

    private List<Map<String, Object>> createTagList(List<Tag> tags)
    {
        System.out.printf(">>> createTagList\n");

        List<Map<String, Object>> tagList = new ArrayList<Map<String, Object>>();
        for (Tag tag : tags)
        {
            Map<String, Object> tagMap = new HashMap<String, Object>();
            tagMap.put("name", tag.getName());
            tagList.add(tagMap);
        }
        return tagList;
    }

    private Map<String, Object> createScenarioOutline(ScenarioOutline scenarioOutline)
    {
        System.out.printf(">>> createScenarioOutline\n");

        Map<String, Object> scenarioOutlineMap = new HashMap<String, Object>();
        scenarioOutlineMap.put("name", scenarioOutline.getName());
        scenarioOutlineMap.put("keyword", scenarioOutline.getKeyword());
        scenarioOutlineMap.put("description", scenarioOutline.getDescription() != null ? scenarioOutline.getDescription() : "");
        if (!scenarioOutline.getTags().isEmpty())
        {
            scenarioOutlineMap.put("tags", createTagList(scenarioOutline.getTags()));
        }
        return scenarioOutlineMap;
    }

    private void addOutlineStepsToReport(ScenarioOutline scenarioOutline)
    {
        System.out.printf(">>> addOutlineStepsToReport\n");

        for (Step step : scenarioOutline.getSteps())
        {
            Map<String, Object> stepMap = new HashMap<String, Object>();
            stepMap.put("name", step.getText());
            stepMap.put("keyword", step.getKeyword().trim());

            if (step.getArgument() != null)
            {
                Node argument = step.getArgument();
                if (argument instanceof DocString)
                {
                    stepMap.put("doc_string", createDocStringMap((DocString) argument));
                }
                else if (argument instanceof DataTable)
                {
                    stepMap.put("rows", createDataTableList((DataTable) argument));
                }
            }
            currentStepMap = stepMap;
            jsFunctionCall("step", currentStepMap);

            switch((String)stepMap.get("keyword"))
            {
                case "Given":
                    etCurrentStep = etScenarioOutline.createNode(Given.class, (String)stepMap.get("name"));
                    break;

                case "When":
                    etCurrentStep = etScenarioOutline.createNode(When.class, (String)stepMap.get("name"));
                    break;

                case "And":
                    etCurrentStep = etScenarioOutline.createNode(And.class, (String)stepMap.get("name"));
                    break;

                case "Then":
                    etCurrentStep = etScenarioOutline.createNode(Then.class, (String)stepMap.get("name"));
                    break;
            }
        }
    }

    private Map<String, Object> createDocStringMap(DocString docString)
    {
        System.out.printf(">>> createDocStringMap\n");

        Map<String, Object> docStringMap = new HashMap<String, Object>();
        docStringMap.put("value", docString.getContent());
        return docStringMap;
    }

    private List<Map<String, Object>> createDataTableList(DataTable dataTable)
    {
        System.out.printf(">>> createDataTableList\n");

        List<Map<String, Object>> rowList = new ArrayList<Map<String, Object>>();
        for (TableRow row : dataTable.getRows())
        {
            rowList.add(createRowMap(row));
        }
        return rowList;
    }

    private Map<String, Object> createRowMap(TableRow row)
    {
        System.out.printf(">>> createRowMap\n");

        Map<String, Object> rowMap = new HashMap<String, Object>();
        rowMap.put("cells", createCellList(row));
        return rowMap;
    }

    private List<String> createCellList(TableRow row)
    {
        System.out.printf(">>> createCellList\n");

        List<String> cells = new ArrayList<String>();
        for (TableCell cell : row.getCells())
        {
            cells.add(cell.getValue());
        }
        return cells;
    }

    private Map<String, Object> createExamples(Examples examples)
    {
        System.out.printf(">>> createExamples\n");

        Map<String, Object> examplesMap = new HashMap<String, Object>();
        examplesMap.put("name", examples.getName());
        examplesMap.put("keyword", examples.getKeyword());
        examplesMap.put("description", examples.getDescription() != null ? examples.getDescription() : "");
        List<Map<String, Object>> rowList = new ArrayList<Map<String, Object>>();
        rowList.add(createRowMap(examples.getTableHeader()));
        for (TableRow row : examples.getTableBody())
        {
            rowList.add(createRowMap(row));
        }
        examplesMap.put("rows", rowList);
        if (!examples.getTags().isEmpty())
        {
            examplesMap.put("tags", createTagList(examples.getTags()));
        }
        return examplesMap;
    }

    private Map<String, Object> createTestCase(TestCase testCase)
    {
        System.out.printf(">>> createTestCase\n");

        Map<String, Object> testCaseMap = new HashMap<String, Object>();
        testCaseMap.put("name", testCase.getName());

        com.saucelabs.cucumber.TestSourcesModel.AstNode astNode = testSources.getAstNode(currentFeatureFile, testCase.getLine());
        if (astNode != null)
        {
            ScenarioDefinition scenarioDefinition = com.saucelabs.cucumber.TestSourcesModel.getScenarioDefinition(astNode);
            testCaseMap.put("keyword", scenarioDefinition.getKeyword());
            testCaseMap.put("description", scenarioDefinition.getDescription() != null ? scenarioDefinition.getDescription() : "");
        }
        if (!testCase.getTags().isEmpty())
        {
            List<Map<String, Object>> tagList = new ArrayList<Map<String, Object>>();
            for (PickleTag tag : testCase.getTags())
            {
                Map<String, Object> tagMap = new HashMap<String, Object>();
                tagMap.put("name", tag.getName());
                tagList.add(tagMap);
            }
            testCaseMap.put("tags", tagList);
        }

        return testCaseMap;
    }

    private Map<String, Object> createBackground(TestCase testCase)
    {
        System.out.printf(">>> createBackground\n");

        com.saucelabs.cucumber.TestSourcesModel.AstNode astNode = testSources.getAstNode(currentFeatureFile, testCase.getLine());
        if (astNode != null)
        {
            Background background = com.saucelabs.cucumber.TestSourcesModel.getBackgroundForTestCase(astNode);
            Map<String, Object> testCaseMap = new HashMap<String, Object>();
            testCaseMap.put("name", background.getName());
            testCaseMap.put("keyword", background.getKeyword());
            testCaseMap.put("description", background.getDescription() != null ? background.getDescription() : "");
            return testCaseMap;
        }
        return null;
    }

    private boolean isFirstStepAfterBackground(PickleStepTestStep testStep)
    {
        System.out.printf(">>> isFirstStepAfterBackground\n");

        com.saucelabs.cucumber.TestSourcesModel.AstNode astNode = testSources.getAstNode(currentFeatureFile, testStep.getStepLine());
        if (astNode != null)
        {
            if (currentTestCaseMap != null && !com.saucelabs.cucumber.TestSourcesModel.isBackgroundStep(astNode))
            {
                return true;
            }
        }
        return false;
    }

    private Map<String, Object> createTestStep(PickleStepTestStep testStep)
    {
        System.out.printf(">>> createTestStep\n");

        Map<String, Object> stepMap = new HashMap<String, Object>();
        stepMap.put("name", testStep.getStepText());
        if (!testStep.getStepArgument().isEmpty())
        {
            Argument argument = testStep.getStepArgument().get(0);
            if (argument instanceof PickleString)
            {
                stepMap.put("doc_string", createDocStringMap((PickleString) argument));
            }
            else if (argument instanceof PickleTable)
            {
                stepMap.put("rows", createDataTableList((PickleTable) argument));
            }
        }
        com.saucelabs.cucumber.TestSourcesModel.AstNode astNode = testSources.getAstNode(currentFeatureFile, testStep.getStepLine());
//        if (astNode != null)
//        {
        Step step = (Step) astNode.node;
        stepMap.put("keyword", step.getKeyword().trim());
//        }

        return stepMap;
    }

    private Map<String, Object> createDocStringMap(PickleString docString)
    {
        System.out.printf(">>> createDocStringMap\n");

        Map<String, Object> docStringMap = new HashMap<String, Object>();
        docStringMap.put("value", docString.getContent());
        return docStringMap;
    }

    private List<Map<String, Object>> createDataTableList(PickleTable dataTable)
    {
        System.out.printf(">>> createDataTableList\n");

        List<Map<String, Object>> rowList = new ArrayList<Map<String, Object>>();
        for (PickleRow row : dataTable.getRows())
        {
            rowList.add(createRowMap(row));
        }
        return rowList;
    }

    private Map<String, Object> createRowMap(PickleRow row)
    {
        System.out.printf(">>> createRowMap\n");

        Map<String, Object> rowMap = new HashMap<String, Object>();
        rowMap.put("cells", createCellList(row));
        return rowMap;
    }

    private List<String> createCellList(PickleRow row)
    {
        System.out.printf(">>> createCellList\n");

        List<String> cells = new ArrayList<String>();
        for (PickleCell cell : row.getCells())
        {
            cells.add(cell.getValue());
        }
        return cells;
    }

    private Map<String, Object> createMatchMap(PickleStepTestStep testStep)
    {
        System.out.printf(">>> createMatchMap\n");

        Map<String, Object> matchMap = new HashMap<String, Object>();
        String location = testStep.getCodeLocation();
        if (location != null)
        {
            matchMap.put("location", location);
        }
        return matchMap;
    }

    private Map<String, Object> createResultMap(Result result)
    {
        System.out.printf(">>> createResultMap\n");

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("status", result.getStatus().lowerCaseName());
        resultMap.put("duration", result.getDuration());

        if (result.getErrorMessage() != null)
        {
            resultMap.put("error_message", result.getErrorMessage());
        }
        return resultMap;
    }

    private void jsFunctionCall(String functionName, Object... args)
    {
        NiceAppendable out = jsOut.append(JS_FORMATTER_VAR + ".").append(functionName).append("(");
        boolean comma = false;
        for (Object arg : args)
        {
            if (comma)
            {
                out.append(", ");
            }
            String stringArg = gson.toJson(arg);
            out.append(stringArg);
            comma = true;
        }
        out.append(");").println();
    }

//    private void copyReportFiles()
//    {
//        if (htmlReportDir == null)
//        {
//            return;
//        }
//        for (String textAsset : TEXT_ASSETS)
//        {
//            InputStream textAssetStream = getClass().getResourceAsStream(textAsset);
//            if (textAssetStream == null)
//            {
//                throw new CucumberException("Couldn't find " + textAsset + ". Is cucumber-html on your classpath? Make sure you have the right version.");
//            }
//            String fileName = new File(textAsset).getName();
//            writeStreamToURL(textAssetStream, toUrl(fileName));
//        }
//    }
//
//    private URL toUrl(String fileName)
//    {
//        try
//        {
//            return new URL(htmlReportDir, fileName);
//        }
//        catch (IOException e)
//        {
//            throw new CucumberException(e);
//        }
//    }
//
//    private static void writeStreamToURL(InputStream in, URL url)
//    {
//        OutputStream out = createReportFileOutputStream(url);
//
//        byte[] buffer = new byte[16 * 1024];
//        try
//        {
//            int len = in.read(buffer);
//            while (len != -1)
//            {
//                out.write(buffer, 0, len);
//                len = in.read(buffer);
//            }
//        }
//        catch (IOException e)
//        {
//            throw new CucumberException("Unable to write to report file item: ", e);
//        }
//        finally
//        {
//            closeQuietly(out);
//        }
//    }
//
//    private static void writeBytesToURL(byte[] buf, URL url)
//    throws CucumberException
//    {
//        OutputStream out = createReportFileOutputStream(url);
//        try
//        {
//            out.write(buf);
//        }
//        catch (IOException e)
//        {
//            throw new CucumberException("Unable to write to report file item: ", e);
//        }
//        finally
//        {
//            closeQuietly(out);
//        }
//    }

    private static NiceAppendable createJsOut(URL htmlReportDir)
    {
        try
        {
            return new NiceAppendable(new OutputStreamWriter(createReportFileOutputStream(new URL(htmlReportDir, JS_REPORT_FILENAME)), "UTF-8"));
        }
        catch (IOException e)
        {
            throw new CucumberException(e);
        }
    }

    private static OutputStream createReportFileOutputStream(URL url)
    {
        try
        {
            return new URLOutputStream(url);
        }
        catch (IOException e)
        {
            throw new CucumberException(e);
        }
    }

//    private static void closeQuietly(Closeable out)
//    {
//        try
//        {
//            out.close();
//        }
//        catch (IOException ignored)
//        {
//            // go gentle into that good night
//        }
//    }
}
