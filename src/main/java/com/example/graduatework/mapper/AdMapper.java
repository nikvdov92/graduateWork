package com.example.graduatework.mapper;

import com.example.graduatework.dto.AdDto;
import com.example.graduatework.dto.Ads;
import com.example.graduatework.dto.CreateOrUpdateAd;
import com.example.graduatework.dto.ExtendedAd;
import com.example.graduatework.entity.Ad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdMapper {

    Ads listAdToAds(int count, List<Ad> results);

    Ad adDtoToAd(CreateOrUpdateAd createOrUpdateAd);

    @Mapping(source = "pk", target = "id")
    @Mapping(source = "author", target = "author.id")
    Ad adDtoToAd(AdDto adDto);

    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.id", target = "author")
    AdDto adToAdDto(Ad ad);

    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.firstName", target = "authorFirstName")
    @Mapping(source = "author.lastName", target = "authorLastName")
    @Mapping(source = "author.email", target = "email")
    @Mapping(source = "author.phone", target = "phone")
    ExtendedAd toExtendedAd(Ad ad);
}
