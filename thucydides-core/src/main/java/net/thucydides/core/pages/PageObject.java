package net.thucydides.core.pages;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.pages.components.Dropdown;
import net.thucydides.core.pages.components.FileToUpload;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webelements.Checkbox;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;

/**
 * A base class representing a WebDriver page object.
 *
 * @author johnsmart
 */
public abstract class PageObject {

    private static final int WAIT_FOR_ELEMENT_PAUSE_LENGTH = 5;

    private static final int ONE_SECOND = 1000;

    private long waitForTimeout = WAIT_FOR_ELEMENT_PAUSE_LENGTH;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(PageObject.class);

    private static final long WAIT_FOR_TIMEOUT = 30000;

    private WebDriver driver;

    private MatchingPageExpressions matchingPageExpressions;

    private RenderedPageObjectView renderedView;

    private PageUrls pageUrls;

    private InternalClock clock = new InternalClock();

    public PageObject(final WebDriver driver) {
        this.driver = driver;
        this.waitForTimeout = WAIT_FOR_TIMEOUT;

        setupPageUrls();

        WebDriverFactory.initElementsWithAjaxSupport(this, driver);

    }

    public FileToUpload upload(final String filename) {
        return new FileToUpload(filename);
    }

    private void setupPageUrls() {
        pageUrls = new PageUrls(this);
    }

    public void setWaitForTimeout(final long waitForTimeout) {
        this.waitForTimeout = waitForTimeout;
    }

    protected RenderedPageObjectView getRenderedView() {
        if (renderedView == null) {
            renderedView = new RenderedPageObjectView(driver, waitForTimeout);
        }
        return renderedView;
    }

    protected InternalClock getClock() {
        return clock;
    }

