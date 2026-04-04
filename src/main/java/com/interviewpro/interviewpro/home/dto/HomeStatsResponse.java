package com.interviewpro.interviewpro.home.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeStatsResponse {
private int rounds;
private int questions;
private int tests;
}