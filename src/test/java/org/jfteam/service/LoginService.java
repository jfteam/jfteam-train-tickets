package org.jfteam.service;

import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.jfteam.util.HttpRequestUtils;
import org.jfteam.util.SysConstants;
import org.jfteam.vo.ApiResponse;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: fengwenping
 * Date: 2018-01-14
 * Time: 下午9:48
 */
public class LoginService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    @Test
    public void loginTest() throws IOException {
        String username = "fengwenping";
        String password = "a123456789";
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("appid", "otn");
        final ApiResponse apiResponse = HttpRequestUtils.doPost("http://www.baidu.com", null, params);
        LOGGER.info("login status:{}", apiResponse.getStatusCode());
        EntityUtils.consume(apiResponse.getHttpEntity());
        LOGGER.info("login content:{}", EntityUtils.toString(apiResponse.getHttpEntity(), SysConstants.DEFAULT_CHARSET));
        Assert.assertTrue(apiResponse.getStatusCode() == HttpStatus.SC_OK);
    }
}
