package net.thucydides.core.steps;

import java.lang.reflect.Method;

import static net.thucydides.core.util.NameConverter.humanize;
import static net.thucydides.core.util.NameConverter.withNoArguments;

/**
 * A description of a step executed during a Thucydides step run.
 * Used in the reporting to generate user-readable names for the executed steps.
 */
public class ExecutedStepDescription {

    private final Class<? extends ScenarioSteps> stepsClass;
    private final String name;
    private boolean isAGroup;

    protected ExecutedStepDescription(final Class<? extends ScenarioSteps> stepsClass,
                                      final String name) {
        this.stepsClass = stepsClass;
        this.name = name;
    }


    protected ExecutedStepDescription(final Class<? extends ScenarioSteps> stepsClass,
                                      final String name,
                                      final boolean isAGroup) {
        this.stepsClass = stepsClass;
        this.name = name;
        this.isAGroup = isAGroup;
    }

    protected ExecutedStepDescription(final String name) {
        this.stepsClass = null;
        this.name = name;
    }

    public ExecutedStepDescription clone() {
        return new ExecutedStepDescription(stepsClass, name, isAGroup);
    }

    /**
     * The class of the step library being executed.
     */
    public Class<? extends ScenarioSteps> getStepClass() {
        return stepsClass;
    }


    public String getName() {
        return name;
    }

    /**
     * We might not have the test class provided (e.g. at the end of a test).
     */
    public static ExecutedStepDescription of(final Class<? extends ScenarioSteps> stepsClass,
                                             final String name) {
        return new ExecutedStepDescription(stepsClass, name);
    }

    public static ExecutedStepDescription withTitle(final String name) {
        return new ExecutedStepDescription(name);
    }

    public boolean isAGroup() {
        return isAGroup;
    }

    public void setAGroup(final boolean aGroup) {
        isAGroup = aGroup;
    }

    public Method getTestMethod() {
        if (getStepClass() != null) {
            return methodCalled(withNoArguments(getName()), getStepClass());
        } else {
            return null;
        }
    }


    private Method methodCalled(final String methodName, final Class<?> testClass) {
        Method[] methods = testClass.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new IllegalArgumentException("No test method called " + methodName + " was found in " + testClass);
    }


    /**
     * Turns a method into a human-readable title.
     */
    public String getTitle() {
        return humanize(name);
    }
}
