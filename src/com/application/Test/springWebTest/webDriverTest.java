package com.application.Test.springWebTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WebDriverTest {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();

  @Before
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    baseUrl = "http://localhost:8080/ProjetJEE1/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testWebDriver() throws Exception {
    driver.get(baseUrl + "/ProjetJEE1/annuaires/annuaire/home");
    assertTrue(isElementPresent(By.cssSelector("body")));
    assertTrue(isElementPresent(By.cssSelector("div.container-form")));
    assertTrue(isElementPresent(By.xpath("//button[@type='submit']")));
    try {
      assertEquals("se connecter", driver.findElement(By.xpath("//button[@type='submit']")).getText());
    } catch (Error e) {
      verificationErrors.append(e.toString());
    }
    assertTrue(isElementPresent(By.xpath("//button[@type='button']")));
    try {
      assertEquals("s'inscrire", driver.findElement(By.xpath("//button[@type='button']")).getText());
    } catch (Error e) {
      verificationErrors.append(e.toString());
    }
    driver.get(baseUrl + "/ProjetJEE1/annuaires/annuaire/ajoutform");
    assertTrue(isElementPresent(By.cssSelector("fieldset")));
    assertTrue(isElementPresent(By.xpath("//button[@type='submit']")));
    try {
      assertEquals("Valider", driver.findElement(By.xpath("//button[@type='submit']")).getText());
    } catch (Error e) {
      verificationErrors.append(e.toString());
    }
    assertTrue(isElementPresent(By.xpath("//button[@type='button']")));
    try {
      assertEquals("Annuler", driver.findElement(By.xpath("//button[@type='button']")).getText());
    } catch (Error e) {
      verificationErrors.append(e.toString());
    }
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}
