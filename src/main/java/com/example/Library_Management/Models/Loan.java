package com.example.Library_Management.Models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "loans")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Loan {
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Id
    private Long id;
    @ManyToOne (optional = false)
    @JoinColumn(name = "bookId", nullable = false)
    private Book book;

    @ManyToOne (optional = false)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;


    @Column(nullable = false)
    private LocalDate loanDate;

    private LocalDate returnDate;

    private boolean returned;
    private LocalDate dueDate;
}
