package org.jfteam.util;

import java.nio.charset.Charset;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: fengwenping
 * Date: 2018-01-14
 * Time: 下午5:50
 */
public final class SysConstants {

    public static Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public static class TrainUrl {
        public static final String TICKET_URL = "https://kyfw.12306.cn/otn/leftTicket/init";
        public static final String LOGIN_URL = "https://kyfw.12306.cn/passport/web/login";
        public static final String INITMY_URL = "https://kyfw.12306.cn/otn/index/initMy12306";
        public static final String BUY_URL = "https://kyfw.12306.cn/otn/confirmPassenger/initDc";
        public static final String CAPTCHA_URL = "https://kyfw.12306.cn/passport/captcha/captcha-image?login_site=E&module=login&rand=sjrand&" + System.currentTimeMillis();
    }
}
