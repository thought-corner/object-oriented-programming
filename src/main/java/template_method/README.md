# 템플릿 메서드 패턴 (Template Method Pattern)

## 1. 상황: 카페인 음료 만들기

카페에서 **커피**와 **홍차**를 만든다. 두 음료의 준비 과정은 큰 틀이 똑같다.

```
물을 끓인다 → 우려낸다 → 컵에 따른다 → 첨가물을 넣는다
```

- 물을 끓이고, 컵에 따르는 단계는 **모든 음료가 동일**하다. (변하지 않는 부분)
- 우려내는 방식(`brew`)과 첨가물(`addCondiments`)만 음료마다 **다르다**. (변하는 부분)

**전체 절차(알고리즘의 뼈대)는 고정**되어 있고, 그중 **일부 단계만 달라지는** 상황이다.

---

## 2. 패턴 적용 전 — 절차를 통째로 중복 작성

음료마다 준비 과정을 처음부터 끝까지 각각 작성한다.

```java
public class Coffee {
    public void prepare() {
        System.out.println("물을 끓인다");        // 중복
        System.out.println("필터로 커피를 내린다");
        System.out.println("컵에 따른다");         // 중복
        System.out.println("설탕과 우유를 추가한다");
    }
}

public class Tea {
    public void prepare() {
        System.out.println("물을 끓인다");        // 중복
        System.out.println("찻잎을 우려낸다");
        System.out.println("컵에 따른다");         // 중복
        System.out.println("레몬을 추가한다");
    }
}
```

### 무엇이 문제인가

- 물 끓이기·컵에 따르기 같은 **공통 단계가 클래스마다 중복**된다.
- "물 → 우려내기 → 따르기 → 첨가물" 이라는 **절차의 순서(뼈대)가 여러 곳에 흩어져** 있어, 순서를 바꾸려면 모든 클래스를 고쳐야 한다.
- 새 음료를 추가할 때 절차 전체를 다시 베껴 써야 한다. → **코드 중복 / OCP 위반**

---

## 3. 패턴 적용 — 뼈대는 상위 클래스에, 다른 단계만 하위 클래스에

**변하지 않는 절차의 뼈대**를 상위 추상 클래스의 **템플릿 메서드**에 한 번만 정의하고, **변하는 단계**만 추상 메서드로 만들어 하위 클래스가 채우게 한다.

### 3.1 추상 클래스 (Abstract Class)

`prepare()`가 템플릿 메서드다. 알고리즘의 뼈대(단계와 순서)를 고정하고, 하위 클래스가 순서를 바꾸지 못하도록 `final`로 막는다.

```java
public abstract class CaffeineBeverage {

    // 템플릿 메서드: 알고리즘의 뼈대. final로 오버라이드 금지
    public final void prepare() {
        boilWater();
        brew();                       // ← 하위 클래스마다 다름
        pourInCup();
        if (customerWantsCondiments()) {   // ← 훅으로 단계 On/Off
            addCondiments();          // ← 하위 클래스마다 다름
        }
    }

    // 변하는 단계: 하위 클래스가 반드시 구현
    protected abstract void brew();
    protected abstract void addCondiments();

    // 변하지 않는 공통 단계: 상위 클래스가 구현
    private void boilWater() {
        System.out.println("물을 끓인다");
    }

    private void pourInCup() {
        System.out.println("컵에 따른다");
    }

    // 훅(hook): 기본 동작을 제공하되, 하위 클래스가 필요 시 재정의
    protected boolean customerWantsCondiments() {
        return true;
    }
}
```

### 3.2 구체 클래스 (Concrete Class)

달라지는 단계만 구현한다. 절차의 순서는 신경 쓰지 않는다 — 뼈대는 상위 클래스가 이미 정해 놨다.

```java
public class Coffee extends CaffeineBeverage {

    @Override
    protected void brew() {
        System.out.println("필터로 커피를 내린다");
    }

    @Override
    protected void addCondiments() {
        System.out.println("설탕과 우유를 추가한다");
    }
}
```

```java
public class Tea extends CaffeineBeverage {

    @Override
    protected void brew() {
        System.out.println("찻잎을 우려낸다");
    }

    @Override
    protected void addCondiments() {
        System.out.println("레몬을 추가한다");
    }

    // 훅 재정의: 첨가물을 넣지 않는 홍차
    @Override
    protected boolean customerWantsCondiments() {
        return false;
    }
}
```

