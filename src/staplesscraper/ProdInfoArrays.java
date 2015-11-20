package staplesscraper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProdInfoArrays {

    private int addCounter = -1;
    private int prodCounter = -1;
    private String[] prodNames = new String[StaplesScraper.maxValue];
    private double[] prodPrices = new double[StaplesScraper.maxValue];
    private int[] prodSKUs = new int[StaplesScraper.maxValue];
    private boolean[] prodStockStatus = new boolean[StaplesScraper.maxValue];

    public ArrayList<String> productSKUToArray(int SKU) {
        int foundIndex = -1;
        for (int i = 0; i < StaplesScraper.maxValue; i++) {
            if (prodSKUs[i] == SKU) {
                foundIndex = i;
            }
        }
        if (foundIndex == -1) {
            return new ArrayList<String>();
        } else {
            return new ArrayList<String>(Arrays.asList(
                    new String[]{
                        Integer.toString(prodSKUs[foundIndex]),
                        prodNames[foundIndex],
                        Double.toString(prodPrices[foundIndex]),
                        boolToString(prodStockStatus[foundIndex])
                    }));
        }
    }

    public List<String> getNextProduct() {
        prodCounter++;
        if (prodSKUs[prodCounter] != 0) {
            return new ArrayList<String>(Arrays.asList(new String[]{Integer.toString(prodSKUs[prodCounter]), prodNames[prodCounter], Double.toString(prodPrices[prodCounter])}));
        } else {
            return new ArrayList<String>();
        }
    }

    public void resetProductCounter() {
        prodCounter = 0;
    }

    public void addProduct(int prodSKU, String prodName, double prodPrice, boolean inStock) {
        addCounter++;
        prodSKUs[addCounter] = prodSKU;
        prodNames[addCounter] = prodName;
        prodPrices[addCounter] = prodPrice;
        prodStockStatus[addCounter] = inStock;
    }

    public int getProdCount() {
        return (addCounter + 1);
    }

    public static String boolToString(boolean bool) {
        return bool ? "Yes" : "No";
    }

    public ArrayList<String> getProductOfIndex(int i) {
        return new ArrayList<String>(Arrays.asList(
                new String[]{
                    Integer.toString(prodSKUs[i]),
                    prodNames[i],
                    Double.toString(prodPrices[i]),
                    boolToString(prodStockStatus[i])
                }));
    }

}
