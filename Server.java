import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * Server Class
 * Server run on "localhost" and port 4242 (client class auto-connects to server)
 * The client class will not run if the server is not running!
 *
 * @author Brian Reagan, Justin Shakergayen, Yipeng Wang, Medha Yaragorla, Justin Yu
 * version 7/31/23
 */

public class Server implements Serializable, Runnable {
    private static final Object gateKeeper = new Object();
    Socket socket;

    public Server(Socket socket) {
        this.socket = socket;
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4242);
        System.out.println("Waiting for the clients to connect...");
        while (true) {
            Socket socket = serverSocket.accept();
            Server server = new Server(socket);
            new Thread(server).start();
            System.out.println("Client connected!");
        }
    }

    public void run() {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("Client Connected!, Streams established");
            ArrayList<String> received;
            Customer currentBuyer = null;
            Seller currentSeller = null;
            while (true) {
                try {
                    received = (ArrayList<String>) inputStream.readObject();
                    System.out.println("Received from client");
                    System.out.print(received);
                    System.out.println();
                    if (received.get(0).equals("emptySeller")) {
                        currentSeller = null;
                    }
                    if (received.get(0).equals("updateSeller")) {
                        try {
                            currentSeller = new Seller(currentSeller.getUserName());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    if (received.get(0).equals("emptyBuyer")) {
                        currentBuyer = null;
                    }
                    if (received.get(0).equals("updateBuyer")) {
                        try {
                            currentBuyer = new Customer(currentBuyer.getUsername(),
                                    "1", "customer_" + currentBuyer.getUsername() + ".csv",
                                    currentBuyer.getUsername() + "_cart.csv");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    if (received.get(0).equals("initializeNewSeller")) {
                        try {
                            synchronized (gateKeeper) {
                                currentSeller = new Seller(received.get(1), received.get(2));
                                // create a file consisting of all buyer/seller usernames
                                File f = new File("existingSELLER.csv");
                                try {
                                    if (!f.exists()) {
                                        f.createNewFile();
                                    }
                                    PrintWriter pw = new PrintWriter(new FileOutputStream(f, true));
                                    pw.println(received.get(1));
                                    pw.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            outputStream.writeObject(null);
                            outputStream.flush();
                        } catch (UserAlreadyExistException e) {
                            e.printStackTrace();
                            outputStream.writeObject(e); // Returns exception to client if incorrect login.
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("initializeExistingSeller")) {
                        try {
                            currentSeller = new Seller(received.get(1));
                            outputStream.writeObject(null);
                            outputStream.flush();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("initializeNewBuyer")) {
                        try {
                            synchronized (gateKeeper) {
                                currentBuyer = new Customer(received.get(1), received.get(2));
                            }
                            outputStream.writeObject(null);
                            outputStream.flush();
                        } catch (UserAlreadyExistException e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        } catch (IOException e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("initializeExistingBuyer")) {
                        try {
                            currentBuyer = new Customer(received.get(1), received.get(2), received.get(3),
                                    received.get(4));
                            outputStream.writeObject(null);
                            outputStream.flush();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("addSellerListing")) {
                        int temp = 1;
                        synchronized (gateKeeper) {
                            temp = currentSeller.addListing(received.get(1), received.get(2), received.get(3),
                                    Integer.parseInt(received.get(4)), Double.parseDouble(received.get(5)));
                        }
                        outputStream.writeObject(temp);
                        outputStream.flush();
                    }
                    if (received.get(0).equals("getSellerUsername")) {
                        try {
                            outputStream.writeObject(currentSeller.getUserName());
                            outputStream.flush();
                        } catch (Exception e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("setSellerUsername")) {
                        try {
                            synchronized (gateKeeper) {
                                currentSeller.setUserName(received.get(1));
                            }
                        } catch (Exception e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("getSellerFileName")) {
                        try {
                            outputStream.writeObject(currentSeller.getFileName());
                            outputStream.flush();
                        } catch (Exception e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("setSellerFileName")) {
                        try {
                            synchronized (gateKeeper) {
                                currentSeller.setFileName(received.get(1));
                            }
                        } catch (Exception e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("getSellerPassword")) {
                        try {
                            String fileName = received.get(1);
                            File f = new File(fileName);
                            BufferedReader bfr = new BufferedReader(new FileReader(f));
                            bfr.readLine();
                            String password = bfr.readLine();
                            outputStream.writeObject(password);
                            outputStream.flush();
                        } catch (FileNotFoundException e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        } catch (Exception e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("setSellerPassword")) {
                        try {
                            synchronized (gateKeeper) {
                                currentSeller.setPassword(received.get(1));
                            }
                        } catch (Exception e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("getSellerListings")) {
                        currentSeller = new Seller(currentSeller.getUserName());
                        ArrayList<Listing> allListings = currentSeller.getListings();
                        outputStream.writeObject(allListings);
                        outputStream.flush();
                    }
                    if (received.get(0).equals("getSellerListingsByQuantity")) {
                        currentSeller = new Seller(currentSeller.getUserName());
                        outputStream.writeObject(currentSeller.getListingsByQuantity());
                        outputStream.flush();
                    }
                    if (received.get(0).equals("getSellerListingsByPrice")) {
                        currentSeller = new Seller(currentSeller.getUserName());
                        outputStream.writeObject(currentSeller.getListingsByPrice());
                        outputStream.flush();
                    }
                    if (received.get(0).equals("getSellerAllStores")) {
                        currentSeller = new Seller(currentSeller.getUserName());
                        outputStream.writeObject(currentSeller.getAllStores());
                        outputStream.flush();
                    }
                    if (received.get(0).equals("getSellerTargetStore")) {
                        currentSeller = new Seller(currentSeller.getUserName());
                        try {
                            outputStream.writeObject(currentSeller.getTargetStore(received.get(1)));
                            outputStream.flush();
                        } catch (ListingNotFoundException e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("getSellerTargetName")) {
                        currentSeller = new Seller(currentSeller.getUserName());
                        try {
                            outputStream.writeObject(currentSeller.getTargetName(received.get(1)));
                            outputStream.flush();
                        } catch (ListingNotFoundException e) {
                            outputStream.writeObject(e);
                        }
                    }
                    if (received.get(0).equals("getSellerTargetDesc")) {
                        currentSeller = new Seller(currentSeller.getUserName());
                        try {
                            outputStream.writeObject(currentSeller.getTargetDesc(received.get(1)));
                            outputStream.flush();
                        } catch (ListingNotFoundException e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("importSellerFileName")) {
                        try {
                            synchronized (gateKeeper) {
                                currentSeller.importListings(received.get(1));
                            }
                            outputStream.writeObject(null);
                            outputStream.flush();
                        } catch (Exception e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("modifySellerListings")) {
                        try {
                            synchronized (gateKeeper) {
                                currentSeller.modifyListings(received.get(1), received.get(2),
                                        Integer.parseInt(received.get(3)), Double.parseDouble(received.get(4)),
                                        Integer.parseInt(received.get(5)));
                            }
                            outputStream.writeObject(null);
                            outputStream.flush();
                        } catch (ListingNotFoundException e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("modifySellerListingQuantityAndPrice")) {
                        try {
                            synchronized (gateKeeper) {
                                String storeID = received.get(1);
                                String name = received.get(2);
                                int quantity = Integer.parseInt(received.get(3));
                                double price = Double.parseDouble(received.get(4));
                                currentSeller.modifyListings(storeID, name, quantity, 0, 1);
                                currentSeller.modifyListings(storeID, name, 0, price, 2);
                                outputStream.writeObject(null);
                                outputStream.flush();
                            }
                        } catch (ListingNotFoundException e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("updateSellerSales")) {
                        try {
                            synchronized (gateKeeper) {
                                currentSeller.updateSales(received.get(1), received.get(2), received.get(3),
                                        Integer.parseInt(received.get(4)), Double.parseDouble(received.get(5)));
                            }
                            outputStream.writeObject(null);
                            outputStream.flush();
                        } catch (InvalidPurchaseException e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        } catch (ListingNotFoundException e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("printSellerCustomerHistory")) {
                        try {
                            outputStream.writeObject(currentSeller.printCustomerHistory(received.get(1)));
                            outputStream.flush();
                        } catch (FileNotFoundException e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        } catch (ListingNotFoundException e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("printSellerSortedCustomerHistory")) {
                        try {
                            outputStream.writeObject(currentSeller.printSortedCustomerHistory(received.get(1)));
                            outputStream.flush();
                        } catch (FileNotFoundException e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        } catch (ListingNotFoundException e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("printSellerSalesHistory")) {
                        try {
                            outputStream.writeObject(currentSeller.printSalesHistory(received.get(1)));
                            outputStream.flush();
                        } catch (FileNotFoundException e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        } catch (ListingNotFoundException e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("printSellerSortedSalesHistory")) {
                        try {
                            outputStream.writeObject(currentSeller.printSortedSalesHistory(received.get(1)));
                            outputStream.flush();
                        } catch (FileNotFoundException e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        } catch (ListingNotFoundException e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("storesSellerBySold")) {
                        try {
                            outputStream.writeObject(currentSeller.storesBySold(Boolean.parseBoolean(received.get(1))));
                            outputStream.flush();
                        } catch (FileNotFoundException e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        } catch (ListingNotFoundException e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }

                    // buyer methods
                    if (received.get(0).equals("getBuyerUsername")) {
                        try {
                            outputStream.writeObject(currentBuyer.getUsername());
                            outputStream.flush();
                        } catch (Exception e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("setBuyerUsername")) {
                        try {
                            synchronized (gateKeeper) {
                                currentBuyer.setUsername(received.get(1));
                            }
                        } catch (Exception e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("getBuyerPassword")) {
                        try {
                            String fileName = received.get(1);
                            File f = new File(fileName);
                            BufferedReader bfr = new BufferedReader(new FileReader(f));
                            bfr.readLine();
                            String password = bfr.readLine();
                            outputStream.writeObject(password);
                            outputStream.flush();
                        } catch (FileNotFoundException e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        } catch (Exception e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("setBuyerPassword")) {
                        try {
                            synchronized (gateKeeper) {
                                currentBuyer.setPassword(received.get(1));
                            }
                        } catch (Exception e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("getBuyerCart")) {
                        currentBuyer = new Customer(currentBuyer.getUsername(), currentBuyer.getPassword(),
                                "customer_" + currentBuyer.getUsername() + ".csv",
                                currentBuyer.getUsername() + "_cart.csv");
                        outputStream.writeObject(currentBuyer.getCart());
                        outputStream.flush();
                    }
                    if (received.get(0).equals("setBuyerCart")) {
                        synchronized (gateKeeper) {
                            ArrayList<Listing> setCartListings = (ArrayList<Listing>) inputStream.readObject();
                            currentBuyer.setCart(setCartListings);
                        }
                    }
                    if (received.get(0).equals("getBuyerPurchased")) {
                        outputStream.writeObject(currentBuyer.getPurchased());
                        outputStream.flush();
                    }

                    if (received.get(0).equals("addBuyerListing")) {
                        synchronized (gateKeeper) {
                            Listing listing = (Listing) inputStream.readObject();
                            currentBuyer.addListing(listing);
                        }
                    }
                    if (received.get(0).equals("removeBuyerListing")) {
                        try {
                            synchronized (gateKeeper) {
                                Listing listing = (Listing) inputStream.readObject();
                                currentBuyer.removeListing(listing);
                                outputStream.writeObject(null);
                                outputStream.flush();
                            }
                        } catch (Exception e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("removeBuyerListingByIndex")) {
                        try {
                            synchronized (gateKeeper) {
                                currentBuyer.removeListingByIndex(Integer.parseInt(received.get(1)));
                                outputStream.writeObject(null);
                                outputStream.flush();
                            }
                        } catch (IndexOutOfBoundsException e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("printBuyerCart")) {
                        outputStream.writeObject(currentBuyer.printCart());
                        outputStream.flush();
                    }
                    if (received.get(0).equals("writeBuyerCart")) {
                        synchronized (gateKeeper) {
                            currentBuyer.writeCart(received.get(1));
                        }
                    }
                    if (received.get(0).equals("readBuyerCart")) {
                        outputStream.writeObject(currentBuyer.readCart(received.get(1)));
                        outputStream.flush();
                    }
                    if (received.get(0).equals("sortBuyerByPrice")) {
                        try {
                            synchronized (gateKeeper) {
                                currentBuyer.sortByPrice(received.get(1));
                            }
                        } catch (Exception e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("sortBuyerByQuantity")) {
                        try {
                            synchronized (gateKeeper) {
                                currentBuyer.sortByQuantity(received.get(1));
                            }
                        } catch (Exception e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("searchBuyerByName")) {
                        ArrayList<Listing> listings = (ArrayList<Listing>) inputStream.readObject();
                        currentBuyer.searchByName(received.get(1), listings);
                    }
                    if (received.get(0).equals("searchBuyerByStore")) {
                        try {
                            ArrayList<Listing> listings = (ArrayList<Listing>) inputStream.readObject();
                            outputStream.writeObject(currentBuyer.searchByStore(received.get(1), listings));
                            outputStream.flush();
                        } catch (Exception e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("writeBuyerPurchaseHistory")) {
                        synchronized (gateKeeper) {
                            currentBuyer.writePurchaseHistory();
                        }
                    }
                    if (received.get(0).equals("sortBuyerByPurchased")) {
                        outputStream.writeObject(currentBuyer.sortByPurchased(Boolean.parseBoolean(received.get(1))));
                        outputStream.flush();
                    }
                    if (received.get(0).equals("exportBuyerPurchased")) {
                        currentBuyer.exportPurchase(received.get(1));
                        /*try {
                        synchronized (gateKeeper) {
                            currentBuyer.exportPurchase(received.get(1));
                            File file = new File(received.get(1));
                            byte[] fileContent = Files.readAllBytes(file.toPath());
                            outputStream.writeObject(fileContent);
                            outputStream.flush();
                        }
                        } catch (Exception e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }*/
                    }
                    if (received.get(0).equals("readAllSellerName")) {
                        String line;
                        File sellers = new File("existingSELLER.csv");
                        ArrayList<String> currentSellers = new ArrayList<>();
                        try {
                            BufferedReader bfr = new BufferedReader(new FileReader(sellers));
                            while ((line = bfr.readLine()) != null) {
                                currentSellers.add(line);
                            }
                            outputStream.writeObject(currentSellers);
                            outputStream.flush();
                        } catch (FileNotFoundException e) {
                            outputStream.writeObject(e);
                            outputStream.flush();
                        }
                    }
                    if (received.get(0).equals("updateAfterPurchaseMade")) {
                        ArrayList<String> cartItemSeller = new ArrayList<>();
                        ArrayList<Listing> cartItems = new ArrayList<>();
                        cartItemSeller = (ArrayList<String>) inputStream.readObject();
                        cartItems = (ArrayList<Listing>) inputStream.readObject();
                        Seller updateSeller = null;
                        boolean purchaseSuccess = true;

                        synchronized (gateKeeper) {
                            try {
                                for (int i = 0; i < cartItemSeller.size(); i++) {
                                    updateSeller = new Seller(cartItemSeller.get(i));
                                    if (updateSeller.getTargetNameAndStore(cartItems.get(i).getStoreID(),
                                            cartItems.get(i).getProductName()).getPrice() !=
                                            cartItems.get(i).getPrice()) {
                                        Object wrongPrice = (String) String.valueOf(i);
                                        outputStream.writeObject(wrongPrice);
                                        outputStream.flush();
                                        purchaseSuccess = false;
                                        break;
                                    }
                                    updateSeller.updateSales(cartItems.get(i).getStoreID(),
                                            cartItems.get(i).getProductName(),
                                            currentBuyer.getUsername(),
                                            cartItems.get(i).getQuantityBought(),
                                            cartItems.get(i).getPrice() *
                                                    cartItems.get(i).getQuantityBought());
                                }
                                if (purchaseSuccess) {
                                    currentBuyer.writePurchaseHistory();
                                    currentBuyer.writeCart(currentBuyer.getUsername() +
                                            "_cart.csv");
                                    outputStream.writeObject(null);
                                    outputStream.flush();
                                }
                            } catch (FileNotFoundException e) {
                                System.out.println(e.getMessage());
                                e.printStackTrace();
                            } catch (ListingNotFoundException e) {
                                outputStream.writeObject(e);
                                outputStream.flush();
                            } catch (InvalidPurchaseException e) {
                                outputStream.writeObject(e);
                                outputStream.flush();
                            }
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (EOFException e) {
                    System.out.println("Client disconnected!");
                    inputStream.close();
                    outputStream.close();
                    break; // Client has finished sending data, break out of the loop
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}