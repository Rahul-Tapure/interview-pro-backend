package com.interviewpro.interviewpro.auth.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegister {
	   @NotBlank(message = "Email should not be empty")
	    private String email;

	    @NotBlank(message = "Password should not be empty")
	    private String password;

	    @NotBlank(message = "Role should not be empty")
	    private String role;
}
