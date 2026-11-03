package uz.nodir.file.service.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uz.nodir.file.service.business.StreamService;
import uz.repay.common.utils.HttpServletUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class StreamServiceImpl implements StreamService {

    @Override
    public StreamingResponseBody streamTesting() {
        var httpServletResponse = HttpServletUtils.getCurrentHttpResponse();
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

        StreamingResponseBody stream = out -> {
            try {
                for (int i = 0; i < 10; i++) {
                    String content = "{\"counter\":" + i + "}\n";
                    out.write(content.getBytes());
                    out.flush();
                    log.info("size: " + content.getBytes().length);
                    Thread.sleep(1000);
                }
            } catch (final Exception e) {
                log.error("Exception", e);
            }
            out.close();
        };
        log.info("steaming response {} ", stream);
        return stream;
    }
}
