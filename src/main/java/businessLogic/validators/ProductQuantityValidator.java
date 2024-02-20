package businessLogic.validators;

import model.Product;

public class ProductQuantityValidator implements Validator<Product>{

    private static final int MIN_QUANTITY = 0;
    private static final int MAX_QUANTITY = 99999;
    @Override
    public boolean validate(Product product) {

        if(product.getQuantity() < MIN_QUANTITY || product.getQuantity() > MAX_QUANTITY){
            throw new IllegalArgumentException("The quantity limit is not respected!");
        }

        return false;
    }
}
