package com.now.core.post.presentation;

import com.now.core.file.domain.constants.UploadType;
import com.now.core.post.domain.Community;
import com.now.core.post.domain.Inquiry;
import com.now.core.post.domain.Notice;
import com.now.core.post.domain.Photo;
import com.now.core.post.presentation.dto.Condition;
import com.now.core.post.presentation.dto.Posts;
import com.now.core.authentication.constants.Authority;
import com.now.core.file.application.FileService;
import com.now.core.post.application.PostService;
import com.now.core.post.domain.PostValidationGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Map;

/**
 * 게시글 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final FileService fileService;


    /**
     * 모든 게시글 정보를 조회하는 핸들러 메서드
     *
     * @return 모든 게시글 정보와 함께 OK 응답을 반환
     */
    @GetMapping("/api/posts")
    public ResponseEntity<Posts> retrievePosts(@Valid @ModelAttribute Condition condition) {
        log.debug("retrievePosts 호출, condition : {}", condition);

        Posts posts = Posts.create(Map.of(
                "notices", postService.retrieveAllNotices(condition), "community", postService.retrieveAllCommunity(condition),
                "photos", postService.retrieveAllPhotos(condition), "inquiries", postService.retrieveAllInquiries(condition)
        ));

        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    /**
     * 공지 게시글을 등록
     *
     * @param managerId 작성자의 매니저 ID
     * @param notice    등록할 공지 게시글 정보
     * @return 생성된 게시글에 대한 CREATED 응답을 반환
     */
    @PostMapping("/api/notice")
    public ResponseEntity<Void> registerNotice(@RequestAttribute("id") String managerId, @RequestAttribute("role") String authority,
                                               @RequestBody @Validated(PostValidationGroup.register.class) Notice notice) {
        log.debug("registerNotice 호출, managerId : {}, authority : {}, notice : {}", managerId, authority, notice);

        postService.registerManagerPost(notice.updateManagerId(managerId), Authority.valueOf(authority));

        return ResponseEntity.status(HttpStatus.CREATED).build(); // Status Code 201
    }

    /**
     * 커뮤니티 게시글을 등록
     *
     * @param memberId    작성자의 회원 ID
     * @param community 등록할 커뮤니티 게시글 정보
     * @return 생성된 게시글에 대한 CREATED 응답을 반환
     */
    @PostMapping("/api/community")
    public ResponseEntity<Void> registerCommunity(@RequestAttribute("id") String memberId, @RequestAttribute("role") String authority,
                                                  @RequestPart(value = "community") @Validated(PostValidationGroup.register.class) Community community,
                                                  @RequestPart(value = "file", required = false) MultipartFile[] multipartFiles) {
        log.debug("registerCommunity 호출, memberId : {}, authority : {}, Community : {}, Multipart : {}", memberId, authority, community, (multipartFiles != null ? multipartFiles.length : "null"));

        postService.registerMemberPost(community.updateMemberId(memberId), Authority.valueOf(authority));
        fileService.saveFile(multipartFiles, community.getPostIdx(), UploadType.FILE);

        return ResponseEntity.status(HttpStatus.CREATED).build(); // Status Code 201
    }

    /**
     * 사진 게시글을 등록
     *
     * @param memberId 작성자의 회원 ID
     * @param photo  등록할 사진 게시글 정보
     * @return 생성된 게시글에 대한 CREATED 응답을 반환
     */
    @PostMapping("/api/photo")
    public ResponseEntity<Void> registerPhoto(@RequestAttribute("id") String memberId, @RequestAttribute("role") String authority,
                                              @RequestPart(value = "photo") @Validated(PostValidationGroup.register.class) Photo photo,
                                              @RequestPart(value = "file", required = false) MultipartFile[] multipartFiles) {
        log.debug("registerPhoto 호출, memberId : {}, authority : {}, Community : {}, Multipart : {}", memberId, authority, photo, (multipartFiles != null ? multipartFiles.length : "null"));

        postService.registerMemberPost(photo.updateMemberId(memberId), Authority.valueOf(authority));
        fileService.saveFile(multipartFiles, photo.getPostIdx(), UploadType.IMAGE);

        return ResponseEntity.status(HttpStatus.CREATED).build(); // Status Code 201
    }

    /**
     * 문의 게시글을 등록
     *
     * @param memberId  작성자의 회원 ID
     * @param inquiry 등록할 문의 게시글 정보
     * @return 생성된 게시글에 대한 CREATED 응답을 반환
     */
    @PostMapping("/api/inquiry")
    public ResponseEntity<Void> registerInquiry(@RequestAttribute("id") String memberId, @RequestAttribute("role") String authority,
                                                @RequestBody @Validated(PostValidationGroup.register.class) Inquiry inquiry) {
        log.debug("registerInquiry 호출, memberId : {}, authority : {}, inquiry : {}", memberId, authority, inquiry);

        postService.registerMemberPost(inquiry.updateMemberId(memberId), Authority.valueOf(authority));

        return ResponseEntity.status(HttpStatus.CREATED).build(); // Status Code 201
    }

}
