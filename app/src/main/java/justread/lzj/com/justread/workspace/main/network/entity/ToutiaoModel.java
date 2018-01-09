package justread.lzj.com.justread.workspace.main.network.entity;

import java.util.List;

/**
 * Created by 83827 on 2017/12/24.
 */

public class ToutiaoModel {
    public String has_more;
    public String message;
    public List<DataBean> data;
    public NextBean next;

    public static class DataBean{
        public String title;
        public String media_avatar_url;
        public String chinese_tag;
        public String image_url;
        public String group_id;
        public long behot_time;
        public String tag;
    }

    public static class NextBean{
        public long max_behot_time;
    }
}
