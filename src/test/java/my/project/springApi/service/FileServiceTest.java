package my.project.springApi.service;

import com.amazonaws.services.s3.AmazonS3;
import my.project.springApi.Util.AmazonS3service;
import my.project.springApi.model.*;
import my.project.springApi.model.dto.FileDto;
import my.project.springApi.repository.EventRepository;
import my.project.springApi.repository.FileRepository;
import my.project.springApi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileRepository fileRepositoryMock;
    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private EventRepository eventRepositoryMock;
    @Mock
    private AmazonS3service amazonS3serviceMock;

    @InjectMocks
    private FileService fileService;

    private Authentication authentication;

    @BeforeEach
    public void setupAuth() {
        authentication = new UsernamePasswordAuthenticationToken(User.builder()
                .username("userName")
                .password("")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .build(), "");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// tests for getAll() method ///////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    void getAll_shouldReturnEmptyListForUserEntityNull() {
        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).thenReturn(Optional.empty());

        assertEquals(new ArrayList<>(), fileService.getAll(authentication));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
    }

    @Test
    void getAll_shouldReturnEmptyListForUserRole() {
        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).
                thenReturn(Optional.of(UserEntity.builder()
                        .role(Role.ROLE_USER)
                        .build()));
        Mockito.when(fileRepositoryMock.findAllByUserEntityAndStatus(Mockito.any(UserEntity.class), Mockito.any(Status.class)))
                .thenReturn(new ArrayList<>());

        assertEquals(new ArrayList<>(), fileService.getAll(authentication));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
        Mockito.verify(fileRepositoryMock).findAllByUserEntityAndStatus(Mockito.any(UserEntity.class), Mockito.any(Status.class));
    }

    @Test
    void getAll_shouldReturnListOfFileDtoForUserRole() {
        UserEntity userEntity = UserEntity.builder()
                .email("test@mail.ru")
                .role(Role.ROLE_USER)
                .build();
        userEntity.setId(1L);

        FileEntity fileEntity = FileEntity.builder()
                .location("location 1")
                .size(100L)
                .status(Status.ACTIVE)
                .userEntity(userEntity)
                .build();
        fileEntity.setId(1L);

        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).
                thenReturn(Optional.of(userEntity));
        Mockito.when(fileRepositoryMock.findAllByUserEntityAndStatus(Mockito.any(UserEntity.class), Mockito.any(Status.class)))
                .thenReturn(List.of(fileEntity));

        assertEquals(List.of(FileDto.fromEntity(fileEntity)), fileService.getAll(authentication));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
        Mockito.verify(fileRepositoryMock).findAllByUserEntityAndStatus(Mockito.any(UserEntity.class), Mockito.any(Status.class));
    }

    @Test
    void getAll_shouldReturnEmptyListForNOTUserRole() {
        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).
                thenReturn(Optional.of(UserEntity.builder()
                        .role(Role.ROLE_ADMIN)
                        .build()));
        Mockito.when(fileRepositoryMock.findAllByStatus(Mockito.any(Status.class))).thenReturn(new ArrayList<>());

        assertEquals(new ArrayList<>(), fileService.getAll(authentication));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
        Mockito.verify(fileRepositoryMock).findAllByStatus(Mockito.any(Status.class));
    }

    @Test
    void getAll_shouldReturnListOfFileDtoForNOTUserRole() {
        UserEntity userEntity = UserEntity.builder()
                .email("test@mail.ru")
                .role(Role.ROLE_ADMIN)
                .build();
        userEntity.setId(1L);

        FileEntity fileEntity = FileEntity.builder()
                .location("location 1")
                .size(100L)
                .status(Status.ACTIVE)
                .userEntity(userEntity)
                .build();
        fileEntity.setId(1L);

        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).
                thenReturn(Optional.of(userEntity));
        Mockito.when(fileRepositoryMock.findAllByStatus(Mockito.any(Status.class)))
                .thenReturn(List.of(fileEntity));

        assertEquals(List.of(FileDto.fromEntity(fileEntity)), fileService.getAll(authentication));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
        Mockito.verify(fileRepositoryMock).findAllByStatus(Mockito.any(Status.class));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// tests for getById() method //////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    void getById_shouldReturnNullForUserEntityNull() {
        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).thenReturn(Optional.empty());

        assertNull(fileService.getById(1L, authentication));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
    }

    @Test
    void getById_shouldReturnNullForUserRole() {
        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).
                thenReturn(Optional.of(UserEntity.builder()
                        .role(Role.ROLE_USER)
                        .build()));
        Mockito.when(fileRepositoryMock.findByIdAndUserEntityAndStatus(Mockito.any(Long.class),
                        Mockito.any(UserEntity.class),
                        Mockito.any(Status.class)))
                .thenReturn(Optional.empty());

        assertNull(fileService.getById(1L, authentication));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
        Mockito.verify(fileRepositoryMock).findByIdAndUserEntityAndStatus(Mockito.any(Long.class),
                Mockito.any(UserEntity.class),
                Mockito.any(Status.class));
    }

    @Test
    void getById_shouldReturnFileDtoForUserRole() {
        UserEntity userEntity = UserEntity.builder()
                .email("test@mail.ru")
                .role(Role.ROLE_USER)
                .build();
        userEntity.setId(1L);

        FileEntity fileEntity = FileEntity.builder()
                .location("location 1")
                .size(100L)
                .status(Status.ACTIVE)
                .userEntity(userEntity)
                .build();
        fileEntity.setId(1L);

        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).
                thenReturn(Optional.of(userEntity));
        Mockito.when(fileRepositoryMock.findByIdAndUserEntityAndStatus(Mockito.any(Long.class),
                        Mockito.any(UserEntity.class),
                        Mockito.any(Status.class)))
                .thenReturn(Optional.of(fileEntity));

        assertEquals(FileDto.fromEntity(fileEntity), fileService.getById(1L, authentication));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
        Mockito.verify(fileRepositoryMock).findByIdAndUserEntityAndStatus(Mockito.any(Long.class),
                Mockito.any(UserEntity.class),
                Mockito.any(Status.class));
    }

    @Test
    void getById_shouldReturnNullForNOTUserRole() {
        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).
                thenReturn(Optional.of(UserEntity.builder()
                        .role(Role.ROLE_ADMIN)
                        .build()));
        Mockito.when(fileRepositoryMock.findByIdAndStatus(Mockito.any(Long.class), Mockito.any(Status.class)))
                .thenReturn(Optional.empty());

        assertNull(fileService.getById(1L, authentication));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
        Mockito.verify(fileRepositoryMock).findByIdAndStatus(Mockito.any(Long.class), Mockito.any(Status.class));
    }

    @Test
    void getById_shouldReturnListOfFileDtoForNOTUserRole() {
        UserEntity userEntity = UserEntity.builder()
                .email("test@mail.ru")
                .role(Role.ROLE_ADMIN)
                .build();
        userEntity.setId(1L);

        FileEntity fileEntity = FileEntity.builder()
                .location("location 1")
                .size(100L)
                .status(Status.ACTIVE)
                .userEntity(userEntity)
                .build();
        fileEntity.setId(1L);

        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).
                thenReturn(Optional.of(userEntity));
        Mockito.when(fileRepositoryMock.findByIdAndStatus(Mockito.any(Long.class), Mockito.any(Status.class)))
                .thenReturn(Optional.of(fileEntity));

        assertEquals(FileDto.fromEntity(fileEntity), fileService.getById(1L, authentication));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
        Mockito.verify(fileRepositoryMock).findByIdAndStatus(Mockito.any(Long.class), Mockito.any(Status.class));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// tests for save() method /////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    void save_shouldReturnNull() {
        MultipartFile multipartFile = new MockMultipartFile("fileName",  "Content of the uploaded test file".getBytes());
        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).thenReturn(Optional.empty());

        assertNull(fileService.save(multipartFile, authentication));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
    }

    @Test
    void save_shouldReturnFileDto() throws MalformedURLException {
        AmazonS3 amazonS3Mock = Mockito.mock(AmazonS3.class);
        URL url = new URL("https://example.com/location1");

        UserEntity userEntity = UserEntity.builder()
                .email("test@mail.ru")
                .role(Role.ROLE_ADMIN)
                .build();
        userEntity.setId(1L);

        FileEntity fileEntity = FileEntity.builder()
                .location("https://example.com/location1")
                .size(33L)
                .status(Status.ACTIVE)
                .userEntity(userEntity)
                .build();

        MultipartFile multipartFile =Mockito.spy(new MockMultipartFile("fileName",  "Content of the uploaded test file".getBytes()));
        Mockito.when(multipartFile.getOriginalFilename()).thenReturn("fileName");

        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).thenReturn(Optional.of(userEntity));
        Mockito.when(amazonS3serviceMock.getAmazonS3Client()).thenReturn(amazonS3Mock);
        Mockito.when(amazonS3Mock.putObject(Mockito.any(), Mockito.any(String.class), Mockito.any(File.class)))
                .thenReturn(null);
        Mockito.when(amazonS3Mock.getUrl(Mockito.any(), Mockito.any(String.class)))
                .thenReturn(url);

        assertEquals(FileDto.fromEntity(fileEntity), fileService.save(multipartFile, authentication));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
        Mockito.verify(fileRepositoryMock).save(fileEntity);
        Mockito.verify(eventRepositoryMock).save(EventEntity.builder()
                .date(new Date())
                .description(Description.FILE_CREATED)
                .userEntity(userEntity)
                .fileEntity(fileEntity)
                .build());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// tests for delete() method ///////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    void delete_shouldReturnNullForFileEntityNull() {
        UserEntity userEntity = UserEntity.builder()
                .email("test@mail.ru")
                .role(Role.ROLE_ADMIN)
                .build();
        Mockito.when(fileRepositoryMock.findByIdAndStatus(Mockito.any(Long.class), Mockito.any(Status.class)))
                .thenReturn(Optional.empty());
        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).thenReturn(Optional.of(userEntity));

        assertNull(fileService.delete(1L, authentication));
        Mockito.verify(fileRepositoryMock).findByIdAndStatus(Mockito.any(Long.class), Mockito.any(Status.class));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
    }

    @Test
    void delete_shouldReturnNullForUserEntityNull() {
        FileEntity fileEntity = FileEntity.builder()
                .location("location 1")
                .size(100L)
                .status(Status.ACTIVE)
                .userEntity(null)
                .build();
        Mockito.when(fileRepositoryMock.findByIdAndStatus(Mockito.any(Long.class), Mockito.any(Status.class)))
                .thenReturn(Optional.of(fileEntity));
        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).thenReturn(Optional.empty());

        assertNull(fileService.delete(1L, authentication));
        Mockito.verify(fileRepositoryMock).findByIdAndStatus(Mockito.any(Long.class), Mockito.any(Status.class));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
    }

    @Test
    void delete_shouldReturnSavedFileDto() {
        UserEntity userEntity = UserEntity.builder()
                .email("test@mail.ru")
                .role(Role.ROLE_ADMIN)
                .build();
        FileEntity fileEntity = FileEntity.builder()
                .location("location 1")
                .size(100L)
                .status(Status.ACTIVE)
                .userEntity(userEntity)
                .build();
        Mockito.when(fileRepositoryMock.findByIdAndStatus(Mockito.any(Long.class), Mockito.any(Status.class)))
                .thenReturn(Optional.of(fileEntity));
        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).thenReturn(Optional.of(userEntity));

        fileEntity.setStatus(Status.INACTIVE);
        assertEquals(FileDto.fromEntity(fileEntity), fileService.delete(1L, authentication));
        Mockito.verify(fileRepositoryMock).findByIdAndStatus(Mockito.any(Long.class), Mockito.any(Status.class));
        Mockito.verify(userRepositoryMock).findByEmail(Mockito.any(String.class));
        Mockito.verify(fileRepositoryMock).save(Mockito.any(FileEntity.class));
        Mockito.verify(eventRepositoryMock).save(EventEntity.builder()
                .date(new Date())
                .description(Description.FILE_DELETED)
                .userEntity(userEntity)
                .fileEntity(fileEntity)
                .build());
    }

}