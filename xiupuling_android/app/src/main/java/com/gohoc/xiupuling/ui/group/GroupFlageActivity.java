package com.gohoc.xiupuling.ui.group;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gohoc.xiupuling.R;
import com.gohoc.xiupuling.bean.GroupDetailsBean;
import com.gohoc.xiupuling.bean.VCodeBenan;
import com.gohoc.xiupuling.net.RxRetrofitClient;
import com.gohoc.xiupuling.ui.BasicActivity;
import com.gohoc.xiupuling.utils.ClearEditText;
import com.gohoc.xiupuling.utils.Event;
import com.gohoc.xiupuling.utils.Utils;
import com.gohoc.xiupuling.utils.ViewUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;

public class GroupFlageActivity extends BasicActivity {

    @BindView(R.id.toolbar_left_title)
    TextView toolbarLeftTitle;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar_right)
    TextView toolbarRight;
    @BindView(R.id.name_et)
    ClearEditText nameEt;
    private GroupDetailsBean groupDetailsBeans;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_flage);
        ButterKnife.bind(this);
        setStatusColor(R.color.colorPrimary);
        toolbarTitle.setText("群组标签");
        try {
            groupDetailsBeans = (GroupDetailsBean) getIntent().getExtras().get("groupDetailsBeans");
            nameEt.setText(groupDetailsBeans.getData().getUnion_brief_info()+"");
        } catch (Exception e) {

        }
    }

    @OnClick({R.id.toolbar_left_title, R.id.toolbar_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_left_title:
                GroupFlageActivity.this.finish();
                break;
            case R.id.toolbar_right:
                updateUnion(nameEt.getText()+"");
                break;
        }
    }

    private  void updateUnion(final String name)
    {
        RxRetrofitClient.getInstance(this).updateUnion(groupDetailsBeans.getData().getUnion_id()+"",
                groupDetailsBeans.getData().getUnion_name()+"",
                groupDetailsBeans.getData().getUnion_type()+"",
                name+"",
                groupDetailsBeans.getData().getUnion_portrait()+"", new Observer<VCodeBenan>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.toast(GroupFlageActivity.this,  "请检查网络是否正常");
                        try {
                            throw e;
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(VCodeBenan vCodeBenan) {
                        Utils.toast(GroupFlageActivity.this, vCodeBenan.getMessage());
                        if(vCodeBenan.getCode()==1)
                        {
                            groupDetailsBeans.getData().setUnion_brief_info(name);
                            EventBus.getDefault().post(new Event.GroupEvent(groupDetailsBeans));
                            GroupFlageActivity.this.finish();
                        }

                    }
                });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (ViewUtils.isTouchedViewOutSideView(nameEt, event)) {
            showInput(false);
        }
        return super.onTouchEvent(event);

    }
}
