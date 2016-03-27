package com.duang.easyecard.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;

/**
 * Created by MrD on 2016/3/27.
 */
public class MessagesCommonProblemsFragment extends Fragment {

    private View viewFragment;  // 缓存Fragment的View
    private TextView textView;

    private GetSelectedTabListener getSelectedTabListener;

    private int position;
    private final String TAG = "MessagesCommonProblemsFragment";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof GetSelectedTabListener)) {
            throw new IllegalStateException("The host activity must implement the" +
                    " GetSelectedTabListener callback.");
        }
        // 把绑定的activity当成callback对象
        getSelectedTabListener = (GetSelectedTabListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreateView");
        if (viewFragment == null) {
            viewFragment = inflater.inflate(R.layout.fragment_messages_common_problems,
                    container, false);
        }
        // 缓存的rootView需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误
        ViewGroup parent = (ViewGroup) viewFragment.getParent();
        if (parent != null) {
            parent.removeView(viewFragment);
        }
        if (getArguments().containsKey("POSITION")) {
            position = getArguments().getInt("POSITION");
        }
        return viewFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.d(TAG, "onActivityCreated");
        initView();
    }

    private void initView() {
        // 实例化控件
        textView = (TextView) viewFragment.findViewById(R.id.messages_common_problems_text_view);
        textView.setText("第" + position + "个Fragment");
    }

    // 在Activity中实现此接口，获取当前Tab的位置
    public interface GetSelectedTabListener {
        int getSelectedTabPosition();
    }
}
