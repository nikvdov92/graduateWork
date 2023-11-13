package com.example.graduatework.service.impl;

import com.example.graduatework.dto.CommentDto;
import com.example.graduatework.dto.Comments;
import com.example.graduatework.dto.CreateOrUpdateComment;
import com.example.graduatework.entity.Ad;
import com.example.graduatework.entity.Comment;
import com.example.graduatework.entity.User;
import com.example.graduatework.exception.AdNotFoundException;
import com.example.graduatework.exception.CommentNotFoundException;
import com.example.graduatework.exception.UserNotFoundException;
import com.example.graduatework.mapper.CommentMapper;
import com.example.graduatework.repository.AdRepository;
import com.example.graduatework.repository.CommentRepository;
import com.example.graduatework.repository.UserRepository;
import com.example.graduatework.service.CommentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final AdRepository adRepository;

    /**
     * Добавление комментария к объявлению
     */

    @Override
    @Transactional
    public CommentDto addComment(int adId, CreateOrUpdateComment text, Authentication authentication) {
            User user = userRepository.findUserByEmail(authentication.getName())
                    .orElseThrow(UserNotFoundException::new);
            Ad ad = adRepository.findById(adId)
                    .orElseThrow(AdNotFoundException::new);
            Comment comment = Comment.builder()
                    .text(text.getText())
                    .author(user)
                    .ad(ad)
                    .build();
            commentRepository.save(comment);
            log.info("Комментарий создан: " + comment);
            return commentMapper.commentToCommentDto(comment);
    }

    /**
     * Удаление комментария
     */

    @Override
    @Transactional
    public void deleteComment(int adId, int commentId, Authentication authentication) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
        if (comment.getAd().getId() != adId) {
            throw new AdNotFoundException();
        }
            commentRepository.delete(comment);
            log.info("Комментарий удалён: " + comment);
    }

    /**
     * Получение всех комментариев по ID объявления
     */

    @Override
    public Comments getComments(int id) {
        List<Comment> comments = commentRepository.findAllByAdId(id);
        return commentMapper.listCommentToComments(comments.size(), comments);
    }

    /**
     * Получить комментарии по ID комментария
     */

    @Override
    public Comment getComment(int id) {
        return commentRepository.findById(id)
                .orElseThrow(CommentNotFoundException::new);
    }

    /**
     * Обновление комментария
     */

    @Override
    @Transactional
    public CommentDto updateComment(int adId, int commentId,
                                    CreateOrUpdateComment text, Authentication authentication) {
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(CommentNotFoundException::new);
            if (comment.getAd().getId() != adId) {
                throw new AdNotFoundException();
            }
                comment.setText(text.getText());
                commentRepository.save(comment);
                log.info("Комментарий обновлен: " + comment);
        return commentMapper.commentToCommentDto(comment);
    }

    /**
     * Удаление всех комментариев к объявлению
     */

    @Override
    public void deleteComments(int adId) {
        commentRepository.deleteAllByAdId(adId);
        log.info("Комментарии к объявлению удалены: " + adId);
    }
}
