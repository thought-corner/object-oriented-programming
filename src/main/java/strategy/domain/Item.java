package strategy.domain;

public class Item {
    private String name;
    private int price;

    public Item(String name, int price) {  // 생성자로 값을 받음
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public int getPrice() { return price; }
}