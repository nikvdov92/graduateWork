package com.example.graduatework.service;

import com.example.graduatework.dto.Comment;
import com.example.graduatework.dto.Comments;
import com.example.graduatework.dto.CreateOrUpdateComment;

public interface CommentService {
    Comment addComment(int adId, CreateOrUpdateComment createOrUpdateComment);
    Comments getComments(int adId);


}
