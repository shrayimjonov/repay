package uz.repay.card.client.decoder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Configuration;
import uz.repay.common.exception.CardException;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;

import java.io.IOException;
import java.util.Optional;

@Configuration
public class HuMoMiddlewareErrorDecoder implements ErrorDecoder {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<ResultData<CoreError>> CORE_ERROR_TYPE = new TypeReference<>() {
    };

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response == null) {
            return new IllegalStateException("HUMO middleware error response is empty");
        }

        var body = readBody(response);
        var errorMessage = parseErrorMessage(body);

        if (errorMessage.isPresent()) {
            return switch (response.status()) {
                case 400 -> new CardException(errorMessage.orElse("Bad Request"));
                case 404 -> new CardException(errorMessage.orElse("Not found"));
                case 500 -> new CardException(errorMessage.orElse("Card not found from huMo"));
                default -> FeignException.errorStatus(methodKey, rebuildResponse(response, body));
            };
        }

        return FeignException.errorStatus(methodKey, rebuildResponse(response, body));
    }

    private byte[] readBody(Response response) {
        if (response == null || response.body() == null) {
            return new byte[0];
        }

        try (var bodyInputStream = response.body().asInputStream()) {
            return bodyInputStream.readAllBytes();
        } catch (IOException ignored) {
            return new byte[0];
        }
    }

    private Optional<String> parseErrorMessage(byte[] body) {
        if (body == null || body.length == 0 || isHtml(body)) {
            return Optional.empty();
        }

        try {
            var message = OBJECT_MAPPER.readValue(body, CORE_ERROR_TYPE);
            if (message == null || message.getError() == null || message.getError().getMessage() == null) {
                return Optional.empty();
            }

            var errorMessage = message.getError().getMessage().trim();
            return errorMessage.isEmpty() ? Optional.empty() : Optional.of(errorMessage);
        } catch (IOException ignored) {
            return Optional.empty();
        }
    }

    private Response rebuildResponse(Response response, byte[] body) {
        return response.toBuilder()
                .body(body)
                .build();
    }

    private boolean isHtml(byte[] body) {
        for (byte value : body) {
            if (!Character.isWhitespace(value)) {
                return value == '<';
            }
        }
        return false;
    }
}
