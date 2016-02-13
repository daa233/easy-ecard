package com.duang.easyecard.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.HttpUtil;
import com.duang.easyecard.Util.LogUtil;
import com.rey.material.widget.ProgressView;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;

public class ManageBasicInfoActivity extends BaseActivity {

    private String name;  // 姓名
    private String stuId;  // 学工号
    private String ecardId;  // 校园卡号
    private String balance;  // 校园卡余额
    private String transition;  // 过渡余额
    private String reportLossState;  // 挂失状态
    private String freezeState;  // 冻结状态

    private String responseHtml;

    private List<String> stringList;
    private HttpClient httpClient;

    private static final int RESPONSE_SUCCESS = 1;
    private static final int FINISH_STRING_LIST = 2;
    private static final int NETWORK_ERROR = 0x404;

	private UITableView tableView;
    private ProgressDialog mProgressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_basic_info);
        // 显示返回按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 绑定控件
		tableView = (UITableView) findViewById(R.id.manage_basic_info_table_view);

        // Create a progressDialog
        mProgressDialog = new ProgressDialog(ManageBasicInfoActivity.this);
        // Set progressDialog message
        mProgressDialog.setMessage("正在努力加载并解析... o(>﹏<)o");
        mProgressDialog.setIndeterminate(false);
        // Show progressDialog
        mProgressDialog.show();

        initData();  // 初始化数据
	}

    // 初始化数据
    private void initData() {
        // 获得全局变量httpClient
        /*
        MyApplication myApp = (MyApplication) getApplication();
        httpClient = myApp.getHttpClient();
        sendPOSTRequest();  // 发送POST请求
        */
    }

    // 处理从线程中传递出来的消息
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RESPONSE_SUCCESS:
                    // 确保responseHtml已成功赋值后解析
                    new JsoupHtmlData().execute();
                    break;
                case FINISH_STRING_LIST:
                    // 将数据填充到布局
                    createList();  // 组建列表布局
                    LogUtil.d("ManageBasicInfoActivity", "total items: " + tableView.getCount());
                    tableView.commit();  // 显示列表布局
                    break;
                case NETWORK_ERROR:
                    // 网络错误
                    Toast.makeText(ManageBasicInfoActivity.this, "网络错误",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    // 发送POST请求
    private void sendPOSTRequest() {
        // 装填POST数据
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("needHeader", "false"));
        HttpUtil.sendPostRequest(httpClient, UrlConstant.BASIC_INFO, params,
                new HttpUtil.HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        // 成功响应
                        responseHtml = response;
                        Message message = new Message();
                        message.what = RESPONSE_SUCCESS;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onError(Exception e) {
                        // 网络错误
                        Message message = new Message();
                        message.what = NETWORK_ERROR;
                        handler.sendMessage(message);
                    }
                });
    }

    // 组建列表布局
	private void createList() {
		CustomClickListener listener = new CustomClickListener();
		tableView.setClickListener(listener);
        generateCustomItem(tableView, "姓名", name);
        generateCustomItem(tableView, "学工号", stuId);
        generateCustomItem(tableView, "校园卡号", ecardId);
        generateCustomItem(tableView, "校园卡余额", balance);
        generateCustomItem(tableView, "过渡余额", transition);
        generateCustomItem(tableView, "挂失状态", reportLossState);
        generateCustomItem(tableView, "冻结状态", freezeState);
	}

    // 监听列表项点击事件
	private class CustomClickListener implements UITableView.ClickListener {
		@Override
		public void onClick(int index) {
			Toast.makeText(ManageBasicInfoActivity.this, "item clicked: " + index,
                    Toast.LENGTH_SHORT).show();
		}
	}

    // 通过网站返回的html文本解析数据
    private class JsoupHtmlData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            LogUtil.d("JsouphtmlData", "onPreExecute");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            LogUtil.d("JsouphtmlData", "doInBackground.");
            // 解析返回的responseHtml
            Document doc = null;
            try {
                stringList = new ArrayList<String>();
                doc = Jsoup.parse(responseHtml);
                Elements es = doc.getElementsByTag("em");
                for (Element e : es) {
                    stringList.add(e.text());
                    LogUtil.d("e", e.text());
                }
                // 从List获取数据，并匹配相关变量
                getDataFromList();
                Message message = new Message();
                message.what = FINISH_STRING_LIST;
                handler.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            LogUtil.d("JsouphtmlData", "onPostExecute");
            // Close the progressDialog
            // mProgressDialog.dismiss();
        }
    }

    // 从List获取数据，并匹配相关变量
    private void getDataFromList() {
        if (!stringList.isEmpty()) {
            // 将stringList的数据与变量对应
            name = stringList.get(0);
            stuId = stringList.get(1);
            ecardId = stringList.get(2);
            balance = stringList.get(3);
            transition = stringList.get(4);
            reportLossState = stringList.get(5);
            freezeState = stringList.get(6);
        } else {
            Toast.makeText(this, "数据获取失败！", Toast.LENGTH_SHORT).show();
        }
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
    // 菜单项选择
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return false;
    }
}
