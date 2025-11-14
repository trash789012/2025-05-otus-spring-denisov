package ru.otus.hw.dto.user;

import java.util.List;

public record UserWithRolesAndPasswordDto(Long id,
                                          String name,
                                          String password,
                                          String firstName,
                                          String lastName,
                                          String shortDescription,
                                          List<String> roles) {
}
