package my.project.springApi.rest;

import lombok.RequiredArgsConstructor;
import my.project.springApi.model.dto.UserDto;
import my.project.springApi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserRestControllerV1 {

    private final UserService userService;

    @Secured({"ROLE_MODERATOR", "ROLE_ADMIN"})
    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        List<UserDto> listUserDto = userService.getAll();

        if(listUserDto.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(listUserDto, HttpStatus.OK);
    }

    @Secured({"ROLE_MODERATOR", "ROLE_ADMIN"})
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable("id") Long id) {
        UserDto userDto = userService.getById(id);

        if (userDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe(Authentication authentication) {
        UserDto userDto = userService.getMe(authentication);

        if (userDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping
    public ResponseEntity<UserDto> save(@RequestBody UserDto userDto) {
        if(userDto == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(userService.save(userDto), HttpStatus.CREATED);

    }

    @Secured({"ROLE_ADMIN"})
    @PutMapping
    public @ResponseBody ResponseEntity<UserDto> update(@RequestBody UserDto userDto) {
        if(userDto.getId()==null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(userService.save(userDto), HttpStatus.OK);
    }

    @Secured({"ROLE_ADMIN"})
    @DeleteMapping("/{id}")
    public ResponseEntity<UserDto> delete(@PathVariable("id") Long id) {
        UserDto userDto = userService.delete(id);

        if (userDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
