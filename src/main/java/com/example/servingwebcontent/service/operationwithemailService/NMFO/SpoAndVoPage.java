package com.example.servingwebcontent.service.operationwithemailService.NMFO;

import com.example.servingwebcontent.Config.operationwithemailService.NMFOLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class SpoAndVoPage {
    public WebDriver driver;
    private static final Logger logger = LogManager.getLogger();

    private static String registryBtn;
    private static String cyclePcTasksBtn;
    private static String numberRequestField;
    private static String searchBtn;
    private static String focusToClientField;
    private static String requestsAndResultsEducationBtn;
    private static String numberRequestOnOpenRequestsPageField;
    private static String confirmationCheckBox;

    public SpoAndVoPage(WebDriver driver, NMFOLocators locators) {
        PageFactory.initElements(driver, this);
        this.driver = driver;

        registryBtn = locators.getRegistryBtn();
        cyclePcTasksBtn = locators.getCyclePcTasksBtn();
        numberRequestField = locators.getNumberRequestField();
        searchBtn = locators.getSearchBtn();
        focusToClientField = locators.getFocusToClientField();
        requestsAndResultsEducationBtn = locators.getRequestsAndResultsEducationBtn();
        numberRequestOnOpenRequestsPageField = locators.getNumberRequestOnOpenRequestsPageField();
        confirmationCheckBox = locators.getConfirmationCheckBox();
    }

    @FindAll(@FindBy(xpath = "//td[contains(@class,'v-table-cell-content')]"))
    private List<WebElement> cells;


    public void desktopPreparation() {
        WebDriverWait wait = new WebDriverWait(driver, 20);
        try {
            wait.until(visibilityOfElementLocated(By.xpath(registryBtn))).click(); //??????????????
            Thread.sleep(2000);
            wait.until(visibilityOfElementLocated(By.xpath(cyclePcTasksBtn))).click(); //?????????? ????: ????????????
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            driver.close();// TODO
        }
    }

    public void searchForApplication(String numberApplicationClient) {
        WebDriverWait wait = new WebDriverWait(driver, 20);
        try {
            logger.info("search " + numberApplicationClient);
            Thread.sleep(2000);
            wait.until(visibilityOfElementLocated(By.xpath(numberRequestField))).sendKeys(numberApplicationClient); //?????????? ????????????
            Thread.sleep(2000);
            wait.until(visibilityOfElementLocated(By.xpath(searchBtn))).click(); //??????????
            Thread.sleep(2000);
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(focusToClientField))).click(); //?????????? ???? ????????????????
            Thread.sleep(1000);
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(requestsAndResultsEducationBtn))).click(); //???????????????? ???????????? ???? ??????????
            Thread.sleep(2000);
            wait.until(visibilityOfElementLocated(By.xpath(numberRequestOnOpenRequestsPageField))).sendKeys(numberApplicationClient); //?????????? ???????????? wait.until(ExpectedConditions.elementToBeClickable(By.xpath(ConfProperties.getProperty("searchBtn")))).click(); //??????????
            Thread.sleep(2000);
            wait.until(visibilityOfElementLocated(By.xpath(searchBtn))).click(); //??????????
            Thread.sleep(2000);
            logger.info("???????????????????? ???? ?????????????? ????????????????");

        } catch (InterruptedException | NoSuchElementException | TimeoutException e) {
            e.printStackTrace();
            logger.warn("???? ???????? ?????????? ?????????????? ???? ??????????????: " + e);
        }
    }

    public List<String> getClientInfo() {
        List<String> cellsMap = new ArrayList<>();
        try {
            cells.forEach((WebElement cell) -> cellsMap.add(cell.findElement(By.xpath("./div")).getText()));
            return cellsMap;
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            logger.error("failed to get information");
            return cellsMap;
        }
    }

    public void setConfirmationCheckBox() {
        try {
            WebElement element = driver.findElement(By.xpath(confirmationCheckBox));
            Actions actions = new Actions(driver);
            actions.moveToElement(element).click().build().perform();
            Thread.sleep(2000);
            logger.info("???????????? ?????????????? ???????????????????????? ???? ??????????");
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            logger.warn("don't press CheckBox, fucking developers");
        }
    }

    public void closeWindowsAndReturnCyclePc() {
        logger.info("return www.google.com");
        driver.get("https://www.google.com/");

//        WebDriverWait wait = new WebDriverWait(driver, 20);
//        try {
//            wait.until(visibilityOfElementLocated(By.xpath(ConfProperties.getProperty("returnReturnCyclePc")))).click();
//            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(ConfProperties.getProperty("okBtn")))).click();
//            Thread.sleep(2000);
//            desktopPreparation();
//        } catch (InterruptedException e) {
//                    logger.error(e.getMessage());
//            e.printStackTrace();
//        }
    }
}