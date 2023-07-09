package com.now.domain.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 커뮤니티 게시글을 나타내는 도메인 객체 
 */
@SuperBuilder(toBuilder = true)
@Getter
@ToString
@NoArgsConstructor(force = true)
public class Community extends Post {
}
