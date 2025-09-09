package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.core.parameters.P;
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

    private final AclServiceWrapperService aclService;

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
                    "Book with id %s not found".formatted(bookId)
            );
        }

        return commentRepository.findByBookId(bookId)
                .stream().map(commentConverter::commentToDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto insert(CommentDto commentDto) {
        if (commentDto.bookId() == 0) {
            throw new IllegalArgumentException("Book id is empty");
        }

        return save(commentDto);
    }

    @Override
    @Transactional
    public CommentDto update(CommentDto commentDto) {
        return save(commentDto);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasPermission(#id, 'ru.otus.hw.models.Comment', 'DELETE')")
    public void deleteById(@P("id") long id) {
        commentRepository.deleteById(id);
    }

    private CommentDto save(CommentDto commentDto) {
        boolean isCreate = commentDto.id() == 0;

        Book book = null;
        if (commentDto.bookId() != 0) {
            book = bookRepository.findById(commentDto.bookId()).orElseThrow(
                    () -> new EntityNotFoundException("Book with id %d not found".formatted(commentDto.bookId()))
            );
        }

        Comment comment = prepareComment(commentDto, book);
        var savedComment = commentRepository.save(comment);

        if (isCreate) {
            aclService.createPermission(savedComment, BasePermission.DELETE);
            aclService.createPermission(savedComment, BasePermission.WRITE);
            aclService.createAdminPermission(savedComment);
        }

        return commentConverter.commentToDto(savedComment);
    }

    private Comment prepareComment(CommentDto commentDto, Book book) {
        Comment comment;
        if (commentDto.id() == 0) {
            comment = new Comment();
        } else {
            comment = commentRepository.findById(commentDto.id())
                    .orElseThrow(() ->
                            new EntityNotFoundException(
                                    "Comment with id %s not found".formatted(commentDto.id()))
                    );
        }

        if (commentDto.text().isEmpty()) {
            throw new IllegalArgumentException("Comment text is empty");
        }

        comment.setText(commentDto.text());
        if (book != null) {
            comment.setBook(book);
        }
        return comment;
    }
}
