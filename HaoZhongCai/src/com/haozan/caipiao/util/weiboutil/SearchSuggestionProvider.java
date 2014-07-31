package com.haozan.caipiao.util.weiboutil;

import android.content.SearchRecentSuggestionsProvider;

// 记录全局搜索关键字
public class SearchSuggestionProvider
    extends SearchRecentSuggestionsProvider {
    /**
     * Authority
     */
    public final static String AUTHORITY = "com.haozan.caipiao.util.weiboutil.SearchSuggestionProvider";
    /**
     * Mode
     */
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SearchSuggestionProvider() {
        super();
        setupSuggestions(AUTHORITY, MODE);
    }
}
