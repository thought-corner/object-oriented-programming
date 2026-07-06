package template_method.beverage;

import template_method.template.BeverageManufacturing;

public class Water extends BeverageManufacturing {

    @Override
    protected boolean customerWantsCondiments() {
        return false;
    }

    @Override
    protected void brew() {
        System.out.println("생수를 끓인다.");
    }

    @Override
    protected void addAdditives() {
        System.out.println("첨가물이 없이 끓여서 필요가 없습니다.");
    }
}