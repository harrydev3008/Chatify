package com.hisu.zola.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import com.google.gson.annotations.Expose;

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
    private String type;
    private String uri;
//    private LocalDateTime sentDate;
}