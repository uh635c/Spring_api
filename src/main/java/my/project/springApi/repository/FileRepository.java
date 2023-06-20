package my.project.springApi.repository;

import my.project.springApi.model.FileEntity;

import my.project.springApi.model.Status;
import my.project.springApi.model.UserEntity;
import my.project.springApi.model.dto.FileDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findAllByUserEntityAndStatus(UserEntity userEntity, Status status);
    List<FileEntity> findAllByStatus(Status status);

    Optional<FileEntity> findByIdAndStatus(Long id, Status status);
    Optional<FileEntity> findByIdAndUserEntityAndStatus(Long id, UserEntity userEntity, Status status);
}
