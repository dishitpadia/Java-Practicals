package discountEngine.src.com.discount.model;

@FunctionalInterface
public interface DiscountRule {
    double apply(double price);
}
