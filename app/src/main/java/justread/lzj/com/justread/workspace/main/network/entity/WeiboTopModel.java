package justread.lzj.com.justread.workspace.main.network.entity;

import java.util.List;

/**
 * Created by 83827 on 2017/12/20.
 */

public class WeiboTopModel {
    public int status;
    public int pattern;
    public String message;
    public int total_number;
    public List<DataBean> data;

    public static class DataBean{
        public String source;
        public String title;
        public String article_url;
        public long time;
        public List<Image_240Bean> image_240;
        public int id;
        public static class Image_240Bean{
            public String des_url;
            public int height;
            public int width;
        }
    }

}
