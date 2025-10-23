package com.example.Library_Management.Dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BookDto {
    private Long id;

    @NotBlank(message = "this field is must to type something in")
    private String title;

    @NotBlank(message = "this field is must to enter")
    private String author;

    @NotBlank(message = "isbn field to be entered must")
    private String isbn;

    @NotNull(message = "must be at least one ")
    @Min(value = 1)
    private Integer copies;
    private Integer availableCopies ;







}
