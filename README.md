# Sauce Demo Test Suite

To execute, simply run:

    $ mvn test
    
And each scenario will execute.

## Scenario breakdown

|Tag|Test Name|Smoke Test|Regression Test|End to End|
|---|---|---|---|---|
|SignOn|Verify valid users can sign in|Yes|Yes|No|
|SignOn|Verify locked out user gets locked out message|No|Yes|No|
|SignOn|Verify invalid users cannot sign in|No|Yes|No|
|Orders|Place a single item in the shopping cart|Yes|Yes|No|
|Orders|Place multiple items in the shopping cart|No|Yes|No|
|Orders|Validate Order Totals|No|Yes|No|

