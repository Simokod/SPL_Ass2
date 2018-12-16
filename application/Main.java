package bgu.spl.mics.application;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
public class Main {

    public static void main(String[] args) {

        Scanner myScanner = new Scanner(System.in);
        System.out.println("Enter directory path to place Results folder");
        String path = myScanner.nextLine();
        System.out.println("Enter amount of Jsons to generate");
        final int numberOfJsons = myScanner.nextInt();
        System.out.println("Enter number of runs for each Json file");
        final int numberOfRuns = myScanner.nextInt();

        System.out.println("-------------------Test starts !!!-------------------");

        for (int i = 0; i < numberOfJsons; i++) {
            String json = JsonGenerator.createJson();
            String folder = String.format("%s//Results//Json%d",path, i + 1);
            String jsonPath = String.format("%s//json%d.json",folder ,i+1);
            File jsonFile = new File(jsonPath);
            jsonFile.getParentFile().mkdirs();
            String [] outputs = new String[4];
            try {
                jsonFile.createNewFile();

                FileWriter fileWriter = new FileWriter(jsonFile);
                fileWriter.write(json);
                fileWriter.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int j = 1; j <= numberOfRuns; j++) {
                String outputFolder = String.format(String.format("%s//Json%dRunNumber%d//", folder, i+1, j));
                for(int k = 0; k <= 3; k++) {
                    String outputPath = String.format("%s//output%d.ser",outputFolder,k+1);
                    File output1 = new File(outputPath);
                    output1.getParentFile().mkdirs();
                    outputs[k] = outputPath;
                    try {
                        output1.createNewFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                String [] args1 = {jsonPath,outputs[0], outputs[1], outputs[2], outputs[3]};
                try {
                    System.out.println(String.format("-------------------Test %d.%d started-------------------",i+1,j));
                    BookStoreRunner.main(args1);
                }
                catch (Exception e){
                    String errorsFolder = String.format("%s//Results//JsonErrors//Json%dErrors",path,i+1);
                    String errorPath = String.format("%s//json%dRunNumber%dError.json",errorsFolder ,i+1, j);
                    File errorFile = new File(errorPath);
                    errorFile.getParentFile().mkdirs();
                    System.out.println(String.format("Test failed. Check jsonError %d folder, runNumber %d",i+1,j));
                    try {
                        errorFile.createNewFile();
                        PrintWriter errorsWriter = new PrintWriter(errorFile);
                        e.printStackTrace(errorsWriter);
                        errorsWriter.close();
                    }
                    catch(Exception e1){}
                }
            }
        }
    }

    private static class JsonGenerator {
        private static final int InventoryMin = 1;
        private static final int InventoryMax = 10;
        private static int InventorySize;
        private static final int ResourcesMin = 1;
        private static final int ResourcesMax = 5;
        private static final int MinVehicleSpeed = 1;
        private static final int MaxVehicleSpeed = 500;
        private static final int MinSpeed = 1;
        private static final int MaxSpeed = 500;
        private static final int MinDuration = 1;
        private static final int MaxDuration = 20;
        private static int DurationSize;
        private static final int MaxServiceAmount = 10;
        private static final int MaxCustomers = 10;
        private static final int MaxDistance = 100;
        private static final int MaxCredit = 1000;
        private static final int MaxOrders = 10;

        public static String createJson() {
            return "{" + createInventory() + "," + createResources() + "," + createServices() + "," + createCustomers() + "}}";
        }

        private static String createInventory() {
            int randomAmount = random(InventoryMin, InventoryMax);
            String[] Inventory = new String[randomAmount];
            for (int i = 0; i < randomAmount; i++) {
                String bookTitle = String.format("Book %d", i + 1);
                int amount = random(0, 15);
                int price = random(0, 201);
                Inventory[i] = "{\"bookTitle\":\"" + bookTitle + "\", \"amount\": " + amount + ", \"price\": " + price + "}";
            }
            InventorySize = Inventory.length;
            return "\"initialInventory\": " + Arrays.toString(Inventory);
        }

        private static String createResources() {
            int randomAmount = random(ResourcesMin, ResourcesMax);
            String[] Resources = new String[randomAmount];
            for (int i = 0; i < randomAmount; i++) {
                int speed = random(MinVehicleSpeed, MaxVehicleSpeed);
                Resources[i] = String.format("{\"license\":%d, \"speed\": %d}", i + 1, speed);
            }
            return " \"initialResources\":[ {\"vehicles\": " + Arrays.toString(Resources) + "}]";
        }

        private static String createServices() {
            int speed = random(MinSpeed, MaxSpeed);
            int duration = random(MinDuration, MaxDuration);
            String time = String.format("\"time\": {\"speed\": %d,\"duration\": %d}", speed, duration);
            String selling = String.format("\"selling\": %d", random(1, MaxServiceAmount));
            String inventory = String.format("\"inventoryService\": %d", random(1, MaxServiceAmount));
            String logistics = String.format("\"logistics\": %d", random(1, MaxServiceAmount));
            String resources = String.format("\"resourcesService\": %d", random(1, MaxServiceAmount));
            String result = time + "," + selling + "," + inventory + "," + logistics + "," + resources;
            DurationSize = duration;
            return "\"services\":{ " + result;
        }

        private static String createCustomers() {
            int randomAmount = random(1, MaxCustomers);
            String[] Customers = new String[randomAmount];
            for (int i = 0; i < randomAmount; i++) {
                int distance = random(1, MaxDistance);
                int amount = random(1, MaxCredit);
                int bookOrders = random(1, MaxOrders);
                String[] Orders = new String[bookOrders];
                for (int j = 0; j < bookOrders; j++) {
                    int bookIndex = random(0, InventorySize - 1);
                    int tick = random(1, DurationSize);
                    String book = String.format("{\"bookTitle\": \"Book %d\", \"tick\": %d}", bookIndex+1, tick);
                    Orders[j] = book;
                }
                String details = String.format("{\"id\": %d,\"name\": \"Person%d\",\"address\": \"Address%d\",\"distance\":%d,\"creditCard\":{\"number\":%d,\"amount\":%d}", i + 1, i + 1, i + 1, distance, i + 1, amount);
                String orderSchedule = Arrays.toString(Orders);
                String customer = details + ", \"orderSchedule\": " + orderSchedule + "}";
                Customers[i] = customer;
            }
            return " \"customers\": " + Arrays.toString(Customers);
        }

        private static int random(int min, int max) {
            return ThreadLocalRandom.current().nextInt(min, max + 1);
        }
    }
}