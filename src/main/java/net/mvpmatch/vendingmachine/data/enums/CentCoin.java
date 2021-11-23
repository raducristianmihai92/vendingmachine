package net.mvpmatch.vendingmachine.data.enums;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum CentCoin {

    FIVE(5),
    TEN(10),
    TWENTY(20),
    FIFTY(50),
    ONE_HUNDRED(100);

    private final Integer value;

    CentCoin(Integer value) {
      this.value = value;
    }

    public static boolean contains(Integer enteredValue){
       return Stream.of(CentCoin.values()).filter(e -> e.getValue().equals(enteredValue)).findAny().isPresent();
    }
}
