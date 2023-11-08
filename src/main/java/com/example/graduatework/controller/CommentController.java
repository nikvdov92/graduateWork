package com.example.graduatework.controller;

import com.example.graduatework.dto.CommentDto;
import com.example.graduatework.dto.Comments;
import com.example.graduatework.dto.CreateOrUpdateComment;
import com.example.graduatework.repository.CommentRepository;
import com.example.graduatework.service.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping("/ads")
@Tag(name = "Комментарии", description = "CRUD-операции для работы с комментариями")
@RequiredArgsConstructor

public class CommentController {
    public final CommentService commentService;

    public final CommentRepository commentRepository;

    @GetMapping(value = "/{id}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Получение комментариев объявления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
    })

    public ResponseEntity<Comments> getComment(@PathVariable int id) {
            log.info("Запрос на получение комментариев объявления");
        Comments comments = commentService.getComments(id);
        return ResponseEntity.ok(comments);
    }

    @PostMapping(value = "/{id}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Добавление комментария к объявлению")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
    })

    public ResponseEntity<CommentDto> addComment(@PathVariable int id,
                                                 @RequestBody @Valid CreateOrUpdateComment text,
                                                 Authentication authentication) {
        log.info("Запрос на добавление комментария к объявлению, id комментария: " + id);
        CommentDto commentDto = commentService.addComment(id, text, authentication);
        return ResponseEntity.ok(commentDto);
    }

    @DeleteMapping(value = "/{adId}/comments/{commentId}")
    @Operation(summary = "Удаление комментария")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })

    public ResponseEntity<Void> deleteComment(@PathVariable int adId,
                                              @PathVariable int commentId,
                                              Authentication authentication) {
        log.info("Запрос на удаление комментария, идентификатор объявления:" + adId);
        if (commentService.deleteComment(adId, commentId, authentication)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PatchMapping(value = "/{adId}/comments/{commentId}")
    @Operation(summary = "Обновление комментария")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })

    public ResponseEntity<CommentDto> updateComment(@PathVariable int adId,
                                                    @PathVariable int commentId,
                                                    @RequestBody CreateOrUpdateComment text,
                                                    Authentication authentication) {
        log.info("Запрос на обновление комментария, идентификатор объявления:" + adId);
        CommentDto commentDto = commentService.updateComment(adId, commentId, text, authentication);
        if (commentDto == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(commentDto);
    }
}
