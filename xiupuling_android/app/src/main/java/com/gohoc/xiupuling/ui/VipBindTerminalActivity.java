package com.gohoc.xiupuling.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gohoc.xiupuling.R;
import com.gohoc.xiupuling.adapter.VipBindTerminalAdapter;
import com.gohoc.xiupuling.bean.VCodeBenan;
import com.gohoc.xiupuling.bean.VipBindTerminalBean;
import com.gohoc.xiupuling.callback.Callback2;
import com.gohoc.xiupuling.net.RxRetrofitClient;
import com.gohoc.xiupuling.utils.LogUtil;
import com.gohoc.xiupuling.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;

/*
* 用vip码去绑定终端
* */
public class VipBindTerminalActivity extends BasicActivity {

    @BindView(R.id.tv_cancel)
    TextView mTvCancel;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.toolbar_right)
    TextView mToolbarRight;
    @BindView(R.id.listView)
    ListView mListView;

    private String terminal_id,vip_code,terminal_no,shop_name;
    private VipBindTerminalAdapter mVipBindTerminalAdapter;
    private List<VipBindTerminalBean.DataBean> beanList_unbind = new ArrayList<VipBindTerminalBean.DataBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_list_layout);
        ButterKnife.bind(this);
        setStatusColor(R.color.colorPrimary);
        initView();
        initListener();
        initDatas();
    }

    private void initView() {
        mToolbarTitle.setText("选择终端");

        mVipBindTerminalAdapter=new VipBindTerminalAdapter(mContext);
        mVipBindTerminalAdapter.setCallback(new Callback2() {
            @Override
            public void callBack(String string1, String string2) {
                LogUtil.d("位置："+string1+"=====编码:"+string2);
                if(!TextUtils.isEmpty(string1)&&!TextUtils.isEmpty(string2)){
                    terminal_id=string2;
                    terminal_no=beanList_unbind.get(Integer.parseInt(string1)).terminal_no;
                    shop_name=beanList_unbind.get(Integer.parseInt(string1)).shop_name;
                    for (int i=0;i<beanList_unbind.size();i++){
                        if(Integer.parseInt(string1)==i){
                            beanList_unbind.get(i).flag=1;
                        }else {
                            beanList_unbind.get(i).flag=0;
                        }
                    }
                    mVipBindTerminalAdapter.setmLists(beanList_unbind);
                }
            }
        });
        mListView.setAdapter(mVipBindTerminalAdapter);
    }

    private void initListener() {

    }

    private void initDatas() {
        vip_code=getIntent().getStringExtra("code");
        getViplList();
    }

    //获取没有绑定的终端列表
    private void getViplList() {
        RxRetrofitClient.getInstance(VipBindTerminalActivity.this).getAllltermList("0",new Observer<VipBindTerminalBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Utils.toast(VipBindTerminalActivity.this, "请检查网络是否正常");
                try {
                    throw e;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            @Override
            public void onNext(VipBindTerminalBean vipBindTerminalBean) {
                LogUtil.d("vip列表记载成功");
                if (vipBindTerminalBean!=null&&vipBindTerminalBean.code == 1 && vipBindTerminalBean.data != null && vipBindTerminalBean.data.size() > 0) {
                    beanList_unbind=vipBindTerminalBean.data;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick({R.id.tv_cancel, R.id.toolbar_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.toolbar_right:
                if(mVipBindTerminalAdapter.isSelect()){
                    vipbindterminal();
                }else {
                    Toast.makeText(mContext,"请选择终端",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void vipbindterminal() {
        RxRetrofitClient.getInstance(VipBindTerminalActivity.this).bindVip(terminal_id,vip_code,new Observer<VCodeBenan>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Utils.toast(VipBindTerminalActivity.this, "请检查网络是否正常");
                try {
                    throw e;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            @Override
            public void onNext(VCodeBenan vCodeBenan) {
                LogUtil.d("绑定成功");
                if(vCodeBenan.getCode()==1){
                    setResult(RESULT_OK,new Intent().putExtra("shop_name",shop_name).putExtra("terminal_no",terminal_no));
                    finish();
                }else {
                    Utils.toast(VipBindTerminalActivity.this, vCodeBenan.getMessage());
                }
            }
        });
    }
}
