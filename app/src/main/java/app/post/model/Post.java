package app.post.model;

import app.category.model.CategoryType;
import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Many posts can belong to one user
    @ManyToOne
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    private String imageUrl;

    @Column(nullable = false)
    private LocalDateTime updatedOn;

    private int views;
    private int likes;
    private double rating;
    private int shares;

    @Enumerated(EnumType.STRING)
    private PostStatus status = PostStatus.ACTIVE;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType categoryType;

    // For tests
    public Post(UUID id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }
}