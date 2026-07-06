package strategy.client;

import java.util.List;

import strategy.discount.DiscountStrategy;
import strategy.discount.FirstGuestDiscountStrategy;
import strategy.discount.OldSaleDiscountStrategy;
import strategy.domain.DiscountType;
import strategy.domain.Item;
import strategy.factory.DiscountStrategyFactory;

public class Store {

    public static void main(String[] args) {
        Item apple = new Item("사과", 5000);
        Item banana = new Item("바나나", 3000);

        // 사용 가능한 전략들을 모아 팩토리에 주입
        DiscountStrategyFactory discountStrategyFactory = new DiscountStrategyFactory(
                List.of(new FirstGuestDiscountStrategy(), new OldSaleDiscountStrategy())
        );

        // 첫 손님이 왔을 때: 타입으로 전략 구현체 선택
        DiscountStrategy firstGuestStrategy = discountStrategyFactory.getStrategy(DiscountType.FIRST_GUEST);
        System.out.println("첫 손님 할인 적용가: " + firstGuestStrategy.calculate(apple));

        // 덜 신선한 과일을 팔 때: 타입으로 전략 구현체 선택
        DiscountStrategy oldSaleStrategy = discountStrategyFactory.getStrategy(DiscountType.OLD_SALE);
        System.out.println("덜 신선한 과일 할인 적용가: " + oldSaleStrategy.calculate(banana));
    }
}