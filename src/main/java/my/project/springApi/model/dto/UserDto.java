package my.project.springApi.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import my.project.springApi.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto extends BaseEntity {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Role role;
    private Status status;
    private List<EventDto> eventDto;

    public static UserDto fromEntity(UserEntity userEntity){
        UserDto userDto = UserDto.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .role(userEntity.getRole())
                .status(userEntity.getStatus())
                .build();
        if(!userEntity.getEventEntities().isEmpty()){
            userDto.setEventDto(userEntity.getEventEntities().stream()
                    .map(EventDto::fromEntityForUser)
                    .collect(Collectors.toList()));
        }
        return userDto;
    }

    public UserEntity toEntity(){
        UserEntity userEntity = UserEntity.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .role(role)
                .status(status)
                .build();
        userEntity.setId(id);
        return userEntity;
    }
}
