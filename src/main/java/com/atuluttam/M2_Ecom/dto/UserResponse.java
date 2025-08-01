package com.atuluttam.M2_Ecom.dto;

import com.atuluttam.M2_Ecom.model.UserRole;
import lombok.Data;

@Data
public class UserResponse {
    private String id;
    private String fname;
    private String lname;
    private String email;
    private String phone;
    private UserRole role;
    private AddressDTO addressDTO;
}
