package app.comment.model;

import app.post.model.Post;
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
@Table(name = "comment")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)// Много коментари могат да принадлежат на един потребител
    private User owner;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)// Много коментари могат да бъдат свързани с един пост
    private Post post;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @Column(nullable = false)
    private LocalDateTime updatedOn;

}