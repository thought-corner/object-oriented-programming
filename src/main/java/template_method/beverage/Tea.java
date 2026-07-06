package template_method.beverage;

import template_method.template.BeverageManufacturing;

public class Tea extends BeverageManufacturing {

    @Override
    protected void brew() {
        System.out.println("찻잎을 우려낸다");
    }

    @Override
    protected void addAdditives() {
        System.out.println("레몬을 추가한다");
    }
}