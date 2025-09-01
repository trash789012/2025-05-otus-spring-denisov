package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public Mono<CommentDto> insert(CommentDto commentDto) {
        if (commentDto.bookId() == null) {
            return Mono.error(new IllegalArgumentException("Book id is empty"));
        }
        return save(commentDto);
    }

    @Override
    @Transactional
    public Mono<CommentDto> update(CommentDto commentDto) {
        return save(commentDto);
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(String id) {
        return commentRepository.deleteById(id);
    }

//    private Mono<CommentDto> save(CommentDto commentDto) {
//        if (commentDto.text() == null || commentDto.text().isBlank()) {
//            return Mono.error(new IllegalArgumentException("Comment text is empty"));
//        }
//
//        Mono<Book> bookMono = commentDto.bookId() != null
//                ? bookRepository.findById(commentDto.bookId())
//                .switchIfEmpty(Mono.error(
//                        new EntityNotFoundException("Book with id %s not found".formatted(commentDto.bookId()))
//                ))
//                : Mono.empty();
//
//        Mono<Comment> commentMono;
//        if (commentDto.id() == null) {
//            commentMono = Mono.just(new Comment());
//        } else {
//            commentMono = commentRepository.findById(commentDto.id())
//                    .switchIfEmpty(Mono.error(
//                            new EntityNotFoundException("Comment with id %s not found".formatted(commentDto.id()))
//                    ));
//        }
//
//        return Mono.zip(commentMono, bookMono.defaultIfEmpty(null))
//                .flatMap(tuple -> {
//                    Comment comment = tuple.getT1();
//                    Book book = tuple.getT2();
//
//                    comment.setText(commentDto.text());
//                    if (book != null) {
//                        comment.setBook(book);
//                    }
//
//                    return commentRepository.save(comment);
//                })
//                .map(commentConverter::commentToDto);
//    }

    private Mono<CommentDto> save(CommentDto commentDto) {
        return validateCommentText(commentDto)
                .zipWith(getBookForComment(commentDto))
                .flatMap(tuple -> getComment(commentDto)
                        .flatMap(comment -> assembleComment(comment, tuple.getT2(), commentDto))
                )
                .flatMap(commentRepository::save)
                .map(commentConverter::commentToDto);
    }

    private Mono<Void> validateCommentText(CommentDto commentDto) {
        if (commentDto.text() == null || commentDto.text().isBlank()) {
            return Mono.error(new IllegalArgumentException("Comment text is empty"));
        }
        return Mono.empty();
    }

    private Mono<Book> getBookForComment(CommentDto commentDto) {
        if (commentDto.bookId() == null) {
            return Mono.empty();
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
        if (book != null) {
            comment.setBook(book);
        }
        return Mono.just(comment);
    }

}
