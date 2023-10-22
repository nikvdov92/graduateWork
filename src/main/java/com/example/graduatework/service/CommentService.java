package com.example.graduatework.service;

import com.example.graduatework.dto.CommentDto;
import com.example.graduatework.dto.Comments;
import com.example.graduatework.dto.CreateOrUpdateComment;

public interface CommentService {
    CommentDto addComment(int adId, CreateOrUpdateComment createOrUpdateComment);
    Comments getComments(int adId);


}
