package my.project.springApi.repository;

import my.project.springApi.model.EventEntity;
import my.project.springApi.model.FileEntity;
import my.project.springApi.model.Status;
import my.project.springApi.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface EventRepository extends JpaRepository<EventEntity, Long> {
    List<EventEntity> findAllByUserEntity(UserEntity userEntity);
    Optional<EventEntity> findByIdAndUserEntity(Long id, UserEntity userEntity);
}
