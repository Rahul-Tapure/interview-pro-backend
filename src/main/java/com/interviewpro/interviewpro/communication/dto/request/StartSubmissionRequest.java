package com.interviewpro.interviewpro.communication.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StartSubmissionRequest {

    private Long testId;

 // was: private Long userId;
    private String userId;
}
