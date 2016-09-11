/*
 * Copyright 2016 Alexei Barantsev
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package ru.stqa.selenium.decorated;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.WrapsDriver;

import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * This class allows to extend WebDriver by adding new functionality to a decorated.
 * Example of use:
 * <code>WebDriver driver = DecoratedWebDriver.decorate(originalDriver, MyWebDriverWrapper.class);</code>
 * or
 * <code>MyWebDriverWrapper decorated = new MyWebDriverWrapper(originalDriver, otherParameter);<br>
 * WebDriver driver = new MyWebDriverWrapper(originalDriver, otherParameter).getDriver();</code>
 */
public class DecoratedWebDriver extends AbstractDecoratedTopmost<WebDriver>
    implements DecoratedTopmost, WebDriver, WrapsDriver, JavascriptExecutor, HasInputDevices, HasTouchScreen {

  public DecoratedWebDriver(WebDriver driver) {
    super(driver);
  }

  protected Decorated<WebElement> createDecorated(WebElement original) {
    return new DecoratedWebElement(original, DecoratedWebDriver.this);
  }

  protected List<WebElement> wrapElements(final List<WebElement> elements) {
    for (ListIterator<WebElement> iterator = elements.listIterator(); iterator.hasNext(); ) {
      // TODO: WTF?
      iterator.set(activate(createDecorated(iterator.next())));
    }
    return elements;
  }

  protected Decorated<TargetLocator> createDecorated(final TargetLocator original) {
    return new DecoratedTargetLocator(original, DecoratedWebDriver.this);
  }

  protected Decorated<Alert> createDecorated(final Alert original) {
    return new DecoratedAlert(original, DecoratedWebDriver.this);
  }

  protected Decorated<Navigation> createDecorated(final Navigation original) {
    return new DecoratedNavigation(original, DecoratedWebDriver.this);
  }

  protected Decorated<Options> createDecorated(final Options original) {
    return new DecoratedOptions(original, DecoratedWebDriver.this);
  }

  protected Decorated<Timeouts> createDecorated(final Timeouts original) {
    return new DecoratedTimeouts(original, DecoratedWebDriver.this);
  }

  protected Decorated<Window> createDecorated(final Window original) {
    return new DecoratedWindow(original, DecoratedWebDriver.this);
  }

  protected Decorated<Coordinates> createDecorated(final Coordinates original) {
    return new DecoratedCoordinates(original, DecoratedWebDriver.this);
  }

  protected Decorated<Keyboard> createDecorated(final Keyboard original) {
    return new DecoratedKeyboard(original, DecoratedWebDriver.this);
  }

  protected Decorated<Mouse> createDecorated(final Mouse original) {
    return new DecoratedMouse(original, DecoratedWebDriver.this);
  }

  protected Decorated<TouchScreen> createDecorated(final TouchScreen original) {
    return new DecoratedTouchScreen(original, DecoratedWebDriver.this);
  }

  // TODO: implement proper wrapping for arbitrary objects
  Object wrapObject(final Object object) {
    if (object instanceof WebElement) {
      return activate(createDecorated((WebElement) object));
    } else {
      return object;
    }
  }

  @Override
  public final WebDriver getWrappedDriver() {
    return getOriginal();
  }

  @Override
  public void get(String url) {
    getOriginal().get(url);
  }

  @Override
  public String getCurrentUrl() {
    return getOriginal().getCurrentUrl();
  }

  @Override
  public String getTitle() {
    return getOriginal().getTitle();
  }

  @Override
  public WebElement findElement(final By by) {
    return activate(createDecorated(getOriginal().findElement(by)));
  }

  @Override
  public List<WebElement> findElements(final By by) {
    return wrapElements(getOriginal().findElements(by));
  }

  @Override
  public String getPageSource() {
    return getOriginal().getPageSource();
  }

  @Override
  public void close() {
    getOriginal().close();
  }

  @Override
  public void quit() {
    getOriginal().quit();
  }

  @Override
  public Set<String> getWindowHandles() {
    return getOriginal().getWindowHandles();
  }

  @Override
  public String getWindowHandle() {
    return getOriginal().getWindowHandle();
  }

  @Override
  public TargetLocator switchTo() {
    return activate(createDecorated(getOriginal().switchTo()));
  }

  @Override
  public Navigation navigate() {
    return activate(createDecorated(getOriginal().navigate()));
  }

  @Override
  public Options manage() {
    return activate(createDecorated(getOriginal().manage()));
  }

  @Override
  public Object executeScript(String script, Object... args) {
    WebDriver driver = getOriginal();
    if (driver instanceof JavascriptExecutor) {
      return wrapObject(((JavascriptExecutor) driver).executeScript(script, args));
    } else {
      throw new WebDriverException("Wrapped webdriver does not support JavascriptExecutor: " + driver);
    }
  }

  @Override
  public Object executeAsyncScript(String script, Object... args) {
    WebDriver driver = getOriginal();
    if (driver instanceof JavascriptExecutor) {
      return wrapObject(((JavascriptExecutor) driver).executeAsyncScript(script, args));
    } else {
      throw new WebDriverException("Wrapped webdriver does not support JavascriptExecutor: " + driver);
    }
  }

  @Override
  public Keyboard getKeyboard() {
    return activate(createDecorated(((HasInputDevices) getOriginal()).getKeyboard()));
  }

  @Override
  public Mouse getMouse() {
    return activate(createDecorated(((HasInputDevices) getOriginal()).getMouse()));
  }

  @Override
  public TouchScreen getTouch() {
    return activate(createDecorated(((HasTouchScreen) getOriginal()).getTouch()));
  }

}