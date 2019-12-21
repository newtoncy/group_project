package com.newtoncy.group_project.adapter;

import android.app.Activity;

import com.newtoncy.group_project.javaclass.ImgInfo;
import com.newtoncy.utils.ERR_CODE;
import com.newtoncy.utils.JSONRequest;
import com.newtoncy.utils.Login;
import com.newtoncy.utils.RequestFail;
import com.newtoncy.utils.RequestFailImp;
import com.newtoncy.utils.ServerURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Response;

//实现数据加载
//通知适配器刷新
public class EndlessScrollListenerImp extends EndlessScrollListener {
    private List<ImgInfo> imgInfoList;
    private Activity activity;
    private ImgListAdapter adapter;
    private RequestFail requestFail;

    public EndlessScrollListenerImp(Activity activity, List<ImgInfo> imgInfoList, ImgListAdapter adapter) {
        this.activity = activity;
        this.adapter = adapter;
        this.imgInfoList = imgInfoList;
        requestFail = new RequestFailImp(activity);
    }

    @Override
    protected boolean loadMore() {
        if(!Login.isLogin())
            return true;
        //加载更多
        JSONObject jsonObject = new JSONObject();
        try {
            if(imgInfoList.size()>0)
                jsonObject.put("lastOne", imgInfoList.get(imgInfoList.size() - 1).id); //已加载的最后一个元素的id
            jsonObject.put("count", 20); //欲获取的元素数量
            jsonObject.put("uid", Login.getUserProfile().uid); //uid
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONRequest.call(ServerURL.getURL(ServerURL.imgList), jsonObject,
                new JSONRequest.Callback() {
                    @Override
                    public void success(final JSONObject jsonObject) {
                        //操作数据结构但是不想做线程同步，所以runOnUiThread好了
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int index = imgInfoList.size();
                                int count = parseDataFromServer(jsonObject, imgInfoList);
                                notifyLoadingCompletion();
                                adapter.notifyItemRangeInserted(index,count);
                            }
                        });

                    }

                    @Override
                    public void fail(Response response, int reason, Object e) {
                        notifyLoadingCompletion();
                        requestFail.onFail(response, reason, e);
                    }
                });
        return false;
    }

    private int parseDataFromServer(JSONObject jsonObject, List<ImgInfo> imgInfoList) {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("img");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);
                ImgInfo imgInfo = new ImgInfo();
                imgInfo.id = item.getInt("id");
                imgInfo.comment = item.getString("comment");
                imgInfo.imgPath = item.getString("imgPath");
                imgInfo.tag = item.getString("tag");
                imgInfoList.add(imgInfo);
            }
            return jsonArray.length();
        } catch (JSONException e) {
            requestFail.onFail(null, ERR_CODE.FORMAT_ERR,e);
        }
        return 0;
    }

}
