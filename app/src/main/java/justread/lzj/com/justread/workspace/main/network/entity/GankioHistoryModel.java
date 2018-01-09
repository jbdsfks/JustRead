package justread.lzj.com.justread.workspace.main.network.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 83827 on 2017/12/18.
 */

public class GankioHistoryModel implements Serializable{

    public boolean error;
    public List<GankioHistoryModel.ResultsBean> results;
    public static class ResultsBean {
        public String _id;
        public String createdAt;
        public String desc;
        public String publishedAt;
        public String source;
        public String type;
        public String url;
        public String used;
        public String who;
        public List<String> images;
    }
}
