package com.springboot.blog.payload;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class CommentsDto {
    private long id;
    private String name;
    private String email;
    private String body;
}
