package bgu.spl.mics.application;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class ReadOutPutFiles {
    public static void main(String args[]){
        // reading books output
        try {
            FileInputStream inputStream = new FileInputStream(("books"));
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            System.out.println("books:");
            System.out.println(ois.readObject().toString());
            ois.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // reading customers output
        try {
            FileInputStream inputStream = new FileInputStream(("customers"));
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            System.out.println("customers:");
            System.out.println(ois.readObject());
            ois.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // reading receipts output
        try {
            FileInputStream inputStream = new FileInputStream(("orderReceipts"));
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            System.out.println("receipts");
            System.out.println(ois.readObject());
            ois.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // reading MoneyRegister object
        try {
            FileInputStream inputStream = new FileInputStream(("moneyRegister"));
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            System.out.println("moneyRegi:");
            System.out.println(ois.readObject().toString());
            ois.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
