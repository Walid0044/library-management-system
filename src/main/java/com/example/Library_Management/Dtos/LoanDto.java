package com.example.Library_Management.Dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanDto {

    private Long id;

    @NotNull(message = "book id must be entered ")
    private Long bookId;


    private Long memberId;

    private LocalDate loanDate;
    private LocalDate returnDate;
    private boolean returned;
    private LocalDate dueDate;
}
