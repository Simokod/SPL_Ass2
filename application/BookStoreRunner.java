package bgu.spl.mics.application;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;

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

            // creating TimeService
            JsonObject JsonTime = JsonServices.getAsJsonObject("time");
            TimeService timeService = gson.fromJson(JsonTime, TimeService.class);

            // creating SellingServices
            JsonPrimitive JsonSellings = JsonServices.getAsJsonPrimitive("selling");
            int amountOfSellingServices = gson.fromJson(JsonSellings, int.class);
            SellingService[] sellingServices = new SellingService[amountOfSellingServices];
            for(int i=0;i<amountOfSellingServices;i++)
                sellingServices[i] = new SellingService("Selling Service"+i);

            // creating InventoryServices
            JsonPrimitive JsonInventories = JsonServices.getAsJsonPrimitive("inventoryService");
            int amountOfInventoryServices = gson.fromJson(JsonInventories, int.class);
            InventoryService[] inventoryServices = new InventoryService[amountOfInventoryServices];
            for(int i=0;i<amountOfInventoryServices;i++)
                inventoryServices[i] = new InventoryService("Inventory Service"+i);

            // creating LogisticsServices
            JsonPrimitive JsonLogistics = JsonServices.getAsJsonPrimitive("logistics");
            int amountOfLogisticsServices = gson.fromJson(JsonLogistics, int.class);
            LogisticsService[] logisticsServices = new LogisticsService[amountOfLogisticsServices];
            for(int i=0;i<amountOfLogisticsServices;i++)
                logisticsServices[i] = new LogisticsService("Logistics Service"+i);

            // creating ResourceServices
            JsonPrimitive JsonResources = JsonServices.getAsJsonPrimitive("resourcesService");
            int amountOfResourcesServices = gson.fromJson(JsonResources, int.class);
            ResourceService[] resourceServices = new ResourceService[amountOfResourcesServices];
            for(int i=0;i<amountOfLogisticsServices;i++)
                resourceServices[i] = new ResourceService("Resource Service"+i);

            // creating customers list
            JsonArray JsonArrCustomers = JsonServices.getAsJsonArray("customers");
            JsonCustomer[] JsoncustomersList = gson.fromJson(JsonArrCustomers, JsonCustomer[].class);
            Customer[] customers = new Customer[JsoncustomersList.length];
            for(int i=0;i<JsoncustomersList.length;i++) {
                customers[i] = JsonToCustomer(JsoncustomersList[i]);
            }
            // creating APIServices
            int orderId=0;
            APIService[] APIServices = new APIService[JsoncustomersList.length];
            for(int i=0;i<JsoncustomersList.length;i++) {
                HashMap<String, Integer> orderList = new HashMap<>();
                for(int j=0;i<JsoncustomersList[i].orderSchedule.length;i++)
                    orderList.put(JsoncustomersList[i].orderSchedule[j].bookTitle, JsoncustomersList[i].orderSchedule[j].tick);
                APIServices[i] = new APIService("API Service" + i, orderId, customers[i], orderList);
                orderId+=JsoncustomersList[i].orderSchedule.length;
            }

            // TODO: initialize and run all services
        } catch (Exception e) {
            e.printStackTrace();
        }
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
