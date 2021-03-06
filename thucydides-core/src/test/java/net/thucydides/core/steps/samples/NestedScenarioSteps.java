package net.thucydides.core.steps.samples;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.StepGroup;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.pages.WrongPageError;
import net.thucydides.core.steps.ScenarioSteps;

public class NestedScenarioSteps extends ScenarioSteps {

    @Steps
    public FlatScenarioSteps innerSteps;

    public NestedScenarioSteps(Pages pages) {
        super(pages);
    }

    @StepGroup("Step group 1")
    public void step1() throws WrongPageError {
        innerSteps.step_one();
        innerSteps.step_two();
        innerSteps.step_three();
    }
    
    @StepGroup("Step group 2")
    public void step2() throws WrongPageError {
        innerSteps.step_one();
        innerSteps.step_three();
    }

    @Step
    public void step3() throws WrongPageError {
    }

    @Step
    public void nestedFailingStep() {
        innerSteps.failingStep();
    }

    @StepGroup("Step with nested failure")
    public void step_with_nested_failure() throws WrongPageError {
        innerSteps.step_one();
        innerSteps.failingStep();
    }

}
