import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Seller Class
 * Handles backend seller user operations
 * Takes commands from Server class
 *
 * @author Brian Reagan, Justin Shakergayen, Yipeng Wang, Medha Yaragorla, Justin Yu
 * version 7/31/23
 */

/**
 * Methods, parameter, purpose
 * <p>
 * Seller(String userName, String password): create new seller constructor
 * <p>
 * Seller(String userName): constructor for logging into existing seller
 * skipping the usual get/set methods
 * <p>
 * getListingsByQuantity(): return a Listing arraylist of all listing sorted by quantity available (descending)
 * <p>
 * getListingsByPrice(): return a Listing arraylist of all listing sorted by price (ascending)
 * <p>
 * getAllStores(): return a String arraylist of all existing stores, with no duplicates
 * <p>
 * getTargetStore(String store): given a store name, returns a Listing arraylist of all existing listings with that
 * store name, throw ListingNotFoundException if none is found
 * <p>
 * getTargetName(String name): give a product name, returns a Listing arraylist of all existing listings with that
 * product name, throw ListingNotFoundException if none is found
 * <p>
 * getTargetDesc(String desc): given a product description, returns a Listing arraylist of all existing listings with
 * that product description, throw ListingNotFoundException if none is found
 * <p>
 * importListings(String importFileName): give an import file's name, import that file's listings into the current's
 * seller's profile, if the import file is not in the right format, throw Exception
 * <p>
 * addListing(String storeID, String name, String description, int quantityAvailable, double price):
 * given all parameters as names suggested, add that listing to the seller's existing listing.
 * returns an int,
 * if int == 2, the seller already has a listing with the same storeID and name as the given listing, if that
 * occurs, prompt user to modify the existing listing
 * if int == 1, listing added successfully
 * <p>
 * modifyListings(String storeID, String productName, int newQuantity, double newPrice, int option):
 * given all parameters as name suggested. if option == 1: modify quantity available, 2: modify price, 3: remove listing
 * throws ListingNotFoundException if given listing is not found
 * <p>
 * updateSales(String storeID, String productName, String buyerName, int sold, double sales):
 * given all parameters as name suggest, update the quantity available of the given listing, auto create/update sales
 * and customer log, throws ListingNotFoundException if given listing is not found, throws InvalidPurchaseException
 * if purchased more than available quantity
 * <p>
 * printCustomerHistory(String store)  and  printSalesHistory(String store):
 * given a store name, return an arraylist of String containing the customer/sales history of that store, throws
 * FileNotFoundException if the customer/sales log currently do not exist, throws ListingNotFoundException if the given
 * store does not exist
 * <p>
 * printSortedCustomerHistory(String store)  and  printSortedSalesHistory(String store):
 * given a store name, return an arraylist of String containing the customer/sales history of that store, sorted in
 * descending order, throws FileNotFoundException if the customer/sales log currently do not exist, throws
 * ListingNotFoundException if the given store does not exist
 */

public class Seller implements Serializable {
    private String userName;
    private String fileName;
    private String password;
    private ArrayList<Listing> listings;

