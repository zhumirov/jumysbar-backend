package kz.btsd.edmarket.file.repository;

import kz.btsd.edmarket.file.model.File;
import kz.btsd.edmarket.file.model.FileDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FileRepository extends CrudRepository<File, String> {
    @Query("SELECT new kz.btsd.edmarket.file.model.FileDto(f.id, f.fileName) FROM file_ed f WHERE f.id = :id")
    FileDto findDtoById(@Param("id") String id);
}
