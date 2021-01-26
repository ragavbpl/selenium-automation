@echo off
@REM "Start the Selenium Automation Execution"

cd ..

call ant -f bin\RunDistributedTests.xml runDistributedTests

cd bin