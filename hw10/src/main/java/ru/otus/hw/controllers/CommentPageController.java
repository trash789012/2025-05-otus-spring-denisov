package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import ru.otus.hw.services.CommentService;

@RequiredArgsConstructor
@Controller
public class CommentPageController {

    private final CommentService commentService;

//    @PostMapping("/books/{bookId}/comments/{commentId}/deleteComment")
//    public String deleteComment(@PathVariable("bookId") String bookId,
//                                @PathVariable("commentId") String commentId) {
//        commentService.deleteById(commentId);
//        return "redirect:/book/" + bookId;
//    }
//
//    @PostMapping("/book/{bookId}/comments/addComment")
//    public String saveComment(@PathVariable("bookId") String bookId, @ModelAttribute("comment") CommentDto commentDto) {
//        commentService.insert(commentDto);
//        return "redirect:/book/" + bookId;
//    }
}
