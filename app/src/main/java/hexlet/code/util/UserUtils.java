package hexlet.code.util;

import hexlet.code.exception.UserNotFoundException;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userUtils")
@AllArgsConstructor
public class UserUtils {

    private UserRepository userRepository;

    public boolean isCurrentUser(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        String currentUser = authentication.getName();
        return currentUser.equals(user.getEmail());
    }
}
