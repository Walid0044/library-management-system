package com.example.Library_Management.services;

import com.example.Library_Management.Dtos.BookDto;
import com.example.Library_Management.Models.Book;
import com.example.Library_Management.Repositories.BookRepository;
import com.example.Library_Management.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // ✅ Pagination + sorting
    public Page<BookDto> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable).map(this::toDto);
    }

    // ✅ Single book fetch
    public BookDto getBook(Long id) {
        return bookRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
    }

    // ✅ Filters
    public List<BookDto> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> searchBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> searchBooksByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ✅ CRUD

    public BookDto createBook(BookDto dto) {
        Book book = toEntity(dto);
        book.setAvailableCopies(dto.getCopies()); // ✅ initialize available copies
        return toDto(bookRepository.save(book));
    }


    public BookDto updateBook(Long id, BookDto dto) {
        return bookRepository.findById(id).map(existing -> {
            existing.setTitle(dto.getTitle());
            existing.setAuthor(dto.getAuthor());
            existing.setIsbn(dto.getIsbn());

            // update total copies
            existing.setCopies(dto.getCopies());

            // ✅ if copies changed and no loans conflict, reset availableCopies
            if (existing.getAvailableCopies() > dto.getCopies()) {
                existing.setAvailableCopies(dto.getCopies());
            }

            return toDto(bookRepository.save(existing));
        }).orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found with id " + id);
        }
        bookRepository.deleteById(id);
    }



    // ✅ Mapping helpers
    private BookDto toDto(Book book) {
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .copies(book.getCopies())
                .availableCopies(book.getAvailableCopies())
                .build();
    }

    private Book toEntity(BookDto dto) {
        return Book.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .isbn(dto.getIsbn())
                .copies(dto.getCopies())
                .availableCopies(dto.getAvailableCopies())
                .build();
    }


}
