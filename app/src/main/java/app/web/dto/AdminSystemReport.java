package app.web.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class AdminSystemReport {
    // User-related statistics
    private long totalCountUsers;
    private long totalCountActiveUsers;
    private long totalCountInactiveUsers;
    private long totalCountAdmins;
    private long totalCountNonAdmins;

    // Post-related statistics
    private long totalCountPosts;
    private long totalCountLikedPosts;
    private BigDecimal totalLikes;

    // Additional admin-specific metrics
    private long totalReportedContent;
    private long totalBannedUsers;
}