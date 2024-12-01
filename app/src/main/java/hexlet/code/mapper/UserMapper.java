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

import java.util.function.Consumer;

@Mapper(
        uses = {
                JsonNullableMapper.class,
                ReferenceMapper.class,
                TaskMapper.class},
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
        if (password != null) {
            userCreateDTO.setPassword(passwordEncoder.encode(password));
        }
    }

    public void updateUser(UserUpdateDTO userUpdateDTO, @MappingTarget User user) {
        updateField(userUpdateDTO.getFirstName(), user::setFirstName);
        updateField(userUpdateDTO.getLastName(), user::setLastName);
        updateField(userUpdateDTO.getEmail(), user::setEmail);
        updateField(userUpdateDTO.getPassword(), password -> user.setPasswordDigest(passwordEncoder.encode(password)));
    }

    private <T> void updateField(JsonNullable<T> newValue, Consumer<T> setter) {
        if (newValue != null && newValue.isPresent()) {
            setter.accept(newValue.get());
        }
    }
}
