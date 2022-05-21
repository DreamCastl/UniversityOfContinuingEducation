package com.example.servingwebcontent.service.operationwithemailService.NMFO;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {
    public WebDriver driver;
    public LoginPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
        this.driver = driver; }

    @FindBy(xpath = "//input[@id='username']")
    private WebElement loginField;

    @FindBy(xpath = "//button[@title='Войти']")
    private WebElement loginBtn;

    @FindBy(xpath = "//input[@id='password']")
    private WebElement passwdField;

    private String pageMyClass = "//li[@title='Мои кабинеты']";

    public void inputLogin(String login) {
        loginField.sendKeys(login); }

    public void inputPasswd(String passwd) {
        passwdField.sendKeys(passwd); }

    public void clickLoginBtn() {
        loginBtn.click(); }

    public void waitingForAuthorization() {
        WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(pageMyClass)));
    }
}