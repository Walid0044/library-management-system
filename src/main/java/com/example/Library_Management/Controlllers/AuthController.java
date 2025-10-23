package com.example.Library_Management.Controlllers;

import com.example.Library_Management.Dtos.MemberDto;
import com.example.Library_Management.Models.Member;
import com.example.Library_Management.Repositories.MemberRepository;
import com.example.Library_Management.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(MemberRepository memberRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    // ✅ Register new member (default ROLE_USER)
    @PostMapping("/register")
    public ResponseEntity<MemberDto> register(@RequestBody MemberDto dto) {
        if (memberRepository.existsByUsername(dto.getUsername())) {
            return ResponseEntity.badRequest().build(); // username already exists
        }

        Member member = Member.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword())) // encrypt password
                .role(dto.getRole() != null ? dto.getRole().toUpperCase() : "ROLE_USER")
                .build();

        Member saved = memberRepository.save(member);

        MemberDto response = MemberDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .phone(saved.getPhone())
                .username(saved.getUsername())
                .role(saved.getRole())
                .build();

        return ResponseEntity.ok(response);
    }

    // ✅ Login with AuthenticationManager
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody MemberDto dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        // Generate token
        String token = jwtUtil.generateToken(authentication.getName());

        // Get the logged-in user's details
        Member user = memberRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create response with token and role
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
    }

}
