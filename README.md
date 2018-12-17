# Sauce Demo Test Suite

To execute the full test suite, simply run:

    $ mvn test
    
And each scenario will execute.

## Scenario breakdown

|Feature|Scenario|Smoke Test|Regression Test|End to End|
|---|---|---|---|---|
|login|Verify valid users can sign in|Yes|Yes|No|
|login|Verify locked out user gets locked out message|No|Yes|No|
|login|Verify invalid users cannot sign in|No|Yes|No|
|orders|Place a single item in the shopping cart|Yes|Yes|No|
|orders|Place multiple items in the shopping cart|No|Yes|No|
|orders|Validate Order Totals|No|Yes|No|

## Tags

Tags are defined to allow execution of specific Features and Scenarios.

__Example__: Execute smoke tests for tests related to `orders`:

    $ mvn test "-Dcucumber.options=--tags '@orders and @smoke1'" -f pom.xml   

__Example__: Execute regression tests for tests related to `login`:

    $ mvn test "-Dcucumber.options=--tags '@login and @regression1'" -f pom.xml   
