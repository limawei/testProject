package us.codecraft.webmagic.downloader.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class WebSeleniumTest {

	private String chromeDriverPath = "D:\\spider\\chormedriver\\chromedriver.exe";
	
	public void testWebDriver(){
		System.getProperties().setProperty("webdriver.chrome.driver", chromeDriverPath);
		WebDriver webDriver = new ChromeDriver();
        webDriver.get("http://huaban.com/");
        WebDriver.Options manage = webDriver.manage();
        String content;
		try {
			WebElement webElement = webDriver.findElement(By.xpath("/html"));
			content = webElement.getAttribute("outerHTML");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			content = "";
		}
        /*Page page = new Page();
        page.setRawText(content);
        page.setHtml(new Html(UrlUtils.fixAllRelativeHrefs(content, request.getUrl())));
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);*/
	}
}
