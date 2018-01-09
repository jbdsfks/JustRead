package justread.lzj.com.justread.workspace.main.network.entity;

import justread.lzj.com.justread.thirdparty.logger.Logger;
import rx.Subscriber;

/**
 * Created by 83827 on 2017/12/16.
 */

public class BaseSubscriber<T> extends Subscriber<T> {
    @Override
    public void onStart() {

    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        Logger.e(e.getMessage());
    }

    @Override
    public void onNext(T response) {

    }
}
