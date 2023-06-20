package my.project.springApi.security;

import lombok.RequiredArgsConstructor;
import my.project.springApi.model.UserEntity;
import my.project.springApi.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userDetailsServiceImpl")
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow((()->
                new UsernameNotFoundException("User does not exists")));
        return  JwtUserDetails.builder()
                .userEmail(userEntity.getEmail())
                .password(userEntity.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(userEntity.getRole().toString())))
                .isActive(userEntity.getStatus().toString().equals("ACTIVE"))
                .build();
    }
}
