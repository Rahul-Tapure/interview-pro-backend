package com.interviewpro.interviewpro.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

import com.interviewpro.interviewpro.auth.entity.UserTable;
import com.interviewpro.interviewpro.auth.repository.UserRepository;

@Service
public class EntryPointService implements UserDetailsService {
	@Autowired
	private UserRepository usersTableRepo;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		UserTable dbUser = usersTableRepo.findById(email)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found " + email));

		return User.builder().username(dbUser.getEmail()).password(dbUser.getPassword()).roles(dbUser.getRole())
				.build();
	}
}
