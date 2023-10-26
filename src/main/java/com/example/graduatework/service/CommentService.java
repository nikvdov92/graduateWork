package com.example.graduatework.service;

import com.example.graduatework.dto.CommentDto;
import com.example.graduatework.dto.Comments;
import com.example.graduatework.dto.CreateOrUpdateComment;
import org.springframework.security.core.Authentication;

public interface CommentService {

    CommentDto addComment(int adId, CreateOrUpdateComment text, Authentication authentication);

    boolean deleteComment(int adId, int commentId, Authentication authentication);

    Comments getComments(int id);

    CommentDto updateComment(int adId, int commentId, CreateOrUpdateComment text, Authentication authentication);
}
