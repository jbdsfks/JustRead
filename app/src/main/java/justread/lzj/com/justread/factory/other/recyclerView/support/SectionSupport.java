package justread.lzj.com.justread.factory.other.recyclerView.support;

public interface SectionSupport<T> {
    public int sectionHeaderLayoutId();

    public int sectionTitleTextViewId();

    public String getTitle(T t);
}
