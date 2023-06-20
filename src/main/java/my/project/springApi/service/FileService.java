package my.project.springApi.service;

import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import my.project.springApi.Util.AmazonS3service;
import my.project.springApi.model.*;
import my.project.springApi.model.dto.FileDto;
import my.project.springApi.repository.EventRepository;
import my.project.springApi.repository.FileRepository;
import my.project.springApi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final AmazonS3service amazonS3service;

    @Value("${bucket.name}")
    String bucketName;


    public List<FileDto> getAll(Authentication authentication){

        UserEntity userEntity = userRepository.findByEmail(authentication.getName()).orElse(null);

        if(userEntity == null){
            return new ArrayList<>();
        }

        if(userEntity.getRole().toString().equals("ROLE_USER")){
            List<FileEntity> listFileEntity = fileRepository.findAllByUserEntityAndStatus(userEntity, Status.ACTIVE);

            if(listFileEntity.isEmpty()){
                return new ArrayList<>();
            }

            return listFileEntity.stream().map(FileDto::fromEntity).collect(Collectors.toList());
        }

        List<FileEntity> listFileEntity =fileRepository.findAllByStatus(Status.ACTIVE);

        if(listFileEntity.isEmpty()){
            return new ArrayList<>();
        }

        return listFileEntity.stream().map(FileDto::fromEntity).collect(Collectors.toList());
    }

    public FileDto getById(Long id, Authentication authentication){
        UserEntity userEntity = userRepository.findByEmail(authentication.getName()).orElse(null);

        if(userEntity == null){
            return null;
        }

        if(userEntity.getRole().toString().equals("ROLE_USER")){
            FileEntity fileEntity = fileRepository.findByIdAndUserEntityAndStatus(id, userEntity, Status.ACTIVE)
                    .orElse(null);

            if(fileEntity == null){
                return null;
            }
            return FileDto.fromEntity(fileEntity);
        }

        FileEntity fileEntity = fileRepository.findByIdAndStatus(id, Status.ACTIVE).orElse(null);

        if(fileEntity == null){
            return null;
        }

        return FileDto.fromEntity(fileEntity);
    }

    public FileDto save(MultipartFile multipartFile, Authentication authentication){
        UserEntity userEntity = userRepository.findByEmail(authentication.getName()).orElse(null);

        if(userEntity == null){
            return null;
        }

        String fileName = multipartFile.getOriginalFilename();
        long size = multipartFile.getSize();

        File file = new File(fileName);

        try (FileOutputStream fos = new FileOutputStream(file)){
            fos.write(multipartFile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        AmazonS3 s3client = amazonS3service.getAmazonS3Client();
        s3client.putObject(bucketName, fileName, file);
        file.delete();
        String location = s3client.getUrl(bucketName, fileName).toString();

        FileEntity fileEntity = FileEntity.builder()
                .location(location)
                .size(size)
                .status(Status.ACTIVE)
                .userEntity(userEntity)
                .build();

        fileRepository.save(fileEntity);

        eventRepository.save(EventEntity.builder()
                .date(new Date())
                .description(Description.FILE_CREATED)
                .userEntity(userEntity)
                .fileEntity(fileEntity)
                .build());

        return FileDto.fromEntity(fileEntity);
    }

    public FileDto delete(Long id, Authentication authentication){
        FileEntity fileEntity = fileRepository.findByIdAndStatus(id, Status.ACTIVE).orElse(null);
        UserEntity userEntity = userRepository.findByEmail(authentication.getName()).orElse(null);

        if(fileEntity == null || userEntity == null){
            return null;
        }

        fileEntity.setStatus(Status.INACTIVE);
        fileRepository.save(fileEntity);

        eventRepository.save(EventEntity.builder()
                .date(new Date())
                .description(Description.FILE_DELETED)
                .userEntity(userEntity)
                .fileEntity(fileEntity)
                .build());

        return FileDto.fromEntity(fileEntity);
    }
}
