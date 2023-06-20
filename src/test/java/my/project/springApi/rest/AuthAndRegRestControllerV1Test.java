package my.project.springApi.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.project.springApi.model.Role;
import my.project.springApi.model.Status;
import my.project.springApi.model.UserEntity;
import my.project.springApi.model.dto.UserDto;
import my.project.springApi.repository.UserRepository;
import my.project.springApi.security.JwtAuthenticationException;
import my.project.springApi.security.JwtProvider;
import my.project.springApi.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthAndRegRestControllerV1Test {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthenticationManager authenticationManagerMock;
    @MockBean
    private JwtProvider jwtTokenProviderMock;
    @MockBean
    private UserRepository userRepositoryMock;
    @MockBean
    private UserService userServiceMock;


    @Test
    void authentication_shouldReturnForbiddenBecauseOfUsernameNotFoundException() throws Exception {
        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/login")
                        .contentType("application/json")
                        .content("{\"userEmail\":\"test@mail.ru\",\"password\":\"password\"}"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void authentication_shouldReturnForbiddenBecauseOfBadAuthentication() throws Exception {
        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class)))
                .thenReturn(Optional.of(UserEntity.builder()
                        .role(Role.ROLE_USER)
                        .build()));
        Mockito.when(authenticationManagerMock.authenticate(Mockito.any(Authentication.class)))
                .thenThrow(new JwtAuthenticationException("String msg"));

        mockMvc.perform(post("/api/v1/login")
                        .contentType("application/json")
                        .content("{\"userEmail\":\"test@mail.ru\",\"password\":\"password\"}"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void authentication_shouldReturnResponse() throws Exception {
        Mockito.when(userRepositoryMock.findByEmail(Mockito.any(String.class)))
                .thenReturn(Optional.of(UserEntity.builder()
                        .role(Role.ROLE_USER)
                        .build()));
        Mockito.when(authenticationManagerMock.authenticate(Mockito.any(Authentication.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("name", "password"));
        Mockito.when(jwtTokenProviderMock.createToken(Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn("This is a token for test@mail.ru with ROLE_USER");

        mockMvc.perform(post("/api/v1/login")
                        .contentType("application/json")
                        .content("{\"userEmail\":\"test@mail.ru\",\"password\":\"password\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"email\":\"test@mail.ru\",\"token\":\"This is a token for test@mail.ru with ROLE_USER\"}"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void registration_shouldReturnBadRequest() throws Exception {

        mockMvc.perform(post("/api/v1/reg")
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void registration_shouldReturnSavedUserDto() throws Exception {
        UserDto userDtoIn = UserDto.builder()
                .email("test@mail.ru")
                .password("password")
                .firstName("testFirstName")
                .lastName("testLastName")
                .role(Role.ROLE_USER)
                .status(Status.ACTIVE)
                .build();

        UserDto userDtoOut = UserDto.builder()
                .id(1L)
                .email("test@mail.ru")
                .firstName("testFirstName")
                .lastName("testLastName")
                .role(Role.ROLE_USER)
                .status(Status.ACTIVE)
                .build();

        Mockito.when(userServiceMock.save(userDtoIn)).thenReturn(userDtoOut);

        String jsonExpected = new ObjectMapper().writeValueAsString(userDtoOut);

        mockMvc.perform(post("/api/v1/reg")
                        .contentType("application/json")
                        .content("{\"email\":\"test@mail.ru\",\"password\":\"password\"," +
                                "\"firstName\":\"testFirstName\",\"lastName\":\"testLastName\"}"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonExpected));
        Mockito.verify(userServiceMock).save(Mockito.any(UserDto.class));
    }
}