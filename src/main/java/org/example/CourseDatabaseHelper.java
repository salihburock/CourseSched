package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Set;

public class CourseDatabaseHelper {

    private static String getSISCookiesViaSelenium() throws InterruptedException {
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--headless=new");
        opts.addArguments("--disable-gpu");
        opts.addArguments("--no-sandbox");
        WebDriver driver = new ChromeDriver(opts);

        try {
            driver.get("https://www.ozyegin.edu.tr/en/ects-course-catalog-courses-offered/courses-offered");

            // Iframe stuff
            driver.switchTo().frame(0);


            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));

            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.id("isc_D")
            ));

            Set<Cookie> cookies = driver.manage().getCookies();
            System.out.println("All cookies: " + cookies);
            StringBuilder cookieHeader = new StringBuilder();
            for (Cookie c : cookies) {
                if (c.getDomain().contains("ozyegin.edu.tr")) {
                    cookieHeader.append(c.getName()).append("=").append(c.getValue()).append("; ");
                }
            }

            return cookieHeader.toString();
        } finally {
            driver.quit();
        }
    }

    private static void callPlm(String cookieHeader, String FacultyCode, String TermCode) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String body =
        "7|0|14|https://sis.ozyegin.edu.tr/OZU_GWT/coreneo_web_server/|BA56C9DBD74965AAFE0921F0CB04BBB5|com.fiba.web.client.PLMServiceCaller|callService|java.lang.String/2004016611|com.fiba.web.core.shared.CBBagLite/302181325|SIS_SEARCH_COURSE_CATALOG_OFFERED|com.fiba.web.core.shared.CBBagLiteImpl/1659781223|FACULTYCODE|com.fiba.web.core.shared.CBValueString/166305959|FACULTIESCODE|TERMCODE|TERMSCODE|NoWindow_EN|1|2|3|4|3|5|6|5|7|6|8|2|5|9|10|11|5|12|10|13|14|".replace("FACULTIESCODE", FacultyCode).replace("TERMSCODE", TermCode);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://sis.ozyegin.edu.tr/OZU_GWT/coreneo_web_server/plm"))
                .header("Content-Type", "text/x-gwt-rpc; charset=UTF-8")
                .header("Origin", "https://sis.ozyegin.edu.tr")
                .header("Referer", "https://sis.ozyegin.edu.tr/OZU_GWT/NoWindow.jsp?shellType=NoWindow&locale=EN&windowName=NoWindow_EN")
                .header("X-GWT-Module-Base", "https://sis.ozyegin.edu.tr/OZU_GWT/coreneo_web_server/")
                .header("X-GWT-Permutation", "3368599EBEA77CB9F0359AA37AC08203")
                .header("Cookie", cookieHeader)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status: " + resp.statusCode());
        System.out.println(resp.body());
    }

    // TODO: Load current local data

    // TODO: Overwite local data with fetched data
    public static void main(String[] args) throws Exception {
        String cookies = getSISCookiesViaSelenium();
        callPlm(cookies, "FE","202510");
    }
}
