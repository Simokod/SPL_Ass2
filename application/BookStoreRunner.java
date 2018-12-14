package bgu.spl.mics.application;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

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
            JsonPrimitive JsonSpeed = JsonTime.getAsJsonPrimitive("speed");
            int speed = gson.fromJson(JsonSpeed, int.class);
            JsonPrimitive JsonDuration = JsonTime.getAsJsonPrimitive("duration");
            int duration = gson.fromJson(JsonDuration, int.class);
            TimeService timeService = new TimeService(speed, duration);
            Thread timeServiceT = new Thread(timeService);

            // creating SellingServices
            JsonPrimitive JsonSellings = JsonServices.getAsJsonPrimitive("selling");
            int amountOfSellingServices = gson.fromJson(JsonSellings, int.class);
            Thread[] sellingServices = new Thread[amountOfSellingServices];
            for(int i=0;i<amountOfSellingServices;i++)
                sellingServices[i] = new Thread(new SellingService("Selling Service " + (i + 1)));

            // creating InventoryServices
            JsonPrimitive JsonInventories = JsonServices.getAsJsonPrimitive("inventoryService");
            int amountOfInventoryServices = gson.fromJson(JsonInventories, int.class);
            Thread[] inventoryServices = new Thread[amountOfInventoryServices];
            for(int i=0;i<amountOfInventoryServices;i++)
                inventoryServices[i] = new Thread(new InventoryService("Inventory Service " + (i + 1)));

            // creating LogisticsServices
            JsonPrimitive JsonLogistics = JsonServices.getAsJsonPrimitive("logistics");
            int amountOfLogisticsServices = gson.fromJson(JsonLogistics, int.class);
            Thread[] logisticsServices = new Thread[amountOfLogisticsServices];
            for(int i=0;i<amountOfLogisticsServices;i++)
                logisticsServices[i] = new Thread(new LogisticsService("Logistics Service "+(i+1)));

            // creating ResourceServices
            JsonPrimitive JsonResources = JsonServices.getAsJsonPrimitive("resourcesService");
            int amountOfResourcesServices = gson.fromJson(JsonResources, int.class);
            Thread[] resourceServices = new Thread[amountOfResourcesServices];
            for(int i=0;i<amountOfResourcesServices;i++)
                resourceServices[i] = new Thread(new ResourceService("Resource Service "+(i+1)));

            // creating customers list
            JsonArray JsonArrCustomers = JsonServices.getAsJsonArray("customers");
            JsonCustomer[] JsonCustomersList = gson.fromJson(JsonArrCustomers, JsonCustomer[].class);
            int amountOfCustomers = JsonCustomersList.length;
            Customer[] customers = new Customer[amountOfCustomers];
            for(int i=0;i<amountOfCustomers;i++) {
                customers[i] = JsonToCustomer(JsonCustomersList[i]);
            }
            // creating APIServices
            AtomicInteger orderId = new AtomicInteger(0);
            Thread[] APIServices = new Thread[amountOfCustomers];
            for(int i=0;i<amountOfCustomers;i++) {
                LinkedList<OrderPair> orderList = new LinkedList<>();
                for(int j=0;j<JsonCustomersList[i].orderSchedule.length;j++)
                    orderList.add(new OrderPair(JsonCustomersList[i].orderSchedule[j].bookTitle, JsonCustomersList[i].orderSchedule[j].tick));
                APIServices[i] = new Thread(new APIService("API Service " + (i+1), orderId, customers[i], orderList));
                orderId.addAndGet(JsonCustomersList[i].orderSchedule.length);
            }


            // TODO: initialize and run all services
            runServices(sellingServices);
            runServices(inventoryServices);
            runServices(logisticsServices);
            runServices(resourceServices);
            runServices(APIServices);
            //runs and initializes time service after all of the other services has been initialized
            Thread.sleep(1000);     // TODO: need to make sure all other services have initialized
//            while (!(isSubscribedToTime(APIServices) & isSubscribedToTime(sellingServices))) ;
            System.out.println("starting time");
                timeServiceT.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // initializes an array of MicroServices
    private static void runServices(Thread[] services){
        for (int i=0;i<services.length;i++)
            services[i].start();
    }
    // checking if all APIServices have subscribed to time Broadcast
    private static boolean isSubscribedToTime(APIService[] services){
        for (int i=0;i<services.length;i++)
            if(!services[i].isSubscribedToTime())
                return false;
        return true;
    }
    // checking if all SellingServices have subscribed to time Broadcast
    private static boolean isSubscribedToTime(SellingService[] services){
        for (int i=0;i<services.length;i++)
            if(!services[i].isSubscribedToTime())
                return false;
        return true;
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

