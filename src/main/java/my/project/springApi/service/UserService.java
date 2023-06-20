package my.project.springApi.service;

import lombok.RequiredArgsConstructor;
import my.project.springApi.model.Status;
import my.project.springApi.model.UserEntity;
import my.project.springApi.model.dto.UserDto;
import my.project.springApi.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> getAll(){
        List<UserEntity> listUserEntity = userRepository.findAll();

        if(listUserEntity.isEmpty()){
            return new ArrayList<>();
        }

        return listUserEntity.stream().map(UserDto::fromEntity).collect(Collectors.toList());
    }

    public UserDto getById(Long id){
        UserEntity  userEntity = userRepository.findById(id).orElse(null);

        if(userEntity == null){
            return null;
        }

        return UserDto.fromEntity(userEntity);
    }

    public UserDto getMe(Authentication authentication){
        UserEntity userEntity = userRepository.findByEmail(authentication.getName()).orElse(null);

        if(userEntity == null){
            return null;
        }

        return UserDto.fromEntity(userEntity);
    }

    public UserDto save(UserDto userDto){
        UserEntity userEntity = userDto.toEntity();
        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));

        return UserDto.fromEntity(userRepository.save(userEntity));
    }

    public UserDto delete(Long id){
        UserEntity userEntity = userRepository.findById(id).orElse(null);

        if(userEntity == null){
            return null;
        }

        userEntity.setStatus(Status.INACTIVE);

        return UserDto.fromEntity(userRepository.save(userEntity));
    }
}
