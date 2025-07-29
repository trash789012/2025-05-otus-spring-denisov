package ru.otus.hw.mongo.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.CommentRepository;

@Component
public class BookCascadeDeleteMongoListener extends AbstractMongoEventListener<Book> {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public void onBeforeDelete(BeforeDeleteEvent<Book> event) {
        super.onBeforeDelete(event);

        var bookId = event.getSource().getObjectId("_id").toString();
        commentRepository.deleteAllByBookId(bookId);
    }
}
