package app.web.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class UserSystemReport {
    // User-specific statistics
    private long userTotalPosts;
    private long userTotalLikes;
    private long userTotalComments;


}