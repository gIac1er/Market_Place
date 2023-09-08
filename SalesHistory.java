import java.io.Serializable;

/**
 * SalesHistory Class
 * Used to create sales history for Seller class
 *
 * @author Brian Reagan, Justin Shakergayen, Yipeng Wang, Medha Yaragorla, Justin Yu
 * version 7/31/23
 */
public class SalesHistory implements Serializable {
    private String store;
    private String product;
    private double sales;

    public SalesHistory(String store, String product, double sales) {
        this.store = store;
        this.product = product;
        this.sales = sales;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public double getSales() {
        return sales;
    }

    public void setSales(double sales) {
        this.sales = sales;
    }


    @Override
    public String toString() {
        return String.format("Store:%s, Product:%s, Sales_Made:%.2f", store, product, sales);
    }
}
