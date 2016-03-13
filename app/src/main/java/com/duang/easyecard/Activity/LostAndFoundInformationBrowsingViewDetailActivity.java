package com.duang.easyecard.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.Model.LostAndFoundEvent;
import com.duang.easyecard.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;
import cz.msebera.android.httpclient.Header;

/**
 * LostAndFoundInformationBrowsingViewDetailActivity
 * Created by MrD on 2016/3/12.
 */
public class LostAndFoundInformationBrowsingViewDetailActivity extends BaseActivity {

    private UITableView mTableView;

    private AsyncHttpClient httpClient;
    private String response;
    private LostAndFoundEvent event;
    private String lostPlace;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_and_found_information_browsing_view_detail);
        // 显示Back按钮
        setupActionBar();
        // 获得传递的LostAndFoundEvent对象
        Intent intent = this.getIntent();
        event = (LostAndFoundEvent) intent.getSerializableExtra("LostAndFoundEvent");
        // 重设标题
        setTitle(getString(R.string.LostAndFoundInformationBrowsingViewDetailActivity_label) +
                getString(R.string.number_point) + event.getId());
        initView();
        initData();
    }

    private void initView() {
        // 实例化控件
        mTableView = (UITableView) findViewById(
                R.id.lost_and_found_information_browsing_view_detail_table_view);
    }

    private void initData() {
        // 获得全局变量httpClient
        MyApplication myApp = (MyApplication) getApplication();
        httpClient = myApp.getHttpClient();
        sendGETRequest();  // 发送GET请求
    }

    public void buttonOnClick(View v) {

    }

    // 发送GET请求
    private void sendGETRequest() {
        // 组装Url
        UrlConstant.cardLossViewDetailId = event.getId();
        httpClient.get(UrlConstant.getCardLossInfoViewDetail(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 成功响应
                response = new String(responseBody);
                // 解析返回的数据
                new JsoupHtmlData().execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                //  网络错误
                Toast.makeText(LostAndFoundInformationBrowsingViewDetailActivity.this,
                        R.string.network_error, Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    // 解析响应数据
    private class JsoupHtmlData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // 解析返回的responseString
            Document doc;
            try {
                doc = Jsoup.parse(response);
                for (Element ps : doc.select("p[class=heightauto]")) {
                    lostPlace = ps.ownText();
                }
                for (Element ps : doc.select("p[class=heightauto clear]")) {
                    description = ps.ownText();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // 组装UITableView的数据列表
            createTableViewList();
        }
    }

    // 创建UITableView列表
    private void createTableViewList() {
        generateCustomItem(mTableView, getString(R.string.name), event.getName());
        generateCustomItem(mTableView, getString(R.string.stu_id), event.getStuId());
        generateCustomItem(mTableView, getString(R.string.card_account), event.getAccount());
        generateCustomItem(mTableView, getString(R.string.contact), event.getContact());
        generateCustomItem(mTableView, getString(R.string.publish_time), event.getPublishTime());
        generateCustomItem(mTableView, getString(R.string.lost_place), lostPlace);
        generateCustomItem(mTableView, getString(R.string.description), description);
        generateCustomItem(mTableView, getString(R.string.state), event.getState());
        generateCustomItem(mTableView, getString(R.string.found_time), event.getFoundTime());
        // 更新UI
        mTableView.commit();
    }

    // 构造UItableView的列表项，传入title和content
    private void generateCustomItem(UITableView tableView, String title, String content) {
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout relativeLayout = (RelativeLayout) mLayoutInflater.inflate(
                R.layout.table_view_custom_item, null);
        TextView titleText = (TextView) relativeLayout.getChildAt(0);
        titleText.setText(title);
        TextView contentText = (TextView) relativeLayout.getChildAt(1);
        contentText.setText(content);
        ViewItem v = new ViewItem(relativeLayout);
        v.setClickable(false);
        tableView.addViewItem(v);
    }
}
