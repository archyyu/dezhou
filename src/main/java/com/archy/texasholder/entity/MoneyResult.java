package com.archy.texasholder.entity;

public record MoneyResult(int money, boolean result) {
    public static MoneyResult failed() {
        return new MoneyResult(0, false);
    }
    public static MoneyResult success(int money) {
        return new MoneyResult(money, true);
    }
}
