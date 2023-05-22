package mega.waka.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Operation(summary = "서버 상태 API", description = "서버에 문제가 없는지 확인합니다.")
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
