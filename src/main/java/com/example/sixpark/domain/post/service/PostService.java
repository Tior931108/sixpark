package com.example.sixpark.domain.post.service;

import com.example.sixpark.common.enums.ErrorMessage;
import com.example.sixpark.common.excepion.CustomException;
import com.example.sixpark.common.security.userDetail.AuthUser;
import com.example.sixpark.domain.post.entity.Post;
import com.example.sixpark.domain.post.model.dto.PostDto;
import com.example.sixpark.domain.post.model.request.PostCreateRequest;
import com.example.sixpark.domain.post.model.request.PostUpdateRequest;
import com.example.sixpark.domain.post.model.response.PostCreateResponse;
import com.example.sixpark.domain.post.model.response.PostGetAllResponse;
import com.example.sixpark.domain.post.model.response.PostGetOneResponse;
import com.example.sixpark.domain.post.model.response.PostUpdateResponse;
import com.example.sixpark.domain.post.reository.PostRepository;
import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import com.example.sixpark.domain.showinfo.repository.ShowInfoRepository;
import com.example.sixpark.domain.user.entity.User;
import com.example.sixpark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ShowInfoRepository showInfoRepository;

    // 게시글 생성 기능
    @Transactional
    public PostCreateResponse createPost(PostCreateRequest request, AuthUser authUser) {
        User user = userRepository.findById(authUser.getUserId())
                .orElseThrow(() -> new CustomException(ErrorMessage.NOT_FOUND_USER));

        if (user.isDeleted()) {
            throw new CustomException(ErrorMessage.NOT_FOUND_USER);
        }

        ShowInfo showInfo = showInfoRepository.findById(request.getShowInfoId())
                .orElseThrow(() -> new CustomException(ErrorMessage.NOT_FOUND_SHOWINFO));

        if (showInfo.isDeleted()) {
            throw new CustomException(ErrorMessage.NOT_FOUND_SHOWINFO);
        }

        Post post = new Post(user, showInfo, request.getTitle(), request.getContent());
        Post savedPost = postRepository.save(post);

        PostDto postDto = PostDto.from(savedPost);
        return PostCreateResponse.from(postDto);
    }

    // 게시글 전체 조회 기능
    @Transactional(readOnly = true)
    public Page<PostGetAllResponse> getPostList(Pageable pageable) {
        Page<Post> posts = postRepository.findAllByIsDeletedFalse(pageable);

        return posts.map(post -> {
            PostDto postDto = PostDto.from(post);
            return PostGetAllResponse.from(postDto);
        });
    }

    // 게시글 상세 조회 기능
    @Transactional(readOnly = true)
    public PostGetOneResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorMessage.NOT_FOUND_POST));

        if (post.isDeleted()) {
            throw new CustomException(ErrorMessage.NOT_FOUND_POST);
        }

        PostDto postDto = PostDto.from(post);
        return PostGetOneResponse.from(postDto);
    }

    @Transactional
    public PostUpdateResponse updatePost(Long userId, Long postId, PostUpdateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorMessage.NOT_FOUND_POST));

        if (post.isDeleted()) {
            throw new CustomException(ErrorMessage.NOT_FOUND_POST);
        }

        if (!post.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorMessage.NOT_MODIFY_AUTHORIZED);
        }

        post.update(request.getTitle(), request.getContent());

        PostDto postDto = PostDto.from(post);
        return PostUpdateResponse.from(postDto);
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorMessage.NOT_FOUND_POST));

        if (post.isDeleted()) {
            throw new CustomException(ErrorMessage.NOT_FOUND_POST);
        }

        // 작성자 본인 확인
        if (!post.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorMessage.NOT_DELETE_AUTHORIZED);
        }
        post.softDelete();
    }
}