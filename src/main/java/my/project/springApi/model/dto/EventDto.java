package my.project.springApi.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import my.project.springApi.model.Description;
import my.project.springApi.model.EventEntity;

import java.util.Date;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDto {

    private Long id;
    @EqualsAndHashCode.Exclude
    private Date date;
    private Description description;
    private UserDto userDto;
    private FileDto fileDto;

    public static EventDto fromEntity(EventEntity eventEntity){
        return EventDto.builder()
                .id(eventEntity.getId())
                .date(eventEntity.getDate())
                .description(eventEntity.getDescription())
                .userDto(UserDto.builder()
                        .id(eventEntity.getUserEntity().getId())
                        .build())
                .fileDto(FileDto.builder()
                        .id(eventEntity.getFileEntity().getId())
                        .location(eventEntity.getFileEntity().getLocation())
                        .size(eventEntity.getFileEntity().getSize())
                        .build())
                .build();
    }

    public static EventDto fromEntityForUser(EventEntity eventEntity){
        return EventDto.builder()
                .id(eventEntity.getId())
                .date(eventEntity.getDate())
                .description(eventEntity.getDescription())
                .fileDto(FileDto.builder()
                        .id(eventEntity.getFileEntity().getId())
                        .build())
                .build();
    }

    public EventEntity toEntity(){
        EventEntity eventEntity = EventEntity.builder()
                .date(date)
                .description(description)
                .userEntity(userDto.toEntity())
                .fileEntity(fileDto.toEntity())
                .build();
        eventEntity.setId(id);
        return eventEntity;
    }
}
