package net.thucydides.samples;

import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.TestsRequirement;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.annotations.Managed;
import net.thucydides.junit.runners.ThucydidesRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(ThucydidesRunner.class)
public class SampleNoSuchElementExceptionScenario {
    
    @Managed
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "http://www.google.com")
    public Pages pages;
    
    @Steps
    public SampleScenarioSteps steps;

    @Test
    @TestsRequirement("ABC")
    public void failing_happy_day_scenario() {
        steps.stepThatSucceeds();
        steps.stepThatIsIgnored();
        steps.stepThatIsPending();
        steps.anotherStepThatSucceeds();
        steps.failsToFindElement();
        steps.stepThatShouldBeSkipped();
    }


    @Test
    @TestsRequirement("ABC")
    public void successful_happy_day_scenario() {
        steps.stepThatSucceeds();
        steps.stepThatIsIgnored();
        steps.stepThatIsPending();
        steps.anotherStepThatSucceeds();
    }

    @Test
    @TestsRequirement("DEF")
    public void other_test_case_1() {
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
        steps.stepThatIsPending();
    }

    @Test
    public void other_test_case_2() {
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
    }
}
