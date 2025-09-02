package ru.otus.hw.controllers.rest;

//@WebMvcTest(CommentController.class)
public class CommentControllerTest {
//    @Autowired
//    private MockMvc mvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockitoBean
//    private CommentService commentService;
//
//    @Test
//    void shouldGetCommentsByBookId() throws Exception {
//        List<CommentDto> comments = List.of(
//                new CommentDto("1", "Comment 1", "book1"),
//                new CommentDto("2", "Comment 2", "book1")
//        );
//
//        when(commentService.findByBookId("1")).thenReturn(comments);
//
//        String URI = "/api/v1/book/1/comment";
//        var status = mvc.perform(get(URI)).andReturn();
//
//        mvc.perform(get("/api/v1/book/1/comment"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(content().json(objectMapper.writeValueAsString(comments)));
//    }
//
//    @Test
//    void shouldReturnEmptyListWhenNoCommentsForBook() throws Exception {
//        when(commentService.findByBookId("book1")).thenReturn(List.of());
//
//        mvc.perform(get("/api/v1/book/book1/comment"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", empty()));
//    }
//
//    @Test
//    void shouldCreateCommentForBook() throws Exception {
//        CommentDto commentToCreate = new CommentDto(null, "New Comment", "book1");
//        CommentDto createdComment = new CommentDto("1", "New Comment", "book1");
//
//        when(commentService.insert(any(CommentDto.class))).thenReturn(createdComment);
//
//        mvc.perform(post("/api/v1/book/book1/comment")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(commentToCreate)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is("1")))
//                .andExpect(jsonPath("$.text", is("New Comment")))
//                .andExpect(jsonPath("$.bookId", is("book1")));
//    }
//
//    @Test
//    void shouldDeleteComment() throws Exception {
//        mvc.perform(delete("/api/v1/book/book1/comment/comment1"))
//                .andExpect(status().isNoContent());
//
//        Mockito.verify(commentService).deleteById("comment1");
//    }
//
//    @Test
//    void shouldValidateCommentTextNotEmpty() throws Exception {
//        CommentDto emptyComment = new CommentDto(null, "", "book1");
//
//        mvc.perform(post("/api/v1/book/book1/comment")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(emptyComment)))
//                .andExpect(status().isBadRequest());
//    }
}
