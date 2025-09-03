package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentConverter commentConverter;

    private final BookRepository bookRepository;

    @Override
    public Mono<CommentDto> findById(String id) {
        return commentRepository.findById(id)
                .map(commentConverter::commentToDto);
    }

    @Override
    public Flux<CommentDto> findByBookId(String bookId) {
        return bookRepository.existsById(bookId)
                .flatMapMany(exists -> {
                    if (!exists) {
                        return Flux.error(new EntityNotFoundException(
                                "Book with id %s not found".formatted(bookId)
                        ));
                    }
                    return commentRepository.findByBookId(bookId)
                            .map(commentConverter::commentToDto);
                });
    }

    @Override
    public Mono<CommentDto> update(CommentDto commentDto) {
        return save(commentDto);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return commentRepository.deleteById(id);
    }

    @Override
    public Mono<CommentDto> insert(CommentDto commentDto) {
        if (commentDto.bookId() == null) {
            return Mono.error(new IllegalArgumentException("Book id is empty"));
        }
        return save(commentDto);
    }

    private Mono<CommentDto> save(CommentDto commentDto) {
        return validateCommentText(commentDto)
                .flatMap(validationResult -> getBookForComment(commentDto))
                .flatMap(book -> getComment(commentDto)
                        .flatMap(comment -> assembleComment(comment, book, commentDto))
                )
                .flatMap(commentRepository::save)
                .map(commentConverter::commentToDto);
    }

    private Mono<Boolean> validateCommentText(CommentDto commentDto) {
        if (commentDto.text() == null || commentDto.text().isBlank()) {
            return Mono.error(new IllegalArgumentException("Comment text is empty"));
        }
        return Mono.just(true);
    }

    private Mono<Book> getBookForComment(CommentDto commentDto) {
        if (commentDto.bookId() == null || commentDto.bookId().isBlank()) {
            return Mono.error(new IllegalArgumentException("Book ID is null or empty"));
        }

        return bookRepository.findById(commentDto.bookId())
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException("Book with id %s not found".formatted(commentDto.bookId()))
                ));
    }

    private Mono<Comment> getComment(CommentDto commentDto) {
        if (commentDto.id() == null) {
            return Mono.just(new Comment());
        }

        return commentRepository.findById(commentDto.id())
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException("Comment with id %s not found".formatted(commentDto.id()))
                ));
    }

    private Mono<Comment> assembleComment(Comment comment, Book book, CommentDto commentDto) {
        comment.setText(commentDto.text());
        comment.setBook(book);
        comment.setBookId(book.getId());
        return Mono.just(comment);
    }

}
