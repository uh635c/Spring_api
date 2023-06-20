package my.project.springApi.service;

import my.project.springApi.model.*;
import my.project.springApi.model.dto.EventDto;
import my.project.springApi.repository.EventRepository;
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
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepositoryMock;
    @Mock
    private UserRepository userRepositoryMock;

    @InjectMocks
    private EventService eventService;

    private Authentication authentication;

    @BeforeEach
    public void setupAuth() {
        UserDetails userDetails = new User("userName", "", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// tests for getAll() method ///////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    void getAll_shouldReturnEmptyListForUserEntityNull() {
        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).thenReturn(Optional.empty());

        assertEquals(new ArrayList<>(), eventService.getAll(authentication));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
    }

    @Test
    void getAll_shouldReturnEmptyListForUserRole() {
        UserEntity userEntity = UserEntity.builder()
                .role(Role.ROLE_USER)
                .build();

        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).thenReturn(Optional.of(userEntity));
        Mockito.when(eventRepositoryMock.findAllByUserEntity(Mockito.any(UserEntity.class))).thenReturn(new ArrayList<>());

        assertEquals(new ArrayList<>(), eventService.getAll(authentication));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
        Mockito.verify(eventRepositoryMock).findAllByUserEntity(Mockito.any(UserEntity.class));
    }

    @Test
    void getAll_shouldReturnListOfEventDtoForUserRole() {
        UserEntity userEntity = UserEntity.builder()
                .role(Role.ROLE_USER)
                .build();
        userEntity.setId(1L);

        FileEntity fileEntity = FileEntity.builder()
                .location("location 1")
                .size(100L)
                .build();
        fileEntity.setId(1L);

        EventEntity eventEntity = EventEntity.builder()
                .date(new Date())
                .description(Description.FILE_CREATED)
                .userEntity(userEntity)
                .fileEntity(fileEntity)
                .build();
        eventEntity.setId(1L);

        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).thenReturn(Optional.of(userEntity));
        Mockito.when(eventRepositoryMock.findAllByUserEntity(Mockito.any(UserEntity.class)))
                .thenReturn(List.of(eventEntity));

        assertEquals(List.of(EventDto.fromEntity(eventEntity)), eventService.getAll(authentication));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
        Mockito.verify(eventRepositoryMock).findAllByUserEntity(Mockito.any(UserEntity.class));
    }

    @Test
    void getAll_shouldReturnEmptyListForNOTUserRole() {
        UserEntity userEntity = UserEntity.builder()
                .role(Role.ROLE_ADMIN)
                .build();

        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).thenReturn(Optional.of(userEntity));
        Mockito.when(eventRepositoryMock.findAll()).thenReturn(new ArrayList<>());

        assertEquals(new ArrayList<>(), eventService.getAll(authentication));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
        Mockito.verify(eventRepositoryMock).findAll();
    }

    @Test
    void getAll_shouldReturnListOfEventDtoForNOTUserRole() {
        UserEntity userEntity = UserEntity.builder()
                .role(Role.ROLE_ADMIN)
                .build();
        userEntity.setId(1L);

        FileEntity fileEntity = FileEntity.builder()
                .location("location 1")
                .size(100L)
                .build();
        fileEntity.setId(1L);

        EventEntity eventEntity = EventEntity.builder()
                .date(new Date())
                .description(Description.FILE_CREATED)
                .userEntity(userEntity)
                .fileEntity(fileEntity)
                .build();
        eventEntity.setId(1L);

        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).thenReturn(Optional.of(userEntity));
        Mockito.when(eventRepositoryMock.findAll()).thenReturn(List.of(eventEntity));

        assertEquals(List.of(EventDto.fromEntity(eventEntity)), eventService.getAll(authentication));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
        Mockito.verify(eventRepositoryMock).findAll();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// tests for getById() method //////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    void getById_shouldReturnNullForUserEntityNull() {
        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).thenReturn(Optional.empty());

        assertNull(eventService.getById(1L, authentication));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
    }

    @Test
    void getById_shouldReturnNullForUserRole() {
        UserEntity userEntity = UserEntity.builder()
                .role(Role.ROLE_USER)
                .build();

        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).thenReturn(Optional.of(userEntity));
        Mockito.when(eventRepositoryMock.findByIdAndUserEntity(Mockito.any(Long.class), Mockito.any(UserEntity.class)))
                .thenReturn(Optional.empty());

        assertNull(eventService.getById(1L, authentication));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
        Mockito.verify(eventRepositoryMock).findByIdAndUserEntity(Mockito.any(Long.class), Mockito.any(UserEntity.class));
    }

    @Test
    void getById_shouldReturnEventDtoForUserRole() {
        UserEntity userEntity = UserEntity.builder()
                .role(Role.ROLE_USER)
                .build();
        userEntity.setId(1L);

        FileEntity fileEntity = FileEntity.builder()
                .location("location 1")
                .size(100L)
                .build();
        fileEntity.setId(1L);

        EventEntity eventEntity = EventEntity.builder()
                .date(new Date())
                .description(Description.FILE_CREATED)
                .userEntity(userEntity)
                .fileEntity(fileEntity)
                .build();
        eventEntity.setId(1L);

        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).thenReturn(Optional.of(userEntity));
        Mockito.when(eventRepositoryMock.findByIdAndUserEntity(Mockito.any(Long.class), Mockito.any(UserEntity.class)))
                .thenReturn(Optional.of(eventEntity));

        assertEquals(EventDto.fromEntity(eventEntity), eventService.getById(1L, authentication));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
        Mockito.verify(eventRepositoryMock).findByIdAndUserEntity(Mockito.any(Long.class), Mockito.any(UserEntity.class));
    }

    @Test
    void getById_shouldReturnNullForNOTUserRole() {
        UserEntity userEntity = UserEntity.builder()
                .role(Role.ROLE_ADMIN)
                .build();

        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).thenReturn(Optional.of(userEntity));
        Mockito.when(eventRepositoryMock.findById(Mockito.any(Long.class))).thenReturn(Optional.empty());

        assertNull(eventService.getById(1L, authentication));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
        Mockito.verify(eventRepositoryMock).findById(Mockito.any(Long.class));
    }

    @Test
    void getById_shouldReturnEventDtoForNOTUserRole() {
        UserEntity userEntity = UserEntity.builder()
                .role(Role.ROLE_ADMIN)
                .build();
        userEntity.setId(1L);

        FileEntity fileEntity = FileEntity.builder()
                .location("location 1")
                .size(100L)
                .build();
        fileEntity.setId(1L);

        EventEntity eventEntity = EventEntity.builder()
                .date(new Date())
                .description(Description.FILE_CREATED)
                .userEntity(userEntity)
                .fileEntity(fileEntity)
                .build();
        eventEntity.setId(1L);

        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).thenReturn(Optional.of(userEntity));
        Mockito.when(eventRepositoryMock.findById(Mockito.any(Long.class))).thenReturn(Optional.of(eventEntity));

        assertEquals(EventDto.fromEntity(eventEntity), eventService.getById(1L, authentication));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
        Mockito.verify(eventRepositoryMock).findById(Mockito.any(Long.class));
    }
}