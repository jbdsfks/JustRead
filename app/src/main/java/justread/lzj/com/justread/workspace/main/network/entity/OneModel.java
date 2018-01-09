package justread.lzj.com.justread.workspace.main.network.entity;

import java.util.List;

/**
 * Created by 83827 on 2017/12/26.
 */

public class OneModel {

    public DataBean data;
    public static class ShareInfoBean{
        public String title;
    }
    public static class DataBean{
        public String item_id;
        public String img_url;
        public String forward;
        public String title;
        public String post_date;
        public String volume;
        public String pic_info;
        public String words_info;
        public String share_url;
        public ShareInfoBean share_info;
    }
}
