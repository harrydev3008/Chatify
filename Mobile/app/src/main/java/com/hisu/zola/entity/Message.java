package com.hisu.zola.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    //  Todo: Message object structure will change later, this is just for quick test
    private String from;
    private String content;
//    private LocalDateTime sentDate;
}