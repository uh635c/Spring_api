package my.project.springApi.rest;

import lombok.RequiredArgsConstructor;
import my.project.springApi.model.Role;
import my.project.springApi.model.Status;
import my.project.springApi.model.UserEntity;
import my.project.springApi.model.dto.AuthenticationRequestDto;
import my.project.springApi.model.dto.UserDto;
import my.project.springApi.repository.UserRepository;
import my.project.springApi.security.JwtProvider;
import my.project.springApi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthAndRegRestControllerV1 {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> authentication(@RequestBody AuthenticationRequestDto authRequestDto) {

        try{
            UserEntity userEntity = userRepository.findByEmail(authRequestDto.getUserEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User does not exists"));

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDto.getUserEmail(), authRequestDto.getPassword()));

            String token = jwtTokenProvider.createToken(authRequestDto.getUserEmail(), userEntity.getRole().toString());

            Map<Object, Object> response = new HashMap<>();
            response.put("email", authRequestDto.getUserEmail());
            response.put("token", token);
            return ResponseEntity.ok(response);

        }catch (AuthenticationException e){
            return new ResponseEntity<>("Invalid email/password combination", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/reg")
    public ResponseEntity<UserDto> registration(@RequestBody UserDto userDto){
        if(userDto == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        userDto.setRole(Role.ROLE_USER);
        userDto.setStatus(Status.ACTIVE);

        return new ResponseEntity<>(userService.save(userDto), HttpStatus.CREATED);
    }

}
