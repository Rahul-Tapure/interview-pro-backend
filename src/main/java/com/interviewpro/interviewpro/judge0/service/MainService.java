package com.interviewpro.interviewpro.judge0.service;

import com.interviewpro.interviewpro.judge0.dto.LanguagesResponse;
import com.interviewpro.interviewpro.judge0.entity.CodingSubmission;
import com.interviewpro.interviewpro.judge0.repository.CodingSubmissionRepository;

import java.util.List;

import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MainService {

	 private final Judge0Service judge0Service;
	 
	public LanguagesResponse[] languages() {
		return judge0Service.getLanguages();
	}
}
