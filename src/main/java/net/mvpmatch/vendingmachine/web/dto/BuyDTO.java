package net.mvpmatch.vendingmachine.web.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BuyDTO implements Serializable {

    private Integer productId;
    private Integer amountOfProducts;
}
