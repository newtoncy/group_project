package com.newtoncy.group_project.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {
    private boolean isLoading = false;

    public interface DataLoader
    {
        /**
         * 加载更多数据到imgInfoList中
         *
         * @return 如果异步加载数据，则应该返回假，并在数据加载完成后调用notifyLoadingCompletion()
         * 如果同步加载，则返回真
         */
        void loadMore(EndlessScrollListener endlessScrollListener);
    }

    public void notifyLoadingCompletion() {
        isLoading = false;
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (!isLoading && NeedToLoadMore(recyclerView)) {
            doLoadMore();
        }
    }



    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (!isLoading && NeedToLoadMore(recyclerView)) {
            doLoadMore();
        }

    }

    boolean NeedToLoadMore(RecyclerView recyclerView) {
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (manager == null)
            return false;
        int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
        int itemCount = manager.getItemCount();

        return lastItemPosition >= (itemCount - 10);


    }

    public void doLoadMore() {
        isLoading = true;
        if (loadMore()) {
            notifyLoadingCompletion();
        }
    }

    /**
     * 加载更多数据到imgInfoList中
     *
     * @return 如果异步加载数据，则应该返回假，并在数据加载完成后调用notifyLoadingCompletion()
     * 如果同步加载，则返回真
     */
    protected abstract boolean loadMore();
}
