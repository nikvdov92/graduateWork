package com.example.graduatework.service;

import com.example.graduatework.dto.AdDto;
import com.example.graduatework.dto.Ads;
import com.example.graduatework.dto.CreateOrUpdateAd;
import com.example.graduatework.dto.ExtendedAd;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface AdService {

    Ads getAllAds();

    AdDto addAd(CreateOrUpdateAd updateAd, MultipartFile image, Authentication authentication);

    ExtendedAd getAds(Integer id);

    void removeAd(int id, Authentication authentication);

    AdDto updateAds(int id, CreateOrUpdateAd createOrUpdateAd, Authentication authentication);

    Ads getAdsMe(Authentication authentication);

    void updateImage(int id, MultipartFile imageFile, Authentication authentication);
}
