package businessLogic.validators;

import model.Order;

public class OrderTotalValidator implements Validator<Order> {

    private static final int MIN_PRICE = 0;
    @Override
    public boolean validate(Order order) {

        if(order.getOrderTotal() < MIN_PRICE){
            throw new IllegalArgumentException("Order total is not respected!");
        }

        return false;
    }
}
