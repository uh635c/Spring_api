package my.project.springApi.rest;

import lombok.RequiredArgsConstructor;
import my.project.springApi.model.dto.FileDto;
import my.project.springApi.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileRestControllerV1 {

    private final FileService fileService;

    @GetMapping
    public ResponseEntity<List<FileDto>> getAll(Authentication authentication) {
        List<FileDto> listFileDto = fileService.getAll(authentication);

        if (listFileDto.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(listFileDto, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<FileDto> getById(@PathVariable("id") Long id, Authentication authentication) {
        FileDto fileDto = fileService.getById(id, authentication);

        if (fileDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


        return new ResponseEntity<>(fileDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<FileDto> save(@RequestParam(name = "file") MultipartFile multipartFile, Authentication authentication) {
        FileDto fileDto = fileService.save(multipartFile, authentication);

        if (multipartFile.isEmpty() || fileDto == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(fileDto, HttpStatus.CREATED);
    }

    @Secured({"ROLE_MODERATOR", "ROLE_ADMIN"})
    @DeleteMapping("/{id}")
    public ResponseEntity<FileDto> delete(@PathVariable("id") Long id, Authentication authentication) {
        FileDto fileDto = fileService.delete(id, authentication);

        if (fileDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
