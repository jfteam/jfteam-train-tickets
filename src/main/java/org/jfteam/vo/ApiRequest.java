package org.jfteam.vo;

import org.apache.http.Header;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: fengwenping
 * Date: 2018-01-14
 * Time: 下午6:57
 */
public interface ApiRequest {

    String getUrl();

    Header[] getAllHeaders();


}
