package net.mvpmatch.vendingmachine.web.dto;

import lombok.Data;
import net.mvpmatch.vendingmachine.data.entity.User;

import java.io.Serializable;

@Data
public class ProductDTO implements Serializable {

    private Integer id;
    private String productName;
    private Integer cost;
    private Integer amountAvailable;
    private UserDTO seller;

}
