package net.mvpmatch.vendingmachine.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserDTO implements Serializable {

    private Integer id;
    private String userName;
    private Integer deposit;
    private String role;
}
