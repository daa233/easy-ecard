package com.duang.easyecard.Activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.duang.easyecard.R;
import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.Utils.HttpUtil;
import com.duang.easyecard.Utils.LogUtil;
import com.duang.easyecard.Utils.HttpUtil.HttpCallbackListener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LostAndFoundRegistrationActivity extends BaseActivity {
	
	private TextView nameText;
	private TextView stuIdText;
	private TextView accountText;
	private EditText contactEditText;
	private EditText lostPlaceEditText;
	private EditText descriptionEditText;
	private Button saveAndSubmitButton;
	
	private final int GET_SUCCESS_RESPONSE = 200;
	private final int FINISH_PARSING_GET_RESPONSE = 201;
	private final int POST_SUCCESS_RESPONSE = 300;
	private final int NETWORK_ERROR = 404;
	private final int FOUND_CARD_POST_SUCCESS_RESPONSE = 301;
	
	private int CARD_LOSS_FLAG = 0;
	
	private HttpClient httpClient;
	private String responseString;
	private List<String> parsingData = null;
	private String name;
	private String stuId;
	private String account;
	private String contact;
	private String lostPlace;
	private String description;
	
	private String LostInfoId = "";  // 失卡招领信息ID
	private String traverseLostInfoResponseString;  // 遍历返回的数据
	private int pageIndex = 1;  // 访问的网页信息的页码
	private int maxPageIndex = 1;  // 最大页码，默认为1
	private final int TRAVERSE_GET_SUCCESS_RESPONSE = 800;
	private final int TRAVERSE_NEED_MORE_DATA = 801;
	private final int TRAVERSE_ALL_DATA_GOT = 805;
	private final int TRAVERSE_FIND_ID = 806;
	private int TRAVERSE_FIRST_JSOUP_FLAG = 1;  // 遍历首次解析标志
	private int TRAVERSE_FOUND_LOST_INFO_ID_FLAG = 0;  // 找到ID标志
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lost_and_found_registration);
		// 显示返回按钮
		getActionBar().setDisplayHomeAsUpEnabled(true);
		initView();
		initData();
	}
	
	private void initView() {
		// 实例化控件
		nameText = (TextView) findViewById(R.id.lost_info_registration_name);
		stuIdText = (TextView) findViewById(
				R.id.lost_info_registration_stu_id);
		accountText = (TextView) findViewById(
				R.id.lost_info_registration_account);
		contactEditText = (EditText) findViewById(
				R.id.lost_info_registration_contact);
		lostPlaceEditText = (EditText) findViewById(
				R.id.lost_info_registration_lost_place);
		descriptionEditText = (EditText) findViewById(
				R.id.lost_info_registration_description);
		saveAndSubmitButton = (Button) findViewById(
				R.id.lost_info_registration_btn);
		// 保存并提交按钮的点击事件
		saveAndSubmitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 先判断当前状态，是否已经登记丢失
				if (CARD_LOSS_FLAG == 0) {
					// 没有登记丢失，从布局获取相应数据
					name = nameText.getText().toString();
					stuId = stuIdText.getText().toString();
					account = accountText.getText().toString();
					contact = contactEditText.getText().toString();
					lostPlace = lostPlaceEditText.getText().toString();
					description = descriptionEditText.getText().toString();
					if (contact.length() < 7) {
						// 联系方式为空
						Toast.makeText(LostAndFoundRegistrationActivity.this,
								"联系方式不能少于7位",
								Toast.LENGTH_LONG).show();
					} else {
						// 弹出对话框确定
						AlertDialog.Builder alertDialog =
								new AlertDialog.Builder(
								LostAndFoundRegistrationActivity.this);
						alertDialog.setMessage("提交后无法更改，是否确定保存并提交？");
						alertDialog.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 点击确定按钮，发送POST请求
								sendPOSTRequest();
							}
						});
						alertDialog.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {}
						});
						alertDialog.show();
					}
				} else if (CARD_LOSS_FLAG == 1) {
					// 已经登记丢失，点击招领。通过遍历失卡招领信息获得ID
					traverseLostInfoBrowsingToGetId();
				} else {
					LogUtil.e("LostAndFoundRegistrationActivity",
							"Unknown Error.");
				}
			}
		});
	}
	// 初始化数据
	private void initData() {
		// 获得全局变量httpClient
		MyApplication myApp = (MyApplication) getApplication();
		httpClient = myApp.getHttpClient();
		sendGETRequest();
	}
	
	// 处理各种Message请求
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_SUCCESS_RESPONSE:
				// 已成功得到响应数据responseString
				new JsoupGetResponseData().execute();
				break;
			case FINISH_PARSING_GET_RESPONSE:
				// 先判断当前状态，是否已经登记丢失
				if (CARD_LOSS_FLAG == 0) {
					// 没有登记丢失，将解析好的数据填充到布局
					nameText.setText(parsingData.get(2));
					stuIdText.setText(parsingData.get(3));
					accountText.setText(parsingData.get(1));
					saveAndSubmitButton.setText("保 存  并  提  交");
				} else if (CARD_LOSS_FLAG == 1) {
					// 已经登记丢失，将解析好的数据填充到布局
					nameText.setText(parsingData.get(2));
					stuIdText.setText(parsingData.get(3));
					accountText.setText(parsingData.get(1));
					contactEditText.setText(parsingData.get(4));
					contactEditText.setFocusable(false);
					lostPlaceEditText.setText(parsingData.get(5));
					lostPlaceEditText.setFocusable(false);
					descriptionEditText.setText(parsingData.get(6));
					descriptionEditText.setFocusable(false);
					saveAndSubmitButton.setText("卡已找到？--标记为已招领");
				} else {
					LogUtil.e("LostAndFoundRegistrationActivity",
							"Unknown Error.");
				}
				break;
			case POST_SUCCESS_RESPONSE:
				// 登记丢失成功
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						LostAndFoundRegistrationActivity.this);
				alertDialog.setMessage(responseString);
				alertDialog.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 点击确定按钮，通过再次发送GET请求刷新布局
						sendGETRequest();
						contactEditText.setText("");
						contactEditText.setFocusable(true);
						lostPlaceEditText.setText("");
						lostPlaceEditText.setFocusable(true);
						descriptionEditText.setText("");
						descriptionEditText.setFocusable(true);
					}
				});
				alertDialog.show();
				break;
			case TRAVERSE_GET_SUCCESS_RESPONSE:
				// 遍历时GET请求成功获取响应数据
				new JsoupTraverseLostInfoData().execute();
				break;
			case TRAVERSE_FIND_ID:
				// 已找到ID，发送新的POST请求，标记为已招领
				LogUtil.d("LostInfoId", LostInfoId);
				sendFoundPOSTRequest();
				break;
			case TRAVERSE_NEED_MORE_DATA:
				// 暂时未找到ID，需要遍历更多数据
				pageIndex++;
				sendTraverseLostInfoGETRequest();
				break;
			case TRAVERSE_ALL_DATA_GOT:
				// 错误，已遍历所有数据，仍未找到ID
				Toast.makeText(LostAndFoundRegistrationActivity.this,
						"未知错误，操作失败",
						Toast.LENGTH_SHORT).show();
				break;
			case FOUND_CARD_POST_SUCCESS_RESPONSE:
				// 卡已找到，并已标记为“已招领
				String displayMsg;
				if (responseString.contains("True")) {
					displayMsg = "操作成功";
					AlertDialog.Builder foundAlertDialog =
							new AlertDialog.Builder(
							LostAndFoundRegistrationActivity.this);
					foundAlertDialog.setMessage(displayMsg);
					foundAlertDialog.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// 点击确定按钮，通过再次发送GET请求刷新布局
							CARD_LOSS_FLAG = 0;
							sendGETRequest();
							
						}
					});
					foundAlertDialog.show();
				} else {
					displayMsg = "操作失败";
					AlertDialog.Builder foundAlertDialog =
							new AlertDialog.Builder(
							LostAndFoundRegistrationActivity.this);
					foundAlertDialog.setMessage(displayMsg);
					foundAlertDialog.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {}
					});
					foundAlertDialog.show();
				}
				break;
			case NETWORK_ERROR:
				// 网络错误
				Toast.makeText(LostAndFoundRegistrationActivity.this,
						"网络错误",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};
	// 发送GET请求
	private void sendGETRequest() {
		HttpUtil.sendGetRequest(httpClient,
				UrlConstant.CARD_LOSS_REGISTRATION_GET,
				new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				// 成功响应
				responseString = response;
				// 发送消息到线程，已得到响应数据responseString
				Message message = new Message();
				message.what = GET_SUCCESS_RESPONSE;
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
	
	// 发送POST请求
	private void sendPOSTRequest() {
		// 装填POST数据
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("Address", lostPlace));
		params.add(new BasicNameValuePair("CardNo", account));
		params.add(new BasicNameValuePair("Name", name));
		params.add(new BasicNameValuePair("Note", description));
		params.add(new BasicNameValuePair("Phone", contact));
		params.add(new BasicNameValuePair("Sno", stuId));
		params.add(new BasicNameValuePair("Status", "1"));
		HttpUtil.sendPostRequest(httpClient,
				UrlConstant.CARD_LOSS_REGISTRATION_POST,
				params,
				new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				// 成功响应
				responseString = response;
				Message message = new Message();
				message.what = POST_SUCCESS_RESPONSE;
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
	
	// 解析响应数据
	private class JsoupGetResponseData extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			// 解析返回的responseString
			Document doc = null;
			try {
				doc = Jsoup.parse(responseString);
				parsingData = new ArrayList<String>();
				for (Element span : doc.select("span")) {
					if (span.text().contains("挂失")) {
						// 已经登记丢失
						CARD_LOSS_FLAG = 1;
						break;
					} else {
						parsingData.add(span.text());
					}
				}
				if (CARD_LOSS_FLAG == 1) {
					// 解析已经登记丢失界面的数据
					parsingData.clear();
					for (Element p : doc.select("p")) {
						parsingData.add(p.ownText());
					}
				}
				// 发送解析成功地消息
				Message message = new Message();
				message.what = FINISH_PARSING_GET_RESPONSE;
				handler.sendMessage(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	// 遍历失卡招领信息以获取当前用户丢失卡事件ID
	private void traverseLostInfoBrowsingToGetId() {
		sendTraverseLostInfoGETRequest();
	}
	// 向失卡招领信息网页发送GET请求
	private void sendTraverseLostInfoGETRequest() {
		UrlConstant.cardLossPageIndex = pageIndex;  // 组装Url
		HttpUtil.sendGetRequest(httpClient,
				UrlConstant.getCardLossInfoBrowsing(),
				new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				// 成功响应
				traverseLostInfoResponseString = response;
				// 发送消息到线程，已得到响应数据responseString
				Message message = new Message();
				message.what = TRAVERSE_GET_SUCCESS_RESPONSE;
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
	// 解析失卡招领信息网页返回的响应数据
	private class JsoupTraverseLostInfoData extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			// 解析返回的responseString
			Document doc = null;
			try {
				doc = Jsoup.parse(traverseLostInfoResponseString);
				// 获取总页数，当前累计丢失信息条数，已招领条数
				if (TRAVERSE_FIRST_JSOUP_FLAG == 1) {
					// 首次解析时得到最大页码，避免maxPageIndex在解析到最后一页时减小
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
						LogUtil.d("JsoupHtmlData  maxPageIndex",
								maxPageIndex + "");
					} else {
						// remainString为空, maxIndex值保持不变
						LogUtil.d("JsoupHtmlData  maxPageIndex",
								maxPageIndex + "");
					}
				}
				// 判断字段中是否有含有待招领的ID
				for (Element a : doc.select("a")) {
					if (a.text().contains("招领")) {
						// 获取到了含有“招领”字样的a标签
						TRAVERSE_FOUND_LOST_INFO_ID_FLAG = 1;
						LostInfoId = a.toString().substring(
								a.toString().indexOf("(") + 1);
						LostInfoId = LostInfoId.substring(
								0, LostInfoId.indexOf(")"));
						LogUtil.d("JsoupTraverseLostInfoData", a.toString());
						break;
					}
				}
				if (TRAVERSE_FOUND_LOST_INFO_ID_FLAG == 1) {
					// 已经找到
					Message message = new Message();
					message.what = TRAVERSE_FIND_ID;
					handler.sendMessage(message);
				} else if (TRAVERSE_FOUND_LOST_INFO_ID_FLAG == 0) {
					// 判断是否还有信息
					Message message = new Message();
					if (pageIndex < maxPageIndex) {
						// 如果当前页码不是最大页码，发送请求，获取更多数据
						message.what = TRAVERSE_NEED_MORE_DATA;
						handler.sendMessage(message);
					} else {
						// 如果当前页码是最大页码，已获取到全部数据
						message.what = TRAVERSE_ALL_DATA_GOT;
						handler.sendMessage(message);
					}
				} else {
					LogUtil.d("JsoupTraverseLostInfoData", "Unknown Error");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	// 卡已找到，发送POST请求标记为“已招领”
	private void sendFoundPOSTRequest() {
		// 装填POST数据
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		HttpUtil.sendPostRequest(httpClient,
				UrlConstant.CARD_LOSS_PICK_UP_CARD + LostInfoId,
				params,
				new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				// 成功响应
				responseString = response;
				Message message = new Message();
				message.what = FOUND_CARD_POST_SUCCESS_RESPONSE;
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
	// 返回键的点击
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
