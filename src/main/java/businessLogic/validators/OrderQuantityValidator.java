package businessLogic.validators;

import model.Order;

public class OrderQuantityValidator implements Validator<Order>{

    private static final int MIN_QUANTITY = 0;
    @Override
    public boolean validate(Order order) {
        if(order.getOrderQuantity() < MIN_QUANTITY){
            throw new IllegalArgumentException("The quantity limit is not respected");
        }
        return false;
    }
}
