package com.example.Library_Management.Repositories;

import com.example.Library_Management.Models.Member;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByUsername(@NotBlank(message = "username is required ") String username);

    boolean existsByUsername(@NotBlank(message = "username is required ") String username);
}
