import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

public class SanityCheck
{
    public static void main(String[] args)
    {
        WebDriver driver = new ChromeDriver();

        // Need page load timeout because of lingering request to https://pixel.jumptap.com
        driver.manage().timeouts().pageLoadTimeout(80, TimeUnit.SECONDS);
        long start = System.currentTimeMillis();
        driver.navigate().to("https://www.sleepnumber.com");
        long stop = System.currentTimeMillis();
        driver.manage().timeouts().pageLoadTimeout(0, TimeUnit.SECONDS);

        System.out.printf("Navigation complete: %d seconds\n", ((stop - start) / 1000));
        driver.close();
    }
}