package my.project.springApi.model.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import my.project.springApi.model.FileEntity;
import my.project.springApi.model.Status;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileDto {

    private Long id;
    private String location;
    private long size;
    private Status status;
    private UserDto userDto;

    public static FileDto fromEntity(FileEntity fileEntity){
        return FileDto.builder()
                .id(fileEntity.getId())
                .location(fileEntity.getLocation())
                .size(fileEntity.getSize())
                .status(fileEntity.getStatus())
                .userDto(UserDto.builder()
                        .id(fileEntity.getUserEntity().getId())
                        .email(fileEntity.getUserEntity().getEmail())
                        .role(fileEntity.getUserEntity().getRole())
                        .build())
                .build();
    }

    public FileEntity toEntity(){
        FileEntity fileEntity = FileEntity.builder()
                .location(location)
                .size(size)
                .status(status)
                .userEntity(userDto.toEntity()) // Нужен только id что бы заполнить колонку user_id
                .build();
        fileEntity.setId(id);
        return fileEntity;
    }

}
