package com.learn.libeventconsumer.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class LibaryEvent {

    @Id
    @GeneratedValue
    private Integer lEventId;
    @Enumerated(EnumType.STRING)
    private LibraryEventType libraryEventType;
    @OneToOne(mappedBy = "libaryEvent",cascade = CascadeType.ALL)
    @ToString.Exclude
    private Book book;
}
