package template_method.template;

public abstract class BeverageManufacturing {

    public final void manufacture() {
        boilWater();
        brew();
        pourIntoCup();
        if (customerWantsCondiments()) {
            addAdditives();
        }
    }

    private void boilWater() {
        System.out.println("물을 끓인다.");
    }

    protected abstract void brew();                 // 우려내기

    private void pourIntoCup() {
        System.out.println("컵에 따른다.");
    }

    protected abstract void addAdditives();         // 첨가물 추가

    protected boolean customerWantsCondiments() {
        return true;
    }
}