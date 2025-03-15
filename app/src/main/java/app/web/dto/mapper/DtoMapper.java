package app.web.dto.mapper;

import app.post.model.Post;
import app.user.model.User;
import app.user.model.UserRole;
import app.web.dto.*;
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
