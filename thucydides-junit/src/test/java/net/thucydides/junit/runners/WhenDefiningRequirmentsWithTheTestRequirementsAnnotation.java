package net.thucydides.junit.runners;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.ConcreteTestStep;
import net.thucydides.core.model.TestStep;
import net.thucydides.junit.runners.mocks.TestableWebDriverFactory;
import net.thucydides.samples.SingleTestScenarioWithSeveralBusinessRules;
import net.thucydides.samples.SuccessfulSingleTestScenario;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.notification.RunNotifier;

import java.io.File;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

public class WhenDefiningRequirmentsWithTheTestRequirementsAnnotation extends AbstractTestStepRunnerTest {


    @Before
    public void initMocks() {
    }
    
    @Test
    public void the_TestsRequirement_annotation_can_associated_a_business_rule_to_a_test() throws Exception {
        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        TestOutcome testOutcome = executedScenarios.get(0);
        
        assertThat(testOutcome.getTestedRequirements(), hasItem("SOME_BUSINESS_RULE"));

    }    
    
    @Test
    public void the_TestsRequirement_annotation_can_associated_several_business_rules_to_a_test() throws Exception {
        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenarioWithSeveralBusinessRules.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        TestOutcome testOutcome = executedScenarios.get(0);
        
        assertThat(testOutcome.getTestedRequirements(), hasItem("SOME_BUSINESS_RULE_1"));
        assertThat(testOutcome.getTestedRequirements(), hasItem("SOME_BUSINESS_RULE_2"));

    }    

    
    @Test
    public void the_TestsRequirement_annotation_can_associated_a_business_rule_to_a_test_step() throws Exception {
        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        TestOutcome testOutcome = executedScenarios.get(0);
        List<TestStep> steps = testOutcome.getTestSteps();
        ConcreteTestStep step1 = (ConcreteTestStep) steps.get(0);
        
        assertThat(step1.getTestedRequirements(), hasItem("LOW_LEVEL_BUSINESS_RULE"));

    }    

    @Test
    public void the_TestsRequirement_annotation_can_associated_multiple_business_rules_to_a_test_step() throws Exception {
        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        TestOutcome testOutcome = executedScenarios.get(0);
        List<TestStep> steps = testOutcome.getTestSteps();
        ConcreteTestStep step2 = (ConcreteTestStep) steps.get(3);
        
        assertThat(step2.getTestedRequirements(), hasItem("LOW_LEVEL_BUSINESS_RULE_1"));
        assertThat(step2.getTestedRequirements(), hasItem("LOW_LEVEL_BUSINESS_RULE_2"));

    }    

    @Test
    public void the_test_run_can_calculate_all_the_tested_business_rules_in_a_test_run() throws Exception {
        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        TestOutcome testOutcome = executedScenarios.get(0);
        Set<String> allTestedRequirements = testOutcome.getAllTestedRequirements();
        assertThat(allTestedRequirements, hasItem("SOME_BUSINESS_RULE"));
        assertThat(allTestedRequirements, hasItem("LOW_LEVEL_BUSINESS_RULE"));
        assertThat(allTestedRequirements, hasItem("LOW_LEVEL_BUSINESS_RULE_1"));
        assertThat(allTestedRequirements, hasItem("LOW_LEVEL_BUSINESS_RULE_2"));

    }    
    
}
