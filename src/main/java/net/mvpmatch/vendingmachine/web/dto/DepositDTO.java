package net.mvpmatch.vendingmachine.web.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DepositDTO implements Serializable {

    private Integer coin;
}
