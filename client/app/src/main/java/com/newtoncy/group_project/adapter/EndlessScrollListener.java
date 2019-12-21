package com.newtoncy.group_project.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EndlessScrollListener extends RecyclerView.OnScrollListener {

    public interface DataLoader
    {
        /**
         * 加载更多数据到imgInfoList中
         */
        void loadMore();

        /**
         * 刷新数据
         */
        void flush();
    }
    private DataLoader dataLoader;
    public EndlessScrollListener(DataLoader dataLoader){
        this.dataLoader = dataLoader;
    }


    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState==RecyclerView.SCROLL_STATE_IDLE && needToFlush(recyclerView)) {
            dataLoader.flush();
        }
    }



    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dy>0 && needToLoadMore(recyclerView)) {
            dataLoader.loadMore();
        }

    }

    boolean needToLoadMore(RecyclerView recyclerView) {

        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (manager == null)
            return false;
        int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
        int itemCount = manager.getItemCount();

        return lastItemPosition >= (itemCount - 10);


    }

    boolean needToFlush(RecyclerView recyclerView){
        return recyclerView.computeVerticalScrollOffset() <= 0;
    }

}
