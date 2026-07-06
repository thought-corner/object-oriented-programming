package strategy.discount;

import strategy.domain.DiscountType;
import strategy.domain.Item;

public class FirstGuestDiscountStrategy implements DiscountStrategy {

    @Override
    public boolean support(DiscountType discountType) {
        return discountType.equals(DiscountType.FIRST_GUEST);
    }

    @Override
    public int calculate(Item item) {
        return item.getPrice() - (int) (item.getPrice() * 0.1); // 10% 할인
    }
}