### 3.3 사용하는 코드 (Client)

클라이언트는 구체 음료를 만들고 **템플릿 메서드 하나(`prepare`)만 호출**한다. 내부 절차는 몰라도 된다.

```java
public class Cafe {

    public static void main(String[] args) {
        CaffeineBeverage coffee = new Coffee();
        coffee.prepare();

        System.out.println("---");

        CaffeineBeverage tea = new Tea();
        tea.prepare();
    }
}
```

실행 결과:

```
물을 끓인다
필터로 커피를 내린다
컵에 따른다
설탕과 우유를 추가한다
---
물을 끓인다
찻잎을 우려낸다
컵에 따른다
```

---

## 4. 구조 정리

| 역할 | 클래스 | 설명 |
| --- | --- | --- |
| Abstract Class | `CaffeineBeverage` | 템플릿 메서드 `prepare()`로 알고리즘 뼈대를 정의. 공통 단계는 직접 구현, 변하는 단계는 추상 메서드로 위임 |
| Concrete Class | `Coffee`, `Tea` | 추상 단계(`brew`, `addCondiments`)만 구현. 필요 시 훅 재정의 |
| Client | `Cafe` | 구체 음료를 만들어 템플릿 메서드 `prepare()` 호출 |

```
        Cafe (Client)
             │ prepare()
             ▼
   CaffeineBeverage (Abstract Class)
     ├─ prepare()            ← 템플릿 메서드 (final, 뼈대 고정)
     ├─ boilWater()          ← 공통 단계 (구현)
     ├─ pourInCup()          ← 공통 단계 (구현)
     ├─ brew()               ← 추상 (하위 위임)
     ├─ addCondiments()      ← 추상 (하위 위임)
     └─ customerWantsCondiments()  ← 훅 (기본 구현, 선택적 재정의)
             ▲
      ┌──────┴───────┐
    Coffee          Tea      (Concrete Class)
```

> 알고리즘의 **골격(단계와 순서)은 상위 클래스에 고정**하고, 그중 **일부 단계의 구현만 하위 클래스로 미루는** 설계가 템플릿 메서드 패턴이다.
> 뼈대를 담은 `prepare()`를 **템플릿 메서드**, 하위 클래스가 채우는 `brew()`·`addCondiments()`를 **훅/추상 단계**라고 부른다.

---

## 5. 전략 패턴과의 차이

| | 템플릿 메서드 | 전략 |
| --- | --- | --- |
| 재사용 방식 | **상속** (컴파일 타임에 고정) | **위임/구성** (런타임에 교체) |
| 무엇을 바꾸나 | 알고리즘의 **일부 단계** | 알고리즘 **전체** |
| 뼈대 위치 | 상위 추상 클래스 | 콘텍스트 |

- 절차 전체를 통째로 갈아 끼워야 하면 → **전략**
- 절차의 뼈대는 같고 **일부 단계만** 다르면 → **템플릿 메서드**

---

## 6. 언제 쓰나

- 여러 클래스가 **거의 같은 절차**를 따르는데, **일부 단계만** 다를 때.
- 절차의 **순서·뼈대를 한 곳에 고정**하고, 하위 클래스가 함부로 바꾸지 못하게 하고 싶을 때.
- 공통 단계의 **중복 코드를 상위 클래스로 모으고** 싶을 때.

---

## 7. 장점

- 절차의 **뼈대를 한 곳(상위 클래스)에만** 두어 중복을 제거한다.
- 하위 클래스는 **달라지는 단계만** 구현하면 되므로 새 구현 추가가 쉽다. → OCP
- 템플릿 메서드를 `final`로 두면 **절차의 순서가 훼손되지 않는다.**
- **훅 메서드**로 특정 단계를 선택적으로 켜고 끌 수 있다.

## 8. 주의할 점

- **상속 기반**이라 상위-하위 클래스가 강하게 결합된다. (전략 패턴은 구성으로 이 결합을 피함)
- 상위 클래스의 절차가 바뀌면 **모든 하위 클래스가 영향**을 받을 수 있다.
- 단계가 많아지면 추상 메서드/훅이 늘어 하위 클래스가 구현할 것이 많아진다.
- 하위 클래스가 상위의 흐름을 깨지 않도록, 템플릿 메서드는 **`final`**, 위임 단계는 **`protected`** 로 두는 것이 안전하다.