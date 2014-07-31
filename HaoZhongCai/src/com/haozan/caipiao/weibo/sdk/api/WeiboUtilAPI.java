package com.haozan.caipiao.weibo.sdk.api;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.net.RequestListener;
/**
 * 此类封装了账号的接口，详情见<a href="http://open.weibo.com/wiki/API%E6%96%87%E6%A1%A3_V2#.E8.B4.A6.E5.8F.B7">账号接口</a>
 * @author xiaowei6@staff.sina.com.cn
 *
 */
public class WeiboUtilAPI extends WeiboAPI {
    private static final String SERVER_URL_PRIX = API_SERVER + "/account";
	public WeiboUtilAPI(Oauth2AccessToken accessToken) {
        super(accessToken);
    }

    

	/**
	 * 发送一条微博
	 */
	public void request( final String url, final WeiboParameters params,
	                     final String httpMethod,RequestListener listener) {
	    super.request(url, params, httpMethod, listener);
	}
}
