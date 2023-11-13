package com.example.graduatework.service;

import com.example.graduatework.dto.CommentDto;
import com.example.graduatework.dto.Comments;
import com.example.graduatework.dto.CreateOrUpdateComment;
import com.example.graduatework.entity.Comment;
import org.springframework.security.core.Authentication;

public interface CommentService {

    CommentDto addComment(int adId, CreateOrUpdateComment text, Authentication authentication);

    void deleteComment(int adId, int commentId, Authentication authentication);

    Comments getComments(int id);

    Comment getComment(int id);

    CommentDto updateComment(int adId, int commentId, CreateOrUpdateComment text, Authentication authentication);

    void deleteComments(int adId);
}
