package justread.lzj.com.justread.workspace.main.network.service;

import justread.lzj.com.justread.workspace.main.network.entity.OneModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by 83827 on 2017/12/26.
 */

public interface RequestServiceOne {

    @GET("api/hp/bydate/{date}")
    Observable<OneModel> getOne(@Path("date") String date);
}
