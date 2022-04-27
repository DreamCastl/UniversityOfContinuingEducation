package com.example.servingwebcontent.service.WorkWithWebSite.NMFO;

import com.example.servingwebcontent.Config.NMFOLocators;
import com.example.servingwebcontent.Config.NMFOProperties;
import com.example.servingwebcontent.service.RowData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Map;
import java.util.concurrent.TimeUnit;


public class DriverNMFO {
    private WebDriver driver;
    private static SpoAndVoPage spoAndVoPage;
    private static boolean AuthorizationConnect = false;
    private static final Logger logger = LogManager.getLogger();

    private static String loginPage;
    private static String spoPage;
    private static String voPage;
    private static String loginAdmin;
    private static String PassAdmin;

    public DriverNMFO(NMFOProperties propertiesNMFO, NMFOLocators locators) {
        logger.info("Подключение к НМФО.");
        loginPage = propertiesNMFO.getLoginPage();
        spoPage = propertiesNMFO.getSpoPage();
        voPage = propertiesNMFO.getVoPage();
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
            return -1;
        }
    }

    public static void voPageDesktopPreparation(WebDriver driver) {
        do {
            logger.info("Переход на NMOV");
            driver.get(voPage);
        } while (checkingUrl(driver));
        spoAndVoPage.desktopPreparation();

        try {
            Thread.sleep(2000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void spoPageDesktopPreparation(WebDriver driver) {
        do {
            logger.info("Переход на NMOS");
            driver.get(spoPage);
        } while (checkingUrl(driver));
        spoAndVoPage.desktopPreparation();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean isAutorizationConnnect() {
        return AuthorizationConnect;
    }

    public void getDataNMFO(Map<String, String> currentLine) {
        logger.info("------------>" + currentLine.get("Number") + "<------------");
        if (currentLine.get("Number").contains("NMOV")) {
            DriverNMFO.voPageDesktopPreparation(driver);//TODO убрать статику, сделать метод приватным
        } else {
            DriverNMFO.spoPageDesktopPreparation(driver);//TODO убрать статику, сделать метод приватным
        }
        getSpoAndVoPage().searchForApplication(currentLine.get("Number")); //TODO убрать статику, сделать метод приватным, в парсер добавить вытаскивание из письма
        RowData.AddClientData(getSpoAndVoPage().getClientInfo(),currentLine);
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
            e.printStackTrace();
            return false;
        }
    }
}
