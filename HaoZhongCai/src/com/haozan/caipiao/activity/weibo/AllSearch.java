package com.haozan.caipiao.activity.weibo;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.Login;

public class AllSearch
    extends BasicActivity {
    private final int SEARCH_MENU = 1;
    private String searchStr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
        handleSearchQuery(getIntent());
    }

    private void handleSearchQuery(Intent queryIntent) {
        final String queryAction = queryIntent.getAction();
        if (Intent.ACTION_SEARCH.equals(queryAction)) {
            onSearch(queryIntent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleSearchQuery(intent);
    }

    private void onSearch(Intent intent) {
        final String queryString = intent.getStringExtra(SearchManager.QUERY);
        searchStr = queryString;

        if (!checkLogin()) {
            lottery("搜索");
        }
        else {
            Intent intent2 = new Intent();
            Bundle b = new Bundle();
            b.putString("searchStr", searchStr);
            intent2.putExtras(b);
            intent2.setClass(AllSearch.this, WeiboSearch.class);
            AllSearch.this.startActivity(intent2);
            finish();
        }
    }

// @Override
// public boolean onSearchRequested() {
// Bundle appDataBundle = new Bundle();
// appDataBundle.putString("searchStr", searchStr);
// startSearch(null, true, appDataBundle, false);
// return true;
// }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, SEARCH_MENU, 0, "查询").setIcon(android.R.drawable.ic_menu_search);
        return result;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case SEARCH_MENU:
                onSearchRequested();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    // 判断是否登录
    private boolean checkLogin() {
        String userid = appState.getSessionid();
        if (userid == null) {
            return false;
        }
        else {
            return true;
        }
    }

    private void lottery(String flag) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("searchStr", searchStr);
        bundle.putString("forwardFlag", flag);
        bundle.putBoolean("ifStartSelf", false);
        intent.putExtras(bundle);
// intent.setClass(AllSearch.this, StartUp.class);
        intent.setClass(AllSearch.this, Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AllSearch.this.finish();
        }
        return super.onKeyDown(keyCode, event);

    }

}