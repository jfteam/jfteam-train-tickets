package org.jfteam.api;

import org.apache.http.Header;
import org.jfteam.util.HttpRequestUtils;
import org.jfteam.util.SysConstants;
import org.jfteam.vo.ApiResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: fengwenping
 * Date: 2018-01-14
 * Time: 下午9:22
 */
@WebServlet(
        name = "LoginServlet",
        urlPatterns = "/api/login"
)
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String username = req.getParameter("username");
        final String password = req.getParameter("password");
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        ApiResponse apiResponse = HttpRequestUtils.doPost(SysConstants.TrainUrl.LOGIN_URL, null, params);
        resp.setStatus(apiResponse.getStatusCode());
        Header[] headers = apiResponse.getHeaders();
        if (apiResponse != null && headers != null) {
            for (Header header : headers) {
                resp.setHeader(header.getName(), header.getValue());
            }
        }
        super.doPost(req, resp);
    }
}
