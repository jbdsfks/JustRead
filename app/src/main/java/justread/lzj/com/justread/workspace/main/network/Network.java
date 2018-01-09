package justread.lzj.com.justread.workspace.main.network;

import justread.lzj.com.justread.thirdparty.logger.Logger;
import justread.lzj.com.justread.thirdparty.okhttp.OkHttpUtils;
import justread.lzj.com.justread.workspace.main.network.entity.OneModel;
import justread.lzj.com.justread.workspace.main.network.service.RequestServiceGankio;
import justread.lzj.com.justread.workspace.main.network.service.RequestServiceOne;
import justread.lzj.com.justread.workspace.main.network.service.RequestServiceToutiao;
import justread.lzj.com.justread.workspace.main.network.service.RequestServiceWeibo;
import justread.lzj.com.justread.workspace.main.network.service.RequestServiceZhihu;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by 83827 on 2017/12/16.
 */

public class Network {
    private static RequestServiceZhihu serviceZhihu;// zhihu.com
    private static RequestServiceGankio serviceGankio; // Gank.io
    private static RequestServiceWeibo serviceWeibo; //top.weibo.cn
    private static RequestServiceToutiao serviceToutiao;  //www.toutiao.com
    private static RequestServiceOne serviceOne; //wufazhuce.com

    private static Converter.Factory scalarsConverterFactory = ScalarsConverterFactory.create();
    private static Converter.Factory gsonConverterFactory = GsonConverterFactory.create();

    private static CallAdapter.Factory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();

    private volatile static Network INSTANCE;

    private Network() {

    }
    public RequestServiceZhihu getServiceZhihuAPI() {
        return getServiceZhihuAPI(false);
    }
    /**
     * @param hasChangeOkHttpClientConfig 当改变了OkHttpClient的配置后，调用改方法重新实例化RequestService
     * @return
     */
    public RequestServiceZhihu getServiceZhihuAPI(boolean hasChangeOkHttpClientConfig) {
        if (serviceZhihu == null || hasChangeOkHttpClientConfig) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(OkHttpUtils.getInstance().getOkHttpClient())
                    .baseUrl(UrlPath.BaseUrlRequestServiceZhihu)
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();
            serviceZhihu = retrofit.create(RequestServiceZhihu.class);
        }
        return serviceZhihu;
    }
    public RequestServiceGankio getServiceGankioAPI() {
        return getServiceGankioAPI(false);
    }
    /**
     * @param hasChangeOkHttpClientConfig 当改变了OkHttpClient的配置后，调用改方法重新实例化RequestService
     * @return
     */
    public RequestServiceGankio getServiceGankioAPI(boolean hasChangeOkHttpClientConfig) {
        if (serviceGankio == null || hasChangeOkHttpClientConfig) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(OkHttpUtils.getInstance().getOkHttpClient())
                    .baseUrl(UrlPath.BaseUrlRequestServiceGankio)
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();
            serviceGankio = retrofit.create(RequestServiceGankio.class);
        }
        return serviceGankio;
    }

    public RequestServiceWeibo getServiceWeiboAPI() {
        return getServiceWeiboAPI(false);
    }
    /**
     * @param hasChangeOkHttpClientConfig 当改变了OkHttpClient的配置后，调用改方法重新实例化RequestService
     * @return
     */
    public RequestServiceWeibo getServiceWeiboAPI(boolean hasChangeOkHttpClientConfig) {
        if (serviceWeibo == null || hasChangeOkHttpClientConfig) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(OkHttpUtils.getInstance().getOkHttpClient())
                    .baseUrl(UrlPath.BaseUrlRequestServiceWeibo)
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();
            serviceWeibo = retrofit.create(RequestServiceWeibo.class);
        }
        return serviceWeibo;
    }

    public RequestServiceToutiao getServiceToutiaoAPI() {
        return getServiceToutiaoAPI(false);
    }
    /**
     * @param hasChangeOkHttpClientConfig 当改变了OkHttpClient的配置后，调用改方法重新实例化RequestService
     * @return
     */
    public RequestServiceToutiao getServiceToutiaoAPI(boolean hasChangeOkHttpClientConfig) {
        if (serviceToutiao == null || hasChangeOkHttpClientConfig) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(OkHttpUtils.getInstance().getOkHttpClient())
                    .baseUrl(UrlPath.BaseUrlRequestServiceToutiao)
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();
            serviceToutiao = retrofit.create(RequestServiceToutiao.class);
        }
        return serviceToutiao;
    }

    public RequestServiceOne getServiceOneApi(){
        return getServiceOneApi(false);
    }
    public RequestServiceOne getServiceOneApi(boolean hasChangeOkHttpClientConfig){
        if (serviceOne == null || hasChangeOkHttpClientConfig) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(OkHttpUtils.getInstance().getOkHttpClient())
                    .baseUrl(UrlPath.BaseUrlRequestServiceOne)
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();

            serviceOne = retrofit.create(RequestServiceOne.class);
        }
        return serviceOne;
    }



    public static Network getInstance() {
        if (INSTANCE == null) {
            synchronized (Network.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Network();
                }
            }
        }
        return INSTANCE;
    }

}
