package app.post.model;

import app.comment.model.Comment;
import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private User owner;

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

    @ManyToMany (fetch = FetchType.EAGER, mappedBy = "owner")
    private List<Comment> comments = new ArrayList<>();  // List of comments for this post
}