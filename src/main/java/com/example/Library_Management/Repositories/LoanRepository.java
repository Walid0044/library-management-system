package com.example.Library_Management.Repositories;

import com.example.Library_Management.Models.Loan;
import com.example.Library_Management.Models.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByMember(Member member);
    // filter by returned status
    Page<Loan> findByReturned(boolean returned, Pageable pageable);

    // filter by memberId
    Page<Loan> findByMember_Id(Long memberId, Pageable pageable);

    // filter by bookId
    Page<Loan> findByBook_Id(Long bookId, Pageable pageable);
}
