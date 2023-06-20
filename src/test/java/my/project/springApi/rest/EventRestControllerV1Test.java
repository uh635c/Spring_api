package my.project.springApi.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.project.springApi.model.Description;
import my.project.springApi.model.dto.EventDto;
import my.project.springApi.model.dto.FileDto;
import my.project.springApi.model.dto.UserDto;
import my.project.springApi.service.EventService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class EventRestControllerV1Test {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventServiceMock;

    private EventDto eventDto;

    @BeforeEach
    public void setupEventDto(){
        eventDto = EventDto.builder()
                .id(1L)
                .description(Description.FILE_CREATED)
                .fileDto(FileDto.builder()
                        .id(1L)
                        .build())
                .userDto(UserDto.builder()
                        .id(1L)
                        .build())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAll_shouldReturnListOfEventDto() throws Exception {

        Mockito.when(eventServiceMock.getAll(Mockito.any(Authentication.class))).thenReturn(List.of(eventDto));
        String jsonExpected = new ObjectMapper().writeValueAsString(List.of(eventDto));

        mockMvc.perform(get("/api/v1/events"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(jsonExpected));
        Mockito.verify(eventServiceMock).getAll(Mockito.any(Authentication.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAll_shouldReturnNotFound() throws Exception {

        Mockito.when(eventServiceMock.getAll(Mockito.any(Authentication.class))).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/events"))
                .andDo(print())
                .andExpect(status().isNotFound());
        Mockito.verify(eventServiceMock).getAll(Mockito.any(Authentication.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getById_ShouldReturnEventDtoById() throws Exception {

        Mockito.when(eventServiceMock.getById(Mockito.any(Long.class), Mockito.any(Authentication.class)))
                .thenReturn(eventDto);
        String jsonExpected = new ObjectMapper().writeValueAsString(eventDto);

        mockMvc.perform(get("/api/v1/events/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(jsonExpected));
        Mockito.verify(eventServiceMock).getById(Mockito.any(Long.class), Mockito.any(Authentication.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getById_ShouldReturnNotFound() throws Exception {

        Mockito.when(eventServiceMock.getById(Mockito.any(Long.class), Mockito.any(Authentication.class)))
                .thenReturn(null);

        mockMvc.perform(get("/api/v1/events/{id}", 1L))
                .andDo(print())
                .andExpect(status().isNotFound());
        Mockito.verify(eventServiceMock).getById(Mockito.any(Long.class), Mockito.any(Authentication.class));
    }
}