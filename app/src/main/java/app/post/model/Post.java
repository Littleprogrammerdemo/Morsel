package app.post.model;

import app.category.model.Category;
import app.category.model.CategoryType;
import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "posts")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Много постове могат да принадлежат на един потребител
    @ManyToOne
    private User owner;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(name = "creation_date",nullable = false)
    private LocalDateTime createdOn;

    @Lob  // Use @Lob to store large objects (binary data)
    private byte[] image;  // Store image as byte array

    @Column(nullable = false)
    private LocalDateTime updatedOn;

    @Column(nullable = false)
    private int likes;

    @Column(nullable = false)
    private double rating;


    @Enumerated(EnumType.STRING)
    private PostStatus status = PostStatus.ACTIVE;

    @ManyToOne
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType category_type;
}