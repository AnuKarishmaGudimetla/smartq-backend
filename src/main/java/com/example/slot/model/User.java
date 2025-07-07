package com.example.slot.model;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class User {
    private String userId;
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private Role role;
    private String email;
}
