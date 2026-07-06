package strategy.discount;

import strategy.domain.DiscountType;
import strategy.domain.Item;

public class OldSaleDiscountStrategy implements DiscountStrategy {

    @Override
    public boolean support(DiscountType discountType) {
        return discountType.equals(DiscountType.OLD_SALE);
    }

    @Override
    public int calculate(Item item) {
        return item.getPrice() - 500;   // 500원 할인
    }
}