package my.project.springApi.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.project.springApi.model.Role;
import my.project.springApi.model.Status;
import my.project.springApi.model.dto.UserDto;
import my.project.springApi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserRestControllerV1Test {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userServiceMock;
    private UserDto userDto;

    @BeforeEach
    public void setupUserDto(){
        userDto = UserDto.builder()
                .id(1L)
                .email("test@mail.ru")
                .password("password")
                .firstName("testFirstName")
                .lastName("testLastName")
                .role(Role.ROLE_USER)
                .status(Status.ACTIVE)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAll_shouldReturnListOfUserDto() throws Exception {
        Mockito.when(userServiceMock.getAll()).thenReturn(List.of(userDto));

        String jsonExpected = new ObjectMapper().writeValueAsString(List.of(userDto));

        mockMvc.perform(get("/api/v1/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(jsonExpected));
        Mockito.verify(userServiceMock).getAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAll_shouldReturnNotFound() throws Exception {
        Mockito.when(userServiceMock.getAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/users"))
                .andDo(print())
                .andExpect(status().isNotFound());
        Mockito.verify(userServiceMock).getAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getById_shouldReturnUserDtoById() throws Exception {
        Mockito.when(userServiceMock.getById(Mockito.any(Long.class))).thenReturn(userDto);

        String jsonExpected = new ObjectMapper().writeValueAsString(userDto);

        mockMvc.perform(get("/api/v1/users/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(jsonExpected));
        Mockito.verify(userServiceMock).getById(Mockito.any(Long.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getById_shouldReturnNotFound() throws Exception {
        Mockito.when(userServiceMock.getById(Mockito.any(Long.class))).thenReturn(null);

        mockMvc.perform(get("/api/v1/users/{id}", 1L))
                .andDo(print())
                .andExpect(status().isNotFound());
        Mockito.verify(userServiceMock).getById(Mockito.any(Long.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getMe_shouldReturnUserDto() throws Exception {
        Mockito.when(userServiceMock.getMe(Mockito.any(Authentication.class))).thenReturn(userDto);

        String jsonExpected = new ObjectMapper().writeValueAsString(userDto);

        mockMvc.perform(get("/api/v1/users/me"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(jsonExpected));
        Mockito.verify(userServiceMock).getMe(Mockito.any(Authentication.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getMe_shouldReturnNotFound() throws Exception {
        Mockito.when(userServiceMock.getMe(Mockito.any(Authentication.class))).thenReturn(null);

        mockMvc.perform(get("/api/v1/users/me"))
                .andDo(print())
                .andExpect(status().isNotFound());
        Mockito.verify(userServiceMock).getMe(Mockito.any(Authentication.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void save_shouldReturnSavedUserDto() throws Exception {
        Mockito.when(userServiceMock.save(Mockito.any(UserDto.class))).thenReturn(userDto);

        String jsonExpected = new ObjectMapper().writeValueAsString(userDto);

        mockMvc.perform(post("/api/v1/users")
                        .contentType("application/json")
                        .content("{\"id\":1,\"email\":\"test@mail.ru\",\"password\":\"password\"," +
                                "\"firstName\":\"testFirstName\",\"lastName\":\"testLastName\"," +
                                "\"role\":\"ROLE_ADMIN\",\"status\":\"ACTIVE\"}"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonExpected));
        Mockito.verify(userServiceMock).save(Mockito.any(UserDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void save_shouldReturnBadRequest() throws Exception {

        mockMvc.perform(post("/api/v1/users")
                        .contentType("application/json")
                        .content(""))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void update_shouldReturnUpdatedUseDto() throws Exception {
        Mockito.when(userServiceMock.save(Mockito.any(UserDto.class))).thenReturn(userDto);

        String jsonExpected = new ObjectMapper().writeValueAsString(userDto);

        mockMvc.perform(put("/api/v1/users")
                        .contentType("application/json")
                        .content("{\"id\":1,\"email\":\"test@mail.ru\",\"password\":\"password\"," +
                                "\"firstName\":\"testFirstName\",\"lastName\":\"testLastName\"," +
                                "\"role\":\"ROLE_ADMIN\",\"status\":\"ACTIVE\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(jsonExpected));
        Mockito.verify(userServiceMock).save(Mockito.any(UserDto.class));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void update_shouldReturnBadRequest() throws Exception {
        Mockito.when(userServiceMock.save(Mockito.any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(put("/api/v1/users")
                        .contentType("application/json")
                        .content("{\"email\":\"test@mail.ru\",\"password\":\"password\"," +
                                "\"firstName\":\"testFirstName\",\"lastName\":\"testLastName\"," +
                                "\"role\":\"ROLE_ADMIN\",\"status\":\"ACTIVE\"}"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_shouldReturnNoContent() throws Exception {
        Mockito.when(userServiceMock.delete(Mockito.any(Long.class))).thenReturn(userDto);

        mockMvc.perform(delete("/api/v1/users/{id}", 1L))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_shouldReturnBadRequest() throws Exception {
        Mockito.when(userServiceMock.delete(Mockito.any(Long.class))).thenReturn(null);

        mockMvc.perform(delete("/api/v1/users/{id}", 1L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}