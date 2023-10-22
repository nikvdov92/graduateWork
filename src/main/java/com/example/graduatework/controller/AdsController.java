package com.example.graduatework.controller;

import com.example.graduatework.dto.*;
import com.example.graduatework.exception.ForbiddenException;
import com.example.graduatework.exception.NoContentException;
import com.example.graduatework.exception.NotFoundException;
import com.example.graduatework.exception.UnauthorizedException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Validated
@RestController
@CrossOrigin(value = "http://localhost:5432")
@RequestMapping("/ads")
@Tag(name = "Объявления", description = "CRUD-операции для работы с объявлениями")
@RequiredArgsConstructor

public class AdsController {

    @GetMapping()
    @Operation(summary = "Получение всех объявлений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = AdDto.class))))
    })

    public ResponseEntity<Ads> getAllAds() {
        log.info("Запросить получение всех объявлений");
        return ResponseEntity.ok(new Ads());
    }

    @PostMapping()
    @Operation(summary = "Добавление объявления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = AdDto.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })

    public ResponseEntity<AdDto> addAd(@RequestPart("properties") CreateOrUpdateAd properties,
                                       @RequestPart("image") MultipartFile image) {
        try {
        log.info("Запрос на добавление объявления");
        return ResponseEntity.ok(new AdDto());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение информации об объявлении")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ОК",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = AdDto.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })

    public ResponseEntity<ExtendedAd> getAds(@PathVariable("id") int id) {
        try {
        log.info("Запрос информации по объявлению, идентификатор:" + id);
        return ResponseEntity.ok(new ExtendedAd());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление объявления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })

    public ResponseEntity<Void> deleteAd(@PathVariable("id") int id) {
        try {
        log.info("Запрос на удаление объявления, идентификатор:" + id);
        return ResponseEntity.ok().build();
        } catch (NoContentException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновление информации об объявлении")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = AdDto.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })

    public ResponseEntity<AdDto> updateAds(@PathVariable("id") int id,
                                           @RequestBody CreateOrUpdateAd createOrUpdateAd) {
        try {
        log.info("Запрос на обновление объявления, идентификатор:" + id);
        return ResponseEntity.ok(new AdDto());
        } catch (NoContentException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Получение объявлений авторизованного пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ОК",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Ads.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })

    public ResponseEntity<Ads> getAdsMe() {
        try {
            log.info("Запрос на получение объявлений от авторизованного пользователя");
            return ResponseEntity.ok(new Ads());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PatchMapping(value = "/{id}/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Обновление картинки объявления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })

    public ResponseEntity<?> updateImage(@PathVariable("id") int id,
                                         @RequestPart("image") MultipartFile image) {
        try {
        log.info("Запрос на обновление изображения объявления");
        return ResponseEntity.ok().build();
        } catch (NoContentException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}




