package com.TaiNguyen.AuthenticationService.Modal;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class UserModal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String fullname;
    private String email;

    //An Mat Khau
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private int projectSize;

}
