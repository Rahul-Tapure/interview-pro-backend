package com.interviewpro.interviewpro.mcq.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoResponse {

    private String email;
    private List<String> roles;
}
