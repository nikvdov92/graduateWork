package com.example.graduatework.service;

import com.example.graduatework.dto.Ad;
import com.example.graduatework.dto.CreateOrUpdateAd;
import com.example.graduatework.dto.ExtendedAd;
import org.springframework.web.multipart.MultipartFile;

public interface AdsService {
    Ad getAllAds();
    Ad addAd(MultipartFile image, CreateOrUpdateAd adProperties);
    ExtendedAd getAdById(int id);

    boolean deleteAd(int id);


    Ad updateAd(int id, CreateOrUpdateAd adData);


}
