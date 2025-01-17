package app.admin.service;

import app.admin.repository.AdminRepository;
import app.admin.model.Admin;
import app.post.repository.PostRepository;
import app.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository, UserRepository userRepository, PostRepository postRepository) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public Optional<Admin> getAdminById(UUID id) {
        return adminRepository.findById(id);
    }

    public Admin saveAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    public void deleteAdmin(UUID id) {
        adminRepository.deleteById(id);
    }
    public void deleteUserById(UUID userId) {
        // Use the repository to delete the user by their ID
        userRepository.deleteById(userId);



    }
}
