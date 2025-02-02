package app.web.dto.mapper;

import app.post.model.Post;
import app.user.model.User;
import app.user.model.UserRole;
import app.web.dto.AdminSystemReport;
import app.web.dto.UserEditRequest;
import app.web.dto.UserSystemReport;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.List;

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
                // Add more admin-specific metrics as needed
                .totalReportedContent(0) // You'd need to implement this logic
                .totalBannedUsers(users.stream().filter(User::isBanned).count())
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
                .userTotalComments(0)
                .communityTotalUsers(allUsers.size())
                .communityTotalPosts(allPosts.size())
                .communityTotalLikes(allPosts.stream().mapToLong(Post::getLikes).sum())
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

}