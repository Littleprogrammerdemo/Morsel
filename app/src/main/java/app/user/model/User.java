package app.user.model;

import app.comment.model.Comment;
import app.like.model.Like;
import app.post.model.Post;
import app.rating.model.Rating;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    private String firstName;

    private String lastName;

    private String profilePicture;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private boolean isActive;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @Column(nullable = false)
    private LocalDateTime updatedOn;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner")
    private List<Like> likes = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner")
    private List<Rating> ratings = new ArrayList<>();

    public User(UUID id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    private UserStatus status;


    public boolean isBanned() {
        return status == UserStatus.BANNED;
    }
}