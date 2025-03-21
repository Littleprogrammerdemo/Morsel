package app.follow.model;

import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendshipInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private User invitingUser;

    @ManyToOne
    private User acceptingUser;

    private boolean accepted = false;

    // Constructor with validation
    public FriendshipInvitation(User invitingUser, User acceptingUser) {
        if (invitingUser == null || acceptingUser == null) {
            throw new IllegalArgumentException("Both invitingUser and acceptingUser must be provided.");
        }
        this.invitingUser = invitingUser;
        this.acceptingUser = acceptingUser;
        this.accepted = false;  // Default to false
    }
}
