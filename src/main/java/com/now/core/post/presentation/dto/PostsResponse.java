package com.now.core.post.presentation.dto;

import com.now.core.post.domain.Community;
import com.now.core.post.domain.Inquiry;
import com.now.core.post.domain.Notice;
import com.now.core.post.domain.Photo;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 게시글 목록을 담는 데이터 전송 객체
 */
@Slf4j
@Builder(toBuilder = true)
@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class PostsResponse {

    private final List<Notice> notices;
    private final List<Community> communities;
    private final List<Photo> photos;
    private final List<Inquiry> inquiries;

    /**
     * 전달받은 게시글 목록으로 데이터 전송 객체에 담은 후 반환
     *
     * @param postsList 게시글 목록
     * @return 전달받은 게시글 목록으로 데이터 전송 객체에 담은 후 반환
     */
    public static PostsResponse convertToPostsResponse(List<Posts> postsList) {
        List<Notice> notices = new ArrayList<>();
        List<Community> communities = new ArrayList<>();
        List<Photo> photos = new ArrayList<>();
        List<Inquiry> inquiries = new ArrayList<>();

        for (Posts posts : postsList) {
            if (posts.getNotice() != null) {
                notices.add(posts.getNotice());
            }
            if (posts.getCommunity() != null) {
                communities.add(posts.getCommunity());
            }
            if (posts.getPhoto() != null) {
                photos.add(posts.getPhoto());
            }
            if (posts.getInquiry() != null) {
                inquiries.add(posts.getInquiry());
            }
        }

        return new PostsResponse(notices, communities, photos, inquiries);
    }

}


