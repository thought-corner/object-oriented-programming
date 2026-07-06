package template_method.beverage;

import template_method.template.BeverageManufacturing;

public class Coffee extends BeverageManufacturing {

    @Override
    protected void brew() {
        System.out.println("필터로 커피를 내린다");
    }

    @Override
    protected void addAdditives() {
        System.out.println("설탕과 우유를 추가한다");
    }
}