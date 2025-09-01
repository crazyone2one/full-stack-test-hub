package cn.master.hub.controller;

import cn.master.hub.service.DemoPerformanceTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Created by 11's papa on 2025/9/1
 */
@RestController
@RequestMapping("/performance-test")
@RequiredArgsConstructor
public class PerformanceTestController {
    private final DemoPerformanceTestService demoPerformanceTestService;

    @PostMapping("/execute")
    public void execute() throws Exception {
        demoPerformanceTestService.execute();
    }
}
