package hexlet.code.service;

import hexlet.code.exception.UserAlreadyExistsException;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsManager {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserUtils userUtils;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User '" + email + "' not found"));
    }

    @Override
    public void createUser(UserDetails userData) {
        if (userRepository.findByEmail(userData.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User was already exist");
        }
        User newUser = User.builder()
                .email(userData.getUsername())
                .passwordDigest(passwordEncoder.encode(userData.getPassword()))
                .build();
        userRepository.save(newUser);
    }

    @Override
    public void updateUser(UserDetails user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }

    @Override
    public void deleteUser(String username) {
        User existingUser = userRepository.findByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException("User  '" + username + "' not found"));
        userRepository.delete(existingUser);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        User user = userUtils.getCurrentUser();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        User existingUser = userRepository.findByEmail(user.getUsername()).orElseThrow(() ->
                new UsernameNotFoundException("User  '" + user.getUsername() + "' not found"));

        existingUser.setPasswordDigest(passwordEncoder.encode(newPassword));
        userRepository.save(existingUser);
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.findByEmail(username).isPresent();
    }
}
