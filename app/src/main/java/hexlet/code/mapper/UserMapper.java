package hexlet.code.mapper;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.model.User;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {

    @Autowired
    private PasswordEncoder passwordEncoder;

    // TODO: fix?
//    @Mapping(target = "password", ignore = true)
    public abstract UserDTO map(User user);

    public abstract User map(UserDTO userDTO);

    @Mapping(target = "passwordDigest", source = "password")
    public abstract User map(UserCreateDTO userCreateDTO);

    @Mapping(target = "passwordDigest", source = "password")
    public abstract void update(UserUpdateDTO userUpdateDTO, @MappingTarget User user);

    @BeforeMapping
    public void encryptPassword(UserCreateDTO userCreateDTO) {
        String password = userCreateDTO.getPassword();
        userCreateDTO.setPassword(passwordEncoder.encode(password));
    }

    public void updateUser(UserUpdateDTO userUpdateDTO, @MappingTarget User user) {
        updateFirstName(userUpdateDTO.getFirstName(), user);
        updateLastName(userUpdateDTO.getLastName(), user);
        updateEmail(userUpdateDTO.getEmail(), user);
        updatePassword(userUpdateDTO.getPassword(), user);
    }

    protected void updateFirstName(JsonNullable<String> firstName, @MappingTarget User user) {
        if (firstName != null && firstName.isPresent()) {
            user.setFirstName(firstName.get());
        }
    }

    protected void updateLastName(JsonNullable<String> lastName, @MappingTarget User user) {
        if (lastName != null && lastName.isPresent()) {
            user.setLastName(lastName.get());
        }
    }

    protected void updateEmail(JsonNullable<String> email, @MappingTarget User user) {
        if (email != null && email.isPresent()) {
            user.setEmail(email.get());
        }
    }

    protected void updatePassword(JsonNullable<String> password, @MappingTarget User user) {
        if (password != null && password.isPresent()) {
            user.setPasswordDigest(passwordEncoder.encode(password.get()));
        }
    }
}
