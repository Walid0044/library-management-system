package com.example.Library_Management.services;

import com.example.Library_Management.Dtos.LoanDto;
import com.example.Library_Management.Models.Book;
import com.example.Library_Management.Models.Loan;
import com.example.Library_Management.Models.Member;
import com.example.Library_Management.Repositories.BookRepository;
import com.example.Library_Management.Repositories.LoanRepository;
import com.example.Library_Management.Repositories.MemberRepository;
import com.example.Library_Management.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository, MemberRepository memberRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    // ✅ Get all loans (Admin only — checked in controller)
    public Page<LoanDto> getAllLoans(Pageable pageable) {
        return loanRepository.findAll(pageable).map(this::toDto);
    }

    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }

    // ✅ Get loans for currently logged-in USER
    public List<Loan> getLoansForCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        return loanRepository.findByMember(member);
    }

    // ✅ Admin filters
    public Page<LoanDto> getLoansByReturned(boolean returned, Pageable pageable) {
        return loanRepository.findByReturned(returned, pageable).map(this::toDto);
    }

    public Page<LoanDto> getLoansByMember(Long memberId, Pageable pageable) {
        return loanRepository.findByMember_Id(memberId, pageable).map(this::toDto);
    }

    public Page<LoanDto> getLoansByBook(Long bookId, Pageable pageable) {
        return loanRepository.findByBook_Id(bookId, pageable).map(this::toDto);
    }

    // ✅ Borrow book (USER always borrows for themselves)
    public LoanDto borrowBook(LoanDto dto, String username) {
        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID " + dto.getBookId()));

        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No copies available for this book");
        }

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with username " + username));

        LocalDate loanDate = LocalDate.now();
        Loan loan = Loan.builder()
                .book(book)
                .member(member)
                .loanDate(loanDate)
                .returned(false)
                .dueDate(loanDate.plusDays(1)) // ✅ 1 day loan period
                .build();

        // reduce available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        return toDto(loanRepository.save(loan));
    }

    // ✅ For Admin: Get all overdue loans
    public List<LoanDto> getAllOverdueLoans() {
        LocalDate today = LocalDate.now();
        return loanRepository.findAll().stream()
                .filter(loan -> !loan.isReturned() && loan.getDueDate().isBefore(today))
                .map(this::toDto)
                .toList();
    }

    public List<LoanDto> getOverdueLoansForUser(String username) {
        LocalDate today = LocalDate.now();
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        return loanRepository.findByMember(member).stream()
                .filter(loan -> !loan.isReturned() && !loan.getDueDate().isAfter(today))
                .map(this::toDto)
                .toList();
    }

    // ✅ Return book (User only their loans, Admin can return any in controller)
    public LoanDto returnLoan(Long id, String username) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID " + id));

        // ensure ownership (for USERs)
        if (!loan.getMember().getUsername().equals(username)) {
            throw new AccessDeniedException("You can only return your own loans");
        }

        if (loan.isReturned()) {
            throw new IllegalStateException("Book already returned");
        }

        loan.setReturned(true);
        loan.setReturnDate(LocalDate.now());

        // increase available copies
        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return toDto(loanRepository.save(loan));
    }

    // ✅ Convert entity -> DTO
    private LoanDto toDto(Loan loan) {
        return LoanDto.builder()
                .id(loan.getId())
                .bookId(loan.getBook().getId())
                .memberId(loan.getMember().getId())
                .loanDate(loan.getLoanDate())
                .returnDate(loan.getReturnDate())
                .returned(loan.isReturned())
                .dueDate(loan.getDueDate())
                .build();
    }
}
