package org.jfteam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: fengwenping
 * Date: 2018-01-14
 * Time: 下午5:37
 */
public class AppStarter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppStarter.class);

    public static void main(String[] args) {
        String appName = "jfteam-train-tickets";
        LOGGER.info("Application named '{}' has been started......", appName);
    }
}
