package justread.lzj.com.justread.workspace.main.network.service;

import justread.lzj.com.justread.workspace.main.network.entity.WeiboTopModel;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by 83827 on 2017/12/20.
 */

public interface RequestServiceWeibo {
    @GET("2/articles/home_timeline/?from=7529595017&guest_uid=1002341793139&cate_id=0")
    Observable<WeiboTopModel> getWeiboTopData();

}
