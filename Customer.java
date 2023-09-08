import java.util.*;
import java.io.*;
/**
 * Customer Class
 * Handles backend customer user operations
 * Takes commands from Server class
 *
 * @author Brian Reagan, Justin Shakergayen, Yipeng Wang, Medha Yaragorla, Justin Yu
 * version 7/31/23
 */

/**
 * Methods, parameter, purpose
 *
 * Customer(String username, String password): constructor for new customer
 *
 * Customer(String username, String password, String fileName, String cartName):  constructor for existing customer
 *
 * addListing(Listing item): adds items to the cart, and updates it accordingly
 *
 * removeListing(Listing item): removes item from the cart based on the item
 *
 * removeListingByIndex(int i): removes item from the cart based on its location
 *
 * printCart(): returns the items in the arrayList cart including each item's store id, name, seller, price, and
 *              quantity
 *
 * writeCart(String fileName): writes down the items in cart into the customer's csv file
 *
 * readCart(String fileName): reads from the csv file and fills the arrayList cart
 *
 * sortByPrice(String fileName): sorts the cart by price and writes it to the customer csv file in ascending order
 *
 * sortByQuantity(String fileName): sorts the cart by quantity and writes it to the customer csv file in
 *                                  ascending order
 *
 * sortByPurchased (boolean sort): sorts the purchased arrayList by number of items bought in ascending order
 *
 * searchByName(String name, ArrayList<Listing> list): searches the arrayList for any items that have the name
 *                                                     as the given parameter and returns it
 *
 * searchByStore(String storeID, ArrayList<Listing> list): searches the arrayList for any items that have the storeID
 *                                                         as the given parameter and returns it
 *
 * writePurchaseHistory(): writes down the items in purchased into the customer's csv file
 *
 * exportPurchase(String exportFileName): exports the purchased items into a new csv file
 */
 public class Customer implements Serializable {
    private ArrayList<Listing> cart;
    private ArrayList<Listing> purchased;
    private String username;
    private String password;
    private String fileName;

    // Constructor for new account
    public Customer (String username, String password) throws UserAlreadyExistException, IOException {
        this.username = username;
        this.password = password;
        fileName = "customer_" + username + ".csv";
        File file = new File(fileName);
        File cartfile = new File(username + "_cart.csv");
        cartfile.createNewFile();
        if (file.exists()) {
            throw new UserAlreadyExistException("User with username " + username + " already exists");
        }

        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            PrintWriter pw = new PrintWriter(fos);
            pw.println(username);
            pw.println(password);
            pw.close();
        } catch (IOException e) {
            throw new IOException("Cannot create file");
        }

        cart = new ArrayList<Listing>();
        purchased = new ArrayList<Listing>();
    }

    // Constructor for existing account
    public Customer(String username, String password, String fileName, String cartName) throws FileNotFoundException {
        this.username = username;
        this.fileName = fileName;

        cart = new ArrayList<Listing>();  // Initialize cart to an empty array list

        File file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException("The file " + fileName + " was not found.");
        }

        cart = readCart(cartName);
        purchased = new ArrayList<Listing>();
        Listing temp;
        String line;
        String[] tempArr;
        String storeID;
        String name;
        String seller;
        double price;
        int quantityBought;
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(file));
            bfr.readLine();
            this.password = bfr.readLine();
            while((line = bfr.readLine()) != null) {
                if (line.contains(":")) {
                    continue;
                }
                tempArr = line.split(",");
                storeID = tempArr[0].substring(tempArr[0].indexOf("=") + 1);
                name = tempArr[1].substring(tempArr[1].indexOf("=") + 1);
                seller = tempArr[2].substring(tempArr[2].indexOf("=") + 1);
                price = Double.parseDouble(tempArr[3].substring(tempArr[3].indexOf("=") + 1));
                quantityBought = Integer.parseInt(tempArr[4].substring(tempArr[4].indexOf("=") + 1,
                        tempArr[4].length()).trim());

                temp = new Listing(storeID, name, seller, quantityBought, price, true);
                purchased.add(temp);
            }
            bfr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // accesses the username
    public String getUsername() {
        return username;
    }

    // changes the username
    public void setUsername(String username) {
        this.username = username;
    }

    // accesses the password
    public String getPassword() {
        return password;
    }

    // changes the password
    public void setPassword(String password) {
        this.password = password;
    }

    // accesses the cart
    public ArrayList<Listing> getCart() {
        return cart;
    }

    public void setCart(ArrayList<Listing> cart) {
        this.cart = cart;
    }

    public ArrayList<Listing> getPurchased() {
        return purchased;
    }

    // adds items to the cart
    public void addListing(Listing item) {
        int target = -1;
        if (cart.size() != 0) {
            for (int i = 0; i < cart.size(); i++) {
                if (cart.get(i).getStoreID().equals(item.getStoreID()) &&
                        cart.get(i).getProductName().equals(item.getProductName())) {
                    target = i;
                    break;
                }
            }
            if (target != -1) {
                cart.get(target).setQuantityBought(cart.get(target).getQuantityBought() + item.getQuantityBought());
            } else {
                cart.add(item);
            }
        } else {
            cart.add(item);
        }
        writeCart(username + "_cart.csv");
    }

    // removes items from the cart
    public void removeListing(Listing item) {
        cart.remove(item);
    }

    public void removeListingByIndex(int i) throws IndexOutOfBoundsException {
        try {
            cart.remove(i);
            writeCart(username + "_cart.csv");
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException();
        }
    }

    // prints the items in the cart
    public String printCart() {
        int i = 1;
        String output = "";
        Listing item;
        output += "   |Store ID    |Name         |Seller                 |Price     |Quantity   |\n";
        output += "--------------------------------------------------------------------------------\n";
        for (Listing listing : cart) {
            item = listing;
            output += String.format("%d  |%-12s|%-13s|%-23s|%-10.2f|%-11d|\n", i, item.getStoreID(),
                    item.getProductName(), item.getSeller(), item.getPrice(), item.getQuantityBought());
            i++;
        }
        output += "--------------------------------------------------------------------------------\n";

        return output;
    }

    // write list to file
    public void writeCart(String fileName) {
        try {
            File file = new File(fileName);
            FileOutputStream fos = new FileOutputStream(file, false);;
            PrintWriter pw = new PrintWriter(fos);
            String output = "";
            Listing item;
            for (Listing listing : cart) {
                item = listing;
                output += "Listing [storeID=" + item.getStoreID() + ", productName=" + item.getProductName();
                output += ", seller=" + item.getSeller() + ", price=" + item.getPrice();
                output += ", quantity=" + item.getQuantityBought() + ", purchased=";

                if (item.getPurchased()) {
                    output += "true";
                } else {
                    output += "false";
                }
                pw.println(output);
                output = new String();
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // read file to fill cart
    public ArrayList<Listing> readCart(String fileName) {
        try {
            File file = new File(fileName);
            FileReader fr = new FileReader(file);
            BufferedReader bfr = new BufferedReader(fr);

            Listing temp;
            String line;
            String[] tempArr;
            String storeID;
            String name;
            String seller;
            double price;
            int quantityBought;
            boolean buy;

            while((line = bfr.readLine()) != null) {
                if (line.contains(":")) {
                    continue;
                }
                tempArr = line.split(",");
                storeID = tempArr[0].substring(tempArr[0].indexOf("=") + 1);
                name = tempArr[1].substring(tempArr[1].indexOf("=") + 1);
                seller = tempArr[2].substring(tempArr[2].indexOf("=") + 1);
                price = Double.parseDouble(tempArr[3].substring(tempArr[3].indexOf("=") + 1));
                quantityBought = Integer.parseInt(tempArr[4].substring(tempArr[4].indexOf("=") + 1,
                        tempArr[4].length()).trim());
                buy = Boolean.parseBoolean(tempArr[5].substring(tempArr[5].indexOf("=") + 1));

                temp = new Listing(storeID, name, seller, quantityBought, price, buy);
                cart.add(temp);
            }
            bfr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cart;
    }

    // sort list by price
    public void sortByPrice(String fileName) {
        ArrayList<Listing> list = readCart(fileName);

        Collections.sort(list, new Comparator<Listing>() {
            @Override
            public int compare(Listing o1, Listing o2) {
                return Double.valueOf(o1.getPrice()).compareTo(o2.getPrice());
            }
        });

        String priceSortFile = "customer_" + username + "_price_sorted_purchases.csv";
        writeCart(priceSortFile);
    }

    // sort list by quantity
    public void sortByQuantity(String fileName) {
        ArrayList<Listing> list = readCart(fileName);

        Collections.sort(list, new Comparator<Listing>() {
            @Override
            public int compare(Listing o1, Listing o2) {
                return Integer.valueOf(o1.getQuantityBought()).compareTo(o2.getQuantityBought());
            }
        });

        String quantitySortFile = "customer_" + username + "_quantity_sorted_purchases.csv";
        writeCart(quantitySortFile);
    }

    // search products by name
    public Listing searchByName(String name, ArrayList<Listing> list) {
        for (Listing listing : list) {
            if (listing.getProductName().equalsIgnoreCase(name)) {
                return listing;
            }
        }
        return null;
    }

    // search products by store
    public ArrayList<Listing> searchByStore(String storeID, ArrayList<Listing> list) {
        ArrayList<Listing> storeList = new ArrayList<Listing>();
        for (Listing listing : list) {
            if (listing.getStoreID().equalsIgnoreCase(storeID)) {
                storeList.add(listing);
            }
        }
        return storeList;
    }

    public void writePurchaseHistory() {
        // read existing purchase history
        String line;
        String[] tempArr;
        ArrayList<Listing> existingPurchased = new ArrayList<>();
        ArrayList<Integer> targets = new ArrayList<>();
        ArrayList<Integer> spots = new ArrayList<>();
        File f = new File(fileName);
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(f));
            bfr.readLine();
            bfr.readLine();
            while((line = bfr.readLine()) != null) {
                tempArr = line.split(",");
                existingPurchased.add(new Listing(tempArr[0].substring(tempArr[0].indexOf("=") + 1,
                        tempArr[0].length()),
                        tempArr[1].substring(tempArr[1].indexOf("=") + 1, tempArr[1].length()),
                        tempArr[2].substring(tempArr[2].indexOf("=") + 1, tempArr[2].length()),
                        Integer.parseInt(tempArr[4].substring(tempArr[4].indexOf("=") + 1, tempArr[4].length())),
                        Double.parseDouble(tempArr[3].substring(tempArr[3].indexOf("=") + 1, tempArr[3].length())),
                        true));
            }
            bfr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // add all cart items to purchased array
        purchased = cart;
        // empty cart array
        cart = new ArrayList<>();

        // update purchased array based on existingPurchased array
        if (existingPurchased.size() > 0) {
            for (int i = 0; i < purchased.size(); i++) {
                for (int x = 0; x < existingPurchased.size(); x++) {
                    if (purchased.get(i).getStoreID().equals(existingPurchased.get(x).getStoreID()) &&
                            purchased.get(i).getProductName().equals(existingPurchased.get(x).getProductName()) &&
                            purchased.get(i).getSeller().equals(existingPurchased.get(x).getSeller())) {
                        targets.add(x);
                        spots.add(i);
                        break;
                    }
                }
            }

            if (targets.size() > 0) {
                for (int i = 0; i < targets.size(); i++) {
                    existingPurchased.get(targets.get(i)).setQuantityBought(existingPurchased.get(targets.get(i)).
                            getQuantityBought() + purchased.get(spots.get(i)).getQuantityBought());
                }
            } else {
                for (int i = 0; i < purchased.size(); i++) {
                    existingPurchased.add(purchased.get(i));
                }
            }
            purchased = existingPurchased;
        }

        String output = new String();
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(f, false));
            pw.println(username);
            pw.println(password);
            for (int i = 0; i < purchased.size(); i++) {
                output += "Listing [storeID=" + purchased.get(i).getStoreID() + ", productName=" +
                        purchased.get(i).getProductName();
                output += ", seller=" + purchased.get(i).getSeller();
                output += String.format(", price=%.2f",purchased.get(i).getPrice());
                output += ", quantity=" + purchased.get(i).getQuantityBought();
                pw.println(output);
                output = new String();
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Listing> sortByPurchased (boolean sort) {
        ArrayList<Listing> tempArr = purchased;
        if (sort == true) {
            Collections.sort(tempArr, new Comparator<Listing>() {
                @Override
                public int compare(Listing o1, Listing o2) {
                    return Integer.compare(o2.getQuantityBought(), o1.getQuantityBought());
                }
            });
        }
        return tempArr;
    }

    // export purchased items (selection)
    public void exportPurchase(String exportFileName) {
        try {
            File wFile = new File(exportFileName);
            if (!wFile.exists()) {
                wFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(wFile, false);
            PrintWriter pw = new PrintWriter(fos);

            File rFile = new File(fileName);
            FileReader fr = new FileReader(rFile);
            BufferedReader bfr = new BufferedReader(fr);
            String output = "";

            String line;

            bfr.readLine();
            bfr.readLine();
            while((line = bfr.readLine()) != null) {
                pw.println(line);
            }
            bfr.close();
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
