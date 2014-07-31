package com.haozan.caipiao.types;

/**
 * 游戏基本信息类
 * 
 * @author peter_feng
 * @create-time 2012-8-11 下午04:19:50
 */
public class GameDownloadInf
    extends PluginInf {
    // 游戏标识
    private String gameIndex;
    // 游戏支持的彩种,如ssq,3d
    private String gameSupportKind;
    // 游戏图标下载url
    private String gameIconUrl = null;
    // 游戏状态 ：1，下载；2，启动；3，更新；
    private String status;
    // 投注游戏在游戏全部列表中的位置
    private int existPosition;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGameIconUrl() {
        return gameIconUrl;
    }

    public void setGameIconUrl(String gameIconUrl) {
        this.gameIconUrl = gameIconUrl;
    }

    public String getGameIndex() {
        return gameIndex;
    }

    public void setGameIndex(String gameIndex) {
        this.gameIndex = gameIndex;
    }

    public String getGameSupportKind() {
        return gameSupportKind;
    }

    public void setGameSupportKind(String gameSupportKind) {
        this.gameSupportKind = gameSupportKind;
    }

    public int getExistPosition() {
        return existPosition;
    }

    public void setExistPosition(int existPosition) {
        this.existPosition = existPosition;
    }
}
