package com.example.graduatework.service.impl;

import com.example.graduatework.dto.AdDto;
import com.example.graduatework.dto.Ads;
import com.example.graduatework.dto.CreateOrUpdateAd;
import com.example.graduatework.dto.ExtendedAd;
import com.example.graduatework.entity.Ad;
import com.example.graduatework.entity.User;
import com.example.graduatework.exception.AdNotFoundException;
import com.example.graduatework.exception.UserNotFoundException;
import com.example.graduatework.mapper.AdMapper;
import com.example.graduatework.repository.AdRepository;
import com.example.graduatework.repository.UserRepository;
import com.example.graduatework.service.AdService;
import com.example.graduatework.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
@Slf4j
@Service
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final UserRepository userRepository;
    private final ImageService imageService;

    /**
     * Получение всех объявлений
     */

    @Override
    public Ads getAllAds() {
        List<Ad> ads = adRepository.findAll();
        return adMapper.listAdToAds(ads.size(), ads);
    }

    /**
     * Добавление и сохранение объявления
     */

    @Transactional
    @Override
    public AdDto addAd(CreateOrUpdateAd updateAd, MultipartFile imageFile, Authentication authentication) {
        User user = userRepository.findUserByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        Ad ad = adMapper.adDtoToAd(updateAd);
        ad.setAuthor(user);
        try {
            ad.setImage(imageService.uploadImage("ad" + ad.getId(), imageFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        adRepository.saveAndFlush(ad);
        log.info("Новое объявление сохранено: " + ad);
        return adMapper.adToAdDto(ad);
    }

    /**
     * Получение информации об объявлении
     */

    @Override
    public ExtendedAd getAds(Integer id) {
        try {
            Ad ad = adRepository.findById(id)
                    .orElseThrow(AdNotFoundException::new);
            log.info("Запрошенная информация: " + ad);
            return adMapper.toExtendedAd(ad);
        } catch (Exception e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    /**
     * Удаление объявления
     */

    @Override
    @Transactional
    public boolean removeAd(int id, Authentication authentication) {
        try {
            Ad ad = adRepository.findById(id)
                    .orElseThrow(AdNotFoundException::new);
            User author = userRepository.findUserByEmail(authentication.getName())
                    .orElseThrow(UserNotFoundException::new);
            if (ad.getAuthor().equals(author)
                    || authentication.getAuthorities().contains
                    (new SimpleGrantedAuthority("role_admin"))) {
                adRepository.deleteById(id);
                log.info("Объявление удалено: " + ad);
                return true;
            }
        } catch (Exception e) {
            log.warn("Объявление не удалено: " + e);
            return false;
        }
        log.warn("Объявление не удалено, запрещённый доступ ");
        return false;
    }

    /**
     * Обновление информации об объявлении
     */

    @Override
    @Transactional
    public AdDto updateAds(int id, CreateOrUpdateAd createOrUpdateAd, Authentication authentication) {
        try {
            Ad ad = adRepository.findById(id)
                    .orElseThrow(AdNotFoundException::new);
            User author = userRepository.findUserByEmail(authentication.getName())
                    .orElseThrow(UserNotFoundException::new);
            if (ad.getAuthor().equals(author) || authentication.getAuthorities().contains
                    (new SimpleGrantedAuthority("role_admin"))) {
                ad.setTitle(createOrUpdateAd.getTitle());
                ad.setPrice(createOrUpdateAd.getPrice());
                ad.setDescription(createOrUpdateAd.getDescription());
                adRepository.save(ad);
                log.info("Объявление обновлено: " + ad);
                return adMapper.adToAdDto(ad);
            }
        } catch (Exception e) {
            log.warn("Объявление не удалось обновить: " + e);
        }
        return null;
    }

    /**
     * Получение объявлений авторизованного пользователя
     */

    @Override
    public Ads getAdsMe(Authentication authentication) {
        User author = userRepository.findUserByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        List<Ad> adList = adRepository.findAllByAuthor(author);
        return adMapper.listAdToAds(adList.size(), adList);
    }

    /**
     * Обновление изображения объявления
     */

    @Override
    public boolean updateImage(int id, MultipartFile imageFile, Authentication authentication) {
        try {
            Ad ad = adRepository.findById(id)
                    .orElseThrow(AdNotFoundException::new);
            User author = userRepository.findUserByEmail(authentication.getName())
                    .orElseThrow(UserNotFoundException::new);
            if (ad.getAuthor().equals(author) || authentication.getAuthorities().contains
                    (new SimpleGrantedAuthority("role_admin"))) {
                ad.setImage(imageService.uploadImage("ad" + ad.getId(), imageFile));
                adRepository.saveAndFlush(ad);
                log.info("Изображение объявления обновлено");
                return true;
            }
        } catch (
                Exception e) {
            log.warn("Не удалось обновить изображение объявления: " + e);
        }
        return false;
    }
}
