package staplesscraper;

import com.google.common.io.Files;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class StaplesScraper {

    public static int maxValue = 99999999;
    private static int start = 0;
    private static final String outputPath = new WebBrowser().getDriverPath() + "StaplesProductListing.csv";
    private static final String baseURL = "http://www.staples.com/product_";
    private static ProdInfoArrays prodArray = new ProdInfoArrays();
    private static WebDriver drv = null;
    private static Map<String, String> prodMap = new HashMap<String, String>();
    private static Writer w = null;
    private static Map<Integer, Integer> failureMap = new HashMap<Integer, Integer>();

    public static void main(String[] args) {

        if(args.length==1){
            start = Integer.parseInt(args[0]);
        }
        else if(args.length==0){
            System.out.print("Which SKU# to start at?: ");
            start = Integer.parseInt(new Scanner(System.in).nextLine());
        }
        drv = new WebBrowser().getBrowser();
        drv.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        String prodName = "";
        int prodSKU = 0;
        boolean inStock = false;
        double prodPrice = 0d;
        try {
            w = new FileWriter(outputPath, true);
        } catch (Exception ex) {
            ex.printStackTrace();
            drv.quit();
            System.exit(0);
        }
        for (int sku = start; sku < maxValue; sku++) {
            if (isValidSKU(sku)) {
                drv.get(baseURL + sku);
            } else {
                try {
                    prepareFile();
                    Files.append(sku + ",,,Not Available\n", new File(outputPath), Charset.defaultCharset());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                continue;
            }
            try {
                if (drv.getCurrentUrl().contains("redirectrule")) {
                    continue;
                }

                prodName = drv.findElement(By.xpath("//*[@id=\"skuspecial\"]/div[2]/h1")).getText().replaceAll("\\,", ".");
                prodSKU = sku;
                boolean retry = false;
                do { 
                    try {
                        prodPrice = Double.parseDouble(drv.findElement(By.className("finalPrice")).getText().replaceAll("[^0-9\\.]", ""));
                        retry = false;
                    } catch (Exception e) {
                        retry = true;
                    }
                } while (retry);

                if (drv.findElement(By.cssSelector("p[id='stockMessage']")).getAttribute("class").contains("hide")) {
                    inStock = true;
                } else {
                    inStock = false;
                }

            } catch (Exception e) {
                System.out.println("Scrape failed on SKU: " + sku);
                if (failureMap.get(sku) == null) {
                    failureMap.put(sku, 1);
                } else {
                    failureMap.put(sku, failureMap.get(sku) + 1);
                }
                if (failureMap.get(sku) <= 3) {
                    System.out.println("Sleeping 5 and Retrying...");
                    sku -= 1;
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                    }
                } else {
                    try {
                        Files.append(sku + ",,,Not Available\n", new File(outputPath), Charset.defaultCharset());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                continue;
            }
            prodArray.addProduct(prodSKU, prodName, prodPrice, inStock);
            List<String> result = prodArray.getProductOfIndex(prodArray.getProdCount() - 1);
            prepareFile();
            if (!result.isEmpty()) {
                String outputString = Arrays.toString(prodArray.getProductOfIndex(prodArray.getProdCount() - 1).toArray());
                System.out.println(outputString);
                outputString = outputString.substring(1, outputString.length() - 1).replaceAll("\\, ", ",");
                try {
                    Files.append(outputString + "\n", new File(outputPath), Charset.defaultCharset());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }

    }

    private static boolean isValidSKU(int sku) {
        System.out.println("Checking " + sku);
        try {
            URL test = new URL(baseURL + sku);
            HttpURLConnection httpcon = (HttpURLConnection) test.openConnection();
            InputStream is = httpcon.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String inputLine;

            while ((inputLine = rd.readLine()) != null) {
                if (inputLine.contains("Page not found.")) {
                    return false;
                }
            }
            rd.close();
            is.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return false;
        } catch (IOException ex) {
            System.out.println("Retry read");
            try {
                Thread.sleep(400);
            } catch (InterruptedException ex1) {
            }
            isValidSKU(sku);
            return false;
        }
        return true;

    }

    private static void prepareFile() {
        if (new File(outputPath).exists()) {
            if (new File(outputPath).length() == 0) {
                try {
                    Files.append("SKU,Product Name, Price, In Stock?\n", new File(outputPath), Charset.defaultCharset());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    

}
