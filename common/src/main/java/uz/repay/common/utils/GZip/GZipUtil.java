package uz.repay.common.utils.GZip;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResultState;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


@Slf4j
public class GZipUtil {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static SimpleResponse<byte[]> compress(Object object) {
        try {

            var data = objectMapper.writeValueAsString(object);

            if (data == null || data.isEmpty())
                return new SimpleResponse<>(ResultState.InvalidRequest.getCode(), "Data is null or empty");


            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
                gzipOutputStream.write(data.getBytes());
            }
            return new SimpleResponse<>(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            var message = "Occurred error while compressing object: %s with message: %s".formatted(object, e.getMessage());
            log.error(message);
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), message);
        }
    }

    public static <T> SimpleResponse<T> decompress(byte[] compressedData, TypeReference<T> typeReference) {
        if (compressedData == null || compressedData.length == 0)
            return new SimpleResponse<>(ResultState.InvalidRequest.getCode(), "Data is null or empty");

        try {

            String stringValue;
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(compressedData))) {
                stringValue = new String(gzipInputStream.readAllBytes());
            }

            var data = objectMapper.readValue(stringValue, typeReference);
            return new SimpleResponse<>(data);

        } catch (Exception e) {
            var message = "Occurred error while decompressing byte with message: %s".formatted(e.getMessage());
            log.error(message);
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), message);
        }

    }
}
