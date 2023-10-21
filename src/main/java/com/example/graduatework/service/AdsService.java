package com.example.graduatework.service;

import com.example.graduatework.dto.Ad;
import com.example.graduatework.dto.CreateOrUpdateAd;
import com.example.graduatework.dto.ExtendedAd;
import com.example.graduatework.dto.User;
import org.springframework.web.multipart.MultipartFile;

public interface AdsService {
    static User getAuthenticatedUser(){
        return null;
    }


    Ad getAllAds();
    Ad addAd(MultipartFile image, CreateOrUpdateAd adProperties);
    ExtendedAd getAdById(int id);

    boolean deleteAd(int id);


    Ad updateAd(int id, CreateOrUpdateAd adData);


}
