package bgu.spl.mics.application.passiveObjects;

public class OrderPair {
    // a pair object for customer's orders
    private String bookTitle;
    private int orderTick;

    public OrderPair(String bookTitle, int orderTick){
        this.bookTitle = bookTitle;
        this.orderTick = orderTick;
    }
    public String getBookTitle() { return bookTitle; }
    public int getOrderTick() { return orderTick; }
}