import java.io.Serializable;

/**
 * Listing Class
 * Handles backend Listings object operations
 *
 * @author Brian Reagan, Justin Shakergayen, Yipeng Wang, Medha Yaragorla, Justin Yu
 * version 7/31/23
 */
public class Listing implements Serializable {
    private String storeID;
    private String productName;
    private String description;
    private double price;
    private int quantityBought;
    private int quantityAvailable;
    private boolean purchased;
    private int quantityInCart;
    private int quantityInAllCarts;
    private String seller;

    public Listing(String storeID, String productName, String description, int quantityAvailable, double price) {
        this.storeID = storeID;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.quantityAvailable = quantityAvailable;
    }

    public Listing(String storeID, String productName, String seller, int quantityBought, double price,
                   boolean purchased) {
        this.storeID = storeID;
        this.productName = productName;
        this.seller = seller;
        this.price = price;
        this.quantityBought = quantityBought;
        this.purchased = purchased;
    }

    public int getQuantityInCart() {
        return quantityInCart;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    // Setter for quantityInCart
    public void setQuantityInCart(int quantityInCart) {
        if (quantityInCart < 0) {
            throw new IllegalArgumentException("Quantity in cart cannot be less than zero");
        }
        this.quantityInCart = quantityInCart;
    }

    // Getter for quantityInAllCarts
    public int getQuantityInAllCarts() {
        return quantityInAllCarts;
    }

    // Setter for quantityInAllCarts
    public void setQuantityInAllCarts(int quantityInAllCarts) {
        if (quantityInAllCarts < 0) {
            throw new IllegalArgumentException("Quantity in all carts cannot be less than zero");
        }
        this.quantityInAllCarts = quantityInAllCarts;
    }

    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public int getQuantityBought() {
        return quantityBought;
    }

    public void setQuantityBought(int quantityBought) {
        this.quantityBought = quantityBought;
    }

    public boolean getPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    @Override
    public String toString() {
        return String.format("Listing [StoreID=%s, Product_Name=%s, Description=%s, Price=%.2f, Quantity_Available=%s]",
                storeID, productName, description, price, quantityAvailable);
    }
}
