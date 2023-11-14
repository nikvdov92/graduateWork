package com.example.graduatework.config;

import com.example.graduatework.service.AdService;
import com.example.graduatework.service.CommentService;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


@AllArgsConstructor
@Component

public class CustomSecurityExpression {
    private final AdService adService;
    private final CommentService commentService;

    public boolean hasAdAuthority(Authentication authentication, int adId) {
        return authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))
                && adService.getAds(adId).getEmail().equals(authentication.getName());
    }

    public boolean hasCommentAuthority(Authentication authentication, int commentId) {
        return authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))
                && commentService.getComment(commentId).getAuthor().getEmail().equals(authentication.getName());
    }
}
