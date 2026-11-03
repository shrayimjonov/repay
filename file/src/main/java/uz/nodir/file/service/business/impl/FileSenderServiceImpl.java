package uz.nodir.file.service.business.impl;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.nodir.file.model.property.MiniOProperty;
import uz.nodir.file.service.business.FileSenderService;
import uz.repay.common.exception.BadRequestException;
import uz.repay.common.exception.GeneralErrorException;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileSenderServiceImpl implements FileSenderService {
    private final MinioClient minioClient;
    private final MiniOProperty miniOProperty;

    @Override
    public void save(MultipartFile multipartFile, String fileName) {
        log.info("Taken file for saving to miniO: {}", multipartFile.getOriginalFilename());

        InputStream is;
        try {
            is = multipartFile.getInputStream();
        } catch (IOException e) {
            throw new GeneralErrorException(e.getMessage());
        }

        var size = multipartFile.getSize();

        try {
            var result = minioClient.putObject(
                    PutObjectArgs.builder()
                            .stream(is, size, -1)
                            .bucket(miniOProperty.getBucket())
                            .object(fileName)
                            .build()
            );

            log.info("e-Tag: {}", result.etag());
            log.info("saved bucket: {}", result.bucket());

        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new GeneralErrorException(e.getMessage());
        }


    }

    @Override
    public InputStreamResource getByName(String name) {
        try {
            InputStream stream =
                    minioClient.getObject(
                            GetObjectArgs.builder()
                                    .bucket(miniOProperty.getBucket())
                                    .object(name)
                                    .build());
            return new InputStreamResource(stream);

        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new BadRequestException(e.getMessage());
        }

    }

    @Override
    public void deleteByName(String name) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(miniOProperty.getBucket())
                            .object(name)
                            .build()
            );
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new GeneralErrorException(e.getMessage());
        }
    }


}
