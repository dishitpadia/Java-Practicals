package discountEngine.src.com.discount.service;

import java.util.List;
import java.util.ArrayList;
import discountEngine.src.com.discount.model.DiscountRule;


public class DiscountService {

    public List<Double> applyDiscount(List<Double> prices, DiscountRule rule) {
        List<Double> discountedPrices = new ArrayList<>();

        for (double price : prices) {
            discountedPrices.add(rule.apply(price));
        }

        return discountedPrices;
    }
}
