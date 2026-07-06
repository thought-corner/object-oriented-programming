# 전략 패턴 (Strategy Pattern)

## 1. 상황: 과일 매장 가격 계산

한 과일 매장에서 상황에 따라 서로 다른 **가격 할인 정책**을 적용해서 최종 결제 금액을 계산한다.

- 첫 손님 할인 정책
- 덜 신선한 과일 할인 정책
- (앞으로도 새로운 할인 정책이 추가될 수 있음)

가격 계산이라는 **동일한 문제**를 푸는 데 사용되는 **알고리즘(할인 정책)이 여러 개** 존재하는 상황이다.

---

## 2. 패턴 적용 전 — 조건문으로 분기하는 코드

할인 정책이 늘어날 때마다 계산기 코드에 `if-else`가 계속 추가된다.

```java
public int calculate(Item item, String discountType) {
    if (discountType.equals("FIRST_GUEST")) {
        // 첫 손님 할인: 10% 할인
        return item.getPrice() - (int) (item.getPrice() * 0.1);
    } else if (discountType.equals("OLD_SALE")) {
        // 덜 신선한 과일 할인: 정액 500원 할인
        return item.getPrice() - 500;
    }
    return item.getPrice();
}
```

### 무엇이 문제인가

- 새로운 할인 정책이 생길 때마다 `calculate()` 메서드를 **직접 수정**해야 한다.
- 할인 종류를 판별하는 로직과 실제 계산 로직이 **한 메서드에 뒤섞여** 있다.
- 조건 분기가 늘어날수록 코드가 복잡해지고 실수하기 쉬워진다. → **개방-폐쇄 원칙(OCP) 위반**

---

## 3. 패턴 적용 — 알고리즘을 전략으로 분리

변하는 부분(할인 계산 알고리즘)을 **인터페이스로 추상화**한다. 각 전략은 자신이 **어떤 할인 타입을 지원하는지**(`support`)와 **어떻게 계산하는지**(`calculate`)를 스스로 안다. 클라이언트는 `if-else` 없이 팩토리에게 타입만 넘겨 알맞은 전략을 받아 쓴다.

### 3.1 아이템

```java
public class Item {

    private String name;
    private int price;

    public Item(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public int getPrice() { return price; }
}
```

### 3.2 할인 타입 (DiscountType)

전략을 식별하는 키. 문자열 대신 enum으로 관리해 오타·잘못된 값을 컴파일 단계에서 막는다.

```java
public enum DiscountType {

    FIRST_GUEST("FIRST_GUEST", "첫 손님 할인"),
    OLD_SALE("OLD_SALE", "덜 신선한 과일");

    private final String code;
    private final String description;

    DiscountType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }
}
```

### 3.3 전략 인터페이스 (Strategy)

`support`로 "내가 이 타입을 처리할 수 있는지"를 답하고, `calculate`로 실제 할인가를 계산한다.

```java
public interface DiscountStrategy {

    boolean support(DiscountType discountType);   // 해당 할인 타입을 지원하는지?

    int calculate(Item item);                     // 할인 적용가 계산
}
```

### 3.4 구체적인 전략들 (Concrete Strategy)

```java
// 첫 손님 할인 정책: 10% 할인
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
```

```java
// 덜 신선한 과일 할인 정책: 정액 500원 할인
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
```

### 3.5 전략 선택 (Factory)

전략들을 **리스트로 주입받아** 두고, 요청한 타입을 `support`하는 전략을 **순회하며 찾아** 돌려준다. 전략이 늘어도 이 코드는 바뀌지 않는다 — 리스트에 구현체 하나만 추가하면 된다.

```java
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
```

### 3.6 사용하는 코드 (Client)

클라이언트는 어떤 구현체가 있는지 몰라도 된다. **할인 타입만** 팩토리에 넘기면 알맞은 전략을 받아 쓴다.

```java
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
```

실행 결과:

```
첫 손님 할인 적용가: 4500
덜 신선한 과일 할인 적용가: 2500
```

---

## 4. 구조 정리

| 역할 | 클래스 | 설명 |
| --- | --- | --- |
| Strategy | `DiscountStrategy` | 알고리즘(할인 정책)을 표현하는 추상 인터페이스. `support` + `calculate` |
| Concrete Strategy | `FirstGuestDiscountStrategy`, `OldSaleDiscountStrategy` | 실제 할인 알고리즘 구현. 자신이 지원하는 타입을 스스로 안다 |
| Factory | `DiscountStrategyFactory` | 전략 목록을 순회하며 타입에 맞는 전략을 선택해 반환 |
| Client | `Store` | 할인 타입만 넘겨 전략을 받아 사용. 구현체를 직접 알지 않음 |

```
        Store (Client)
             │ getStrategy(type)
             ▼
   DiscountStrategyFactory  ──순회(support)──►  List<DiscountStrategy>
             │ 반환
             ▼
      DiscountStrategy (Strategy)  ◄── interface
             ▲
      ┌──────┴───────────┐
FirstGuestDiscount   OldSaleDiscount   (Concrete Strategy)
```

> 특정 문제를 푸는 **알고리즘을 별도 객체로 분리**하는 설계가 전략 패턴이다.
> 여기서는 각 전략이 `support`로 자신의 적용 대상을 알리고, `DiscountStrategyFactory`가 이를 순회해 알맞은 전략을 골라 준다.

---

## 5. 전략 패턴을 언제 쓰나

- 한 가지 기능(문제)을 푸는 방법(알고리즘)이 **여러 개** 있고, 상황에 따라 **바꿔 끼워야** 할 때.
- 알고리즘을 **런타임에 교체**하고 싶을 때.
- `if-else` 로 분기하던 로직을 **각각의 객체로 분리**하고 싶을 때.

---

## 6. 장점

- **기존 코드를 변경하지 않고** 새로운 전략(할인 정책)을 추가할 수 있다. → **개방-폐쇄 원칙(OCP) 준수**
- 어떤 전략을 쓸지 고르는 `if-else`가 팩토리의 `support` 순회로 대체되어, 클라이언트에서 분기가 사라진다.
- 각 알고리즘을 **독립적으로 테스트**할 수 있다.
- 전략을 **런타임에 교체**할 수 있어 유연하다.

## 7. 주의할 점

- 전략의 개수만큼 **클래스가 늘어난다.**
- 새 전략을 추가하면 팩토리에 넘기는 **리스트에도 등록**해야 실제로 선택된다.
- 두 전략이 같은 타입을 `support`하면 순회 순서상 **먼저 걸린 전략**이 선택되므로, 타입은 전략과 1:1이 되도록 관리한다.