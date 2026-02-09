package com.jimline.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CarType {
    LABO("라보"),
    TON_1("1톤"),
    TON_2_5("2.5톤"),
    TON_5("5톤");

    private final String description;
}