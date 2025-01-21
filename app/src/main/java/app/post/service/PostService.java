package app.post.service;

import app.comment.model.Comment;
import app.comment.repository.CommentRepository;
import app.comment.service.CommentService;
import app.exception.CommentNotFoundException;
import app.like.service.LikeService;
import app.rating.service.RatingService;
import app.post.model.Post;
import app.post.repository.PostRepository;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final CommentService commentService;
    private final LikeService likeService;
    private final RatingService ratingService;
    private final UserService userService;

    @Autowired
    public PostService(PostRepository postRepository,
                       CommentRepository commentRepository, CommentService commentService,
                       LikeService likeService,
                       RatingService ratingService,
                       UserService userService) {
        this.postRepository = postRepository;
        this.commentService = commentService;
        this.likeService = likeService;
        this.ratingService = ratingService;
        this.userService = userService;
    }

    // Създаване на пост
    public void createPost(User user, String title, String content) {
        Post post = Post.builder()
                .owner(user)
                .title(title)
                .content(content)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .likes(0)
                .rating(0)  // Default rating is 0
                .build();
        postRepository.save(post);
    }

    // Получаване на пост по ID
    public Post getPostById(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }
    public List<Post> getAllPosts() {
        return postRepository.findAll(); // This fetches all posts from the database
    }
    // Получаване на коментари за пост
    public List<Comment> getCommentsForPost(UUID postId) {
        return commentService.getCommentsForPost(postId);
    }

    // Добавяне на коментар
    public void addComment(UUID postId, User user, String content) {
        Post post = getPostById(postId);

        // Call the CommentService to save the comment
        commentService.addComment(post, user, content);
    }

    // Изтриване на коментар
    public void deleteCommentFromPost(UUID commentId) {
        commentService.deleteComment(commentId);  // Извикваме deleteComment от CommentService
    }

    // Лайкване на пост
    public void likePost(UUID postId, User user) {
        Post post = getPostById(postId);
        likeService.addLike(post, user);
        post.setLikes(post.getLikes() + 1);  // Increment like count
        postRepository.save(post);
    }

    public void ratePost(UUID postId, double rating, User user) {
        Post post = getPostById(postId);

        // Извикваме метода от RatingService за да добавим или актуализираме рейтинга
        ratingService.ratePost(post, user, rating);

        // Изчисляваме новия среден рейтинг на поста
        double newRating = ratingService.calculateAverageRating(postId);
        post.setRating(newRating);

        // Записваме актуализирания пост в базата
        postRepository.save(post);
    }

    // Изтриване на пост
    public void deletePostById(UUID postId) {
        postRepository.deleteById(postId);
    }

}
