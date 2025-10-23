package com.example.Library_Management.Controlllers;

import com.example.Library_Management.Dtos.LoanDto;
import com.example.Library_Management.Models.Loan;
import com.example.Library_Management.services.LoanService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // ✅ Users can only view their own loans
    @GetMapping("/mine")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Loan> getMyLoans(Authentication authentication) {
        return loanService.getLoansForCurrentUser(authentication);
    }

    // ✅ ADMIN: Get all overdue loans
    @GetMapping("/overdue/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<LoanDto> getAllOverdueLoans() {
        return loanService.getAllOverdueLoans();
    }

    // ✅ USER: Get their own overdue loans
    @GetMapping("/overdue/mine")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")

    public List<LoanDto> getMyOverdueLoans(Authentication authentication) {
        System.out.println("Authorities: " + authentication.getAuthorities());
        String username = authentication.getName();
        return loanService.getOverdueLoansForUser(username);
    }




    // ✅ Only ADMIN can see all loans
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<LoanDto> getAllLoans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return loanService.getAllLoans(pageable);
    }

    // ✅ Only ADMIN can filter by returned status
    @GetMapping("/filter/returned")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<LoanDto> getLoansByReturned(
            @RequestParam boolean returned,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return loanService.getLoansByReturned(returned, pageable);
    }

    // ✅ Only ADMIN can filter by memberId
    @GetMapping("/filter/member/{memberId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<LoanDto> getLoansByMember(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return loanService.getLoansByMember(memberId, pageable);
    }

    // ✅ Only ADMIN can filter by bookId
    @GetMapping("/filter/book/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<LoanDto> getLoansByBook(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return loanService.getLoansByBook(bookId, pageable);
    }

    // ✅ Users and Admins can borrow a book
    @PostMapping("/borrow")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<LoanDto> borrowBook(@Valid @RequestBody LoanDto dto, Authentication authentication) {
        String username = authentication.getName();
        LoanDto loan = loanService.borrowBook(dto, username);
        return new ResponseEntity<>(loan, HttpStatus.CREATED);
    }

    // ✅ Users and Admins can return a book (but user only their own)
    @PutMapping("/{id}/return")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public LoanDto returnLoan(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        return loanService.returnLoan(id, username);
    }

}
