package com.example.servingwebcontent.service.operationwithemailService.NMFO;

import com.example.servingwebcontent.Config.operationwithemailService.NMFOLocators;
import com.example.servingwebcontent.Config.operationwithemailService.NMFOProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class DriverNMFO {
    private WebDriver driver;
    private static SpoAndVoPage spoAndVoPage;
    private static boolean AuthorizationConnect = false;
    private static final Logger logger = LogManager.getLogger();

    private static String loginPage;
    private static String spoPage;
    private static String voPage;
    private static String nmdPage;
    private static String loginAdmin;
    private static String PassAdmin;

    public DriverNMFO(NMFOProperties propertiesNMFO, NMFOLocators locators) {
        logger.info("Подключение к НМФО.");
        loginPage = propertiesNMFO.getLoginPage();
        spoPage = propertiesNMFO.getSpoPage();
        voPage = propertiesNMFO.getVoPage();
        nmdPage = propertiesNMFO.getNmdPage();

        loginAdmin = propertiesNMFO.getLoginAdmin();
        PassAdmin = propertiesNMFO.getPassAdmin();

        System.setProperty("webdriver.chrome.driver", "chromedriver.exe"); //todo
//        driver = new ChromeDriver(new ChromeOptions().addArguments("--headless"));//ChromeOptions Регулирует визуализацию браузера
        driver = new ChromeDriver();

        spoAndVoPage = new SpoAndVoPage(driver,locators);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    public WebDriver getDriver() {
        return driver;
    }

    public SpoAndVoPage getSpoAndVoPage() {
        return spoAndVoPage;
    }

    //TODO перенести эти методы в LoginPage
    public static void authorization(WebDriver driver) {
        for (int i = 0; i < 3; i++){
            if (startAuthorization(driver) == 0)
                AuthorizationConnect = true;
                break;
        }
    }

    private static int startAuthorization(WebDriver driver) {
        try {
            logger.info("запуск авторизации");
            driver.get(loginPage);
            LoginPage loginPage = new LoginPage(driver);

            loginPage.inputLogin(loginAdmin);
            loginPage.inputPasswd(PassAdmin);
            loginPage.clickLoginBtn();
            loginPage.waitingForAuthorization();
            return 0;
        } catch (Exception e) {
            logger.warn("Неудачная попытка входа. Пробуем еще раз");
            logger.error(e.getMessage());
            return -1;
        }
    }

    private void voPageDesktopPreparation() {
        do {
            logger.info("Переход на NMOV");
            driver.get(voPage);
        } while (checkingUrl(driver));
        spoAndVoPage.desktopPreparation();

        try {
            Thread.sleep(2000);

        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }


    private void spoPageDesktopPreparation() {
        do {
            logger.info("Переход на NMOS");
            driver.get(spoPage);
        } while (checkingUrl(driver));
        spoAndVoPage.desktopPreparation();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    private void nmdPageDesktopPreparation() {
        do {
            logger.info("Переход на NMD");
            driver.get(spoPage);
        } while (checkingUrl(driver));
        spoAndVoPage.desktopPreparation();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    public static boolean isAutorizationConnnect() {
        return AuthorizationConnect;
    }

    public List<String> getDataNMFO(String Number) {
        logger.info("------------>" + Number + "<------------");
        if (Number.contains("NMOV")) {
            voPageDesktopPreparation();
        }
        if (Number.contains("NMD")) {
            nmdPageDesktopPreparation();
        }
        if (Number.contains("NMOS")) {
            spoPageDesktopPreparation();
        }
        spoAndVoPage.searchForApplication(Number); //TODO убрать статику, сделать метод приватным, в парсер добавить вытаскивание из письма
        return spoAndVoPage.getClientInfo();

    }

    private static boolean checkingUrl(WebDriver driver) {
        try {
            Thread.sleep(2000);
            logger.info("Проверяем, что время сессии не закончилось");
            if (driver.getCurrentUrl().contains("a.edu.rosminzdrav.ru")) {
                logger.info("Время сессии закончилось. Запуск авторизации");
                authorization(driver);
                return true;
            }
            return false;
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
