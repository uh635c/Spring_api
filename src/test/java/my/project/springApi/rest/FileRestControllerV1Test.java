package my.project.springApi.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.project.springApi.model.Status;
import my.project.springApi.model.dto.FileDto;
import my.project.springApi.model.dto.UserDto;
import my.project.springApi.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FileRestControllerV1Test {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileServiceMock;

    FileDto fileDto;

    @BeforeEach
    public void setupFileDto(){
        fileDto = FileDto.builder()
                .id(1L)
                .location("location 1")
                .size(100L)
                .status(Status.ACTIVE)
                .userDto(UserDto.builder()
                        .id(1L)
                        .email("test@email.ru")
                        .build())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAll_shouldReturnListOfFileDto() throws Exception {
        Mockito.when(fileServiceMock.getAll(Mockito.any(Authentication.class))).thenReturn(List.of(fileDto));

        String jsonExpected = new ObjectMapper().writeValueAsString(List.of(fileDto));

        mockMvc.perform(get("/api/v1/files"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(jsonExpected));
        Mockito.verify(fileServiceMock).getAll(Mockito.any(Authentication.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAll_shouldReturnNotFound() throws Exception {
        Mockito.when(fileServiceMock.getAll(Mockito.any(Authentication.class))).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/files"))
                .andDo(print())
                .andExpect(status().isNotFound());
        Mockito.verify(fileServiceMock).getAll(Mockito.any(Authentication.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getById_shouldReturnUserDto() throws Exception {
        Mockito.when(fileServiceMock.getById(Mockito.any(Long.class), Mockito.any(Authentication.class)))
                .thenReturn(fileDto);

        String jsonExpected = new ObjectMapper().writeValueAsString(fileDto);

        mockMvc.perform(get("/api/v1/files/{id}",1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(jsonExpected));
        Mockito.verify(fileServiceMock).getById(Mockito.any(Long.class), Mockito.any(Authentication.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getById_shouldReturnNotFound() throws Exception {
        Mockito.when(fileServiceMock.getById(Mockito.any(Long.class), Mockito.any(Authentication.class)))
                .thenReturn(null);

        mockMvc.perform(get("/api/v1/files/{id}",1L))
                .andDo(print())
                .andExpect(status().isNotFound());
        Mockito.verify(fileServiceMock).getById(Mockito.any(Long.class), Mockito.any(Authentication.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void save_shouldReturnSavedFileDto() throws Exception{
        MockMultipartFile file = new MockMultipartFile("file",  "Content of the uploaded test file".getBytes());

        Mockito.when(fileServiceMock.save(Mockito.any(MultipartFile.class), Mockito.any(Authentication.class)))
                .thenReturn(fileDto);

        String jsonExpected = new ObjectMapper().writeValueAsString(fileDto);

        mockMvc.perform(multipart("/api/v1/files").file(file))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonExpected));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void save_shouldReturnBadRequestBecauseOfFileEmpty() throws Exception{
        MockMultipartFile file = new MockMultipartFile("file",  "".getBytes());
        Mockito.when(fileServiceMock.save(Mockito.any(MultipartFile.class), Mockito.any(Authentication.class)))
                        .thenReturn(fileDto);

        mockMvc.perform(multipart("/api/v1/files").file(file))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void save_shouldReturnBadRequestBecauseOfUserEntityNull() throws Exception{
        MockMultipartFile file = new MockMultipartFile("file",  "Content of the uploaded test file".getBytes());
        Mockito.when(fileServiceMock.save(Mockito.any(MultipartFile.class), Mockito.any(Authentication.class)))
                .thenReturn(null);

        mockMvc.perform(multipart("/api/v1/files").file(file))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_shouldReturnNoContent() throws Exception {
        Mockito.when(fileServiceMock.delete(Mockito.any(Long.class), Mockito.any(Authentication.class)))
                .thenReturn(fileDto);

        mockMvc.perform(delete("/api/v1/files/{id}",1L))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_shouldReturnNotFound() throws Exception {
        Mockito.when(fileServiceMock.delete(Mockito.any(Long.class), Mockito.any(Authentication.class)))
                .thenReturn(null);

        mockMvc.perform(delete("/api/v1/files/{id}",1L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}