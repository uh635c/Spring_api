package my.project.springApi.rest;

import lombok.RequiredArgsConstructor;
import my.project.springApi.model.dto.EventDto;
import my.project.springApi.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventRestControllerV1 {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventDto>> getAll(Authentication authentication){
        List<EventDto> listEventDto = eventService.getAll(authentication);

        if(listEventDto.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(listEventDto, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getById(@PathVariable("id") Long id, Authentication authentication){
        EventDto eventDto = eventService.getById(id, authentication);

        if(eventDto == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(eventDto, HttpStatus.OK);
    }
}
