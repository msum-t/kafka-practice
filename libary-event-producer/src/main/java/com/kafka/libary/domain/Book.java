package com.kafka.libary.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {
    @NotNull
    private Integer boookId;
    @NotBlank
    private String bookName;
    @NotBlank
    private String authorName;

}
