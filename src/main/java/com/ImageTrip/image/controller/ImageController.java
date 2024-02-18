package com.ImageTrip.image.controller;

import com.ImageTrip.Schedule.dto.ScheduleDto;
import com.ImageTrip.image.dto.ImageDTO;
import com.ImageTrip.image.entity.Image;
import com.ImageTrip.image.mapper.ImageMapper;
import com.ImageTrip.image.service.ImageService;
import com.ImageTrip.member.entity.Member;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/picmap")
@Slf4j
public class ImageController {
    private final ImageService imageService;
    private final ImageMapper imageMapper;

    @Autowired
    public ImageController(ImageService imageService, ImageMapper imageMapper) {
        this.imageService = imageService;
        this.imageMapper = imageMapper;
    }

    @GetMapping("/")
    public void main(){
        System.out.println("picmap");
    }

    @ApiOperation(value = "내 사진 조회")
    @GetMapping("/mine")
    public ResponseEntity getMySchedules(@RequestParam(name = "searchTerm", required = false) String searchTerm, long memberId/*, @RequestHeader(value = "Authorization") String token*/){
//        Member member = memberService.findMemberByToken(token);
        Member member = new Member();
        List<Image> images = imageService.findAllBySearchAndMember(searchTerm, memberId);

        return ResponseEntity.ok(images);
    }

    @ApiOperation(value = "공유된 사진 조회")
    @GetMapping("/shared")
    public ResponseEntity<List<Image>> sharedImages(@RequestParam(name = "searchTerm", required = false) String searchTerm) {
        List<Image> images = imageService.findAllBySearchAndShared(searchTerm, true);
        return ResponseEntity.ok(images);
    }


    @ApiOperation(value = "사진 업로드")
    @PostMapping("/uploadModal")
    public ResponseEntity<?> uploadImages(@RequestPart("file") MultipartFile file,
                                          @RequestParam(value = "tag", required = false) String tag,
                                          @RequestParam(value = "shared", required = false) boolean shared/*,
                                          @RequestHeader(value = "Authorization") String token*/){
        // 토큰 검증 및 사용자 인증 (생략)
        long memberId = 1L;//jwtTokenizer.getUserId(token);


        try {
            System.out.println("1");
            System.out.println(file);
            System.out.println(tag);
            System.out.println(shared);
            ImageDTO.Upload uploadDto = new ImageDTO.Upload();
            uploadDto.setFile(file);
            uploadDto.setTag(tag);
            uploadDto.setShared(shared);

//            List<String> failedUploads = imageService.saveImages(uploadDto.getFiles(), uploadDto.getTag(), uploadDto.isShared(), memberId);
            String failedUploads = imageService.saveImages(uploadDto.getFile(), uploadDto.getTag(), uploadDto.isShared(), memberId);
            System.out.println(failedUploads);
            if (failedUploads == null || failedUploads.isEmpty()) {
                return ResponseEntity.ok("All images uploaded successfully.");
            } else {
                return ResponseEntity.badRequest().body("Failed to upload images: " + failedUploads);
            }
        } catch (Exception e) {
            System.out.println("에러발생");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }

    @ApiOperation(value = "사진 수정")
    @PatchMapping("/updateModal/{imageId}")
    public ResponseEntity update(@PathVariable long imageId,
                                 @RequestPart(value = "file", required = false) MultipartFile file,
                                 @RequestParam(required = false) String tag,
                                 @RequestParam(required = false) Boolean shared
                                    /*, @RequestHeader(value = "Authorization") String token*/){
        // 토큰 검증 및 사용자 인증 (생략)
        long memberId = 1L;//jwtTokenizer.getUserId(token);
        Image image = imageService.findByImageId(imageId);

        String updateTag = tag != null && image.getTag() != tag ? tag : image.getTag();
        boolean updateShared = shared != null && image.isShared() != shared ? shared : image.isShared();

//        ImageDTO.Upload uploadDto = new ImageDTO.Upload();
//        uploadDto.setFile(file);
//        uploadDto.setTag(tag);
//        uploadDto.setShared(shared);

//        Image image = imageMapper.imageUpdateDtoToImage(requestBody);
        if(file != null && !file.isEmpty()){
            imageService.deleteByImageId(imageId);
            imageService.saveImages(file, tag, shared, memberId);
        } else {
            imageService.updateImage(imageId, updateTag, updateShared);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "사진 삭제")
    @DeleteMapping("/delete/{imageId}")
    public ResponseEntity deleteSchedule(@PathVariable("imageId") int imageId /*,@RequestHeader(value = "Authorization") String token*/){
        imageService.deleteByImageId(imageId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}


