package app.like.service;

import app.like.model.Like;
import app.like.repository.LikeRepository;
import app.post.model.Post;
import app.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    private final LikeRepository likeRepository;

    @Autowired
    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    // Добавяне на лайк
    public void addLike(Post post, User user) {
        Like like = Like.builder()
                .post(post)
                .owner(user)
                .build();
        likeRepository.save(like);
    }

    // Премахване на лайк
    public void removeLike(Post post, User user) {
        Like like = likeRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new IllegalArgumentException("Like not found"));
        likeRepository.delete(like);
    }
}
