package com.sparta.forusmarket.domain.user.dto;

import com.sparta.forusmarket.domain.user.entity.Address;
import jakarta.validation.constraints.NotBlank;

public record AddressDto(
        @NotBlank
        String city,

        @NotBlank
        String street,

        @NotBlank
        String zipcode
) {
    public static AddressDto from(Address address) {
        return new AddressDto(address.getCity(), address.getStreet(), address.getZipcode());
    }

    public Address toEntity() {
        return new Address(city, street, zipcode);
    }
}
