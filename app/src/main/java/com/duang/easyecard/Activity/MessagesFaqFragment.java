package com.duang.easyecard.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.Model.FaqItem;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.duang.easyecard.Util.MessagesFaqListAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pgyersdk.crash.PgyCrashManager;
import com.rey.material.widget.ProgressView;
import com.yalantis.phoenix.PullToRefreshView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 常见问题，不同类别均用此Fragment
 * Created by MrD on 2016/3/27.
 */
public class MessagesFaqFragment extends Fragment implements AdapterView.OnItemClickListener {

    private View viewFragment;  // 缓存Fragment的View
    private PullToRefreshView mPullToRefreshView;
    private ListView mListView;
    private ProgressView mProgressView;

    private GetDataListInitFlagListener getDataListInitFlagListener;
    private MessagesFaqListAdapter mAdapter;
    private AsyncHttpClient httpClient;
    private List<FaqItem> dataList;
    private String address;
    private String response;
    private int type;
    private final String TAG = "MessagesFaqFragment";
    private final int CONSTANT_XYKGL = 0, CONSTANT_YYZX = 1, CONSTANT_ZHAQ = 2, CONSTANT_ZXJF = 3;
    private boolean refreshingFlag = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof GetDataListInitFlagListener)) {
            throw new IllegalStateException("The host activity must implement the" +
                    "GetDataListInitFlagListener");
        }
        // 把绑定的activity当成callback对象
        getDataListInitFlagListener = (GetDataListInitFlagListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreateView");
        if (viewFragment == null) {
            viewFragment = inflater.inflate(R.layout.fragment_messages_faq, container, false);
        }
        // 缓存的rootView需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误
        ViewGroup parent = (ViewGroup) viewFragment.getParent();
        if (parent != null) {
            parent.removeView(viewFragment);
        }
        return viewFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.d(TAG, "onActivityCreated");
        initView();
        initData();
    }

    private void initView() {
        // 实例化控件
        mPullToRefreshView = (PullToRefreshView) viewFragment.findViewById(
                R.id.messages_faq_pull_to_refresh_view);
        mListView = (ListView) viewFragment.findViewById(R.id.messages_faq_list_view);
        mProgressView = (ProgressView) viewFragment.findViewById(R.id.messages_faq_progress_view);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshingFlag = true;
                // 清空（刷新）数据列表
                dataList.clear();
                sendGETRequest();
            }
        });
        mListView.setOnItemClickListener(this);
    }

    private void initData() {
        // 通过POSITION确定Fragment的类型，加载相应的数据
        if (getArguments().containsKey("POSITION")) {
            switch (getArguments().getInt("POSITION")) {
                case 0:
                    type = CONSTANT_XYKGL;
                    address = UrlConstant.MOBILE_FAQ_XYKGL;
                    break;
                case 1:
                    type = CONSTANT_YYZX;
                    address = UrlConstant.MOBILE_FAQ_YYZX;
                    break;
                case 2:
                    type = CONSTANT_ZHAQ;
                    address = UrlConstant.MOBILE_FAQ_ZHAQ;
                    break;
                case 3:
                    type = CONSTANT_ZXJF;
                    address = UrlConstant.MOBILE_FAQ_ZXJF;
                    break;
                default:
                    break;
            }
        } else {
            LogUtil.e(TAG, "Can't get arguments: position.");
        }
        // 获得全局变量httpClient和userBasicInformation
        MyApplication myApp = (MyApplication) getActivity().getApplication();
        httpClient = myApp.getHttpClient();
        dataList = new ArrayList<>();
        if (getDataListInitFlagListener.getDataListInitFlag(type)) {
            // 已经初始化，无需操作
            LogUtil.d(TAG, "Has inited.");
        } else {
            sendGETRequest();
        }
    }

    // 如果没有数据，发送GET请求获取数据；如果已经获得数据，直接设置Adapter
    private void sendGETRequest() {
        LogUtil.d(TAG, "Send GET request.");
        // 发送GET请求
        httpClient.get(address, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 响应成功
                response = new String(responseBody);
                // 解析响应数据
                new JsoupHtmlData().execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                // 网络错误
                LogUtil.e(TAG, "Network error.");
                // 使PullToRefreshView退出刷新状态
                mPullToRefreshView.setRefreshing(false);
                Toast.makeText(MyApplication.getContext(), getString(R.string.network_error),
                        Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    // 从MessagesFaqActivity的Lists中获取数据，并设置Adapter
    private void setAdapter() {
        if (refreshingFlag) {
            // 处于刷新状态，表示不是首次加载
            mAdapter.notifyDataSetChanged();
            mPullToRefreshView.setRefreshing(false);
            Toast.makeText(MyApplication.getContext(), getString(R.string.refresh_complete),
                    Toast.LENGTH_SHORT).show();
            refreshingFlag = false;
        } else {
            // 不是刷新，表示首次加载，需要新建适配器
            mAdapter = new MessagesFaqListAdapter(MyApplication.getContext(), dataList,
                    R.layout.item_messages_faq_list);
            mListView.setAdapter(mAdapter);
        }
        // 表明此类型数据已经初始化过
        getDataListInitFlagListener.setDataListInitFlag(type);
    }

    // ListView的Item点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LogUtil.d(TAG, "onItemClick.");
        FaqItem item = (FaqItem) parent.getItemAtPosition(position);
        // 跳转到查看详细信息界面，并传递FaqItem对象
        Intent intent = new Intent(MyApplication.getContext(), MessagesFaqDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("FaqItem", item);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    // 解析响应数据
    private class JsoupHtmlData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            LogUtil.d(TAG, "Start Jsoup");
            dataList = new ArrayList<>();
            Document doc;
            FaqItem item;
            try {
                doc = Jsoup.parse(response);
                // 获取标题
                for (Element span : doc.select("span")) {
                    LogUtil.d(TAG, "span = " + span.text());
                    item = new FaqItem();
                    item.setType(type);
                    item.setTitle(span.text());
                    // 获取详细界面链接
                    for (Element a : span.parent().select("a")) {
                        LogUtil.d(TAG, "a.href = " + a.attr("href"));
                        item.setDetailAddress(UrlConstant.MOBILE_INDEX + a.attr("href"));
                    }
                    dataList.add(item);
                }
            } catch (Exception e) {
                PgyCrashManager.reportCaughtException(MyApplication.getContext(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // 设置适配器
            setAdapter();
            // 隐藏ProgressView
            mProgressView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getDataListInitFlagListener = null;
    }

    // 用于判断对应类型的Fragment是否初始化过
    public interface GetDataListInitFlagListener {
        boolean getDataListInitFlag(int type);

        void setDataListInitFlag(int type);
    }
}
