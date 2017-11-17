package com.gohoc.xiupuling.ui.useraudit;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gohoc.xiupuling.R;
import com.gohoc.xiupuling.bean.VCodeBenan;
import com.gohoc.xiupuling.net.RxRetrofitClient;
import com.gohoc.xiupuling.ui.BasicActivity;
import com.gohoc.xiupuling.utils.Event;
import com.gohoc.xiupuling.utils.LogUtil;
import com.gohoc.xiupuling.utils.Utils;
import com.gohoc.xiupuling.utils.ViewUtils;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

public class AddauditUserActivity2 extends BasicActivity implements View.OnClickListener {

    @BindView(R.id.toolbar_left_title)
    TextView toolbarLeftTitle;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.register2_tips)
    TextView register2Tips;
    @BindView(R.id.register2_tips_ll)
    LinearLayout register2TipsLl;
    @BindView(R.id.register_phone_et)
    EditText registerPhoneEt;
    @BindView(R.id.sumbit_button_ll)
    LinearLayout sumbitButtonLl;

    private String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audituser_layout_2);
        ButterKnife.bind(this);
        toolbarTitle.setText("注册");
        setLisener();
        mobile = this.getIntent().getStringExtra("mobile");
        register2Tips.setText("短信验证码已经发送到手机"+ Utils.doMobile(mobile)+"，请查收！");
        Logger.e("Register2Activity...Register2Activity");
        setStatusColor(R.color.colorPrimary);

        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void RefreshAuditUserListEvent(Event.RefreshAuditUserEvent_2 message) {
        LogUtil.d("监管账户列表222222");
        finish();
    }

    private void setLisener() {
        toolbarLeftTitle.setOnClickListener(this);
        sumbitButtonLl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_left_title:
                this.finish();
                break;
            case R.id.sumbit_button_ll:
                verifysmscode();
        }
    }

    //验证验证码
    private void verifysmscode() {
        String smsCode = registerPhoneEt.getText().toString();
        if (TextUtils.isEmpty(smsCode)) {
            Utils.toast(AddauditUserActivity2.this, "请输入短信中的验证码");
            return;
        }
        RxRetrofitClient.getInstance(AddauditUserActivity2.this).verifysmscode(mobile, smsCode, new Observer<VCodeBenan>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Utils.toast(AddauditUserActivity2.this, "请检查网络是否正常");
                try {
                    throw e;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            @Override
            public void onNext(VCodeBenan vCodeBenan) {
                if (vCodeBenan.getCode() == 1) {
                    bindAuditUser();
                } else {
                    Utils.toast(AddauditUserActivity2.this, vCodeBenan.getMessage());
                }
            }
        });
    }

    //绑定用户
    private void bindAuditUser() {
        RxRetrofitClient.getInstance(AddauditUserActivity2.this).bindAuditUser(mobile, new Observer<VCodeBenan>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Utils.toast(AddauditUserActivity2.this, "请检查网络是否正常");
                try {
                    throw e;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            @Override
            public void onNext(VCodeBenan vCodeBenan) {
                if (vCodeBenan.getCode() == 1) {
                    startActivity(new Intent(AddauditUserActivity2.this, AddauditUserActivity3.class).putExtra("mobile", mobile));
                    finish();
                } else {
                    Utils.toast(AddauditUserActivity2.this, vCodeBenan.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (ViewUtils.isTouchedViewOutSideView(registerPhoneEt, event)) {
            showInput(false);
        }
        return super.onTouchEvent(event);

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
