package strategy.factory;

import java.util.List;

import strategy.discount.DiscountStrategy;
import strategy.domain.DiscountType;

public class DiscountStrategyFactory {

    private final List<DiscountStrategy> discountStrategies;

    public DiscountStrategyFactory(List<DiscountStrategy> discountStrategies) {
        this.discountStrategies = discountStrategies;
    }

    public DiscountStrategy getStrategy(DiscountType discountType) {
        return discountStrategies.stream()
                .filter(strategy -> strategy.support(discountType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 할인 타입입니다: " + discountType));
    }
}