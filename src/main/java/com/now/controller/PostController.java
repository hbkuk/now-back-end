package com.now.controller;

import com.now.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/api/post")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> findAllPost(Map<String, Object> response) {
        log.debug("findAllPost 호출");

        response.put("noties", postService.findAllNotices());
        response.put("community", postService.findAllCommunity());
        response.put("photos", postService.findAllPhotos());
        response.put("inquries", postService.findAllInquries());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
