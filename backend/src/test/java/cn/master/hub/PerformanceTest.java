package cn.master.hub;

import cn.master.hub.util.JacksonUtils;
import cn.master.hub.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.jmeter.protocol.http.util.HTTPConstants;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static us.abstracta.jmeter.javadsl.JmeterDsl.*;
import static us.abstracta.jmeter.javadsl.dashboard.DashboardVisualizer.dashboardVisualizer;

/**
 * @author Created by 11's papa on 2025/9/2
 */

@Slf4j
public class PerformanceTest {
    private final Map<String, String> riskPoint = new HashMap<>();
    private final Map<String, String> userInfo = new HashMap<>();

    @BeforeTest
    public void setUp() {

        riskPoint.put("pointName", "风险点" + StringUtils.generateRandomString(5));
        riskPoint.put("riskType", "0");
        riskPoint.put("resUnit", "12");
        riskPoint.put("resMan", "66");
        riskPoint.put("isRelieve", "0");
        riskPoint.put("pointBasicInfo", "{}");

        userInfo.put("username", "jingll");
        userInfo.put("password", "Zgkd@83539595");
    }

    @Test
    public void testPerformance() throws IOException {
        TestPlanStats stats = testPlan()
                .tearDownOnlyAfterMainThreadsDone()
                .children(
                        vars().set("currentTime", "${__time(,)} "),
                        httpCache().disable(),
                        httpCookies().disable(),
                        threadGroup("初始化获取token信息(登录接口)", 1, 1,
                                httpHeaders()
                                        .header("tenant-id", "150622020001")
                                        .header("content-type", "application/json"),
                                httpSampler("登录请求", "http://172.16.2.15:9290/prod-api/admin-api/system/auth/login")
                                        .method(HTTPConstants.POST)
                                        .body(JacksonUtils.toJSONString(userInfo))
                                        .children(
                                                jsr223PostProcessor("JSR223 后置处理程序", "import groovy.json.JsonSlurper\n"
                                                        + "def response = prev.getResponseDataAsString()\n"
                                                        + "def jsonSlurper = new JsonSlurper()\n"
                                                        + "def parsedJson = jsonSlurper.parseText(response)\n"
                                                        + "def accessToken = parsedJson.data.accessToken\n"
                                                        + "def refreshToken = parsedJson.data.refreshToken\n"
                                                        + "vars.put(\"accessToken\", accessToken)\n"
                                                        + "props.put(\"accessToken\", vars.get(\"accessToken\"))")
                                        )
                        ),

                        threadGroup("main-test", 10, 10,
                                httpHeaders()
                                        .header("authorization", "Bearer ${__property(accessToken)}")
                                        .header("tenant-id", "150622020001")
                                        .header("content-type", "application/json"),
                                constantTimer(Duration.ofSeconds(1)),
                                httpSampler("dict-list", "http://172.16.2.15:9290/prod-api/admin-api/system/dict-data/list-all-simple")
                        ),
//                        threadGroup("风险点模块", 1, 1,
//                                httpHeaders()
//                                        .header("authorization", "Bearer ${__property(accessToken)}")
//                                        .header("tenant-id", "150622020001")
//                                        .header("content-type", "application/json"),
//                                constantTimer(Duration.ofSeconds(1)),
//                                httpSampler("风险点列表", "http://172.16.2.15:9290/prod-api/admin-api/risk/point/page")
//                                        .rawParam("pageNo", "1")
//                                        .rawParam("pageSize", "10"),
//                                httpSampler("添加风险点", "http://172.16.2.15:9290/prod-api/admin-api/risk/point/create")
//                                        .method(HTTPConstants.POST)
//                                        .encoding(StandardCharsets.UTF_8)
//                                        .body(JacksonUtils.toJSONString(riskPoint))
//                        ),
                        dashboardVisualizer()
                ).run();

        assertThat(stats.overall().sampleTimePercentile99()).isLessThan(Duration.ofSeconds(5));
    }
}
