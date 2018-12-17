package bgu.spl.mics.application;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import  bgu.spl.mics.application.passiveObjects.*;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;


/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {
        try {
            String filename = args[0];
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(filename));
            JsonObject input = gson.fromJson(reader, JsonObject.class);

            // creating initial inventory
            JsonArray JsoninitialInventory = input.getAsJsonArray("initialInventory");
            BookInventoryInfo[] initialInventory = gson.fromJson(JsoninitialInventory, BookInventoryInfo[].class);
            Inventory.getInstance().load(initialInventory);

            // creating vehicles
            JsonArray Jsonresources = input.getAsJsonArray("initialResources");
            JsonObject JsonObjvehicles = Jsonresources.get(0).getAsJsonObject();
            JsonArray JsonVehicles = JsonObjvehicles.getAsJsonArray("vehicles");
            DeliveryVehicle[] vehicles = gson.fromJson(JsonVehicles, DeliveryVehicle[].class);
            ResourcesHolder.getInstance().load(vehicles);

            // Json Services
            JsonObject JsonServices = input.getAsJsonObject("services");

            //Read Selling Json
            JsonPrimitive JsonSellings = JsonServices.getAsJsonPrimitive("selling");
            int amountOfSellingServices = gson.fromJson(JsonSellings, int.class);
            //Read Inventory Json
            JsonPrimitive JsonInventories = JsonServices.getAsJsonPrimitive("inventoryService");
            int amountOfInventoryServices = gson.fromJson(JsonInventories, int.class);
            //Read Logistics Json
            JsonPrimitive JsonLogistics = JsonServices.getAsJsonPrimitive("logistics");
            int amountOfLogisticsServices = gson.fromJson(JsonLogistics, int.class);
            //Read Resources Json
            JsonPrimitive JsonResources = JsonServices.getAsJsonPrimitive("resourcesService");
            int amountOfResourcesServices = gson.fromJson(JsonResources, int.class);
            //Read Customers Json
            JsonArray JsonArrCustomers = JsonServices.getAsJsonArray("customers");
            JsonCustomer[] JsonCustomersList = gson.fromJson(JsonArrCustomers, JsonCustomer[].class);
            int amountOfCustomers = JsonCustomersList.length;

                // total amount of services for CountDownLatch
            int amountOfTotalServices=amountOfInventoryServices+amountOfLogisticsServices+
                    amountOfCustomers+amountOfResourcesServices+amountOfSellingServices;

            CountDownLatch latch =new CountDownLatch(amountOfTotalServices);

            // creating TimeService
            JsonObject JsonTime = JsonServices.getAsJsonObject("time");
            JsonPrimitive JsonSpeed = JsonTime.getAsJsonPrimitive("speed");
            int speed = gson.fromJson(JsonSpeed, int.class);
            JsonPrimitive JsonDuration = JsonTime.getAsJsonPrimitive("duration");
            int duration = gson.fromJson(JsonDuration, int.class);
            CountDownLatch programTimer = new CountDownLatch(duration);
            TimeService timeService = new TimeService(speed, duration, programTimer);
            Thread timeServiceT = new Thread(timeService);

            // creating SellingServices
            SellingService[] sellingServices = new SellingService[amountOfSellingServices];
            Thread[] sellingServicesThreads = new Thread[amountOfSellingServices];
            for(int i=0;i<amountOfSellingServices;i++) {
                sellingServices[i] = new SellingService("Selling Service " + (i + 1), latch);
                sellingServicesThreads[i] = new Thread(sellingServices[i]);
            }

            // creating InventoryServices
            InventoryService[] inventoryServices = new InventoryService[amountOfInventoryServices];
            Thread[] inventoryServicesThreads = new Thread[amountOfInventoryServices];
            for(int i=0;i<amountOfInventoryServices;i++) {
                inventoryServices[i] = new InventoryService("Inventory Service " + (i + 1), latch);
                inventoryServicesThreads[i] = new Thread(inventoryServices[i]);
            }

            // creating LogisticsServices
            LogisticsService[] logisticsServices = new LogisticsService[amountOfLogisticsServices];
            Thread[] logisticsServicesThreads = new Thread[amountOfLogisticsServices];
            for(int i=0;i<amountOfLogisticsServices;i++) {
                logisticsServices[i] = new LogisticsService("Logistics Service " + (i + 1), duration, latch);
                logisticsServicesThreads[i] = new Thread(logisticsServices[i]);
            }

            // creating ResourceServices
            ResourceService[] resourceServices = new ResourceService[amountOfResourcesServices];
            Thread[] resourceServicesThreads = new Thread[amountOfResourcesServices];
            for(int i=0;i<amountOfResourcesServices;i++) {
                resourceServices[i] = new ResourceService("Resource Service " + (i + 1), latch);
                resourceServicesThreads[i] = new Thread(resourceServices[i]);
            }

            // creating customers list
            Customer[] customers = new Customer[amountOfCustomers];
            for(int i=0;i<amountOfCustomers;i++) {
                customers[i] = JsonToCustomer(JsonCustomersList[i]);
            }
            // creating APIServices
            int orderId =0;
            APIService[] APIServices = new APIService[amountOfCustomers];
            Thread[] APIServicesThreads = new Thread[amountOfCustomers];
            for(int i=0;i<amountOfCustomers;i++) {
                LinkedList<OrderPair> orderList = new LinkedList<>();
                for(int j=0;j<JsonCustomersList[i].orderSchedule.length;j++)
                    orderList.add(new OrderPair(JsonCustomersList[i].orderSchedule[j].bookTitle, JsonCustomersList[i].orderSchedule[j].tick));
                APIServices[i] = new APIService("API Service " + (i+1), orderId, customers[i], orderList, latch);
                APIServicesThreads[i] = new Thread(APIServices[i]);
                orderId+=JsonCustomersList[i].orderSchedule.length;
            }

            // initializing and starting all services
            runServices(sellingServicesThreads);
            runServices(inventoryServicesThreads);
            runServices(logisticsServicesThreads);
            runServices(resourceServicesThreads);
            runServices(APIServicesThreads);
            // waiting for all MicroServices to initialize
            latch.await();
            // starting the TimeService
            timeServiceT.start();
            // waiting for the program to finish
            programTimer.await();

            // printing to a file a Customers HashMap
            printCustomers(args[1], customers);
            // printing to a file all books
            Inventory.getInstance().printInventoryToFile(args[2]);
            // printing to a file all receipts
            MoneyRegister.getInstance().printOrderReceipts(args[3]);
            // printing to a file the MoneyRegister object
            printMoneyRegister(args[4], MoneyRegister.getInstance());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printCustomers(String filename, Customer[] customers){
        HashMap<Integer, Customer> toFile = new HashMap<>();
        for(Customer customer: customers)
            toFile.put(customer.getId(), customer);
        try {
            FileOutputStream fileOut = new FileOutputStream((filename));
            ObjectOutputStream oos = new ObjectOutputStream(fileOut);
            oos.writeObject(toFile);
            oos.close();
            fileOut.close();
        } catch (Exception e) { e.printStackTrace(); }
    }
    private static void printMoneyRegister(String filename, MoneyRegister regi){
        try {
            FileOutputStream fileOut = new FileOutputStream((filename));
            ObjectOutputStream oos = new ObjectOutputStream(fileOut);
            oos.writeObject(regi);
            oos.close();
            fileOut.close();
        } catch (Exception e) { e.printStackTrace(); }
    }
    // initializes an array of MicroServices
    private static void runServices(Thread[] servicesThreads){
        for (int i=0;i<servicesThreads.length;i++)
            servicesThreads[i].start();
    }

    private static Customer JsonToCustomer(JsonCustomer cust){
        return new Customer(cust.name, cust.id, cust.address, cust.distance, cust.creditCard.amount,
                cust.creditCard.number);
    }
}

class JsonCustomer{

    public int id;
    public String name;
    public String address;
    public int distance;
    public JsonCreditCard creditCard;
    public BookOrderSchedule[] orderSchedule;

    class JsonCreditCard {
        public int number;
        public int amount;
    }
    class BookOrderSchedule {
        public String bookTitle;
        public int tick;
    }
}

