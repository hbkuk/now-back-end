package com.now.service;

import com.now.domain.post.Community;
import com.now.domain.post.Inquiry;
import com.now.domain.post.Notice;
import com.now.domain.post.Photo;
import com.now.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Notice> findAllNotices() {
        return postRepository.findAllNotices();
    }

    public List<Community> findAllCommunity() {
        return postRepository.findAllCommunity();
    }

    public List<Photo> findAllPhotos() {
        return postRepository.findAllPhotos();
    }

    public List<Inquiry> findAllInquries() {
        return postRepository.findAllInquries();
    }
}
