package com.example.project.entity.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostRequest {

    @NotBlank(message = "User email is required")
    private String userEmail;
    @NotBlank(message = "Text cannot be empty")
    private String text;
}