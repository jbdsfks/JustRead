package justread.lzj.com.justread.workspace.main.network.service;

import justread.lzj.com.justread.workspace.main.network.entity.ZhihuModel;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by 83827 on 2017/12/16.
 */
public interface RequestServiceZhihu {
    @GET("api/4/news/latest")
    Observable<ZhihuModel> getLatestData();

    @GET("api/4/news/before/{data}")
    Observable<ZhihuModel> getBeforeData(@Path("data") String data);
}
