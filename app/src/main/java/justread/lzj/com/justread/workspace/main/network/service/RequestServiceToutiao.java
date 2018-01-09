package justread.lzj.com.justread.workspace.main.network.service;

import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

import justread.lzj.com.justread.workspace.main.network.entity.ToutiaoModel;
import retrofit2.http.GET;

/**
 * Created by 83827 on 2017/12/24.
 */

public interface RequestServiceToutiao {
    @GET("api/pc/feed/?widen=1&tadrequire=true&as=A1F51AB3BE55AD9&cp=5A3E755A8DB92E1&_signature=.oLGowAApMYw6UBxv5gmQf6Cxr")
    Observable<ToutiaoModel> getHot(
            @Query("category") String category,
            @Query("max_behot_time") long max_behot_time);
}
