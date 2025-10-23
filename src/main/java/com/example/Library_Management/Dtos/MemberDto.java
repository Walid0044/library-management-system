package com.example.Library_Management.Dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {

    private Long id;

    @NotBlank(message = "name is required ")
    private String name;

    @NotBlank(message = "email is required ")
    @Email(message = "email must be valid ")
    private String email;


    private String phone;


    @NotBlank(message = "username is required ")
    private String username;

    @NotBlank(message = "password is required ")
            @Size(min = 6, message = "password should be at least 6 character")
    private String password;

    private String role;
}
