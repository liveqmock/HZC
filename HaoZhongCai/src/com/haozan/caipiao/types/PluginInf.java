package com.haozan.caipiao.types;

/**
 * 插件下载信息
 * 
 * @author peter_feng
 * @create-time 2012-8-29 上午10:21:44
 */
public class PluginInf {

    // 游戏名称
    private String gameName;
    // 插件描述
    private String gameDescription;
    // 插件下载地址
    private String gameDownloadUrl;
    // 插件包名
    private String gamePackageName;
    // 插件入口类名
    private String gameActivityName;
    // 插件版本号
    private String gameVersion;

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameDescription() {
        return gameDescription;
    }

    public void setGameDescription(String gameDescription) {
        this.gameDescription = gameDescription;
    }

    public String getGameDownloadUrl() {
        return gameDownloadUrl;
    }

    public void setGameDownloadUrl(String gameDownloadUrl) {
        this.gameDownloadUrl = gameDownloadUrl;
    }

    public String getGamePackageName() {
        return gamePackageName;
    }

    public void setGamePackageName(String gamePackageName) {
        this.gamePackageName = gamePackageName;
    }

    public String getGameActivityName() {
        return gameActivityName;
    }

    public void setGameActivityName(String gameActivityName) {
        this.gameActivityName = gameActivityName;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public void setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
    }
}
