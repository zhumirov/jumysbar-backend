package kz.btsd.edmarket.file.controller;

import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.file.model.File;
import kz.btsd.edmarket.file.model.FileDto;
import kz.btsd.edmarket.file.repository.FileRepository;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.validation.UnexpectedTypeException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.UUID;


@CrossOrigin(origins = "*", allowedHeaders="*")
@RestController
public class FileController {
    @Autowired
    public FileRepository repository;

    @GetMapping("/files/{id}/info")
    public FileDto getInfo(@PathVariable String id) {
        return repository.findDtoById(id);
    }

    @GetMapping("/files/{id}")
    public byte[] getFile(@PathVariable String id) {
        File file = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        return file.getData();
    }

    @GetMapping("/files/{id}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String id) throws UnsupportedEncodingException {
        File file = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + URLEncoder.encode(file.getFileName(),"UTF-8"))
                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                .contentLength(file.getData().length)
                .body(file.getData());
    }

    @DeleteMapping("/files/{id}")
    void delete(@PathVariable String id) {
        repository.deleteById(id);
    }

    //todo доработать
    @PostMapping(value = "/files")
    public String uploadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException, MagicParseException, MagicException, MagicMatchNotFoundException {
        String contentType = Magic.getMagicMatch(multipartFile.getBytes()).getMimeType();
        if (!(isSupportedContentType(multipartFile.getContentType()) || isSupportedContentType(contentType))) {
            throw new UnexpectedTypeException("не поддерживыемый тип файла:" + multipartFile.getContentType());
        }
        String uuid = UUID.randomUUID().toString();
        File file = new File(uuid, multipartFile.getOriginalFilename(), multipartFile.getBytes());
        return repository.save(file).getId();
    }

    @RequestMapping(value = "/files", method = RequestMethod.OPTIONS)
    public String uploadFileOpt() {
        return "ok";
    }

    //todo доработать
    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/png")
                || contentType.equals("image/jpg")
                || contentType.equals("image/jpeg")
                || contentType.equals("image/vnd.microsoft.icon")
                || contentType.equals("application/msword")
                || contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                || contentType.equals("application/vnd.ms-excel")
                || contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                || contentType.equals("application/vnd.ms-powerpoint")
                || contentType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")
                || contentType.equals("application/pdf")
                || contentType.equals("application/rtf")
                || contentType.equals("application/vnd.oasis.opendocument.text")
                || contentType.equals("application/vnd.oasis.opendocument.presentation")
                || contentType.equals("text/csv")
                || contentType.equals("text/plain");
    }
}
