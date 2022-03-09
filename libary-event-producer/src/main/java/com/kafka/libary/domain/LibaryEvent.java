package com.kafka.libary.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LibaryEvent {

    private Integer lEventId;
    private LibraryEventType libraryEventType;
    @NotNull
    @Valid
    private Book book;
}
