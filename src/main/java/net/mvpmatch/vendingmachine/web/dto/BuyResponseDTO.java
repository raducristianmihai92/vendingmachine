package net.mvpmatch.vendingmachine.web.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BuyResponseDTO implements Serializable {

    private Integer totalSpent;
    private Integer deposit;
    private String  productName;
    private Integer productQuantity;

}
