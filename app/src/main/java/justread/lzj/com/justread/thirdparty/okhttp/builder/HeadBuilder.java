package justread.lzj.com.justread.thirdparty.okhttp.builder;

import justread.lzj.com.justread.thirdparty.okhttp.OkHttpUtils;
import justread.lzj.com.justread.thirdparty.okhttp.request.OtherRequest;
import justread.lzj.com.justread.thirdparty.okhttp.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
