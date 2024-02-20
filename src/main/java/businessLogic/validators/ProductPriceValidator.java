package businessLogic.validators;

import model.Product;

public class ProductPriceValidator implements Validator<Product>{

    private static final int MIN_PRICE = 0;
    @Override
    public boolean validate(Product product) {
        if(product.getProductPrice() < MIN_PRICE){
            throw new IllegalArgumentException("The price limit is not respected!");
        }
        return false;
    }
}