    private MatchingPageExpressions getMatchingPageExpressions() {
        if (matchingPageExpressions == null) {
            matchingPageExpressions = new MatchingPageExpressions(this);
        }
        return matchingPageExpressions;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public String getTitle() {
        return driver.getTitle();
    }


    protected boolean matchesAnyUrl() {
        return thereAreNoPatternsDefined();
    }

    /**
     * Does this page object work for this URL? When matching a URL, we check
     * with and without trailing slashes
     */
    public final boolean compatibleWithUrl(final String currentUrl) {
        if (thereAreNoPatternsDefined()) {
            return true;
        } else {
            return matchUrlAgainstEachPattern(currentUrl);
        }
    }

    private boolean matchUrlAgainstEachPattern(final String currentUrl) {
        return getMatchingPageExpressions().matchUrlAgainstEachPattern(currentUrl);
    }

    private boolean thereAreNoPatternsDefined() {
        return getMatchingPageExpressions().isEmpty();
    }

    public PageObject waitForRenderedElements(final By byElementCriteria) {
        getRenderedView().waitFor(byElementCriteria);
        return this;
    }

    public PageObject waitForRenderedElementsToDisappear(
            final By byElementCriteria) {
        getRenderedView().waitForElementsToDisappear(byElementCriteria);
        return this;
    }

    /**
     * Waits for a given text to appear anywhere on the page.
     */
    public PageObject waitForTextToAppear(final String expectedText) {
        getRenderedView().waitForText(expectedText);
        return this;
    }

    public PageObject waitForTitleToAppear(final String expectedTitle) {
        getRenderedView().waitForTitle(expectedTitle);
        return this;
    }

    public PageObject waitForTitleToDisappear(final String expectedTitle) {
        getRenderedView().waitForTitleToDisappear(expectedTitle);
        return this;
    }

    /**
     * Waits for a given text to appear anywhere on the page.
     */
    public PageObject waitForTextToAppear(final WebElement element,
                                          final String expectedText) {
        getRenderedView().waitForText(element, expectedText);
        return this;
    }

    public PageObject waitForTextToDisappear(final String expectedText) {
        return waitForTextToDisappear(expectedText, waitForTimeout);
    }

    /**
     * Waits for a given text to not be anywhere on the page.
     */
    public PageObject waitForTextToDisappear(final String expectedText,
                                             final long timeout) {
        getRenderedView().waitForTextToDisappear(expectedText, timeout);
        return this;
    }

    /**
     * Waits for any of a number of text blocks to appear anywhere on the
     * screen.
     */
    public PageObject waitForAnyTextToAppear(final String... expectedText) {
        getRenderedView().waitForAnyTextToAppear(expectedText);
        return this;
    }

    public PageObject waitForAnyTextToAppear(final WebElement element,
                                             final String... expectedText) {
        getRenderedView().waitForAnyTextToAppear(element, expectedText);
        return this;
    }

    /**
     * Waits for all of a number of text blocks to appear on the screen.
     */
    public PageObject waitForAllTextToAppear(final String... expectedTexts) {
        getRenderedView().waitForAllTextToAppear(expectedTexts);
        return this;
    }

    public PageObject waitForAnyRenderedElementOf(final By... expectedElements) {
        getRenderedView().waitForAnyRenderedElementOf(expectedElements);
        return this;
    }

    protected void waitABit(final long timeInMilliseconds) {
        getClock().pauseFor(timeInMilliseconds);
    }

    public List<WebElement> thenReturnElementList(final By byListCriteria) {
        return driver.findElements(byListCriteria);
    }

    /**
     * Check that the specified text appears somewhere in the page.
     */
    public void shouldContainText(final String textValue) {
        if (!containsText(textValue)) {
            String errorMessage = String.format(
                    "The text '%s' was not found in the page", textValue);
            throw new NoSuchElementException(errorMessage);
        }
    }

    /**
     * Does the specified web element contain a given text value. Useful for dropdowns and so on.
     * @deprecated use element(webElement).containsText(textValue)
     */
    @Deprecated
    public boolean containsTextInElement(final WebElement webElement, final String textValue) {
        return element(webElement).containsText(textValue);
    }

    /*
     * Check that the element contains a given text.
     * @deprecated use element(webElement).shouldContainText(textValue)
     */
    @Deprecated
    public void shouldContainTextInElement(final WebElement webElement, final String textValue) {
        element(webElement).shouldContainText(textValue);
    }

    /*
     * Check that the element does not contain a given text.
     * @deprecated use element(webElement).shouldNotContainText(textValue)
     */
    @Deprecated
    public void shouldNotContainTextInElement(final WebElement webElement, final String textValue) {
        element(webElement).shouldNotContainText(textValue);
    }

    /**
     * Clear a field and enter a value into it.
     */
    public void typeInto(final WebElement field, final String value) {
        field.clear();
        field.sendKeys(value);
    }

    public void selectFromDropdown(final WebElement dropdown,
                                   final String visibleLabel) {

        Dropdown.forWebElement(dropdown).select(visibleLabel);
    }

    public void selectMultipleItemsFromDropdown(final WebElement dropdown,
                                                final String... selectedLabels) {
        Dropdown.forWebElement(dropdown).selectMultipleItems(selectedLabels);
    }


    public Set<String> getSelectedOptionLabelsFrom(final WebElement dropdown) {
        return Dropdown.forWebElement(dropdown).getSelectedOptionLabels();
    }

    public Set<String> getSelectedOptionValuesFrom(final WebElement dropdown) {
        return Dropdown.forWebElement(dropdown).getSelectedOptionValues();
    }

    public String getSelectedValueFrom(final WebElement dropdown) {
        return Dropdown.forWebElement(dropdown).getSelectedValue();
    }

    public String getSelectedLabelFrom(final WebElement dropdown) {
        return Dropdown.forWebElement(dropdown).getSelectedLabel();
    }

    public void setCheckbox(final WebElement field, final boolean value) {
        Checkbox checkbox = new Checkbox(field);
        checkbox.setChecked(value);
    }

    /**
     * Check that the specified text appears somewhere in the page.
     */
    public boolean containsText(final String textValue) {
        return getRenderedView().containsText(textValue);

    }

    /**
     * Fail the test if this element is not displayed (rendered) on the screen.
     */
    public void shouldBeVisible(final WebElement field) {
        element(field).shouldBeVisible();
    }

    public void shouldBeVisible(final By byCriteria) {
        WebElement element = getDriver().findElement(byCriteria);
        shouldBeVisible(element);
    }

    public void shouldNotBeVisible(final WebElement field) {
        element(field).shouldNotBeVisible();
    }

    public void shouldNotBeVisible(final By byCriteria) {
        WebElement element = getDriver().findElement(byCriteria);
        shouldNotBeVisible(element);
    }

    /**
     * Open the webdriver browser to the base URL, determined by the DefaultUrl
     * annotation if present. If the DefaultUrl annotation is not present, the
     * default base URL will be used.
     */
    public void open() {
        String startingUrl = updateUrlWithBaseUrlIfDefined(pageUrls.getStartingUrl());
        getDriver().get(startingUrl);
    }

    public String updateUrlWithBaseUrlIfDefined(final String startingUrl) {
        String baseUrl = System.getProperty(ThucydidesSystemProperty.BASE_URL.getPropertyName());
        if (baseUrl != null) {
            return replaceHost(startingUrl, baseUrl); 
        } else {
            return startingUrl;
        }
    }

    private String replaceHost(final String starting, final String base) {

        String updatedUrl = starting;

        try {
            URL startingUrl = new URL(starting);
            URL baseUrl = new URL(base);

            String startingHostComponent = hostComponentFrom(startingUrl.getProtocol(),
                                                             startingUrl.getHost(),
                                                             startingUrl.getPort());
            String baseHostComponent = hostComponentFrom(baseUrl.getProtocol(),
                                                             baseUrl.getHost(),
                                                             baseUrl.getPort());
            updatedUrl = starting.replaceFirst(startingHostComponent, baseHostComponent);
        } catch (MalformedURLException e) {
            LOGGER.error("Failed to analyse default page URL", e);
        }

        return updatedUrl;
    }

    private String hostComponentFrom(final String protocol, final String host, final int port) {
        StringBuffer hostComponent = new StringBuffer(protocol);
        hostComponent.append("://");
        hostComponent.append(host);
        if (port > 0) {
            hostComponent.append(":");
            hostComponent.append(port);
        }
        return hostComponent.toString();
    }

    /**
     * Open the webdriver browser using a paramaterized URL. Parameters are
     * represented in the URL using {0}, {1}, etc.
     */
    public void open(final String... parameterValues) {
        String startingUrl = pageUrls.getStartingUrl(parameterValues);
        getDriver().get(startingUrl);
    }

    public static String[] withParameters(final String... parameterValues) {
        return parameterValues;
    }

    public void open(final String urlTemplateName,
                     final String[] parameterValues) {
        String startingUrl = pageUrls.getNamedUrl(urlTemplateName, parameterValues);
        getDriver().get(startingUrl);
    }

    public void clickOn(final WebElement webElement) {
        try {
            webElement.click();
        } catch (WebDriverException e) {
            LOGGER.warn(
                    "Click failed. This could be a flicking failure, so I'll wait 1 second and try again",
                    e);
            waitABit(ONE_SECOND);
        }
        webElement.click();

    }

    /**
     * Returns true if at least one matching element is found on the page and is visible.
     */
    public Boolean isElementVisible(final By byCriteria) {
        return getRenderedView().elementIsDisplayed(byCriteria);
    }

    public void setDefaultBaseUrl(final String defaultBaseUrl) {
        pageUrls.overrideDefaultBaseUrl(defaultBaseUrl);
    }

    /**
     * Returns true if the specified element has the focus.
     * @deprecated Use element(webElement).hasFocus() instead
     */
    public boolean hasFocus(final WebElement webElement) {
        return element(webElement).hasFocus();
    }

    /**
     * Provides a fluent API for querying web elements.
     */
    public WebElementFacade element(WebElement webElement) {
        return new WebElementFacade(driver, webElement);
    }

    public Object evaluateJavascript(final String script) {
        WebDriverFacade driverFacade = (WebDriverFacade) driver;
        JavascriptExecutor js = (JavascriptExecutor) driverFacade.getProxiedDriver();
        return js.executeScript(script);
    }

}
