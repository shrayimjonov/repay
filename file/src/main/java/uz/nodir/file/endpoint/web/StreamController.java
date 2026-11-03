package uz.nodir.file.endpoint.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uz.nodir.file.service.business.StreamService;

@RestController
@RequestMapping("/stream")
@RequiredArgsConstructor
public class StreamController {
    private final StreamService streamService;

    @GetMapping(value = "/data", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StreamingResponseBody> data() {
        return ResponseEntity.ok(streamService.streamTesting());
    }
}
