package app.web.dto.mapper;

import app.post.model.Post;
import app.user.model.User;
import app.user.model.UserRole;
import app.web.dto.*;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class DtoMapper {
    public static AdminSystemReport mapToAdminSystemReport(List<User> users, List<Post> posts) {
        return AdminSystemReport.builder()
                .totalCountUsers(users.size())
                .totalCountActiveUsers(users.stream().filter(User::isActive).count())
                .totalCountInactiveUsers(users.stream().filter(user -> !user.isActive()).count())
                .totalCountAdmins(users.stream().filter(user -> user.getRole() == UserRole.ADMIN).count())
                .totalCountNonAdmins(users.stream().filter(user -> user.getRole() != UserRole.ADMIN).count())
                .totalCountPosts(posts.size())
                .totalCountLikedPosts(posts.stream().filter(post -> post.getLikes() > 0).count())
                .totalLikes(BigDecimal.valueOf(posts.stream().mapToLong(Post::getLikes).sum()))
                .build();
    }

    public static UserSystemReport mapToUserSystemReport(User currentUser, List<Post> allPosts, List<User> allUsers) {
        // Calculate user-specific metrics
        long userPosts = allPosts.stream()
                .filter(post -> post.getOwner().getId().equals(currentUser.getId()))
                .count();

        long userLikes = allPosts.stream()
                .filter(post -> post.getOwner().getId().equals(currentUser.getId()))
                .mapToLong(Post::getLikes)
                .sum();

        // Calculate community-wide metrics
        return UserSystemReport.builder()
                .userTotalPosts(userPosts)
                .userTotalLikes(userLikes)
                .userTotalComments(0) //default
                .build();
    }
    public static UserEditRequest mapUserToUserEditRequest(User user) {

        return UserEditRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .build();
    }
    public static BookmarkRequest mapToBookmarkRequest(UUID userId, UUID postId) {
        return BookmarkRequest.builder()
                .userId(userId)
                .postId(postId)
                .build();
    }

    public static FollowRequest mapToFollowRequest(UUID followerId, UUID followedId) {
        return FollowRequest.builder()
                .followerId(followerId)
                .followedId(followedId)
                .build();
    }

    public static LoginRequest mapToLoginRequest(String username, String password) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        return loginRequest;
    }

    public static MessageRequest mapToMessageRequest(UUID senderId, UUID receiverId, String content) {
        return MessageRequest.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .build();
    }

        public static CreateNewPost mapToPostCommand(Post post) {
            return CreateNewPost.builder()
                    .title(post.getTitle())
                    .content(post.getContent())
                    .build();
        }
    }
