package app.rating.model;

import app.post.model.Post;
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
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Post post;  // Link to the post being rated

    @ManyToOne
    private User user;  // User who rated the post

    @Column(nullable = false)
    private double rating;  // Rating value between 1.0 and 5.0

    @Column(nullable = false)
    private LocalDateTime createdOn;  // Date the rating was given

}
