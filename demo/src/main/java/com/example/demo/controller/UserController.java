package com.example.demo.controller;

import org.apache.tomcat.jni.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ResponseDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.UserEntity;
import com.example.demo.persistence.UserRepository;
import com.example.demo.security.TokenProvider;
import com.example.demo.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private TokenProvider tokenProvider;
	
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	@Autowired
	private UserRepository userRepository;
	
	
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO){
		try {
			if(userDTO == null || userDTO.getPassword() == null) {
				throw new RuntimeException("Invaild Password value");
			}
			if(userRepository.existsByUsername(userDTO.getUsername())) {
				UserDTO existsUsername = UserDTO.builder()
						.username(userDTO.getUsername())
						.build();
				log.warn("�̹� �ִ� ȸ���Դϴ�.");
				return ResponseEntity.ok().body(existsUsername);
			} 
			//��û�� �̿��� ������ ���� �����
			UserEntity user = UserEntity.builder()
					.username(userDTO.getUsername())
					.password(passwordEncoder.encode(userDTO.getPassword()))
					.build();
			
			//���񽺸� �̿��� �������͸��� ���� ����
			UserEntity registeredUser = userService.create(user);
			UserDTO responseUserDTO = UserDTO.builder()
					.id(registeredUser.getId())
					.username(registeredUser.getUsername())
					.build();
			
			return ResponseEntity.ok().body(responseUserDTO);
			
		}catch(Exception e) {
			// ���� ������ �׻� �ϳ��̹Ƿ� ����Ʈ�� ������ �ϴ� ResponseDTO�� ������� �ʰ� �׳� UserDTO ����.
			ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
//			UserEntity responseDTO = UserEntity.builder().username(userDTO.getUsername()).build(); 
			
			return ResponseEntity
					.badRequest()
					.body(responseDTO);
			}
		}
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO){
		UserEntity user = userService.getByCredentials(userDTO.getUsername(), userDTO.getPassword(),passwordEncoder);
		
		if(user != null) {
			//��ū ����
			final String token = tokenProvider.create(user);
			final UserDTO responseUserDTO = UserDTO.builder()
					.username(user.getUsername())
					.id(user.getId())
					.token(token)
					.build();
			return ResponseEntity.ok().body(responseUserDTO);
			
		}else {
			ResponseDTO responseDTO = ResponseDTO.builder()
					.error("Login failed")
					.build();
			return ResponseEntity.badRequest().body(responseDTO);
					
		}
	}
}