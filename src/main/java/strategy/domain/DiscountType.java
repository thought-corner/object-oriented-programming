package strategy.domain;

public enum DiscountType {

    FIRST_GUEST("FIRST_GUEST", "첫 손님 할인"),
    OLD_SALE("OLD_SALE", "덜 신선한 과일");

    private final String code;
    private final String description;

    DiscountType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}