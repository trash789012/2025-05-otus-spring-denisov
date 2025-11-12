package ru.otus.hw.dto.user;

public record UserInfoDto(Long id,
                          String name,
                          String firstName,
                          String lastName,
                          String shortDescription) {
}
