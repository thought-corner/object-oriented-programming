package template_method.client;

import template_method.beverage.Coffee;
import template_method.beverage.Tea;
import template_method.beverage.Water;

public class Cafe {

    public static void main(String[] args) {
        Coffee coffee = new Coffee();
        coffee.manufacture();

        System.out.println("=======================");

        Tea tea = new Tea();
        tea.manufacture();

        System.out.println("=======================");

        Water water = new Water();
        water.manufacture();
    }
}