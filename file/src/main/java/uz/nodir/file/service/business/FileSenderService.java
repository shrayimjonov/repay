package uz.nodir.file.service.business;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

public interface FileSenderService {

    void save(MultipartFile multipartFile, String fileName);

    InputStreamResource getByName(String name);

    void deleteByName(String name);
}
