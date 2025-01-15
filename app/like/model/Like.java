package app.like.model;

import app.post.model.Post;
import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
