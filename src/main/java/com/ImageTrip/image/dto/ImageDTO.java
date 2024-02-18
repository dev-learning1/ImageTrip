package com.ImageTrip.image.dto;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor
public class ImageDTO {
    @Getter
    public static class Reponse{
        private boolean check;
    }

    @Setter
    @Getter
    public static class Upload {
        private MultipartFile file;
        private boolean shared;
        private String tag;
    }



    @Getter
    @ApiModel(description = "이미지 정보 업데이트를 위한 DTO")
    public static class Update{
        private String tag;
        private Boolean shared;
    }
}
