package justread.lzj.com.justread.workspace.main.network.service;

import justread.lzj.com.justread.workspace.main.network.entity.GankioHistoryModel;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by 83827 on 2017/12/18.
 */

public interface RequestServiceGankio {
    @GET("api/data/Android/{number}/{page}")
    Observable<GankioHistoryModel> getHistoryContent(@Path("number") int number, @Path("page") int page);
}
