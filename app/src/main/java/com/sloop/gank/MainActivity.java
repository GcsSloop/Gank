package com.sloop.gank;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.sloop.gank.adapter.GankCommonAdapter;
import com.sloop.gank.bean.CommonDate;
import com.sloop.gank.callback.ICallBack;
import com.sloop.gank.constant.Constants;
import com.sloop.gank.log.L;
import com.sloop.gank.net.GankApi;
import com.sloop.gank.refresh.RefreshLayout;
import com.sloop.net.utils.NetUtils;
import com.sloop.utils.ToastUtils;

import org.afinal.simplecache.ACache;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, RefreshLayout.OnLoadListener, AdapterView.OnItemClickListener, View.OnClickListener {
    private Toolbar mToolbar;

    private String[] flags = {Constants.FLAG_All, Constants.FLAG_Meizi, Constants.FLAG_Android, Constants.FLAG_iOS,
            Constants.FLAG_JS, Constants.FLAG_Recommend, Constants.FLAG_Video, Constants.FLAG_Expand};
    private String currentFlag = flags[0];
    private int currentIndex = 1;

    private ACache mCache;
    private ListView mListView;
    private GankCommonAdapter mAdapter;
    private RefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCache = ACache.get(getApplicationContext());
        initViews();
        startRefresh();
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView= navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);

        headerView.findViewById(R.id.head_img).setOnClickListener(this);
        headerView.findViewById(R.id.head_web).setOnClickListener(this);
        headerView.findViewById(R.id.head_name).setOnClickListener(this);

        mRefreshLayout = (RefreshLayout) findViewById(R.id.id_swipe_ly);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadListener(this);

        mAdapter = new GankCommonAdapter(MainActivity.this,null);
        mListView = (ListView) findViewById(R.id.id_listview);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, false));
    }


    private ICallBack<CommonDate> mCallBack = new ICallBack<CommonDate>() {
        @Override
        public void onSuccess(String flag, String key, CommonDate commonDate) {
            L.e("onSuccess");
            stopAllState();

            if (flag != currentFlag) return;

            if (commonDate.isError()){
                ToastUtils.show(MainActivity.this, "数据加载出错");
                return;
            }

            mCache.put(key,commonDate,ACache.TIME_DAY*7);
            L.e("cache key="+key);

            List<CommonDate.ResultsEntity> datas = commonDate.getResults();
            mAdapter.addDatas(datas);
        }

        @Override
        public void onFailure(String flag, String key, String why) {
            L.e("onFailure："+why);
            stopAllState();
            ToastUtils.show(MainActivity.this, why);
            getDataFromCache(key);
        }
    };

    private void stopAllState() {
        mRefreshLayout.setLoading(false);
        mRefreshLayout.setRefreshing(false);
    }

    private void resetAllByFlag(String flag){
        // set Title
        mToolbar.setTitle(flag);
        if (flag.equals(Constants.FLAG_All))
            mToolbar.setTitle(R.string.app_name);

        // clear data
        currentFlag = flag;
        currentIndex = 1;
        mAdapter.clearDatas();

        // reset listview
        if (android.os.Build.VERSION.SDK_INT >= 8) {
            mListView.smoothScrollToPosition(0);
        } else {
            mListView.setSelection(0);
        }

        // get new data
        startRefresh();
    }

    private void startRefresh() {
        mRefreshLayout.post(new Runnable(){
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
            }
        });
        onRefresh();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        item.setChecked(true);

        if (id == R.id.nav_main) {
            resetAllByFlag(flags[0]);
        }else if (id == R.id.nav_welfare) {
            resetAllByFlag(flags[1]);
        } else if (id == R.id.nav_android) {
            resetAllByFlag(flags[2]);
        } else if (id == R.id.nav_ios) {
            resetAllByFlag(flags[3]);
        } else if (id == R.id.nav_js) {
            resetAllByFlag(flags[4]);
        } else if (id == R.id.nav_recommend) {
            resetAllByFlag(flags[5]);
        } else if (id == R.id.nav_video) {
            resetAllByFlag(flags[6]);
        } else if (id == R.id.nav_expand) {
            resetAllByFlag(flags[7]);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRefresh() {
        L.e("onRefresh");
        Toast.makeText(this,"Refresh",Toast.LENGTH_LONG);
        currentIndex = 1;
        mAdapter.clearDatas();
        getData();
    }

    @Override
    public void onLoad() {
        L.e("onLoad");
        Toast.makeText(this,"LoadMore",Toast.LENGTH_LONG);
        currentIndex++;
        getData();
    }

    private void getData() {
        try {
            String key = currentFlag+20+currentIndex;
            if (NetUtils.isNetConnection(this)) {
                GankApi.getCommonData(currentFlag, 20, currentIndex, mCallBack);
            }else {
                ToastUtils.show(MainActivity.this, "网络连接异常，请检查网络！");
                getDataFromCache(key);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getDataFromCache(String key) {
        L.e("get data key="+key);
        CommonDate data = (CommonDate) mCache.getAsObject(key);
        if (data!=null)
            mAdapter.addDatas(data.getResults());
        stopAllState();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CommonDate.ResultsEntity data = mAdapter.getDataById(position);
        if (data.getType().equals(Constants.FLAG_Meizi)){
            Intent intent = new Intent(this, ImageActivity.class);
            intent.putExtra(Constants.key_imgurl, data.getUrl());
            startActivity(intent);
        }else {
            showByUrl(data.getUrl());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.head_img:
                showByUrl(Constants.URL_GANK);
                break;
            case R.id.head_name:
            case R.id.head_web:
                showByUrl(Constants.URL_Github);
                break;
        }
    }

    private void showByUrl(String url) {
        Intent ie = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(ie);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopAllState();
    }
}