package com.example.Library_Management.services;

import com.example.Library_Management.Dtos.MemberDto;
import com.example.Library_Management.Models.Member;
import com.example.Library_Management.Repositories.MemberRepository;
import com.example.Library_Management.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<MemberDto> getAllMembers() {
        return memberRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public MemberDto getMember(Long id) {
        return memberRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("user not found " + id));
    }

    public MemberDto createMember(MemberDto dto) {
        Member member = toEntity(dto);

        // encode password before saving
        member.setPassword(passwordEncoder.encode(dto.getPassword()));

        // default role if not provided
        if (dto.getRole() == null || dto.getRole().isBlank()) {
            member.setRole("USER");
        } else {
            member.setRole(dto.getRole());
        }

        Member saved = memberRepository.save(member);
        return toDto(saved);
    }

    public MemberDto updateMember(Long id, MemberDto dto) {
        return memberRepository.findById(id).map(existing -> {
            existing.setName(dto.getName());
            existing.setEmail(dto.getEmail());
            existing.setPhone(dto.getPhone());

            // allow updating password (if provided)
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                existing.setPassword(passwordEncoder.encode(dto.getPassword()));
            }

            // allow updating role
            if (dto.getRole() != null && !dto.getRole().isBlank()) {
                existing.setRole(dto.getRole());
            }

            return toDto(memberRepository.save(existing));
        }).orElseThrow(() -> new ResourceNotFoundException("user not found to update " + id));
    }

    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new ResourceNotFoundException("user not found to delete " + id);
        }
        memberRepository.deleteById(id);
    }

    // Helpers
    private MemberDto toDto(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .phone(member.getPhone())
                .username(member.getUsername())
                .role(member.getRole())
                // ⚠ do not expose password in DTO
                .build();
    }

    private Member toEntity(MemberDto dto) {
        return Member.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .username(dto.getUsername())
                .password(dto.getPassword()) // will be encoded in create/update
                .role(dto.getRole())
                .build();
    }
}
