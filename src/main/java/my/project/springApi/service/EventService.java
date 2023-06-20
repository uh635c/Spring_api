package my.project.springApi.service;

import lombok.RequiredArgsConstructor;
import my.project.springApi.model.EventEntity;
import my.project.springApi.model.UserEntity;
import my.project.springApi.model.dto.EventDto;
import my.project.springApi.repository.EventRepository;
import my.project.springApi.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public List<EventDto> getAll(Authentication authentication){
        UserEntity userEntity = userRepository.findByEmail(authentication.getName()).orElse(null);

        if(userEntity == null){
            return new ArrayList<>();
        }

        if(userEntity.getRole().toString().equals("ROLE_USER")){
            List<EventEntity> list = eventRepository.findAllByUserEntity(userEntity);

            if(list.isEmpty()){
                return new ArrayList<>();
            }

            return list.stream().map(EventDto::fromEntity).collect(Collectors.toList());
        }

        List<EventEntity> list =eventRepository.findAll();

        if(list.isEmpty()){
            return new ArrayList<>();
        }

        return list.stream().map(EventDto::fromEntity).collect(Collectors.toList());
    }

    public EventDto getById(Long id, Authentication authentication){

        UserEntity userEntity = userRepository.findByEmail(authentication.getName()).orElse(null);

        if(userEntity == null){
            return null;
        }

        if(userEntity.getRole().toString().equals("ROLE_USER")){
            EventEntity eventEntity = eventRepository.findByIdAndUserEntity(id, userEntity).orElse(null);

            if(eventEntity == null){
                return null;
            }

            return EventDto.fromEntity(eventEntity);
        }

        EventEntity eventEntity = eventRepository.findById(id).orElse(null);

        if(eventEntity == null){
            return null;
        }

        return EventDto.fromEntity(eventEntity);
    }

}
