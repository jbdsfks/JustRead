package justread.lzj.com.justread.factory.other.recyclerView;

public interface MultiItemTypeSupport<T> {
    int getLayoutId(int itemType);

    int getItemViewType(int position, T t);
}