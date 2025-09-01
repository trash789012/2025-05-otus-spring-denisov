package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentConverter commentConverter;

    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentDto> findById(long id) {
        return commentRepository.findById(id)
                .map(commentConverter::commentToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findByBookId(long bookId) {

        if (!bookRepository.existsById(bookId)) {
            throw new EntityNotFoundException(
                    "Book with id %d not found".formatted(bookId)
            );
        }

        return commentRepository.findByBookId(bookId)
                .stream().map(commentConverter::commentToDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto insert(String text, long bookId) {
        var book = bookRepository.findById(bookId).orElseThrow(
                () -> new EntityNotFoundException("Book with id %d not found".formatted(bookId))
        );

        return save(0, text, book);
    }

    @Override
    @Transactional
    public CommentDto update(long id, String text) {
        return save(id, text, null);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }

    private CommentDto save(long id, String text, Book book) {
        Comment comment;
        if (id == 0) {
            comment = new Comment();
        } else {
            comment = commentRepository.findById(id)
                    .orElseThrow(() ->
                            new EntityNotFoundException(
                                    "Comment with id %d not found".formatted(id))
                    );
        }

        if (text.isEmpty()) {
            throw new IllegalArgumentException("Comment text is empty");
        }

        comment.setText(text);
        if (book != null) {
            comment.setBook(book);
        }

        return commentConverter.commentToDto(commentRepository.save(comment));
    }
}
