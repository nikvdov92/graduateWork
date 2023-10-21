package com.example.graduatework.controller;

import com.example.graduatework.dto.*;
import com.example.graduatework.exception.ForbiddenException;
import com.example.graduatework.exception.UnauthorizedException;
import com.example.graduatework.service.AdsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping("/ads")
@Tag(name = "Объявления", description = "CRUD-операции для работы с объявлениями")
@RequiredArgsConstructor

public class AdsController {
    private final AdsService adsService;

    private List<Ad> adList = new ArrayList<>();


    @GetMapping
    @Operation(summary = "Получение всех объявлений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    public ResponseEntity<Ad> getAllAds() {
        Ad ad = adsService.getAllAds();
        return ResponseEntity.ok(ad);
    }

    @PostMapping
    @Operation(summary = "Добавление объявления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Ad> addAd(@RequestPart("image") MultipartFile image,
                                    @RequestPart("properties") CreateOrUpdateAd adProperties) {
        try {
            Ad ad = adsService.addAd(image, adProperties);
            return ResponseEntity.status(HttpStatus.CREATED).body(ad);
        } catch (UnauthorizedException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized", ex);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение информации об объявлении")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ОК"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<ExtendedAd> getAd(@PathVariable("id") int id) {
        try {
            ExtendedAd ad = adsService.getAdById(id);
            if (ad != null) {
                return ResponseEntity.ok(ad);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping
    @Operation(summary = "Удаление объявления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Void> deleteAd(@PathVariable("id") int id) {
        try {
            boolean deleted = adsService.deleteAd(id);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PatchMapping
    @Operation(summary = "Обновление информации об объявлении")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Ad> updateAd(@PathVariable("id") int id, @RequestBody CreateOrUpdateAd adData) {
        try {
            Ad updatedAd = adsService.updateAd(id, adData);
            if (updatedAd != null) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Получение объявлений авторизованного пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ОК"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Ads> getAdsMe() {
        try {
            Ads ads = new Ads();
            ads.setCount(adList.size());
            ads.setResults(adList);
            return ResponseEntity.ok(ads);
        } catch (Exception e) {
            log.error("Failed to get ads: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{id}/image")
    @Operation(summary = "Обновление картинки объявления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Ad> updateImage(@PathVariable int id,
                                          @RequestParam("image") MultipartFile image) {
        try {
            if (!image.getContentType().startsWith("image/")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            Ad ad = getAdById(id);
            if (ad == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            ad.setImage(Arrays.toString(image.getBytes()));
            saveAd(ad);
            return ResponseEntity.ok(ad);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
    private Ad getAdById(int id) {
        Ad ad = new Ad();
        ad.setPk(id);
        return ad;
    }
    private void saveAd(Ad ad) {
        System.out.println("Saving ad: " + ad);
    }
}




