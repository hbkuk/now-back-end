package com.now.repository;

import com.now.domain.post.Community;
import com.now.domain.post.Inquiry;
import com.now.domain.post.Notice;
import com.now.domain.post.Photo;
import com.now.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostRepository {

    public PostMapper postMapper;

    @Autowired
    public PostRepository(PostMapper postMapper) {
        this.postMapper = postMapper;
    }

    public List<Notice> findAllNotices() {
        return postMapper.findAllNotices();
    }

    public List<Community> findAllCommunity() {
        return postMapper.findAllCommunity();
    }

    public List<Photo> findAllPhotos() {
        return postMapper.findAllPhotos();
    }

    public List<Inquiry> findAllInquries() {
        return postMapper.findAllInquries();
    }
}
