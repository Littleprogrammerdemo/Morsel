package app.post.model;

import app.comment.model.Comment;
import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
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

    // Много постове могат да принадлежат на един потребител
    @ManyToOne
    @JoinColumn(name = "user_id")  // Ensure this column exists in your database
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 800)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @Column(nullable = false)
    private LocalDateTime updatedOn;

    @Column(nullable = false)
    private int likes;

    @Column(nullable = false)
    private double rating;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;  // List of comments for this post
}