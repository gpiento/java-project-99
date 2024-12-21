package hexlet.code.util;

import hexlet.code.exception.UserNotFoundException;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@AllArgsConstructor
public class UserUtils {

    private UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found"));
    }

    public boolean isCurrentUser(Long id) {
        User currentUser = getCurrentUser();
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.isPresent()
                && Objects.equals(userOptional.get().getEmail(), currentUser.getEmail());
    }
}
