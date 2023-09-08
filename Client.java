import java.util.ArrayList;
import java.io.*;
import javax.swing.*;
import java.net.*;

/**
 * Client Class
 * Run this class if you are a client!
 * If the listings isn't up-to-date/is inconsistent, the update your page option should fix it!
 *
 * @author Brian Reagan, Justin Shakergayen, Yipeng Wang, Medha Yaragorla, Justin Yu
 * version 7/31/23
 */

public class Client implements Serializable {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int choice;
        boolean running = true;
        boolean runSeller = false;
        boolean runBuyer = false;
        String typedUsername = "";
        String typedPassword = "";

        Socket socket = new Socket("localhost", 4242);
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

        while (running) {
            String[] loginOptions = {"Create an Account", "Login", "Exit"};
            choice = JOptionPane.showOptionDialog(null, "Welcome to MegaChonk Market!\n" +
                            "What would you like to do?", "Client", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, loginOptions, loginOptions[0]);

            // create new account
            if (choice == 0) {
                JOptionPane.showMessageDialog(null,
                        "We're going to need some information to get started.\n", "Account Creator",
                        JOptionPane.PLAIN_MESSAGE);
                do {
                    typedUsername = JOptionPane.showInputDialog(null, "My Username will be: ",
                            "Account Creator", JOptionPane.QUESTION_MESSAGE);
                    if (typedUsername == null)
                        break;
                    if (typedUsername.isBlank()) {
                        JOptionPane.showMessageDialog(null, "Please enter a valid username",
                                "Account Creator", JOptionPane.ERROR_MESSAGE);
                    } else if (!typedUsername.contains("@purdue.edu")) {
                        JOptionPane.showMessageDialog(null,
                                "Please use a Purdue email as your username!", "Account Creator",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } while (typedUsername.isBlank() || !typedUsername.contains("@purdue.edu"));

                if (typedUsername != null) {
                    do {
                        typedPassword = JOptionPane.showInputDialog(null,
                                "My Password will be: ", "Account Creator",
                                JOptionPane.QUESTION_MESSAGE);
                        if (typedPassword == null)
                            break;
                        if (typedPassword.equals(" "))
                            JOptionPane.showMessageDialog(null, "Please enter a valid password",
                                    "Account Creator", JOptionPane.ERROR_MESSAGE);
                    } while (typedPassword.equals(""));
                }

                if (typedUsername != null && typedPassword != null) {
                    String[] roleOptions = {"Buy", "Sell"};
                    String role = new String();
                    int roleChoice = JOptionPane.showOptionDialog(null, "Are you here to...",
                            "Account Creator", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                            roleOptions, roleOptions[0]);

                    ArrayList<String> createAccount = new ArrayList<>();
                    if (roleChoice == 0) {
                        createAccount.add("initializeNewBuyer");
                    } else {
                        createAccount.add("initializeNewSeller");
                    }
                    createAccount.add(typedUsername);
                    createAccount.add(typedPassword);
                    outputStream.writeObject(createAccount);
                    outputStream.flush();
                    Object temp = inputStream.readObject();
                    if (temp instanceof UserAlreadyExistException) {
                        JOptionPane.showMessageDialog(null, "Error, username taken!",
                                "Account Creator", JOptionPane.ERROR_MESSAGE);
                    } else if (temp instanceof IOException || temp instanceof Exception) {
                        JOptionPane.showMessageDialog(null, "Error, cannot create account!",
                                "Account Creator", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Account Creation Successful.",
                                "Account Creator", JOptionPane.PLAIN_MESSAGE);
                        if (roleChoice == 0) {
                            runBuyer = true;
                        } else {
                            runSeller = true;
                        }
                    }
                }

                // login
            } else if (choice == 1) {
                String password = new String();
                boolean checkedIn = false;
                boolean userExist = false;
                int loginChoice = 0;
                // Seller loginSeller = null;
                // Customer loginCustomer = null;
                int i = 0;
                while (checkedIn == false) {
                    String[] loginginOption = {"Seller", "Customer", "Go Back"};
                    loginChoice = JOptionPane.showOptionDialog(null,
                            "Do you have a Seller or Customer account?\n", "Client",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                            loginginOption, loginginOption[0]);
                    if (loginChoice == 2)
                        break;
                    typedUsername = JOptionPane.showInputDialog(null, "Username: ", "Client"
                            , JOptionPane.QUESTION_MESSAGE);
                    if (typedUsername == null)
                        break;
                    if (loginChoice == 0) { // seller login get password
                        ArrayList<String> sellerLogin = new ArrayList<>();
                        sellerLogin.add("getSellerPassword");
                        sellerLogin.add("seller_" + typedUsername + ".csv");
                        outputStream.writeObject(sellerLogin);
                        outputStream.flush();
                        Object temp = inputStream.readObject();
                        if (temp instanceof FileNotFoundException) {
                            JOptionPane.showMessageDialog(null, "Error, user not found",
                                    "Client", JOptionPane.ERROR_MESSAGE);
                        } else if (temp instanceof Exception) {
                            JOptionPane.showMessageDialog(null, "Error on log-in!",
                                    "Client", JOptionPane.ERROR_MESSAGE);
                        } else {
                            password = (String) temp;
                            userExist = true;
                        }
                    } else {    // buyer login get password
                        ArrayList<String> buyerLogin = new ArrayList<>();
                        buyerLogin.add("getBuyerPassword");
                        buyerLogin.add("customer_" + typedUsername + ".csv");
                        outputStream.writeObject(buyerLogin);
                        outputStream.flush();
                        Object temp = inputStream.readObject();
                        if (temp instanceof FileNotFoundException) {
                            JOptionPane.showMessageDialog(null, "Error, user not found",
                                    "Client", JOptionPane.ERROR_MESSAGE);
                        } else if (temp instanceof Exception) {
                            JOptionPane.showMessageDialog(null, "Error on log-in!",
                                    "Client", JOptionPane.ERROR_MESSAGE);
                        } else {
                            password = (String) temp;
                            userExist = true;
                        }
                    }
                    if (userExist) {
                        typedPassword = JOptionPane.showInputDialog(null, "Password: ",
                                "Client", JOptionPane.QUESTION_MESSAGE);
                        if (typedPassword == null)
                            break;

                        for (i = 0; i < 5; i++) {
                            if (typedPassword == null) {
                                userExist = false;
                                break;
                            }
                            if (typedPassword.equals(password)) {
                                checkedIn = true;
                                break;
                            } else {
                                typedPassword = JOptionPane.showInputDialog(null,
                                        String.format("Wrong password! %d attempts left\n", 5 - i), "Client"
                                        , JOptionPane.QUESTION_MESSAGE);
                            }
                        }
                        if (i == 5) {
                            if (typedPassword.equals(password)) {
                                checkedIn = true;
                            } else {
                                JOptionPane.showMessageDialog(null, "Out of attempts!",
                                        "Client", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                    if (checkedIn == true && typedPassword != null) {    // login success
                        if (loginChoice == 0) { // seller
                            ArrayList<String> loginSeller = new ArrayList<>();
                            loginSeller.add("initializeExistingSeller");
                            loginSeller.add(typedUsername);
                            outputStream.writeObject(loginSeller);
                            outputStream.flush();
                            Object temp = inputStream.readObject();
                            if (temp instanceof FileNotFoundException) {
                                JOptionPane.showMessageDialog(null, "Error on login, seller",
                                        "Client", JOptionPane.ERROR_MESSAGE);
                            } else {
                                runSeller = true;
                            }
                        } else {    // buyer
                            ArrayList<String> loginBuyer = new ArrayList<>();
                            loginBuyer.add("initializeExistingBuyer");
                            loginBuyer.add(typedUsername);
                            loginBuyer.add(password);
                            loginBuyer.add("customer_" + typedUsername + ".csv");
                            loginBuyer.add(typedUsername + "_cart.csv");
                            outputStream.writeObject(loginBuyer);
                            outputStream.flush();
                            Object temp = inputStream.readObject();
                            if (temp instanceof FileNotFoundException) {
                                JOptionPane.showMessageDialog(null, "Error on login, buyer",
                                        "Client", JOptionPane.ERROR_MESSAGE);
                            } else {
                                runBuyer = true;
                            }
                        }
                    }
                }
            } else {
                running = false;
                socket.close();
            }

            // logged into seller
            while (runSeller) {
                String[] sellerInitialOptions = {"1. Logout", "2. Create new listing",
                        "3. Search for an existing listing",
                        "4. View all existing listings", "5. Update your page",
                        "6. View Seller Dashboard Options"};
                String sellerInitialChoice = (String) JOptionPane.showInputDialog(null,
                        "Welcome " + typedUsername + "!\n" + "What would you like to do?",
                        "Seller: " + typedUsername, JOptionPane.PLAIN_MESSAGE, null,
                        sellerInitialOptions, null);

                if (sellerInitialChoice == null) {
                    ArrayList<String> logoutSeller = new ArrayList<>();
                    logoutSeller.add("emptySeller");
                    outputStream.writeObject(logoutSeller);
                    outputStream.flush();
                    runSeller = false;
                    break;
                }
                if (sellerInitialChoice.equals("1. Logout")) {
                    JOptionPane.showMessageDialog(null, "See you next time!", "Seller: " +
                            typedUsername, JOptionPane.PLAIN_MESSAGE);
                    runSeller = false;
                    // running = true;
                }
                if (sellerInitialChoice.equals("2. Create new listing")) {
                    while (true) {
                        String listingName = JOptionPane.showInputDialog(null,
                                "Product name of your listing", "Seller: " + typedUsername,
                                JOptionPane.QUESTION_MESSAGE);
                        if (listingName == null)
                            break;
                        String storeID = JOptionPane.showInputDialog(null,
                                "Store name of your listing", "Seller: " + typedUsername,
                                JOptionPane.QUESTION_MESSAGE);
                        if (storeID == null)
                            break;
                        String description = JOptionPane.showInputDialog(null,
                                "Description for your listing", "Seller: " + typedUsername,
                                JOptionPane.QUESTION_MESSAGE);
                        if (description == null)
                            break;
                        int quantityAvailable = 0;
                        String tempQA = null;
                        while (true) {
                            try {
                                tempQA = JOptionPane.showInputDialog(null,
                                        "Quantity available for your listing", "Seller: " + typedUsername,
                                        JOptionPane.QUESTION_MESSAGE);
                                if (tempQA != null) {
                                    quantityAvailable = Integer.parseInt(tempQA);
                                }
                                if (quantityAvailable < 0) {
                                    JOptionPane.showMessageDialog(null, "Error, please enter" +
                                                    "a integer value > 0", "Seller: " + typedUsername,
                                            JOptionPane.ERROR_MESSAGE);
                                    tempQA = null;
                                } else {
                                    break;
                                }
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(null, "Error, please enter" +
                                                "a valid integer value", "Seller: " + typedUsername,
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        if (tempQA == null)
                            break;
                        String tempPrice = null;
                        double price = 0.0;
                        while (true) {
                            try {
                                tempPrice = JOptionPane.showInputDialog(null,
                                        "Price of your listing", "Seller: " + typedUsername,
                                        JOptionPane.QUESTION_MESSAGE);
                                if (tempPrice != null) {
                                    price = Double.parseDouble(tempPrice);
                                }
                                if (price < 0) {
                                    JOptionPane.showMessageDialog(null, "Error, please enter" +
                                                    "a double value > 0", "Seller: " + typedUsername,
                                            JOptionPane.ERROR_MESSAGE);
                                    tempPrice = null;
                                } else {
                                    break;
                                }
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(null, "Error, please enter" +
                                                "a valid double value", "Seller: " + typedUsername,
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        if (tempPrice == null)
                            break;
                        ArrayList<String> addListing = new ArrayList<>();
                        addListing.add("addSellerListing");
                        addListing.add(storeID);
                        addListing.add(listingName);
                        addListing.add(description);
                        addListing.add(String.valueOf(quantityAvailable));
                        addListing.add(String.valueOf(price));
                        outputStream.writeObject(addListing);
                        outputStream.flush();
                        int state = (Integer) inputStream.readObject();
                        if (state == 2) {
                            int modChoice = JOptionPane.showConfirmDialog(null,
                                    "Listing of the same name inside the same store already exist, " +
                                            "do you want to modify that listing?", "Seller: " + typedUsername,
                                    JOptionPane.YES_NO_OPTION);

                            if (modChoice == JOptionPane.YES_OPTION) {
                                ArrayList<String> modifyBothFields = new ArrayList<>();
                                modifyBothFields.add("modifySellerListingQuantityAndPrice");
                                modifyBothFields.add(storeID);
                                modifyBothFields.add(listingName);
                                modifyBothFields.add(String.valueOf(quantityAvailable));
                                modifyBothFields.add(String.valueOf(price));
                                outputStream.writeObject(modifyBothFields);
                                outputStream.flush();
                                Object temp = inputStream.readObject();
                                if (temp instanceof FileNotFoundException) {
                                    JOptionPane.showMessageDialog(null,
                                            "Error, failed to modify listing", "Seller: " + typedUsername,
                                            JOptionPane.ERROR_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(null,
                                            "Listing successfully modified!", "Seller: " + typedUsername,
                                            JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(null,
                                    "Listing successfully added!", "Seller: " + typedUsername,
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                        break;
                    }
                }
                if (sellerInitialChoice.equals("3. Search for an existing listing")) {
                    while (true) {
                        String desiredListing = JOptionPane.showInputDialog(null,
                                "Enter the product name of the listing", "Seller: " + typedUsername,
                                JOptionPane.INFORMATION_MESSAGE);
                        if (desiredListing == null) {
                            break;
                        }
                        ArrayList<String> getTargetName = new ArrayList<>();
                        getTargetName.add("getSellerTargetName");
                        getTargetName.add(desiredListing);
                        outputStream.writeObject(getTargetName);
                        outputStream.flush();
                        Object temp = inputStream.readObject();
                        if (temp instanceof ListingNotFoundException) {
                            JOptionPane.showMessageDialog(null,
                                    "Error, No listing with the provided product name found.",
                                    "Seller: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                        } else {
                            ArrayList<Listing> foundListings = (ArrayList<Listing>) temp;
                            String[] foundListingsArr = new String[foundListings.size()];
                            for (int i = 0; i < foundListings.size(); i++) {
                                foundListingsArr[i] = foundListings.get(i).toString();
                            }
                            String targetString = (String) JOptionPane.showInputDialog(null,
                                    "Found these listings, which one would you like to modify?",
                                    "Seller: " + typedUsername, JOptionPane.PLAIN_MESSAGE, null,
                                    foundListingsArr, null);
                            if (targetString == null)
                                break;
                            int targetIndex = -1;
                            for (int i = 0; i < foundListingsArr.length; i++) {
                                if (targetString.equals(foundListingsArr[i])) {
                                    targetIndex = i;
                                    break;
                                }
                            }
                            int quantityAvailable = -1;
                            double price = -1;
                            boolean startModify = false;
                            String[] modifyFieldOptions = {"Quantity", "Price", "Remove Listing"};
                            int modifyFieldChoice = JOptionPane.showOptionDialog(null,
                                    "Choice the field which you want to modify", "Seller: " + typedUsername,
                                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                                    modifyFieldOptions, null);
                            if (modifyFieldChoice == 0) {
                                while (true) {
                                    String quantityA = null;
                                    quantityA = JOptionPane.showInputDialog(null,
                                            "Enter desired Quantity value", "Seller: " + typedUsername,
                                            JOptionPane.QUESTION_MESSAGE);
                                    if (quantityA != null) {
                                        try {
                                            quantityAvailable = Integer.parseInt(quantityA);
                                            if (quantityAvailable > 0) {
                                                startModify = true;
                                                break;
                                            } else {
                                                JOptionPane.showMessageDialog(null,
                                                        "Error, please enter a value > 0",
                                                        "Seller: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                                            }
                                        } catch (NumberFormatException e) {
                                            JOptionPane.showMessageDialog(null,
                                                    "Error, please enter a valid integer value",
                                                    "Seller: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                                        }
                                    } else {
                                        break;
                                    }
                                }
                            } else if (modifyFieldChoice == 1) {
                                while (true) {
                                    String priceString = null;
                                    priceString = JOptionPane.showInputDialog(null,
                                            "Enter desired price value", "Seller: " + typedUsername,
                                            JOptionPane.QUESTION_MESSAGE);
                                    if (priceString != null) {
                                        try {
                                            price = Double.parseDouble(priceString);
                                            if (price > 0) {
                                                startModify = true;
                                                break;
                                            } else {
                                                JOptionPane.showMessageDialog(null,
                                                        "Error, please enter a value > 0",
                                                        "Seller: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                                            }
                                        } catch (NumberFormatException e) {
                                            JOptionPane.showMessageDialog(null,
                                                    "Error, please enter a valid double value",
                                                    "Seller: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                                        }
                                    } else {
                                        break;
                                    }
                                }
                            } else if (modifyFieldChoice == 2) {
                                int confirmRemoval = JOptionPane.showConfirmDialog(null,
                                        "Confirm Listing removal?", "Seller: " + typedUsername,
                                        JOptionPane.YES_NO_OPTION);
                                if (confirmRemoval == JOptionPane.YES_OPTION) {
                                    modifyFieldChoice = 2;
                                    startModify = true;
                                } else if (confirmRemoval == JOptionPane.NO_OPTION) {
                                    modifyFieldChoice = -1;
                                } else {
                                    break;
                                }
                            }
                            if (startModify) {
                                ArrayList<String> modifyListings = new ArrayList<>();
                                modifyListings.add("modifySellerListings");
                                modifyListings.add(foundListings.get(targetIndex).getStoreID());
                                modifyListings.add(foundListings.get(targetIndex).getProductName());
                                modifyListings.add(String.valueOf(quantityAvailable));
                                modifyListings.add(String.valueOf(price));
                                modifyListings.add(String.valueOf(modifyFieldChoice + 1));
                                outputStream.writeObject(modifyListings);
                                outputStream.flush();
                                temp = inputStream.readObject();
                                if (temp instanceof ListingNotFoundException) {
                                    JOptionPane.showMessageDialog(null,
                                            "Error, failed to modify listing",
                                            "Seller: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(null,
                                            "Modification successful!", "Seller: " + typedUsername,
                                            JOptionPane.PLAIN_MESSAGE);
                                }
                                break;
                            }
                        }
                    }
                }
                if (sellerInitialChoice.equals("4. View all existing listings")) {
                    String allListing = new String();
                    ArrayList<String> getListings = new ArrayList<>();
                    getListings.add("getSellerListings");
                    outputStream.writeObject(getListings);
                    outputStream.flush();
                    Object temp = inputStream.readObject();
                    ArrayList<Listing> allListings = (ArrayList<Listing>) temp;
                    for (int i = 0; i < allListings.size(); i++) {
                        allListing += allListings.get(i).toString() + "\n";
                    }
                    JOptionPane.showMessageDialog(null, "All Listings:\n" + allListing,
                            "Seller: " + typedUsername, JOptionPane.INFORMATION_MESSAGE);
                }
                if (sellerInitialChoice.equals("5. Update your page")) {
                    ArrayList<String> pageUpdate = new ArrayList<>();
                    pageUpdate.add("updateSeller");
                    outputStream.writeObject(pageUpdate);
                    outputStream.flush();
                }
                if (sellerInitialChoice.equals("6. View Seller Dashboard Options")) { // seller dashboard
                    boolean inDashboard = true;
                    String[] dashboardOption = {"View sales history for a store", "View customer history for a store",
                            "Import a listing file", "Go back"};
                    int dashboardChoice = -1;
                    while (inDashboard) {
                        dashboardChoice = JOptionPane.showOptionDialog(null,
                                "Welcome to the Seller dashboard!\nWhat would you like to do?",
                                "Seller: " + typedUsername, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                                null, dashboardOption,
                                dashboardOption[0]);
                        if (dashboardChoice == 0) {
                            int salesSorted = JOptionPane.showConfirmDialog(null,
                                    "Would you like the data sorted?", "Seller: " + typedUsername,
                                    JOptionPane.YES_NO_OPTION);
                            String storeChoice = new String();
                            String foundListing = new String();
                            Object temp = null;
                            while (true) {
                                storeChoice = JOptionPane.showInputDialog(null,
                                        "Enter the name of desired store", "Seller: " + typedUsername,
                                        JOptionPane.QUESTION_MESSAGE);
                                if (storeChoice == null)
                                    break;
                                if (salesSorted == JOptionPane.YES_OPTION) {
                                    ArrayList<String> printSortedSalesHistory = new ArrayList<>();
                                    printSortedSalesHistory.add("printSellerSortedSalesHistory");
                                    printSortedSalesHistory.add(storeChoice);
                                    outputStream.writeObject(printSortedSalesHistory);
                                    outputStream.flush();
                                    temp = inputStream.readObject();
                                } else if (salesSorted == JOptionPane.NO_OPTION) {
                                    ArrayList<String> printSalesHistory = new ArrayList<>();
                                    printSalesHistory.add("printSellerSalesHistory");
                                    printSalesHistory.add(storeChoice);
                                    outputStream.writeObject(printSalesHistory);
                                    outputStream.flush();
                                    temp = inputStream.readObject();
                                } else {
                                    JOptionPane.showMessageDialog(null, "Invalid input",
                                            "Seller: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                                }
                                if (temp instanceof FileNotFoundException) {
                                    JOptionPane.showMessageDialog(null,
                                            "No sales has been made by this seller yet!",
                                            "Seller: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                                    break;
                                } else if (temp instanceof ListingNotFoundException) {
                                    JOptionPane.showMessageDialog(null,
                                            "This store has not made any sells yet!",
                                            "Seller: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                                } else {
                                    foundListing = (String) temp;
                                    JOptionPane.showMessageDialog(null,
                                            "Sales History\n" + foundListing, "Seller: " + typedUsername,
                                            JOptionPane.INFORMATION_MESSAGE);
                                    break;
                                }
                            }
                        } else if (dashboardChoice == 1) {
                            int customerSorted = JOptionPane.showConfirmDialog(null,
                                    "Would you like the data sorted?", "Seller: " + typedUsername,
                                    JOptionPane.YES_NO_OPTION);
                            String storeChoice = new String();
                            String foundListing = new String();
                            Object temp = null;
                            while (true) {
                                storeChoice = JOptionPane.showInputDialog(null,
                                        "Enter the name of desired store", "Seller: " + typedUsername,
                                        JOptionPane.QUESTION_MESSAGE);
                                if (storeChoice == null)
                                    break;
                                if (customerSorted == JOptionPane.YES_OPTION) {
                                    ArrayList<String> printSortedSalesHistory = new ArrayList<>();
                                    printSortedSalesHistory.add("printSellerSortedCustomerHistory");
                                    printSortedSalesHistory.add(storeChoice);
                                    outputStream.writeObject(printSortedSalesHistory);
                                    outputStream.flush();
                                    temp = inputStream.readObject();
                                } else if (customerSorted == JOptionPane.NO_OPTION) {
                                    ArrayList<String> printSalesHistory = new ArrayList<>();
                                    printSalesHistory.add("printSellerCustomerHistory");
                                    printSalesHistory.add(storeChoice);
                                    outputStream.writeObject(printSalesHistory);
                                    outputStream.flush();
                                    temp = inputStream.readObject();
                                } else {
                                    JOptionPane.showMessageDialog(null, "Invalid input",
                                            "Seller: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                                }
                                if (temp instanceof FileNotFoundException) {
                                    JOptionPane.showMessageDialog(null,
                                            "No sales has been made by this seller yet!",
                                            "Seller: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                                    break;
                                } else if (temp instanceof ListingNotFoundException) {
                                    JOptionPane.showMessageDialog(null,
                                            "This store has not made any sells yet!",
                                            "Seller: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                                } else {
                                    foundListing = (String) temp;
                                    JOptionPane.showMessageDialog(null,
                                            "Sales History\n" + foundListing, "Seller: " + typedUsername,
                                            JOptionPane.INFORMATION_MESSAGE);
                                    break;
                                }
                            }
                        } else if (dashboardChoice == 2) {
                            String importFile = new String();
                            importFile = JOptionPane.showInputDialog(null,
                                    "Enter the import file name (include .csv)",
                                    "Seller: " + typedUsername, JOptionPane.QUESTION_MESSAGE);
                            if (importFile != null) {
                                ArrayList<String> importListings = new ArrayList<>();
                                importListings.add("importSellerFileName");
                                importListings.add(importFile);
                                outputStream.writeObject(importListings);
                                outputStream.flush();
                                Object temp = inputStream.readObject();
                                if (temp instanceof Exception) {
                                    JOptionPane.showMessageDialog(null,
                                            "Error, imported file doesn't exist or is in the wrong format!",
                                            "Seller: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(null, "Import Successful!",
                                            "Seller: " + typedUsername, JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                        } else {
                            inDashboard = false;
                        }
                    }
                }
            }
//
            while (runBuyer) {
                String welcome = "Welcome " + typedUsername + "!\n" +
                        "What would you like to do?";
                String[] choiceStr = {"1. View a seller and their listings",
                        "2. Check out your shopping cart", "3. View your purchasing history",
                        "4. Export your purchasing history to another file", "5. Update your page", "6. Logout"};
                String buyerInitialChoice = new String();
                buyerInitialChoice = (String) (JOptionPane.showInputDialog(null, welcome,
                        "Buyer: " + typedUsername, JOptionPane.PLAIN_MESSAGE,
                        null, choiceStr, null));
                if (buyerInitialChoice == null) {
                    ArrayList<String> logoutBuyer = new ArrayList<>();
                    logoutBuyer.add("emptyBuyer");
                    outputStream.writeObject(logoutBuyer);
                    outputStream.flush();
                    runBuyer = false;
                    break;
                }

                if (buyerInitialChoice.equals("5. Update your page")) {
                    ArrayList<String> updateBuyer = new ArrayList<>();
                    updateBuyer.add("updateBuyer");
                    outputStream.writeObject(updateBuyer);
                    outputStream.flush();
                }
                if (buyerInitialChoice.equals("6. Logout")) {
                    JOptionPane.showMessageDialog(null, "See you next time!",
                            "Buyer: " + typedUsername, JOptionPane.INFORMATION_MESSAGE);
                    ArrayList<String> write = new ArrayList<String>();
                    write.add("writeBuyerCart");
                    write.add(typedUsername + "_cart.csv");
                    outputStream.writeObject(write);
                    outputStream.flush();
                    runBuyer = false;
                }
                if (buyerInitialChoice.equals("1. View a seller and their listings")) {
                    boolean viewingSeller = true;
                    boolean foundSeller = false;
                    String targetSeller = new String();
                    while (viewingSeller) {
                        ArrayList<String> getAllSeller = new ArrayList<>();
                        getAllSeller.add("readAllSellerName");
                        outputStream.writeObject(getAllSeller);
                        outputStream.flush();
                        Object temp = inputStream.readObject();
                        if (temp instanceof FileNotFoundException) {
                            JOptionPane.showMessageDialog(null,
                                    "No sellers currently exist. Please come back later!",
                                    "Buyer: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                            break;
                        } else {
                            ArrayList<String> currentSellers = (ArrayList<String>) temp;
                            String[] currentSellersArr = new String[currentSellers.size()];
                            for (int i = 0; i < currentSellers.size(); i++) {
                                currentSellersArr[i] = currentSellers.get(i);
                            }
                            targetSeller = (String) JOptionPane.showInputDialog(null,
                                    "Chose a seller to view", "Buyer: " + typedUsername,
                                    JOptionPane.PLAIN_MESSAGE, null, currentSellersArr, null);
                            if (targetSeller != null) {
                                ArrayList<String> createViewingSeller = new ArrayList<>();
                                createViewingSeller.add("initializeExistingSeller");
                                createViewingSeller.add(targetSeller);
                                outputStream.writeObject(createViewingSeller);
                                outputStream.flush();
                                temp = inputStream.readObject();
                                if (temp instanceof FileNotFoundException) {
                                    JOptionPane.showMessageDialog(null,
                                            "Please pick a valid seller name", "Buyer: " + typedUsername,
                                            JOptionPane.ERROR_MESSAGE);
                                } else {
                                    foundSeller = true;
                                }
                            } else {
                                break;
                            }
                        }
                        while (foundSeller) {
                            String choice2Str = String.format("Welcome to %s's store\nWhat would you like to do?",
                                    targetSeller);
                            String viewSellerChoice = new String();
                            String[] viewSellerOptions = {"1. View all listings",
                                    "2. Sort all listings by quantity available (descending)",
                                    "3. Sort all listings by price (ascending)", "4. Search by store",
                                    "5. Search by product", "6. Search by description",
                                    "7. Show a list of store by the number of products sold",
                                    "8. Show a list of stores by the number of products purchased by you",
                                    "9. Add a listing to your cart", "10. Update seller's listings", "11. Go back"};
                            viewSellerChoice = (String) JOptionPane.showInputDialog(null, choice2Str,
                                    "Buyer: " + typedUsername, JOptionPane.PLAIN_MESSAGE,
                                    null, viewSellerOptions, null);
                            if (viewSellerChoice == null) {
                                ArrayList<String> emptySeller = new ArrayList<>();
                                emptySeller.add("emptySeller");
                                outputStream.writeObject(emptySeller);
                                outputStream.flush();
                                foundSeller = false;
                                viewingSeller = false;
                                break;
                            } else if (viewSellerChoice.equals("1. View all listings")) {
                                ArrayList<String> sellerList = new ArrayList<>();
                                String str = new String();
                                sellerList.add("getSellerListings");
                                outputStream.writeObject(sellerList);
                                outputStream.flush();
                                ArrayList<Listing> stores = (ArrayList<Listing>) inputStream.readObject();
                                str = "All stores and listings of " + targetSeller + "\n";
                                str += "----------------------------\n";
                                for (Listing listing : stores) {
                                    str += listing.toString() + "\n";
                                }
                                str += "----------------------------\n";
                                JOptionPane.showMessageDialog(null, str, "Buyer: " + typedUsername,
                                        JOptionPane.INFORMATION_MESSAGE);
                            } else if (viewSellerChoice.equals
                                    ("2. Sort all listings by quantity available (descending)")) {
                                String str = new String();
                                ArrayList<String> sellerList = new ArrayList<>();
                                sellerList.add("getSellerListingsByQuantity");
                                outputStream.writeObject(sellerList);
                                outputStream.flush();
                                ArrayList<Listing> sortByQuantity = (ArrayList<Listing>) inputStream.readObject();
                                for (Listing listing : sortByQuantity) {
                                    str += listing.toString() + "\n";
                                }
                                JOptionPane.showMessageDialog(null, str, "Buyer: " + typedUsername,
                                        JOptionPane.INFORMATION_MESSAGE);
                                str = new String();
                            } else if (viewSellerChoice.equals("3. Sort all listings by price (ascending)")) {
                                String string = new String();
                                ArrayList<String> sellerList = new ArrayList<>();
                                sellerList.add("getSellerListingsByPrice");
                                outputStream.writeObject(sellerList);
                                outputStream.flush();
                                ArrayList<Listing> sortByPrice = (ArrayList<Listing>) inputStream.readObject();
                                for (Listing listing : sortByPrice) {
                                    string += listing.toString() + "\n";
                                }
                                JOptionPane.showMessageDialog(null, string,
                                        "Buyer: " + typedUsername, JOptionPane.INFORMATION_MESSAGE);
                                string = new String();
                            } else if (viewSellerChoice.equals("4. Search by store")) {
                                String str = new String();
                                ArrayList<Listing> listings = null;
                                ArrayList<String> sellerList = new ArrayList<>();
                                while (true) {
                                    String storeChoice = JOptionPane.showInputDialog(null,
                                            "Which store would you like to see listings for?",
                                            "Buyer: " + typedUsername, JOptionPane.QUESTION_MESSAGE);
                                    if (storeChoice == null) {
                                        break;
                                    }
                                    sellerList.add("getSellerTargetStore");
                                    sellerList.add(storeChoice);
                                    outputStream.writeObject(sellerList);
                                    outputStream.flush();
                                    temp = inputStream.readObject();
                                    if (temp instanceof ListingNotFoundException) {
                                        JOptionPane.showMessageDialog(null,
                                                "Error, store not found!", "Buyer: " + typedUsername,
                                                JOptionPane.ERROR_MESSAGE);
                                    } else {
                                        listings = (ArrayList<Listing>) temp;
                                        break;
                                    }
                                }

                                if (listings != null) {
                                    str += "Listings---------------------\n";
                                    for (Listing listing : listings) {
                                        str += listing.toString() + "\n";
                                    }
                                    JOptionPane.showMessageDialog(null, str,
                                            "Buyer: " + typedUsername, JOptionPane.INFORMATION_MESSAGE);
                                }
                            } else if (viewSellerChoice.equals("5. Search by product")) {
                                String str = new String();
                                ArrayList<Listing> targetArr = null;
                                ArrayList<String> getTargetName = new ArrayList<>();
                                while (true) {
                                    String targetName = JOptionPane.showInputDialog(null,
                                            "Enter the name of the product you want",
                                            "Buyer: " + typedUsername, JOptionPane.QUESTION_MESSAGE);
                                    if (targetName == null) {
                                        break;
                                    }
                                    getTargetName.add("getSellerTargetName");
                                    getTargetName.add(targetName);
                                    outputStream.writeObject(getTargetName);
                                    outputStream.flush();
                                    temp = inputStream.readObject();
                                    if (temp instanceof ListingNotFoundException) {
                                        JOptionPane.showMessageDialog(null,
                                                "Error, product not found!", "Buyer: " + typedUsername,
                                                JOptionPane.ERROR_MESSAGE);
                                    } else {
                                        targetArr = (ArrayList<Listing>) temp;
                                        break;
                                    }
                                }

                                if (targetArr != null) {
                                    str += "Listings---------------------\n";
                                    for (Listing listing : targetArr) {
                                        str += listing.toString() + "\n";
                                    }

                                    JOptionPane.showMessageDialog(null, str,
                                            "Buyer: " + typedUsername, JOptionPane.INFORMATION_MESSAGE);
                                }
                            } else if (viewSellerChoice.equals("6. Search by description")) {
                                String str = new String();
                                ArrayList<Listing> targetArr = null;
                                ArrayList<String> getTargetDesc = new ArrayList<>();
                                while (true) {
                                    String targetDesc = JOptionPane.showInputDialog(null,
                                            "Enter the description of the product you want",
                                            "Buyer: " + typedUsername, JOptionPane.QUESTION_MESSAGE);
                                    if (targetDesc == null) {
                                        break;
                                    }
                                    getTargetDesc.add("getSellerTargetDesc");
                                    getTargetDesc.add(targetDesc);
                                    outputStream.writeObject(getTargetDesc);
                                    outputStream.flush();
                                    temp = inputStream.readObject();
                                    if (temp instanceof ListingNotFoundException) {
                                        JOptionPane.showMessageDialog(null,
                                                "Error, no listing with that description was found!",
                                                "Buyer: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                                    } else {
                                        targetArr = (ArrayList<Listing>) temp;
                                        break;
                                    }
                                }

                                if (targetArr != null) {
                                    str += "Listings---------------------\n";
                                    for (Listing listing : targetArr) {
                                        str += listing.toString() + "\n";
                                    }

                                    JOptionPane.showMessageDialog(null, str,
                                            "Buyer: " + typedUsername, JOptionPane.INFORMATION_MESSAGE);
                                }
                            } else if (viewSellerChoice.equals
                                    ("7. Show a list of store by the number of products sold")) {
                                String str1 = "Do you want this list sorted (descending)?\n";
                                String str = new String();
                                ArrayList<CustomerHistory> storesBySold = new ArrayList<>();
                                ArrayList<String> sellerList = new ArrayList<>();
                                boolean sort = false;
                                boolean runTheRest = true;
                                int sortedChoice = JOptionPane.showConfirmDialog(null, str1,
                                        "Buyer: " + typedUsername, JOptionPane.YES_NO_OPTION);
                                if (sortedChoice == JOptionPane.YES_OPTION)
                                    sort = true;
                                else if (sortedChoice == JOptionPane.NO_OPTION)
                                    sort = false;
                                else
                                    runTheRest = false;
                                if (runTheRest = true) {
                                    sellerList.add("storesSellerBySold");
                                    sellerList.add(String.valueOf(sort));
                                    outputStream.writeObject(sellerList);
                                    outputStream.flush();
                                    temp = inputStream.readObject();
                                    if (temp instanceof FileNotFoundException) {
                                        JOptionPane.showMessageDialog(null,
                                                "No sales has been made by this seller yet.",
                                                "Buyer: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                                    } else if (temp instanceof ListingNotFoundException) {
                                        JOptionPane.showMessageDialog(null,
                                                "This store has not made any sales yet.",
                                                "Buyer: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                                    } else {
                                        storesBySold = (ArrayList<CustomerHistory>) temp;
                                        for (CustomerHistory customerHistory : storesBySold) {
                                            str += customerHistory.printStoreBySold() + "\n";
                                        }
                                        JOptionPane.showMessageDialog(null, str,
                                                "Buyer: " + typedUsername, JOptionPane.INFORMATION_MESSAGE);
                                    }
                                }
                            } else if (viewSellerChoice.equals
                                    ("8. Show a list of stores by the number of products purchased by you")) {
                                String str1 = "Do you want this list sorted (descending)?\n";
                                String str = new String();
                                boolean sort = false;
                                boolean runTheRest = true;

                                int sortedChoice = JOptionPane.showConfirmDialog(null, str1,
                                        "Buyer: " + typedUsername, JOptionPane.YES_NO_OPTION);
                                if (sortedChoice == JOptionPane.YES_OPTION)
                                    sort = true;
                                else if (sortedChoice == JOptionPane.NO_OPTION)
                                    sort = false;
                                else
                                    runTheRest = false;

                                if (runTheRest) {
                                    ArrayList<String> sellerList = new ArrayList<>();
                                    sellerList.add("sortBuyerByPurchased");
                                    sellerList.add(String.valueOf(sort));
                                    outputStream.writeObject(sellerList);
                                    outputStream.flush();
                                    ArrayList<Listing> sortByPurchased = (ArrayList<Listing>) inputStream.readObject();
                                    if (sortByPurchased.size() > 0) {
                                        for (Listing listing : sortByPurchased) {
                                            str += String.format("Store: %s, Units Purchased: %s\n",
                                                    listing.getStoreID(), listing.getQuantityBought());
                                        }
                                        JOptionPane.showMessageDialog(null, str,
                                                "Buyer: " + typedUsername, JOptionPane.INFORMATION_MESSAGE);
                                    } else {
                                        JOptionPane.showMessageDialog(null,
                                                "Error, you haven't purchased anything from this seller yet!",
                                                "Buyer: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            } else if (viewSellerChoice.equals("9. Add a listing to your cart")) {
                                String desiredPurchase = null;
                                Listing desiredListing = null;
                                boolean makingPurchases = true;
                                String tempQ = new String();
                                int desiredQuantity = -1;

                                while (makingPurchases) {
                                    ArrayList<String> getAllListings = new ArrayList<>();
                                    getAllListings.add("getSellerListings");
                                    outputStream.writeObject(getAllListings);
                                    outputStream.flush();
                                    ArrayList<Listing> allListings = (ArrayList<Listing>) inputStream.readObject();
                                    if (allListings.size() == 0) {
                                        JOptionPane.showMessageDialog(null,
                                                "This seller has no listing yet!",
                                                "Buyer: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                                        break;
                                    } else {
                                        String[] allListingArr = new String[allListings.size()];
                                        for (int i = 0; i < allListingArr.length; i++) {
                                            allListingArr[i] = String.format
                                                    ("Product: %s  |  Store: %s  |  Price: $%.2f  |  " +
                                                                    "Quantity Available: %d",
                                                            allListings.get(i).getProductName(),
                                                            allListings.get(i).getStoreID(),
                                                            allListings.get(i).getPrice(),
                                                            allListings.get(i).getQuantityAvailable());
                                        }
                                        desiredPurchase = (String) JOptionPane.showInputDialog(null,
                                                "Select a product to add to your cart",
                                                "Buyer: " + typedUsername, JOptionPane.PLAIN_MESSAGE,
                                                null, allListingArr, null);
                                        if (desiredPurchase == null)
                                            break;
                                        int target = 0;
                                        for (int i = 0; i < allListingArr.length; i++) {
                                            if (desiredPurchase.equals(allListingArr[i])) {
                                                target = i;
                                                break;
                                            }
                                        }
                                        desiredListing = allListings.get(target);
                                        while (true) {
                                            try {
                                                tempQ = JOptionPane.showInputDialog(null,
                                                        "How many would you like to add to your cart?",
                                                        "Buyer: " + typedUsername, JOptionPane.QUESTION_MESSAGE);
                                                if (tempQ == null) {
                                                    makingPurchases = false;
                                                    break;
                                                }
                                                desiredQuantity = Integer.parseInt(tempQ);

                                            } catch (NumberFormatException e) {
                                                JOptionPane.showMessageDialog(null,
                                                        "Error, please enter a valid integer value",
                                                        "Buyer: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                                                tempQ = null;
                                            }
                                            if (tempQ != null) {
                                                if (desiredQuantity > 0 &&
                                                        desiredQuantity <= desiredListing.getQuantityAvailable()) {
                                                    desiredListing.setSeller(targetSeller);
                                                    desiredListing.setQuantityBought(desiredQuantity);
                                                    desiredListing.setPurchased(false);
                                                    ArrayList<String> addListing = new ArrayList<>();
                                                    addListing.add("addBuyerListing");
                                                    outputStream.writeObject(addListing);
                                                    outputStream.flush();
                                                    outputStream.writeObject(desiredListing);
                                                    outputStream.flush();

                                                    String success = String.format(
                                                            "%d of %s Successfully Added to cart!",
                                                            desiredQuantity, desiredListing.getProductName());
                                                    JOptionPane.showMessageDialog(null, success,
                                                            "Buyer: " + typedUsername,
                                                            JOptionPane.INFORMATION_MESSAGE);
                                                    makingPurchases = false;
                                                    break;
                                                } else {
                                                    JOptionPane.showMessageDialog(null,
                                                            "Error! Not a valid Quantity!",
                                                            "Buyer: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (viewSellerChoice.equals("10. Update seller's listings")) {
                                ArrayList<String> updateSeller = new ArrayList<>();
                                updateSeller.add("updateSeller");
                                outputStream.writeObject(updateSeller);
                                outputStream.flush();
                            } else if (viewSellerChoice.equals("11. Go back")) {
                                String newSeller = "Would you like to look at another seller?";
                                choice = JOptionPane.showConfirmDialog(null, newSeller,
                                        "Buyer: " + typedUsername, JOptionPane.YES_NO_OPTION);
                                if (choice == JOptionPane.YES_OPTION) {
                                    foundSeller = false;
                                    ArrayList<String> emptySeller = new ArrayList<>();
                                    emptySeller.add("emptySeller");
                                    outputStream.writeObject(emptySeller);
                                    outputStream.flush();
                                } else {    // no_option or cancel
                                    viewingSeller = false;
                                    foundSeller = false;
                                }
                            }
                        }
                    }
                }

                if (buyerInitialChoice.equals("2. Check out your shopping cart")) {
                    while (true) {
                        String[] cartOptions = {"View your cart", "Proceed to checkout with cart",
                                "Remove an item in your cart", "Go back"};
                        choice = JOptionPane.showOptionDialog(null, "Wha would you like to do?",
                                "Buyer: " + typedUsername, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                                null, cartOptions, cartOptions[0]);
                        if (choice == 0) {
                            ArrayList<String> printCart = new ArrayList<String>();
                            printCart.add("printBuyerCart");
                            outputStream.writeObject(printCart);
                            outputStream.flush();
                            Object temp = inputStream.readObject();
                            String print = (String) temp;
                            JOptionPane.showMessageDialog(null, print, "Buyer: " + typedUsername,
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else if (choice == 2) {
                            ArrayList<String> printCart = new ArrayList<>();
                            printCart.add("getBuyerCart");
                            outputStream.writeObject(printCart);
                            outputStream.flush();
                            ArrayList<Listing> isEmpty = (ArrayList<Listing>) inputStream.readObject();
                            if (isEmpty.size() != 0) {
                                while (true) {
                                    printCart = new ArrayList<>();
                                    printCart.add("printBuyerCart");
                                    outputStream.writeObject(printCart);
                                    outputStream.flush();
                                    String print = (String) inputStream.readObject();
                                    JOptionPane.showMessageDialog(null, print, 
                                            "Buyer: " + typedUsername, JOptionPane.INFORMATION_MESSAGE);
                                    String tempIndex = JOptionPane.showInputDialog(null,
                                            "Enter the index of the listing you would like to remove",
                                            "Buyer: " + typedUsername, JOptionPane.QUESTION_MESSAGE);
                                    if (tempIndex != null) {
                                        String errorMsg = "Please enter a valid integer value";
                                        int index = 0;
                                        try {
                                            index = Integer.parseInt(tempIndex);
                                            if (index < 0) {
                                                JOptionPane.showMessageDialog(null,
                                                        errorMsg, "Buyer: " + typedUsername,
                                                        JOptionPane.ERROR_MESSAGE);
                                            } else {
                                                ArrayList<String> remove = new ArrayList<String>();
                                                remove.add("removeBuyerListingByIndex");
                                                remove.add(String.valueOf(index - 1));
                                                outputStream.writeObject(remove);
                                                outputStream.flush();
                                                Object temp = inputStream.readObject();
                                                if (temp instanceof IndexOutOfBoundsException) {
                                                    JOptionPane.showMessageDialog(null,
                                                            errorMsg, "Buyer: " + typedUsername,
                                                            JOptionPane.ERROR_MESSAGE);
                                                } else {
                                                    JOptionPane.showMessageDialog(null,
                                                            "Remove Successful!", "Buyer: " + typedUsername,
                                                            JOptionPane.INFORMATION_MESSAGE);
                                                    break;
                                                }
                                            }

                                        } catch (NumberFormatException e) {
                                            JOptionPane.showMessageDialog(null,
                                                    errorMsg, "Buyer: " + typedUsername,
                                                    JOptionPane.ERROR_MESSAGE);
                                        }
                                    } else {
                                        break;
                                    }
                                }
                            } else {
                                JOptionPane.showMessageDialog(null,
                                        "You don't have any items in your cart!",
                                        "Buyer: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                            }
                        } else if (choice == 1) {
                            ArrayList<String> getCart = new ArrayList<>();
                            getCart.add("getBuyerCart");
                            outputStream.writeObject(getCart);
                            outputStream.flush();
                            ArrayList<Listing> cartItems = (ArrayList<Listing>) inputStream.readObject();
                            if (cartItems.size() != 0) {
                                JOptionPane.showMessageDialog(null,
                                        "Purchasing all items in cart...", "Buyer: " + typedUsername,
                                        JOptionPane.INFORMATION_MESSAGE);
                                // get every item's seller
                                ArrayList<String> cartItemSeller = new ArrayList<>();
                                for (Listing cartItem : cartItems) {
                                    cartItemSeller.add(cartItem.getSeller());
                                }

                                // initialize, update all contained seller
                                ArrayList<String> update = new ArrayList<>();
                                update.add("updateAfterPurchaseMade");
                                outputStream.writeObject(update);
                                outputStream.flush();
                                outputStream.writeObject(cartItemSeller);
                                outputStream.flush();
                                outputStream.writeObject(cartItems);
                                outputStream.flush();
                                Object temp = inputStream.readObject();
                                if (temp instanceof String) {
                                    String wrongIndex = (String) temp;
                                    String errorMsg = String.format
                                            ("There is a price change for item at index %d of your cart",
                                                    Integer.parseInt(wrongIndex) + 1);
                                    JOptionPane.showMessageDialog(null, errorMsg,
                                            "Buyer: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                                } else if (temp instanceof ListingNotFoundException) {
                                    JOptionPane.showMessageDialog(null,
                                            "Purchased Unsuccessful, this item has been taken off the shelf",
                                            "Buyer: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                                } else if (temp instanceof InvalidPurchaseException) {
                                    JOptionPane.showMessageDialog(null,
                                            "Purchased Unsuccessful, purchasing more than quantity available!",
                                            "Buyer: " + typedUsername, JOptionPane.ERROR_MESSAGE);
                                } else if (temp == null) {
                                    JOptionPane.showMessageDialog(null, "Purchased Successful!",
                                            "Buyer: " + typedUsername, JOptionPane.INFORMATION_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(null,
                                        "Error! Cart is empty!", "Buyer: " + typedUsername,
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            break;
                        }
                    }
                }

                if (buyerInitialChoice.equals("3. View your purchasing history")) {
                    ArrayList<String> getPurchased = new ArrayList<>();
                    getPurchased.add("getBuyerPurchased");
                    outputStream.writeObject(getPurchased);
                    outputStream.flush();
                    ArrayList<Listing> purchased = (ArrayList<Listing>) inputStream.readObject();
                    String str = "---------------------------\n";
                    String output = "";
                    for (Listing listing : purchased) {
                        output += "Listing [storeID=" + listing.getStoreID() + ", productName=" +
                                listing.getProductName();
                        output += ", seller=" + listing.getSeller();
                        output += String.format(", price=%.2f", listing.getPrice());
                        output += ", quantity=" + listing.getQuantityBought() + "]";
                        str += output + "\n";
                        output = "";
                    }
                    JOptionPane.showMessageDialog(null, str, "Buyer: " + typedUsername,
                            JOptionPane.INFORMATION_MESSAGE);
                }
                if (buyerInitialChoice.equals("4. Export your purchasing history to another file")) {
                    String file = "What is the file you want your purchase history to be exported to (include .csv)";
                    String fileName = JOptionPane.showInputDialog(null, file,
                            "Buyer: " + typedUsername, JOptionPane.QUESTION_MESSAGE);
                    ArrayList<String> export = new ArrayList<String>();
                    if (fileName != null) {
                        export.add("exportBuyerPurchased");
                        export.add(fileName);
                        outputStream.writeObject(export);
                        outputStream.flush();
                    }
                }
            }
        }
        JOptionPane.showMessageDialog(null, "Thank you for visiting the MegaChonk Market!",
                "Buyer: " + typedUsername, JOptionPane.INFORMATION_MESSAGE);
    }
}
