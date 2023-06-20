package my.project.springApi.service;

import my.project.springApi.model.*;
import my.project.springApi.model.dto.UserDto;
import my.project.springApi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private PasswordEncoder passwordEncoderMock;
    @InjectMocks
    private UserService userService;
    private UserEntity userEntity;

    @BeforeEach
    public void setupUserEntity(){
        FileEntity fileEntity = FileEntity.builder()
                .build();
        fileEntity.setId(1L);

        EventEntity eventEntity = EventEntity.builder()
                .date(new Date())
                .description(Description.FILE_CREATED)
                .fileEntity(fileEntity)
                .build();
        eventEntity.setId(1L);

        userEntity = UserEntity.builder()
                .email("test@mail.ru")
                .password("encrypted string")
                .firstName("testFirstName")
                .lastName("testLastName")
                .role(Role.ROLE_ADMIN)
                .status(Status.ACTIVE)
                .eventEntities(List.of(eventEntity))
                .build();
        userEntity.setId(1L);
    }

    @Test
    void getAll_shouldReturnEmptyList() {
        Mockito.when(userRepositoryMock.findAll()).thenReturn(new ArrayList<>());

        assertEquals(new ArrayList<>(), userService.getAll());
        Mockito.verify(userRepositoryMock).findAll();
    }

    @Test
    void getAll_shouldReturnListOfUserDto() {
        Mockito.when(userRepositoryMock.findAll()).thenReturn(List.of(userEntity));

        assertEquals(List.of(UserDto.fromEntity(userEntity)), userService.getAll());
        Mockito.verify(userRepositoryMock).findAll();
    }

    @Test
    void getById_shouldReturnNull() {
        Mockito.when(userRepositoryMock.findById(Mockito.any(Long.class))).thenReturn(Optional.empty());

        assertNull(userService.getById(1L));
        Mockito.verify(userRepositoryMock).findById(Mockito.any(Long.class));
    }

    @Test
    void getById_shouldReturnUserDtoById() {
        Mockito.when(userRepositoryMock.findById(Mockito.any(Long.class))).thenReturn(Optional.of(userEntity));

        assertEquals(UserDto.fromEntity(userEntity), userService.getById(1L));
        Mockito.verify(userRepositoryMock).findById(Mockito.any(Long.class));
    }

    @Test
    void getMe_shouldReturnNull(){
        Authentication authentication = new UsernamePasswordAuthenticationToken(User.builder()
                .username("userName")
                .password("")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .build(), "");

        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).thenReturn(Optional.empty());

        assertNull(userService.getMe(authentication));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
    }

    @Test
    void getMe_shouldReturnUseDto(){
        Authentication authentication = new UsernamePasswordAuthenticationToken(User.builder()
                .username("userName")
                .password("")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .build(), "");

        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).thenReturn(Optional.of(userEntity));

        assertEquals(UserDto.fromEntity(userEntity), userService.getMe(authentication));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
    }

    @Test
    void save_shouldReturnSavedUserDto() {
        UserDto userDto = UserDto.builder()
                .email("test@mail.ru")
                .password("password")
                .firstName("testFirstName")
                .lastName("testLastName")
                .role(Role.ROLE_ADMIN)
                .status(Status.ACTIVE)
                .build();

        userEntity.setEventEntities(new ArrayList<>());

        Mockito.when(userRepositoryMock.save(Mockito.any(UserEntity.class))).thenReturn(userEntity);
        Mockito.when(passwordEncoderMock.encode(Mockito.any(String.class))).thenReturn("encoded string");

        userEntity.setPassword(null);

        assertEquals(UserDto.fromEntity(userEntity),userService.save(userDto));
        Mockito.verify(userRepositoryMock).save(Mockito.any(UserEntity.class));
        Mockito.verify(passwordEncoderMock).encode(Mockito.any(String.class));
    }

    @Test
    void delete_shouldReturnNull() {
        Mockito.when(userRepositoryMock.findById(Mockito.any(Long.class))).thenReturn(Optional.empty());

        assertNull(userService.delete(1L));
        Mockito.verify(userRepositoryMock).findById(Mockito.any(Long.class));
    }

    @Test
    void delete_shouldReturnUserDtoById() {
        Mockito.when(userRepositoryMock.findById(Mockito.any(Long.class))).thenReturn(Optional.of(userEntity));
        Mockito.when(userRepositoryMock.save(Mockito.any(UserEntity.class))).thenReturn(userEntity);

        assertEquals(Status.INACTIVE, userService.delete(1L).getStatus());
        Mockito.verify(userRepositoryMock).findById(Mockito.any(Long.class));
        Mockito.verify(userRepositoryMock).save(Mockito.any(UserEntity.class));

    }
}