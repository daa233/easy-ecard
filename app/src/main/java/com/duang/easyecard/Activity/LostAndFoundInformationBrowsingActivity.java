package com.duang.easyecard.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.Model.LostAndFoundEvent;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.duang.easyecard.Util.LostAndFoundEventAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yalantis.phoenix.PullToRefreshView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

public class LostAndFoundInformationBrowsingActivity extends BaseActivity
        implements AdapterView.OnItemClickListener {

    private PullToRefreshView mPullToRefreshView;
    private ListView mListView;
    private CheckBox mNotFoundedCheckBox;
    private CheckBox mFoundedCheckBox;
    private ImageView mImageView;
    private SweetAlertDialog mProgressDialog;

    private AsyncHttpClient httpClient;
    private LostAndFoundEventAdapter mAdapter;
    private String response;  // 服务器响应数据
    private List<LostAndFoundEvent> lostAndFoundEventList;
    private int pageIndex;  // 访问的网页信息的页码
    private int maxPageIndex;  // 最大页码
    private boolean FIRST_TIME_TO_PARSE_FLAG = true;  // 首次解析标志，默认为true
    private int amountLostAndFoundEvent;
    private int foundedLostAndFoundEvent;
    private boolean DISPLAY_NOT_FOUNDED_EVENTS_FLAG = true;  // 显示未招领事件标志，默认为true
    private boolean DISPLAY_FOUNDED_EVENTS_FLAG = true;  // 显示已招领事件标志，默认为true

    private final String TAG = "LostAndFoundInformationBrowsingActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_and_found_information_browsing);
        initView();
        initData();
    }

    // 初始化布局
    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.lost_and_found_information_browsing_toolbar);
        setSupportActionBar(toolbar);
        // 显示Back按钮
        setDisplayHomeButton();
        // 实例化控件
        mPullToRefreshView = (PullToRefreshView) findViewById(
                R.id.lost_and_found_information_browsing_pull_to_refresh_view);
        mListView = (ListView) findViewById(R.id.lost_and_found_information_browsing_list_view);
        mNotFoundedCheckBox = (CheckBox) findViewById(
                R.id.lost_and_found_information_browsing_not_founded_check_box);
        mFoundedCheckBox = (CheckBox) findViewById(
                R.id.lost_and_found_information_browsing_founded_check_box);
        mImageView = (ImageView) findViewById(
                R.id.lost_and_found_information_browsing_nothing_image_view);
        // 监听PullToRefreshView的下拉刷新事件
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 开始刷新
                initData();
            }
        });
        // 监听ListView的Item点击事件
        mListView.setOnItemClickListener(this);
        // 监听CheckBox的选择事件
        mNotFoundedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DISPLAY_NOT_FOUNDED_EVENTS_FLAG = isChecked;
                updateListView();
            }
        });
        mFoundedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DISPLAY_FOUNDED_EVENTS_FLAG = isChecked;
                updateListView();
            }
        });
    }

    // 初始化数据
    private void initData() {
        // 获得全局变量httpClient
        MyApplication myApp = (MyApplication) getApplication();
        httpClient = myApp.getHttpClient();
        // 初始化数据List
        lostAndFoundEventList = new ArrayList<>();
        // 初始化网页信息页码，首次解析标志置为true
        pageIndex = 1;
        maxPageIndex = 1;
        FIRST_TIME_TO_PARSE_FLAG = true;
        // 显示ProgerssDialog
        mProgressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText(getString(R.string.loading));
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        // 需要先访问到“应用中心”界面，准备好后发送GET请求
        sendPreGETRequest();
    }

    // 更新ListView
    private void updateListView() {
        LogUtil.d(TAG, "setAdapter");
        List<LostAndFoundEvent> partialLostAndFoundEventList = new ArrayList<>();
        // 根据标志位状态设置Adapter
        if (DISPLAY_NOT_FOUNDED_EVENTS_FLAG && DISPLAY_FOUNDED_EVENTS_FLAG) {
            // 显示所有事件
            mImageView.setVisibility(View.GONE);
            mAdapter = new LostAndFoundEventAdapter(MyApplication.getContext(),
                    lostAndFoundEventList, R.layout.item_lost_and_found_information_browsing_list);
            mListView.setAdapter(mAdapter);
        } else {
            if (DISPLAY_NOT_FOUNDED_EVENTS_FLAG) {
                // 仅显示未招领事件
                mImageView.setVisibility(View.GONE);
                for (int i = 0; i < lostAndFoundEventList.size(); i++) {
                    if (lostAndFoundEventList.get(i).getState().contains(
                            getString(R.string.card_in_lost_state))) {
                        partialLostAndFoundEventList.add(lostAndFoundEventList.get(i));
                    }
                }
            } else if (DISPLAY_FOUNDED_EVENTS_FLAG) {
                // 仅显示已招领事件
                mImageView.setVisibility(View.GONE);
                for (int i = 0; i < lostAndFoundEventList.size(); i++) {
                    if (lostAndFoundEventList.get(i).getState().contains(
                            getString(R.string.card_has_been_founded))) {
                        partialLostAndFoundEventList.add(lostAndFoundEventList.get(i));
                    }
                }
            } else {
                // 不显示任何事件
                LogUtil.d(TAG, "updateListView: Display nothing.");
            }
            // 设置适配器
            if (partialLostAndFoundEventList.isEmpty()) {
                // 显示没有搜索到结果的图片
                Glide
                        .with(this)
                        .load(R.drawable.nothing_founded_404)
                        .into(mImageView);
                mImageView.setVisibility(View.VISIBLE);
            }
            mAdapter = new LostAndFoundEventAdapter(MyApplication.getContext(),
                    partialLostAndFoundEventList,
                    R.layout.item_lost_and_found_information_browsing_list);
            mListView.setAdapter(mAdapter);
        }
    }

    // 先访问应用中心界面，准备发送PreGET请求
    private void sendPreGETRequest() {
        httpClient.get(UrlConstant.MANAGEMENT, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                LogUtil.d(TAG, "PreGET response success.");
                response = new String(responseBody);
                LogUtil.d(TAG, "PreGET response: " + response);
                // 响应成功，发送GET请求
                sendGETRequest();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                // 网络错误
                mProgressDialog.cancel();
                Toast.makeText(LostAndFoundInformationBrowsingActivity.this,
                        R.string.network_error, Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    // 发送GET请求
    private void sendGETRequest() {
        UrlConstant.cardLossPageIndex = pageIndex;  // 组装Url
        LogUtil.d(TAG, "GET address: " + UrlConstant.getCardLossInfoBrowsing());
        httpClient.get(UrlConstant.getCardLossInfoBrowsing(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 成功响应
                LogUtil.d(TAG, "GET response success.");
                response = new String(responseBody);
                new JsoupHtmlData().execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                // 网络错误
                mProgressDialog.cancel();
                Toast.makeText(LostAndFoundInformationBrowsingActivity.this,
                        R.string.network_error, Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    // ListView的Item点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 跳转到查看详细信息界面，并传递LostAndFoundEvent对象
        Intent intent = new Intent(MyApplication.getContext(),
                LostAndFoundInformationBrowsingViewDetailActivity.class);
        Bundle bundle = new Bundle();
        LostAndFoundEvent event = (LostAndFoundEvent) parent.getItemAtPosition(position);
        bundle.putSerializable("LostAndFoundEvent", event);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    // 解析响应数据
    private class JsoupHtmlData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            LogUtil.d(TAG, "Start Jsoup");
            Document doc;
            try {
                doc = Jsoup.parse(response);
                // 首次解析时得到最大页码，避免maxPageIndex在解析到最后一页时减小
                if (FIRST_TIME_TO_PARSE_FLAG) {
                    String remainString = "";
                    for (Element page : doc.select("a[data-ajax=true]")) {
                        remainString = page.attr("href");
                    }
                    // 当记录页数少于1时，remainString为空
                    if (!remainString.isEmpty()) {
                        // remainString不为空
                        remainString = remainString.substring(
                                remainString.indexOf("pageindex=") + 10);
                        maxPageIndex = Integer.valueOf(remainString);
                        LogUtil.d(TAG, "maxPageIndex = " + maxPageIndex);
                    } else {
                        // remainString为空, maxIndex值保持不变
                        LogUtil.d(TAG, "maxPageIndex = " + maxPageIndex);
                    }
                    // 解析当前累计丢失信息条数和已招领条数
                    for (Element div : doc.select("div[class=content]")) {
                        Element span = div.getElementById("lblLostCount");
                        amountLostAndFoundEvent = Integer.valueOf(span.text());
                        span = div.getElementById("lblClaimCount");
                        foundedLostAndFoundEvent = Integer.valueOf(span.text());
                    }
                    // 将首次解析标志置为flase
                    FIRST_TIME_TO_PARSE_FLAG = false;
                }
                // 找到表格
                for (Element table : doc.select("table[class=table_show widthtable]")) {
                    Elements tbody = table.select("tbody");
                    // 找到表格的所有行
                    for (Element row : tbody.select("tr")) {
                        // 找到每一行所包含的td
                        Elements tds = row.select("td");
                        // 将数据按照顺序填入event对象
                        if (!tds.get(0).text().isEmpty()) {
                            LostAndFoundEvent event = new LostAndFoundEvent();
                            // 通过字符串截取获得丢失信息ID
                            String eventIdString = tds.get(0).toString();
                            eventIdString = eventIdString.substring(
                                    eventIdString.indexOf("(") + 1);
                            eventIdString = eventIdString.substring(
                                    0, eventIdString.indexOf(")"));
                            event.setId(Integer.valueOf(eventIdString));
                            event.setName(tds.get(0).text());
                            event.setStuId(tds.get(1).text());
                            event.setAccount(tds.get(2).text());
                            event.setPublishTime(tds.get(3).text());
                            event.setContact(tds.get(4).text());
                            event.setState(tds.get(5).text());
                            event.setFoundTime(tds.get(6).text());
                            lostAndFoundEventList.add(event);
                        }
                    }
                }
            } catch (Exception e) {
                mProgressDialog.cancel();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // 判断是否已经全部加载完成
            if (pageIndex < maxPageIndex) {
                // 还有数据需要加载，页码加1，然后继续发送GET请求
                pageIndex++;
                sendGETRequest();
            } else {
                // 已经全部加载完成
                LogUtil.d(TAG, "Loading Finished.");
                // 更新CheckBox的数量显示
                mNotFoundedCheckBox.setText(getString(R.string.not_founded) + " (" +
                        (amountLostAndFoundEvent - foundedLostAndFoundEvent) + ")");
                mFoundedCheckBox.setText(getString(R.string.founded) + " (" +
                        foundedLostAndFoundEvent + ")");
                // 设置适配器并显示
                updateListView();
                // 关停PullToRefreshView的刷新
                mPullToRefreshView.setRefreshing(false);
                // 准备停止ProgressDialog
                mProgressDialog
                        .setTitleText(getString(R.string.loading_complete))
                        .setContentText(getString(R.string.total_queried_events)
                                + amountLostAndFoundEvent + getString(R.string.in_records))
                        .setConfirmText(getString(R.string.OK))
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                // 延迟一段时间后，关闭ProgressDialog
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                    }
                }, 1200);
            }
        }
    }
}
