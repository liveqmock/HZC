package com.haozan.caipiao.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.GameDownloadInf;
import com.haozan.caipiao.types.PluginInf;

public class JsonUtil {
    /**
     * 获取服务器返回的游戏列表
     * 
     * @param gameList 保存游戏列表
     * @param response_data 服务器返回的json格式游戏列表
     */
    public static void analyseGameListData(ArrayList<GameDownloadInf> gameList, String response_data) {
        try {
            gameList.clear();
            JSONArray hallArray = new JSONArray(response_data);
            int length = hallArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject jo = hallArray.getJSONObject(i);
                String id = jo.getString("id");
//                if (Integer.parseInt(id) >= 2000) {
                    GameDownloadInf gameInf = new GameDownloadInf();
                    gameInf.setGameIndex(id);
                    gameInf.setGameName(jo.getString("name"));
                    gameInf.setGameDescription(jo.getString("description"));
                    gameInf.setGameDownloadUrl(jo.getString("url"));
                    gameInf.setGamePackageName(jo.getString("package"));
                    gameInf.setGameActivityName(jo.getString("class"));
                    gameInf.setGameVersion(jo.getString("version"));
                    // TODO
                    if (jo.has("icon")) {
                        gameInf.setGameIconUrl(jo.getString("icon"));
                    }
//                    gameInf.setGameIconUrl("http://lh3.ggpht.com/_GEnSvSHk4iE/TDSfmyCfn0I/AAAAAAAAF8Y/cqmhEoxbwys/s144-c/_MG_3675.jpg");
                    gameList.add(gameInf);
//                }
            }
        }
        catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 获取服务器返回的游戏列表，并保存指定彩种的游戏信息
     * 
     * @param gameList 保存游戏列表
     * @param response_data 服务器返回的json格式游戏列表
     * @param lotteryKind 指定的彩种
     */
    public static void analyseGameListData(ArrayList<GameDownloadInf> gameList, String response_data,
                                           String lotteryKind) {
        try {
            gameList.clear();
            JSONArray hallArray = new JSONArray(response_data);
            int length = hallArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject jo = hallArray.getJSONObject(i);
                String id = jo.getString("id");
                if (Integer.parseInt(id) >= 2000) {
                    GameDownloadInf gameInf = new GameDownloadInf();
                    gameInf.setGameName(jo.getString("name"));
                    gameInf.setGameDescription(jo.getString("description"));
                    gameInf.setGameDownloadUrl(jo.getString("url"));
                    gameInf.setGamePackageName(jo.getString("package"));
                    gameInf.setGameActivityName(jo.getString("class"));
                    gameInf.setGameVersion(jo.getString("version"));
                    gameInf.setGameSupportKind(jo.getString("lottery"));
                    gameInf.setExistPosition(i);
                    if (gameInf.getGameSupportKind().contains(lotteryKind)) {
                        gameList.add(gameInf);
                    }
                }
            }
        }
        catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 获取服务器返回的插件信息
     * 
     * @param pluginInf 保存游戏信息
     * @param response_data 服务器返回的json格式游戏列表
     */
    public static void analysePluginData(PluginInf pluginInf, String json) {
        JsonAnalyse analyse = new JsonAnalyse();
        pluginInf.setGameName(analyse.getData(json, "name"));
        pluginInf.setGameDescription(analyse.getData(json, "description"));
        pluginInf.setGameDownloadUrl(analyse.getData(json, "url"));
        pluginInf.setGamePackageName(analyse.getData(json, "package"));
        pluginInf.setGameActivityName(analyse.getData(json, "class"));
        pluginInf.setGameVersion(analyse.getData(json, "version"));
    }
}
