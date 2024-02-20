package model;

public class Order {

    private int orderId;
    private String clientName;
    private String productName;
    private double orderTotal;
    private int orderQuantity;

    public Order(int orderId, String clientName, String productName, double orderTotal, int orderQuantity) {
        this.orderId = orderId;
        this.clientName = clientName;
        this.productName = productName;
        this.orderTotal = orderTotal;
        this.orderQuantity = orderQuantity;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(double orderTotal) {
        this.orderTotal = orderTotal;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    @Override
    public String toString() {
        return "Order: " + orderId +
                ", for client: " + clientName +
                ", product: " + productName +
                ", quantity: " + orderQuantity +
                ", total: " + orderTotal;
    }

}
