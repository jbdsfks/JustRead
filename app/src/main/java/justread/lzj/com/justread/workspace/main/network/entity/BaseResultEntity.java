package justread.lzj.com.justread.workspace.main.network.entity;

import java.io.Serializable;
/**
 * Created by 83827 on 2017/12/16.
 */

public class BaseResultEntity<T> implements Serializable {
    public int code;
    public String msg;
    public T data;
}
