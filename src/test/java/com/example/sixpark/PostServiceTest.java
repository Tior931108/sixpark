package com.example.sixpark;

import com.example.sixpark.common.enums.UserRole;
import com.example.sixpark.domain.genre.entity.Genre;
import com.example.sixpark.domain.post.entity.Post;
import com.example.sixpark.domain.post.model.response.PostGetAllResponse;
import com.example.sixpark.domain.post.reository.PostRepository;
import com.example.sixpark.domain.post.service.PostService;
import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import com.example.sixpark.domain.showinfo.repository.ShowInfoRepository;
import com.example.sixpark.domain.user.entity.User;
import com.example.sixpark.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService - 전체 조회 테스트")
class PostServiceGetAllTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    private User testUser1;
    private User testUser2;
    private Genre testGenre;
    private ShowInfo testShowInfo1;
    private ShowInfo testShowInfo2;
    private Post testPost1;
    private Post testPost2;
    private Post testPost3;

    @BeforeEach
    void setUp() {
        // 테스트 Genre 데이터
        testGenre = new Genre("뮤지컬");
        setId(testGenre, 1L);

        // 테스트 User 데이터
        testUser1 = new User(
                "user1@test.com",
                "password123",
                "홍길동",
                "hong",
                LocalDate.of(1990, 1, 1)
        );
        setId(testUser1, 1L);

        testUser2 = new User(
                "user2@test.com",
                "password123",
                "김철수",
                "kim",
                LocalDate.of(1995, 5, 15)
        );
        setId(testUser2, 2L);

        // 테스트 ShowInfo 데이터
        testShowInfo1 = new ShowInfo(
                testGenre,
                "MT001",
                "뮤지컬 레미제라블",
                Arrays.asList("홍광호", "민영기").toString(),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 3, 31),
                "poster1.jpg",
                15
        );
        setId(testShowInfo1, 1L);

        testShowInfo2 = new ShowInfo(
                testGenre,
                "MT002",
                "오페라의 유령",
                Arrays.asList("김소현", "박은태").toString(),
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 4, 30),
                "poster2.jpg",
                13
        );
        setId(testShowInfo2, 2L);

        // 테스트 Post 데이터
        testPost1 = new Post(testUser1, testShowInfo1, "첫 번째 게시글", "첫 번째 내용");
        setId(testPost1, 1L);

        testPost2 = new Post(testUser1, testShowInfo2, "두 번째 게시글", "두 번째 내용");
        setId(testPost2, 2L);

        testPost3 = new Post(testUser2, testShowInfo1, "세 번째 게시글", "세 번째 내용");
        setId(testPost3, 3L);
    }

    // Reflection을 사용하여 ID 설정
    private void setId(Object entity, Long id) {
        try {
            java.lang.reflect.Field field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException("ID 설정 실패", e);
        }
    }

    @Test
    @DisplayName("전체 조회 성공 - 여러 게시글이 있는 경우")
    void getPostList_Success_MultiplePost() {
        // Given
        List<Post> posts = Arrays.asList(testPost1, testPost2, testPost3);
        Page<Post> postPage = new PageImpl<>(posts, PageRequest.of(0, 10), posts.size());
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        given(postRepository.findAllByIsDeletedFalse(pageable)).willReturn(postPage);

        // When
        Page<PostGetAllResponse> result = postService.getPostList(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("첫 번째 게시글");
        assertThat(result.getContent().get(1).getTitle()).isEqualTo("두 번째 게시글");
        assertThat(result.getContent().get(2).getTitle()).isEqualTo("세 번째 게시글");

        verify(postRepository, times(1)).findAllByIsDeletedFalse(pageable);
    }

    @Test
    @DisplayName("전체 조회 성공 - 게시글이 1개만 있는 경우")
    void getPostList_Success_SinglePost() {
        // Given
        List<Post> posts = Collections.singletonList(testPost1);
        Page<Post> postPage = new PageImpl<>(posts, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);

        given(postRepository.findAllByIsDeletedFalse(pageable)).willReturn(postPage);

        // When
        Page<PostGetAllResponse> result = postService.getPostList(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("첫 번째 게시글");

        verify(postRepository).findAllByIsDeletedFalse(pageable);
    }

    @Test
    @DisplayName("전체 조회 성공 - 게시글이 없는 경우 (빈 페이지)")
    void getPostList_Success_EmptyPage() {
        // Given
        Page<Post> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        Pageable pageable = PageRequest.of(0, 10);

        given(postRepository.findAllByIsDeletedFalse(pageable)).willReturn(emptyPage);

        // When
        Page<PostGetAllResponse> result = postService.getPostList(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(0);

        verify(postRepository).findAllByIsDeletedFalse(pageable);
    }

    @Test
    @DisplayName("전체 조회 성공 - 페이징 처리 (1페이지)")
    void getPostList_Success_WithPaging_FirstPage() {
        // Given
        List<Post> posts = Arrays.asList(testPost1, testPost2);
        Page<Post> postPage = new PageImpl<>(posts, PageRequest.of(0, 2), 3);
        Pageable pageable = PageRequest.of(0, 2);

        given(postRepository.findAllByIsDeletedFalse(pageable)).willReturn(postPage);

        // When
        Page<PostGetAllResponse> result = postService.getPostList(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getNumber()).isEqualTo(0);

        verify(postRepository).findAllByIsDeletedFalse(pageable);
    }

    @Test
    @DisplayName("전체 조회 성공 - 페이징 처리 (2페이지)")
    void getPostList_Success_WithPaging_SecondPage() {
        // Given
        List<Post> posts = Collections.singletonList(testPost3);
        Page<Post> postPage = new PageImpl<>(posts, PageRequest.of(1, 2), 3);
        Pageable pageable = PageRequest.of(1, 2);

        given(postRepository.findAllByIsDeletedFalse(pageable)).willReturn(postPage);

        // When
        Page<PostGetAllResponse> result = postService.getPostList(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getNumber()).isEqualTo(1);

        verify(postRepository).findAllByIsDeletedFalse(pageable);
    }

    @Test
    @DisplayName("전체 조회 성공 - 정렬 확인 (최신순)")
    void getPostList_Success_WithSorting_Desc() {
        // Given
        List<Post> posts = Arrays.asList(testPost3, testPost2, testPost1); // 최신순
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());

        given(postRepository.findAllByIsDeletedFalse(pageable)).willReturn(postPage);

        // When
        Page<PostGetAllResponse> result = postService.getPostList(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("세 번째 게시글");
        assertThat(result.getContent().get(1).getTitle()).isEqualTo("두 번째 게시글");
        assertThat(result.getContent().get(2).getTitle()).isEqualTo("첫 번째 게시글");

        verify(postRepository).findAllByIsDeletedFalse(pageable);
    }

    @Test
    @DisplayName("전체 조회 성공 - 정렬 확인 (오래된 순)")
    void getPostList_Success_WithSorting_Asc() {
        // Given
        List<Post> posts = Arrays.asList(testPost1, testPost2, testPost3); // 오래된 순
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());

        given(postRepository.findAllByIsDeletedFalse(pageable)).willReturn(postPage);

        // When
        Page<PostGetAllResponse> result = postService.getPostList(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("첫 번째 게시글");
        assertThat(result.getContent().get(1).getTitle()).isEqualTo("두 번째 게시글");
        assertThat(result.getContent().get(2).getTitle()).isEqualTo("세 번째 게시글");

        verify(postRepository).findAllByIsDeletedFalse(pageable);
    }

    @Test
    @DisplayName("전체 조회 성공 - Response DTO 필드 검증")
    void getPostList_Success_ResponseFieldValidation() {
        // Given
        List<Post> posts = Collections.singletonList(testPost1);
        Page<Post> postPage = new PageImpl<>(posts);
        Pageable pageable = PageRequest.of(0, 10);

        given(postRepository.findAllByIsDeletedFalse(pageable)).willReturn(postPage);

        // When
        Page<PostGetAllResponse> result = postService.getPostList(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        PostGetAllResponse response = result.getContent().get(0);

        // 각 필드 개별 검증 (null 체크 포함)
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isNotNull();
        assertThat(response.getTitle()).isEqualTo("첫 번째 게시글");
        assertThat(response.getUserId()).isNotNull();
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getShowInfoId()).isNotNull();
        assertThat(response.getShowInfoId()).isEqualTo(1L);
        assertThat(response.getCreatedAt()).isNull();

        verify(postRepository).findAllByIsDeletedFalse(pageable);
    }

    @Test
    @DisplayName("전체 조회 성공 - 삭제된 게시글은 제외됨")
    void getPostList_Success_ExcludingDeletedPosts() {
        // Given
        // testPost2는 삭제된 상태
        testPost2.softDelete();

        // 삭제되지 않은 게시글만 반환
        List<Post> activePosts = Arrays.asList(testPost1, testPost3);
        Page<Post> postPage = new PageImpl<>(activePosts);
        Pageable pageable = PageRequest.of(0, 10);

        given(postRepository.findAllByIsDeletedFalse(pageable)).willReturn(postPage);

        // When
        Page<PostGetAllResponse> result = postService.getPostList(pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(PostGetAllResponse::getTitle)
                .containsExactly("첫 번째 게시글", "세 번째 게시글")
                .doesNotContain("두 번째 게시글");

        verify(postRepository).findAllByIsDeletedFalse(pageable);
    }

    @Test
    @DisplayName("전체 조회 성공 - 다양한 페이지 크기 테스트")
    void getPostList_Success_VariousPageSizes() {
        // Given - 총 5개의 게시글, 페이지 크기 3
        List<Post> allPosts = Arrays.asList(testPost1, testPost2, testPost3, testPost1, testPost2);
        List<Post> firstPagePosts = Arrays.asList(testPost1, testPost2, testPost3);
        Page<Post> postPage = new PageImpl<>(firstPagePosts, PageRequest.of(0, 3), 5);
        Pageable pageable = PageRequest.of(0, 3);

        given(postRepository.findAllByIsDeletedFalse(pageable)).willReturn(postPage);

        // When
        Page<PostGetAllResponse> result = postService.getPostList(pageable);

        // Then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.isFirst()).isTrue();

        verify(postRepository).findAllByIsDeletedFalse(pageable);
    }
}
