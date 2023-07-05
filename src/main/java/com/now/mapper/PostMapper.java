package com.now.mapper;

import com.now.domain.post.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {

    List<Notice> findAllNotices();

    List<Community> findAllCommunity();

    List<Photo> findAllPhotos();

    List<Inquiry> findAllInquries();
}
