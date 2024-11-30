package com.consumer.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class JsonEvent {
    private int id;
    private String owner;
    private int pay;
}
