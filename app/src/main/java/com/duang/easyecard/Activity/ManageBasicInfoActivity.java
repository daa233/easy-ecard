package com.duang.easyecard.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;
import cz.msebera.android.httpclient.Header;

public class ManageBasicInfoActivity extends BaseActivity {
    /*
    private String name;  // 姓名
    private String stuId;  // 学工号
    private String cardAccount;  // 校园卡号
    private String balance;  // 校园卡余额
    private String transition;  // 过渡余额
    private String reportLossState;  // 挂失状态
    private String freezeState;  // 冻结状态
    */
    private String response;
    private final String TAG = "ManageBasicInfoActivity";

    private List<String> titleList;
    private List<String> contentList;
    private AsyncHttpClient httpClient;

    private UITableView tableView;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_basic_info);
        // 显示返回按钮
        setupActionBar();
        // 绑定控件
        tableView = (UITableView) findViewById(R.id.manage_basic_info_table_view);

        // Create a progressDialog
        mProgressDialog = new ProgressDialog(ManageBasicInfoActivity.this);
        // Set progressDialog message
        mProgressDialog.setMessage(getResources().getString(R.string.loading) + "  o(>﹏<)o");
        mProgressDialog.setIndeterminate(false);
        // Show progressDialog
        mProgressDialog.show();

        initData();  // 初始化数据
    }

    // 初始化数据
    private void initData() {
        // 获得全局变量httpClient
        MyApplication myApp = (MyApplication) getApplication();
        httpClient = myApp.getHttpClient();
        sendGETRequest();  // 发送GET请求
    }

    // 发送GET请求
    private void sendGETRequest() {
        // 发送GET请求
        httpClient.get(UrlConstant.MOBILE_MANAGE_BASIC_INFO, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 成功响应
                response = new String(responseBody);
                // 解析response
                new JsoupHtmlData().execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                // 网络错误
                Toast.makeText(ManageBasicInfoActivity.this, R.string.network_error,
                        Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    // 组建列表布局
    private void createList() {
        /*
        generateCustomItem(tableView, getResources().getString(R.string.name), name);
        generateCustomItem(tableView, getResources().getString(R.string.stu_id), stuId);
        generateCustomItem(tableView, getResources().getString(R.string.card_account), cardAccount);
        generateCustomItem(tableView, getResources().getString(R.string.balance), balance);
        generateCustomItem(tableView, getResources().getString(R.string.transition), transition);
        generateCustomItem(tableView, getResources().getString(R.string.report_loss_state),
                reportLossState);
        generateCustomItem(tableView, getResources().getString(R.string.freeze_state), freezeState);
        */
        if (titleList.size() == contentList.size()) {
            for (int i = 0; i < titleList.size(); i++) {
                generateCustomItem(tableView, titleList.get(i), contentList.get(i));
            }
        } else {
            LogUtil.e(TAG, "titleList's size dosen't equal to contentList's size.");
        }
    }

    // 通过网站返回的html文本解析数据
    private class JsoupHtmlData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            // 解析返回的responseHtml
            Document doc;
            try {
                titleList = new ArrayList<>();
                contentList = new ArrayList<>();
                doc = Jsoup.parse(response);
                Elements titles = doc.getElementsByClass("first");
                for (Element title : titles) {
                    titleList.add(title.text());
                }
                Elements contents = doc.getElementsByClass("second");
                for (Element content : contents) {
                    contentList.add(content.text());
                }
                // 从List获取数据，并匹配相关变量
                // getDataFromList();
                // 组建列表布局
                createList();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // 在主线程中更新UI
            tableView.commit();
            // Close the progressDialog
            mProgressDialog.dismiss();
        }
    }
    /*
    // 从List获取数据，并匹配相关变量
    private void getDataFromList() {
        if (!stringList.isEmpty()) {
            // 将stringList的数据与变量对应
            name = stringList.get(0);
            stuId = stringList.get(1);
            cardAccount = stringList.get(2);
            balance = stringList.get(3);
            transition = stringList.get(4);
            reportLossState = stringList.get(5);
            freezeState = stringList.get(6);
        } else {
            Toast.makeText(this, R.string.fail_to_get_data, Toast.LENGTH_SHORT).show();
        }
    }
    */

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