    // Create a new Seller
    public Seller(String userName, String password) throws UserAlreadyExistException {
        this.userName = userName;
        this.password = password;
        this.fileName = "seller_" + userName + ".csv";
        this.listings = new ArrayList<>();

        File f = new File(this.fileName);
        if (f.exists()) {   // user tries to create a seller account that already exist
            throw new UserAlreadyExistException("Error, Seller with that Username already exists!");
        }
        try {
            f.createNewFile();
            PrintWriter pw = new PrintWriter(new FileOutputStream(f, true));
            pw.println(this.userName);
            pw.println(this.password);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    // Login to existing Seller
    public Seller(String userName) throws FileNotFoundException {
        String line, store, name, description;
        int quantityAvailable;
        double price;
        String[] tempArr;
        Listing tempListing;

        this.userName = userName;
        this.fileName = "seller_" + userName + ".csv";
        this.listings = new ArrayList<>();

        File f = new File(this.fileName);
        if (!f.exists()) {
            throw new FileNotFoundException("User not found!");
        }

        try {
            BufferedReader bfr = new BufferedReader(new FileReader(f));
            bfr.readLine();
            this.password = bfr.readLine();
            while ((line = bfr.readLine()) != null) {
                tempArr = line.split(",");
                store = tempArr[0].substring(tempArr[0].indexOf("=") + 1, tempArr[0].length());
                name = tempArr[1].substring(tempArr[1].indexOf("=") + 1, tempArr[1].length());
                description = tempArr[2].substring(tempArr[2].indexOf("=") + 1, tempArr[2].length());
                price = Double.parseDouble(tempArr[3].substring(tempArr[3].indexOf("=") + 1, tempArr[3].length()));
                quantityAvailable = Integer.parseInt(tempArr[4].substring(tempArr[4].indexOf("=") + 1,
                        tempArr[4].length() - 1));

                tempListing = new Listing(store, name, description, quantityAvailable, price);
                this.listings.add(tempListing);
            }
            bfr.close();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Listing> getListings() {
        return listings;
    }

    // return listings sorted by quantity in descending order
    public ArrayList<Listing> getListingsByQuantity() {
        ArrayList<Listing> sortedByQuantity = listings;
        Collections.sort(sortedByQuantity, new Comparator<Listing>() {
            @Override
            public int compare(Listing o1, Listing o2) {
                return Integer.compare(o2.getQuantityAvailable(), o1.getQuantityAvailable());
            }
        });

        return sortedByQuantity;
    }

    // return listings sorted by price in ascending order
    public ArrayList<Listing> getListingsByPrice() {
        ArrayList<Listing> sortedByPrice = listings;
        Collections.sort(sortedByPrice, new Comparator<Listing>() {
            @Override
            public int compare(Listing o1, Listing o2) {
                return Double.compare(o1.getPrice(), o2.getPrice());
            }
        });

        return sortedByPrice;
    }

    // returns all existing stores this seller has as a String arraylist
    public ArrayList<String> getAllStores() {
        ArrayList<String> allStore = new ArrayList<>();
        for (int i = 0; i < listings.size(); i++) {
            if (!allStore.contains(listings.get(i).getStoreID())) {
                allStore.add(listings.get(i).getStoreID());
            }
        }
        return allStore;
    }

    // pass in a targeted store string, returns an arraylist containing only listing from that store
    // throws ListingNotFoundException if no listing under given store name is found
    public ArrayList<Listing> getTargetStore(String store) throws ListingNotFoundException {
        ArrayList<Listing> foundStore = new ArrayList<>();
        for (int i = 0; i < listings.size(); i++) {
            if (listings.get(i).getStoreID().equals(store)) {
                foundStore.add(listings.get(i));
            }
        }
        if (foundStore.size() == 0) {
            throw new ListingNotFoundException("This seller has no listings under given store name!");
        }
        return foundStore;
    }

    // search by name
    public ArrayList<Listing> getTargetName(String name) throws ListingNotFoundException {
        ArrayList<Listing> foundStore = new ArrayList<>();
        for (int i = 0; i < listings.size(); i++) {
            if (listings.get(i).getProductName().equals(name)) {
                foundStore.add(listings.get(i));
            }
        }
        if (foundStore.size() == 0) {
            throw new ListingNotFoundException("This seller has no listings under given store name!");
        }
        return foundStore;
    }

    // search by desc
    public ArrayList<Listing> getTargetDesc(String desc) throws ListingNotFoundException {
        ArrayList<Listing> foundStore = new ArrayList<>();
        for (int i = 0; i < listings.size(); i++) {
            if (listings.get(i).getDescription().equals(desc)) {
                foundStore.add(listings.get(i));
            }
        }
        if (foundStore.size() == 0) {
            throw new ListingNotFoundException("This seller has no listings under given store name!");
        }
        return foundStore;
    }

    // search by both name and store
    public Listing getTargetNameAndStore(String storeID, String name) throws ListingNotFoundException {
        Listing found = null;
        for (int i = 0; i < listings.size(); i++) {
            if (listings.get(i).getStoreID().equals(storeID) && listings.get(i).getProductName().equals(name)) {
                found = listings.get(i);
                break;
            }
        }
        if (found == null) {
            throw new ListingNotFoundException("No listing found");
        }
        return found;
    }

    // import a listing file into the Seller csv (selection feature)
    // if Exception is thrown, the import file is not in the correct format
    public void importListings(String importFileName) throws Exception {
        String line, store, name, description;
        int qA;
        double price;
        String[] tempArr;
        Listing tempListing;
        ArrayList<Listing> tempListingArr = new ArrayList<>();

        File importF = new File(importFileName);
        File writeInF = new File(fileName);
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(importF));
            while ((line = bfr.readLine()) != null) {
                tempArr = line.split(",");
                store = tempArr[0].substring(tempArr[0].indexOf("=") + 1, tempArr[0].length());
                name = tempArr[1].substring(tempArr[1].indexOf("=") + 1, tempArr[1].length());
                description = tempArr[2].substring(tempArr[2].indexOf("=") + 1, tempArr[2].length());
                price = Double.parseDouble(tempArr[3].substring(tempArr[3].indexOf("=") + 1, tempArr[3].length()));
                qA = Integer.parseInt(tempArr[4].substring(tempArr[4].indexOf("=") + 1, tempArr[4].length() - 1));
                tempListing = new Listing(store, name, description, qA, price);
                tempListingArr.add(tempListing);
            }
            bfr.close();
            this.listings = tempListingArr;

            PrintWriter pw = new PrintWriter(new FileOutputStream(writeInF, false));
            pw.println(userName);
            pw.println(password);
            for (int i = 0; i < listings.size(); i++) {
                pw.println(listings.get(i).toString());
            }
            pw.close();
        } catch (Exception e) {
            throw new Exception("Imported file is not in the right format!");
        }
    }

    // seller add listings, listings auto added to local ArrayList
    // returns an int: 2 = listing of the same name exist, prompt user to modify
    //                 1 = no special case occurred, added successfully
    public int addListing(String storeID, String name, String description, int quantityAvailable, double price) {
        Listing listing = new Listing(storeID, name, description, quantityAvailable, price);
        for (int i = 0; i < listings.size(); i++) {
            if ((listings.get(i).getStoreID().equals(storeID) && listings.get(i).getProductName().equals(name))) {
                return (2);
            }
        }

        listings.add(listing);
        File f = new File(fileName);
        try {
            FileOutputStream fos = new FileOutputStream(f, true);
            PrintWriter pw = new PrintWriter(fos);
            pw.println(listing.toString());
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (1);
    }

    // modify listings, "option" is a parameter
    // if option = 1: modify quantity available,  2: modify price,  3: remove listing
    public void modifyListings(String storeID, String productName, int newQuantity, double newPrice, int option)
            throws ListingNotFoundException {
        int target = -1;

        for (int i = 0; i < listings.size(); i++) {
            if (listings.get(i).getStoreID().equalsIgnoreCase(storeID) &&
                    listings.get(i).getProductName().equalsIgnoreCase(productName)) {
                target = i;
                break;
            }
        }

        if (target == -1) {
            throw new ListingNotFoundException("Boo, listing not found (This error should never show up) " +
                    "If you got this far without a listing, good on you.");
        }

        if (option == 1) {  // pass in option == 1: modify quantity available
            listings.get(target).setQuantityAvailable(newQuantity);
        } else if (option == 2) {   // pass in option == 2: modify listing price
            listings.get(target).setPrice(newPrice);
        } else {    // pass in option == 3: remove a listing
            listings.remove(target);
        }

        File f = new File(fileName);
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(f, false));
            pw.println(userName);
            pw.println(password);
            for (int i = 0; i < listings.size(); i++) {
                pw.println(listings.get(i).toString());
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // update listing's quantityAvailable, generates sales logs
    // takes in storeID, productName, buyerName (found using currentBuyer's getUsername() method) and unitsSold,
    // returns a salesGenerated as a double
    public void updateSales(String storeID, String productName, String buyerName,
                            int sold, double sales) throws ListingNotFoundException, InvalidPurchaseException {
        int target = -1;
        for (int i = 0; i < listings.size(); i++) {
            if (listings.get(i).getStoreID().equalsIgnoreCase(storeID) &&
                    listings.get(i).getProductName().equalsIgnoreCase(productName)) {
                target = i;
                break;
            }
        }
        if (target == -1) {
            throw new ListingNotFoundException("Listing not found!");
        }
        int temp = listings.get(target).getQuantityAvailable() - sold;
        if (temp < 0) {
            throw new InvalidPurchaseException("Purchased more than available stock!");
        } else {
            listings.get(target).setQuantityAvailable(temp);
        }

        if (listings.get(target).getQuantityAvailable() == 0) {
            listings.remove(target);
        }

        File f = new File(fileName);
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(f, false));
            pw.println(userName);
            pw.println(password);
            for (int i = 0; i < listings.size(); i++) {
                pw.println(listings.get(i).toString());
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // everytime sales is updated and this class is called, auto creates:
        // 1. a list of customers with the number of items that they have purchased
        // 2. a list of products with the number of sales.
        String line;
        String[] tempArr;
        CustomerHistory tempCH;
        SalesHistory tempSH;
        boolean exist = false;
        ArrayList<CustomerHistory> customerHistoryArr = new ArrayList<>();
        ArrayList<SalesHistory> salesHistoryArr = new ArrayList<>();

        File ch = new File("seller_" + userName + "CH.csv");
        File sh = new File("seller_" + userName + "SH.csv");
        try {
            if (!ch.exists()) {
                ch.createNewFile();
            }
            if (!sh.exists()) {
                sh.createNewFile();
            }
            // customerHistory
            BufferedReader customerReader = new BufferedReader(new FileReader(ch));
            while ((line = customerReader.readLine()) != null) {
                tempArr = line.split(",");
                tempCH = new CustomerHistory(tempArr[0].substring(tempArr[0].indexOf(":") + 1, tempArr[0].length()),
                        tempArr[1].substring(tempArr[1].indexOf(":") + 1, tempArr[1].length()),
                        Integer.parseInt(tempArr[2].substring(tempArr[2].indexOf(":") + 1, tempArr[2].length())));
                customerHistoryArr.add(tempCH);
            }
            customerReader.close();
            for (int i = 0; i < customerHistoryArr.size(); i++) {
                if (customerHistoryArr.get(i).getBuyer().equals(buyerName) &&
                        customerHistoryArr.get(i).getStore().equals(storeID)) {
                    customerHistoryArr.get(i).setUnitsBought(customerHistoryArr.get(i).getUnitsBought() + sold);
                    exist = true;
                    break;
                }
            }
            if (exist == false) {
                customerHistoryArr.add(new CustomerHistory(storeID, buyerName, sold));
            }
            exist = false;
            PrintWriter custWriter = new PrintWriter(new FileOutputStream(ch, false));
            for (int i = 0; i < customerHistoryArr.size(); i++) {
                custWriter.println(customerHistoryArr.get(i));
            }
            custWriter.close();

            // salesHistory
            BufferedReader salesReader = new BufferedReader(new FileReader(sh));
            while ((line = salesReader.readLine()) != null) {
                tempArr = line.split(",");
                tempSH = new SalesHistory(tempArr[0].substring(tempArr[0].indexOf(":") + 1, tempArr[0].length()),
                        tempArr[1].substring(tempArr[1].indexOf(":") + 1, tempArr[1].length()),
                        Double.parseDouble(tempArr[2].substring(tempArr[2].indexOf(":") + 1, tempArr[2].length())));
                salesHistoryArr.add(tempSH);
            }
            salesReader.close();
            for (int i = 0; i < salesHistoryArr.size(); i++) {
                if (salesHistoryArr.get(i).getProduct().equals(productName) &&
                        salesHistoryArr.get(i).getStore().equals(storeID)) {
                    salesHistoryArr.get(i).setSales(salesHistoryArr.get(i).getSales() + sales);
                    exist = true;
                    break;
                }
            }
            if (exist == false) {
                salesHistoryArr.add(new SalesHistory(storeID, productName, sales));
            }
            exist = false;
            PrintWriter salesWriter = new PrintWriter(new FileOutputStream(sh, false));
            for (int i = 0; i < salesHistoryArr.size(); i++) {
                salesWriter.println(salesHistoryArr.get(i));
            }
            salesWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // given a store, print a list of customers with the number of items that they have purchased within that store
    public String printCustomerHistory(String store) throws FileNotFoundException, ListingNotFoundException {
        String line;
        String[] tempArr;
        String customerHistory = new String();
        int control = 0;
        File f = new File("seller_" + userName + "CH.csv");
        if (!f.exists()) {
            throw new FileNotFoundException("No sales has been made by this seller yet!");
        }
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(f));
            while ((line = bfr.readLine()) != null) {
                tempArr = line.split(",");
                if (tempArr[0].substring(tempArr[0].indexOf(":") + 1, tempArr[0].length()).equals(store)) {
                    customerHistory += line + "\n";
                    control++;
                }
            }
            if (control == 0) {
                throw new ListingNotFoundException("Error! Store not found!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return customerHistory;
    }

    // given a store, print a sorted Seller dashboard statics for customer's buying history within that store
    // (selection feature)
    public String printSortedCustomerHistory(String store) throws ListingNotFoundException, FileNotFoundException {
        String line;
        String[] tempArr;
        CustomerHistory tempCH = null;
        ArrayList<CustomerHistory> customerHistoriesArr = new ArrayList<>();
        String sortedCustomerHistory = new String();
        File f = new File("seller_" + userName + "CH.csv");
        if (!f.exists()) {
            throw new FileNotFoundException("No sales has been made by this seller yet!");
        }
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(f));
            while ((line = bfr.readLine()) != null) {
                tempArr = line.split(",");
                if (tempArr[0].substring(tempArr[0].indexOf(":") + 1, tempArr[0].length()).equals(store)) {
                    tempCH = new CustomerHistory(tempArr[0].substring(tempArr[0].indexOf(":") + 1, tempArr[0].length()),
                            tempArr[1].substring(tempArr[1].indexOf(":") + 1, tempArr[1].length()),
                            Integer.parseInt(tempArr[2].substring(tempArr[2].indexOf(":") + 1, tempArr[2].length())));
                    customerHistoriesArr.add(tempCH);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (customerHistoriesArr.size() == 0) {
            throw new ListingNotFoundException("Error! Listing not found!");
        }
        Collections.sort(customerHistoriesArr, new Comparator<CustomerHistory>() {
            @Override
            public int compare(CustomerHistory o1, CustomerHistory o2) {
                return Integer.compare(o2.getUnitsBought(), o1.getUnitsBought());    // descending sort
            }
        });
        for (int i = 0; i < customerHistoriesArr.size(); i++) {
            sortedCustomerHistory += (customerHistoriesArr.get(i).toString()) + "\n";
        }
        return sortedCustomerHistory;
    }

    // given a store, print a list of sales by product within that store
    public String printSalesHistory(String store) throws FileNotFoundException, ListingNotFoundException {
        String line;
        String[] tempArr;
        String salesHistory = new String();
        int control = 0;
        File f = new File("seller_" + userName + "SH.csv");
        if (!f.exists()) {
            throw new FileNotFoundException("No sales has been made by this seller yet!");
        }
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(f));
            while ((line = bfr.readLine()) != null) {
                tempArr = line.split(",");
                if (tempArr[0].substring(tempArr[0].indexOf(":") + 1, tempArr[0].length()).equals(store)) {
                    salesHistory += (line) + "\n";
                    control++;
                }
            }
            if (control == 0) {
                throw new ListingNotFoundException("Error! Store not found!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return salesHistory;
    }

    // given a store, print a sorted Seller dashboard statics for sales history within that store (selection feature)
    public String printSortedSalesHistory(String store) throws ListingNotFoundException, FileNotFoundException {
        String line;
        String[] tempArr;
        SalesHistory tempSH;
        ArrayList<SalesHistory> salesHistoriesArr = new ArrayList<>();
        String sortedSalesHistory = new String();
        File f = new File("seller_" + userName + "SH.csv");
        if (!f.exists()) {
            throw new FileNotFoundException("No sales has been made by this seller yet!");
        }
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(f));
            while ((line = bfr.readLine()) != null) {
                tempArr = line.split(",");
                if (tempArr[0].substring(tempArr[0].indexOf(":") + 1, tempArr[0].length()).equals(store)) {
                    tempSH = new SalesHistory(tempArr[0].substring(tempArr[0].indexOf(":") + 1, tempArr[0].length()),
                            tempArr[1].substring(tempArr[1].indexOf(":") + 1, tempArr[1].length()),
                            Double.parseDouble(tempArr[2].substring(tempArr[2].indexOf(":") + 1, tempArr[2].length())));
                    salesHistoriesArr.add(tempSH);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (salesHistoriesArr.size() == 0) {
            throw new ListingNotFoundException("Error! Store not found!");
        }

        Collections.sort(salesHistoriesArr, new Comparator<SalesHistory>() {
            @Override
            public int compare(SalesHistory o1, SalesHistory o2) {
                return Double.compare(o2.getSales(), o1.getSales());    // descending sort
            }
        });
        for (int i = 0; i < salesHistoriesArr.size(); i++) {
            sortedSalesHistory += (salesHistoriesArr.get(i).toString()) + "\n";
        }
        return sortedSalesHistory;
    }


    // prints a list of all stores this seller have and their quantity sold for customer to see
    // if sorting is wanted, pass in a boolean as "true" then the list can be sorted by sales in descending order
    public ArrayList<CustomerHistory> storesBySold(boolean sort) throws FileNotFoundException,
            ListingNotFoundException {
        String line;
        String[] tempArr;
        ArrayList<CustomerHistory> storesBySold = new ArrayList<>();
        String storesBySoldString = new String();
        File f = new File("seller_" + userName + "CH.csv");
        if (!f.exists()) {
            throw new FileNotFoundException("No sales has been made by this seller yet!");
        }
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(f));
            while ((line = bfr.readLine()) != null) {
                tempArr = line.split(",");
                storesBySold.add(new CustomerHistory(tempArr[0].substring(tempArr[0].indexOf(":") + 1,
                        tempArr[0].length()),
                        Integer.parseInt(tempArr[2].substring(tempArr[2].indexOf(":") + 1, tempArr[2].length()))));
            }
            if (storesBySold.size() == 0) {
                throw new ListingNotFoundException("This store has not made any sales yet!");
            }
            if (sort == true) {
                Collections.sort(storesBySold, new Comparator<CustomerHistory>() {
                    @Override
                    public int compare(CustomerHistory o1, CustomerHistory o2) {
                        return Integer.compare(o2.getUnitsBought(), o1.getUnitsBought());   // descending sort
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (storesBySold);
    }
}