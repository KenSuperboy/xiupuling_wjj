package com.gohoc.xiupuling.ui.terminal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.gohoc.xiupuling.R;
import com.gohoc.xiupuling.adapter.LocationSearchAdater;
import com.gohoc.xiupuling.adapter.OnItemClickLitener;
import com.gohoc.xiupuling.bean.MyBaiduLocation;
import com.gohoc.xiupuling.ui.BasicActivity;
import com.gohoc.xiupuling.utils.ACache;
import com.gohoc.xiupuling.utils.BDresult;
import com.gohoc.xiupuling.utils.BaiduLocation;
import com.gohoc.xiupuling.utils.ClearEditText;
import com.gohoc.xiupuling.utils.Utils;
import com.orhanobut.logger.Logger;
import com.scu.miomin.shswiperefresh.core.SHSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectLocationSearchActivity extends BasicActivity implements OnGetPoiSearchResultListener {

    @BindView(R.id.toolbar_left_title)
    TextView toolbarLeftTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.serach_ct)
    ClearEditText serachCt;
    @BindView(R.id.swipeRefreshLayout)
    SHSwipeRefreshLayout mSHSwipeRefreshLayout;
    private PoiSearch mPoiSearch;
    private OnGetPoiSearchResultListener poiListener;
    private BaiduLocation baiduLocation;
    private LocationSearchAdater adater;
    private List<PoiInfo> poiInfoList = new ArrayList<>();
    private ACache ac;
    private String key = "";
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location_search);
        ButterKnife.bind(this);
        setStatusColor(R.color.colorPrimary);
        serachCt.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        key = this.getIntent().getStringExtra("key");
        ac = ACache.get(SelectLocationSearchActivity.this);
        serachCt.setText(key);
        initList();
        initSearch();
        mCurrentPage = 0;
        serach();
        mSHSwipeRefreshLayout.setRefreshEnable(false);
        mSHSwipeRefreshLayout.setLoadmoreEnable(true);
        mSHSwipeRefreshLayout.setOnRefreshListener(new SHSwipeRefreshLayout.SHSOnRefreshListener() {


            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoading() {
                mCurrentPage++;
                serach();
            }

            @Override
            public void onRefreshPulStateChange(float v, int i) {

            }

            @Override
            public void onLoadmorePullStateChange(float v, int i) {

            }
        });


    }

    private void initSearch() {
        final MyBaiduLocation myBaiduLocation = (MyBaiduLocation) ACache.get(this).getAsObject("myBaiduLocation");
        if (myBaiduLocation != null) {
            city = myBaiduLocation.getCity();
        } else {
            baiduLocation = new BaiduLocation(getApplication());
            baiduLocation.setbDresult(new BDresult() {
                @Override
                public void onCallBack(BDLocation location) {
                    city = location.getCity();
                }
            }).initBaiduLocation();


        }

        mPoiSearch = PoiSearch.newInstance();

        mPoiSearch.setOnGetPoiSearchResultListener(this);

        serachCt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    switch (event.getAction()) {
                        case KeyEvent.ACTION_UP:
                            mCurrentPage = 0;
                            serach();
                            return true;
                        default:
                            return true;
                    }
                }
                return false;
            }
        });

    }

    // 当前页数
    private int mCurrentPage = 0;

    private void serach() {

        Logger.d(serachCt.getText() + "".trim());
        if ((serachCt.getText() + "".trim()).isEmpty())
            return;
//        if (!TextUtils.isEmpty(city)) {
        mPoiSearch.searchInCity((new PoiCitySearchOption()).city(city)
                .keyword(serachCt.getText() + "").pageNum(mCurrentPage).pageCapacity(20));
        Logger.e(city + "  ::  " + serachCt.getText() + "");
//        } else {
//            Utils.toast(SelectLocationSearchActivity.this, "定位中，或定位失败请稍后再尝试");
//        }
    }

    @OnClick(R.id.toolbar_left_title)
    public void onClick() {
        finish();
    }

    private void initList() {
        adater = new LocationSearchAdater(SelectLocationSearchActivity.this, poiInfoList);
        recyclerView.setAdapter(adater);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        adater.setOnItemClickLitener(new OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Logger.d(position);
                setResult(RESULT_OK, new Intent().putExtra("PoiResult", poiInfoList.get(position)).putExtra("serachKey", serachCt.getText() + ""));
                SelectLocationSearchActivity.this.finish();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPoiSearch.destroy();
    }

    @Override
    public void onGetPoiResult(PoiResult result) {
        mSHSwipeRefreshLayout.finishLoadmore();
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Utils.toast(SelectLocationSearchActivity.this, "没有搜索到结果");
            //详情检索失败
            // result.error请参考SearchResult.ERRORNO
            Logger.e(result.error.toString());
        } else {
            //检索成功
            if (null != result.getAllPoi() && result.getAllPoi().size() > 0) {
                //Toast.makeText(SelectLocationSearchActivity.this, "成功:", Toast.LENGTH_SHORT).show();
                //获取POI检索结果
                if (mCurrentPage == 0) {
                    poiInfoList.clear();
                    adater.setdatas(result.getAllPoi());
                } else {
                    poiInfoList.addAll(result.getAllPoi());
                    adater.addDatas(result.getAllPoi());
                }

            }
        }

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (ViewUtils.isTouchedViewOutSideView(serachCt, event)) {
//            showInput(false);
//        }
//        return super.onTouchEvent(event);
//
//    }
}

