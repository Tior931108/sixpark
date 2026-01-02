package com.example.sixpark.domain.post.service;

import com.example.sixpark.common.enums.ErrorMessage;
import com.example.sixpark.common.excepion.CustomException;
import com.example.sixpark.domain.post.entity.Post;
import com.example.sixpark.domain.post.model.dto.PostDto;
import com.example.sixpark.domain.post.model.request.PostCreateRequest;
import com.example.sixpark.domain.post.model.response.PostCreateResponse;
import com.example.sixpark.domain.post.reository.PostRepository;
import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import com.example.sixpark.domain.showinfo.repository.ShowInfoRepository;
import com.example.sixpark.domain.user.entity.User;
import com.example.sixpark.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ShowInfoRepository showInfoRepository;

    // 게시글 생성 기능
    @Transactional
    public PostCreateResponse createPost(Long userId, Long showInfoId, PostCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorMessage.NOT_FOUND_USER));

        ShowInfo showInfo = showInfoRepository.findById(showInfoId.intValue())
                .orElseThrow(() -> new CustomException(ErrorMessage.NOT_FOUND_TASK));

        Post post = new Post(user, showInfo, request.getTitle(), request.getContent());
        Post savedPost = postRepository.save(post);

        PostDto postDto = PostDto.from(savedPost);
        return PostCreateResponse.from(postDto);

    }
}
