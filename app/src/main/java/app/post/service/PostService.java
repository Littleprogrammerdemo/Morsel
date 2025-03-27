package app.post.service;

import app.category.model.CategoryType;
import app.comment.model.Comment;
import app.comment.service.CommentService;
import app.post.model.Post;
import app.post.model.PostStatus;
import app.post.repository.PostRepository;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.service.UserService;
import app.web.dto.CreateNewPost;
import app.web.dto.UpdatePostRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final CommentService commentService;
    private final UserService userService;

    @Autowired
    public PostService(PostRepository postRepository, CommentService commentService,
                       UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.commentService = commentService;
    }

    @Transactional
    public void createPost(User user, CreateNewPost createNewPost) {
        Post post = Post.builder()
                .user(user)
                .title(createNewPost.getTitle())
                .content(createNewPost.getContent())
                .categoryType(CategoryType.valueOf(createNewPost.getCategoryType()))
                .imageUrl(createNewPost.getImageUrl())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .likes(0)
                .rating(0)
                .ratingCount(0)
                .status(PostStatus.ACTIVE)
                .likedUsers(new HashSet<>())
                .viewedUsers(new HashSet<>())
                .build();

        postRepository.save(post);
    }

    public void updatePost(UUID id, UpdatePostRequest updatePost) {
        Post post = getPostById(id);

        if (updatePost.getTitle() != null) post.setTitle(updatePost.getTitle());
        if (updatePost.getContent() != null) post.setContent(updatePost.getContent());
        if (updatePost.getCategoryType() != null)
            post.setCategoryType(CategoryType.valueOf(updatePost.getCategoryType()));
        if (updatePost.getImageFile() != null) post.setImageUrl(updatePost.getImageFile());

        post.setUpdatedOn(LocalDateTime.now());
        postRepository.save(post);
    }

    public Post getPostById(UUID id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Transactional
    public void likePost(UUID id, User user) {
        Post post = getPostById(id);

        if (!post.getLikedUsers().contains(user.getId())) {
            post.setLikes(post.getLikes() + 1);
            post.getLikedUsers().add(user.getId());
            postRepository.save(post);
        }
    }

    @Transactional
    public void incrementView(UUID postId, User user) {
        Post post = getPostById(postId);

        if (!post.getViewedUsers().contains(user.getId())) {
            post.setViews(post.getViews() + 1);
            post.getViewedUsers().add(user.getId());
            postRepository.save(post);
        }
    }

    @Transactional
    public void sharePost(UUID id) {
        Post post = getPostById(id);
        post.setShares(post.getShares() + 1);
        postRepository.save(post);
    }

    @Transactional
    public void ratePost(UUID id, User user, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating should be between 1 and 5.");
        }

        Post post = getPostById(id);
        int totalRatings = post.getRatingCount() + 1;
        int newRatingSum = (int) ((post.getRating() * post.getRatingCount()) + rating);
        post.setRating(newRatingSum / totalRatings);
        post.setRatingCount(totalRatings);

        postRepository.save(post);
    }

    public List<Comment> getCommentsForPost(UUID postId) {
        return commentService.getCommentsForPost(postId);
    }

    @Transactional
    public void addComment(UUID id, User user, String content) {
        Post post = getPostById(id);

        if (post == null) {
            throw new RuntimeException("Post not found with id: " + id);
        }

        commentService.addComment(post, user, content);
    }


    public void deleteCommentFromPost(UUID commentId) {
        commentService.deleteComment(commentId);
    }

    public List<Post> getPostsByUser(User user) {
        return postRepository.findByUser(user);
    }

    public void deletePost(UUID id, User user) {
        Post post = getPostById(id);

        if (!isAdmin(user) && !isPostCreator(user, post)) {
            throw new RuntimeException("Only admins or creators can delete posts.");
        }

        postRepository.delete(post);
    }

    private boolean isAdmin(User user) {
        return user != null && user.getRole() == UserRole.ADMIN;
    }

    private boolean isPostCreator(User user, Post post) {
        return user != null && post != null && user.getId().equals(post.getUser().getId());
    }
}
