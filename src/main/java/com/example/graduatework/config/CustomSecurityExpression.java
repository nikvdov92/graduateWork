package com.example.graduatework.config;

import com.example.graduatework.dto.Role;
import com.example.graduatework.service.AdService;
import com.example.graduatework.service.CommentService;

import lombok.AllArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;


@AllArgsConstructor
@Component

public class CustomSecurityExpression {
    private final AdService adService;
    private final CommentService commentService;

    public boolean hasAdAuthority(Authentication authentication, int adId) {
        return hasAdmin(authentication) ||
                adService.getAds(adId).getEmail().equals(authentication.getName());
    }

    public boolean hasCommentAuthority(Authentication authentication, int commentId) {
        return hasAdmin(authentication) ||
                commentService.getComment(commentId).getAuthor().getEmail().equals(authentication.getName());
    }

    private boolean hasAdmin(Authentication authentication) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + Role.ADMIN.name()));
    }
}
