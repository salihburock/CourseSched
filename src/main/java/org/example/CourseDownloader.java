package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v137.network.Network;
import org.openqa.selenium.devtools.v137.network.model.Response;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseDownloader {

    public static void downloadFacultyCoursesForCurrentTerm(String facultyName) {

        Path downloadPath = Paths.get("src", "assets").toAbsolutePath();
        File downloadDir = downloadPath.toFile();
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", downloadPath.toString());
        prefs.put("download.prompt_for_download", false);
        prefs.put("safebrowsing.enabled", true);

        ChromeOptions opts = new ChromeOptions();
        opts.setExperimentalOption("prefs", prefs);
        
        //opts.addArguments("--headless=new"); Download does not work
        opts.addArguments("--incognito");
        opts.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36");

        ChromeDriver driver = new ChromeDriver(opts);
        DevTools devTools = driver.getDevTools();
        devTools.createSession();

        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        CompletableFuture<String> futureResponse = new CompletableFuture<>();

        devTools.addListener(Network.responseReceived(), responseReceived -> {
            Response response = responseReceived.getResponse();
            if (response.getUrl().contains("xls") && response.getMimeType().equals("application/json")) {
                String body = devTools.send(Network.getResponseBody(responseReceived.getRequestId())).getBody();
                futureResponse.complete(body);
            }
        });

        try {
            System.out.println("Navigating to the form URL in headless mode...");
            driver.get("https://sis.ozyegin.edu.tr/OZU_GWT/WEB/CourseCatalogOfferUI?locale=en");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

            System.out.println("Selecting faculty: " + facultyName);
            WebElement facultyDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.name("FACULTYCODE")));
            facultyDropdown.sendKeys(facultyName.split(" ")[0]); 
            WebElement facultyOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//nobr[text()='" + facultyName + "']")));
            facultyOption.click();
            System.out.println("✅ Faculty clicked.");

            WebElement searchBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@role='button' and .//td[text()='Search']]")));
            searchBtn.click();
            System.out.println("✅ Search clicked.");

            Thread.sleep(3000);

            System.out.println("Clicking the Export button...");
            WebElement excelButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//td[@class='toolStripButton' and .//img[contains(@src, 'excel_export.png')]]")
            ));
            excelButton.click();

            System.out.println("Waiting for the server to provide the Excel file path...");
            String responseBody = futureResponse.get(30, TimeUnit.SECONDS);

            Pattern pattern = Pattern.compile("\"(.*?\\.xls)\"");
            Matcher matcher = pattern.matcher(responseBody);

            if (matcher.find()) {
                String tempFilePath = matcher.group(1);
                String downloadUrl = "https://sis.ozyegin.edu.tr/OZU_GWT/coreneo_web_server/ExcelDownloadServlet?fileName=" + tempFilePath;

                System.out.println("Triggering download and waiting for file to appear...");
                String script = "var link = document.createElement('a');" +
                                "link.href = arguments[0];" +
                                "link.download = arguments[1];" +
                                "document.body.appendChild(link);" +
                                "link.click();" +
                                "document.body.removeChild(link);";
                ((JavascriptExecutor) driver).executeScript(script, downloadUrl);

                waitForFileDownload(downloadPath, 30);
                

            } else {
                System.err.println("❌ ERROR: " + responseBody);
            }

        } catch (Exception e) {
            System.err.println("❌ ERROR: " + facultyName);
            e.printStackTrace();
        } finally {
            devTools.close();
            driver.quit();
        }
    }

    private static void waitForFileDownload(Path downloadDir, int timeoutInSeconds) throws Exception {

        File[] filesInDirAtStart = downloadDir.toFile().listFiles();

        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeoutInSeconds * 1000) {
            

        }
        throw new Exception("File download timed out after " + timeoutInSeconds + " seconds.");
    }
    
    public static void main(String[] args) {
        downloadFacultyCoursesForCurrentTerm("Faculty of Engineering");
    }
}