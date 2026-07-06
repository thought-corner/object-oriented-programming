package strategy.discount;

import strategy.domain.DiscountType;
import strategy.domain.Item;

public interface DiscountStrategy {

    boolean support(DiscountType discountType);   // 해당 할인 타입을 지원하는지?

    int calculate(Item item);                     // 할인 적용가 계산
}