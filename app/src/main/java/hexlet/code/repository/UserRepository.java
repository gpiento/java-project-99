package hexlet.code.repository;

import hexlet.code.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    User findByEmailAndPassword(String email, String password);

    User findByEmailOrFirstNameOrLastName(String email, String firstName, String lastName);

    User findByFirstNameOrLastName(String firstName, String lastName);

    User findByFirstNameAndLastName(String firstName, String lastName);

    User findByEmailAndFirstNameAndLastName(String email, String firstName, String lastName);
}
