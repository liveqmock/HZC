package com.haozan.caipiao.activity.topup;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.Feedback;
import com.haozan.caipiao.activity.WeakReferenceHandler;
import com.haozan.caipiao.adapter.topup.BankListAdapter;
import com.haozan.caipiao.adapter.topup.BankListAdapter.BankSelectedListener;
import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.control.topup.SelectBankControlBasic;
import com.haozan.caipiao.control.topup.UnionPayCreditSelectBankControl;
import com.haozan.caipiao.control.topup.UnionPayDebitSelectBankControl;
import com.haozan.caipiao.types.topup.BankInfo;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.ViewUtil;

public class TopupSelectBank extends BasicActivity implements OnClickListener,
		BankSelectedListener {
	public static final int TOPUP_DEBIT_CARD = 0;
	public static final int TOPUP_CREDIT_CARD = 1;

	private static String JSON_BANKS = "[{\"f\":\"ahsncxys\",\"z\":\"安徽省农村信用社\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"asssyyh\",\"z\":\"安顺市商业银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"ayssyyh\",\"z\":\"安阳市商业银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"\"},{\"f\":\"ayyh\",\"z\":\"安阳银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"bsyh\",\"z\":\"包商银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"bdyh\",\"z\":\"保定银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"bjnsyh\",\"z\":\"北京农商银行\",\"k\":\"\",\"d\":\"\",\"c\":\"UP\"},{\"f\":\"bjyh\",\"z\":\"北京银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"bhyh\",\"z\":\"渤海银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"czyh\",\"z\":\"沧州银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"csnsyh\",\"z\":\"常熟农商银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"YY,UP\"},{\"f\":\"cyyh\",\"z\":\"朝阳银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"cdncsyyh\",\"z\":\"成都农村商业银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"AN,UP\"},{\"f\":\"cdsncxys\",\"z\":\"成都省农村信用社\",\"k\":\"\",\"d\":\"\",\"c\":\"YY\"},{\"f\":\"cdyh\",\"z\":\"成都银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"cdyh\",\"z\":\"承德银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"UP\"},{\"f\":\"dhyh\",\"z\":\"大华银行\",\"k\":\"dh\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"dlyh\",\"z\":\"大连银行\",\"k\":\"dh\",\"d\":\"UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"ddyh\",\"z\":\"丹东银行\",\"k\":\"dh\",\"d\":\"AN\",\"c\":\"\"},{\"f\":\"dyyh\",\"z\":\"德阳银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"dzyh\",\"z\":\"德州银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"\"},{\"f\":\"dgssyyh\",\"z\":\"东莞市商业银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"YY\"},{\"f\":\"dgyh\",\"z\":\"东莞银行\",\"k\":\"dh\",\"d\":\"\",\"c\":\"AN,UP\"},{\"f\":\"dyyh\",\"z\":\"东亚银行\",\"k\":\"\",\"d\":\"\",\"c\":\"YY,UP\"},{\"f\":\"dyssyyh\",\"z\":\"东营市商业银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"AN\"},{\"f\":\"dyyh\",\"z\":\"东营银行\",\"k\":\"dh\",\"d\":\"\",\"c\":\"UP\"},{\"f\":\"eedsyh\",\"z\":\"鄂尔多斯银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"YY,UP\"},{\"f\":\"fxyh\",\"z\":\"法兴银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"fjhxyh\",\"z\":\"福建海峡银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"fjncxys\",\"z\":\"福建农村信用社\",\"k\":\"\",\"d\":\"\",\"c\":\"YY,UP\"},{\"f\":\"fsyh\",\"z\":\"抚顺银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"fxyh\",\"z\":\"阜新银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"\"},{\"f\":\"fdyh\",\"z\":\"富滇银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"UP\"},{\"f\":\"gssncxyslhs\",\"z\":\"甘肃省农村信用社联合社\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"gzyh\",\"z\":\"赣州银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"AN,UP\"},{\"f\":\"gdyh\",\"z\":\"光大银行\",\"k\":\"\",\"d\":\"YY,AN,UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"gdhxyh\",\"z\":\"广东华兴银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"\"},{\"f\":\"gdncxys\",\"z\":\"广东农村信用社\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"gfyh\",\"z\":\"广发银行\",\"k\":\"\",\"d\":\"YY,AN\",\"c\":\"YY,AN,UP\"},{\"f\":\"gxbbwyh\",\"z\":\"广西北部湾银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"gxncxys\",\"z\":\"广西农村信用社\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"gzhdczczyh\",\"z\":\"广州花都稠州村镇银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"gznsyh\",\"z\":\"广州农商银行\",\"k\":\"\",\"d\":\"YY,AN,UP\",\"c\":\"AN,UP\"},{\"f\":\"gzsncxyhzs\",\"z\":\"广州市农村信用合作社\",\"k\":\"\",\"d\":\"\",\"c\":\"YY\"},{\"f\":\"gzyh\",\"z\":\"广州银行\",\"k\":\"\",\"d\":\"YY,AN,UP\",\"c\":\"YY,UP\"},{\"f\":\"gyyh\",\"z\":\"贵阳银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"YY,UP\"},{\"f\":\"glyh\",\"z\":\"桂林银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"hebyh\",\"z\":\"哈尔滨银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"YY,UP\"},{\"f\":\"hmssyyh\",\"z\":\"哈密市商业银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"hnsncxyslhs\",\"z\":\"海南省农村信用社联合社\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"hdyh\",\"z\":\"邯郸银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"hgzxqyyh\",\"z\":\"韩国中小企业银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"hyyh\",\"z\":\"韩亚银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"\"},{\"f\":\"hkyh\",\"z\":\"汉口银行\",\"k\":\"hk\",\"d\":\"AN\",\"c\":\"AN\"},{\"f\":\"hzyh\",\"z\":\"杭州银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"YY,AN,UP\"},{\"f\":\"hbsncxys\",\"z\":\"河北省农村信用社\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"hbyh\",\"z\":\"河北银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"hbyh\",\"z\":\"鹤壁银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"hfyh\",\"z\":\"恒丰银行\",\"k\":\"hh\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"hsssyyh\",\"z\":\"衡水市商业银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"hldyh\",\"z\":\"葫芦岛银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"hbsncxyslhs\",\"z\":\"湖北省农村信用社联合社\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"hbyh\",\"z\":\"湖北银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"hnsncxys\",\"z\":\"湖南省农村信用社\",\"k\":\"\",\"d\":\"UP\",\"c\":\"YY,UP\"},{\"f\":\"hqyh\",\"z\":\"花旗银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"UP\"},{\"f\":\"hrxjyh\",\"z\":\"华蓉湘江银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"UP\"},{\"f\":\"hxyh\",\"z\":\"华夏银行\",\"k\":\"hh\",\"d\":\"YY,AN,UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"hsyh\",\"z\":\"徽商银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"hfyh\",\"z\":\"汇丰银行\",\"k\":\"hh\",\"d\":\"AN\",\"c\":\"\"},{\"f\":\"jlsncxys\",\"z\":\"吉林省农村信用社\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"jlyh\",\"z\":\"吉林银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"jnyh\",\"z\":\"济宁银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"\"},{\"f\":\"jxyh\",\"z\":\"嘉兴银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"jnncsyyh\",\"z\":\"江南农村商业银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"jsjyncsyyh\",\"z\":\"江苏江阴农村商业银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"AN\"},{\"f\":\"jssncxyslhs\",\"z\":\"江苏省农村信用社联合社\",\"k\":\"\",\"d\":\"UP\",\"c\":\"UP\"},{\"f\":\"jsyh\",\"z\":\"江苏银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"jscjsyyh\",\"z\":\"江苏长江商业银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"jxncxylhs\",\"z\":\"江西农村信用联合社\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"jyncsyyh\",\"z\":\"江阴农村商业银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"YY,UP\"},{\"f\":\"jtyh\",\"z\":\"交通银行\",\"k\":\"jh\",\"d\":\"YY,AN,UP\",\"c\":\"YY,UP\"},{\"f\":\"jhyh\",\"z\":\"金华银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"AN,UP\"},{\"f\":\"jzyh\",\"z\":\"锦州银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"YY,AN,UP\"},{\"f\":\"jcyh\",\"z\":\"晋城银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"jsyh\",\"z\":\"晋商银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"jzssyyh\",\"z\":\"晋中市商业银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"jjyh\",\"z\":\"九江银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"YY,UP\"},{\"f\":\"kfssyyh\",\"z\":\"开封市商业银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"\"},{\"f\":\"kelssyyh\",\"z\":\"库尔勒市商业银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"klyh\",\"z\":\"昆仑银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"ksncsyyh\",\"z\":\"昆山农村商业银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"lsyh\",\"z\":\"莱商银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"\"},{\"f\":\"lzyh\",\"z\":\"兰州银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"lsssyyh\",\"z\":\"乐山市商业银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"lszsyyh\",\"z\":\"凉山州商业银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"lsyh\",\"z\":\"临商银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"lzyh\",\"z\":\"柳州银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"ljyh\",\"z\":\"龙江银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"YY,UP\"},{\"f\":\"lzssyyh\",\"z\":\"泸州市商业银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"thyh\",\"z\":\"漯河银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"mxkjczyh\",\"z\":\"梅县客家村镇银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"msyh\",\"z\":\"民生银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"ncyh\",\"z\":\"南昌银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"YY,UP\"},{\"f\":\"ncssyyh\",\"z\":\"南充市商业银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"njyh\",\"z\":\"南京银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"AN,UP\"},{\"f\":\"nyczyh\",\"z\":\"南阳村镇银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"\"},{\"f\":\"nmgncxys\",\"z\":\"内蒙古农村信用社\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"nmgyh\",\"z\":\"内蒙古银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"AN\"},{\"f\":\"nbdhyh\",\"z\":\"宁波东海银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"nbyh\",\"z\":\"宁波银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"YY,AN,UP\"},{\"f\":\"nxhhnsyh\",\"z\":\"宁夏黄河农商银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"nxyh\",\"z\":\"宁夏银行\",\"k\":\"\",\"d\":\"\",\"c\":\"YY,AN\"},{\"f\":\"pzhssyyh\",\"z\":\"攀枝花市商业银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"pjssyyh\",\"z\":\"盘锦市商业银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"payh\",\"z\":\"平安银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"pdsyh\",\"z\":\"平顶山银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"pfyh\",\"z\":\"浦发银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"YY,UP\"},{\"f\":\"qlyh\",\"z\":\"齐鲁银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"YY,UP\"},{\"f\":\"qhdyh\",\"z\":\"秦皇岛银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"qdyh\",\"z\":\"青岛银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"UP\"},{\"f\":\"qhsncxys\",\"z\":\"青海省农村信用社\",\"k\":\"\",\"d\":\"UP\",\"c\":\"UP\"},{\"f\":\"qhyh\",\"z\":\"青海银行\",\"k\":\"\",\"d\":\"\",\"c\":\"YY,AN,UP\"},{\"f\":\"qjssyyh\",\"z\":\"曲靖市商业银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"qzyh\",\"z\":\"泉州银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"rzyh\",\"z\":\"日照银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"\"},{\"f\":\"smxssyyh\",\"z\":\"三门峡市商业银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"\"},{\"f\":\"smxyh\",\"z\":\"三门峡银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"xmyh\",\"z\":\"厦门银行\",\"k\":\"xh\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"sdsncxys\",\"z\":\"山东省农村信用社\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"sxsncxyslhs\",\"z\":\"山西省农村信用社联合社\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"sxydncsyyh\",\"z\":\"山西尧都农村商业银行\",\"k\":\"\",\"d\":\"\",\"c\":\"UP\"},{\"f\":\"sqyh\",\"z\":\"商丘银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"shnsyh\",\"z\":\"上海农商银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"shyh\",\"z\":\"上海银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"sryh\",\"z\":\"上饶银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"szftyzczyh\",\"z\":\"深圳福田银座村镇银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"szlgdyczyh\",\"z\":\"深圳龙岗鼎业村镇银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"sznsyh\",\"z\":\"深圳农商银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"szssyyh\",\"z\":\"深圳市商业银行\",\"k\":\"\",\"d\":\"\",\"c\":\"YY\"},{\"f\":\"sjyh\",\"z\":\"盛京银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"AN\"},{\"f\":\"szsyh\",\"z\":\"石嘴山银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"sdnsyh\",\"z\":\"顺德农商银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"scsncxyhzs\",\"z\":\"四川省农村信用合作社\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"szyh\",\"z\":\"苏州银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"snssyyh\",\"z\":\"遂宁市商业银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"tzyh\",\"z\":\"台州银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"YY,UP\"},{\"f\":\"tcncsyyh\",\"z\":\"太仓农村商业银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"tassyyh\",\"z\":\"泰安市商业银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"tsssyyh\",\"z\":\"唐山市商业银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"tjbhncsyyh\",\"z\":\"天津滨海农村商业银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"tjnsyh\",\"z\":\"天津农商银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"UP\"},{\"f\":\"tjyh\",\"z\":\"天津银行\",\"k\":\"\",\"d\":\"\",\"c\":\"AN,UP\"},{\"f\":\"whssyyh\",\"z\":\"威海市商业银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"YY,UP\"},{\"f\":\"wfyh\",\"z\":\"潍坊银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"YY,UP\"},{\"f\":\"wzyh\",\"z\":\"温州银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"whyh\",\"z\":\"乌海银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"wlmqssyyh\",\"z\":\"乌鲁木齐市商业银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"YY,UP\"},{\"f\":\"wxncsyyh\",\"z\":\"无锡农村商业银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"YY,UP\"},{\"f\":\"wjncsyyh\",\"z\":\"吴江农村商业银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"xayh\",\"z\":\"西安银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"\"},{\"f\":\"xhyh\",\"z\":\"新韩银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"xjhhyh\",\"z\":\"新疆汇和银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"xxyh\",\"z\":\"新乡银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"\"},{\"f\":\"xyyh\",\"z\":\"信阳银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"\"},{\"f\":\"xzyh\",\"z\":\"星展银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"\"},{\"f\":\"xtyh\",\"z\":\"邢台银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"xyyh\",\"z\":\"兴业银行\",\"k\":\"xh\",\"d\":\"YY,UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"xcyh\",\"z\":\"许昌银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"yassyyh\",\"z\":\"雅安市商业银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"ytyh\",\"z\":\"烟台银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"yqssyyh\",\"z\":\"阳泉市商业银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"ydncsyyh\",\"z\":\"尧都农村商业银行\",\"k\":\"\",\"d\":\"\",\"c\":\"YY,AN\"},{\"f\":\"ybssyyh\",\"z\":\"宜宾市商业银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"ycssyyh\",\"z\":\"宜昌市商业银行\",\"k\":\"\",\"d\":\"\",\"c\":\"YY,UP\"},{\"f\":\"ycssyyh\",\"z\":\"银川市商业银行\",\"k\":\"\",\"d\":\"\",\"c\":\"UP\"},{\"f\":\"yzyh\",\"z\":\"鄞州银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"AN,UP\"},{\"f\":\"ykyh\",\"z\":\"营口银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"ydyh\",\"z\":\"永德银行\",\"k\":\"\",\"d\":\"\",\"c\":\"YY\"},{\"f\":\"yxssyyh\",\"z\":\"玉溪市商业银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"ynsncxys\",\"z\":\"云南省农村信用社\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"zdyh\",\"z\":\"渣打银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"zjgncsyyh\",\"z\":\"张家港农村商业银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"zjkssyyh\",\"z\":\"张家口市商业银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"cayh\",\"z\":\"长安银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"csyh\",\"z\":\"长沙银行\",\"k\":\"\",\"d\":\"\",\"c\":\"YY,AN,UP\"},{\"f\":\"czssyyh\",\"z\":\"长治市商业银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"zsyh\",\"z\":\"招商银行\",\"k\":\"zh\",\"d\":\"YY,AN,UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"zjczsyyh\",\"z\":\"浙江稠州商业银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"zjmtsyyh\",\"z\":\"浙江民泰商业银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"YY,UP\"},{\"f\":\"zjsncxys\",\"z\":\"浙江省农村信用社\",\"k\":\"\",\"d\":\"AN\",\"c\":\"\"},{\"f\":\"zjtlsyyh\",\"z\":\"浙江泰隆商业银行\",\"k\":\"\",\"d\":\"\",\"c\":\"YY,AN,UP\"},{\"f\":\"zzyh\",\"z\":\"郑州银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"YY\"},{\"f\":\"zzyxcxk\",\"z\":\"郑州银行储蓄卡\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"zggsyh\",\"z\":\"中国工商银行\",\"k\":\"gh\",\"d\":\"YY,AN,UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"zgjsyh\",\"z\":\"中国建设银行\",\"k\":\"jh\",\"d\":\"YY,AN,UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"zgnyyh\",\"z\":\"中国农业银行\",\"k\":\"nh\",\"d\":\"YY,AN,UP\",\"c\":\"AN,UP\"},{\"f\":\"zgyh\",\"z\":\"中国银行\",\"k\":\"zh\",\"d\":\"AN,UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"zgyzcxyh\",\"z\":\"中国邮政储蓄银行\",\"k\":\"yz\",\"d\":\"AN,UP\",\"c\":\"UP\"},{\"f\":\"zsxlczyh\",\"z\":\"中山小榄村镇银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"\"},{\"f\":\"ztyt\",\"z\":\"中铁银通\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"zxyh\",\"z\":\"中信银行\",\"k\":\"\",\"d\":\"YY,AN,UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"cqbbczczyh\",\"z\":\"重庆北碚稠州村镇银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"cqncsyczyh\",\"z\":\"重庆南川石银村镇银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"cqncsyyh\",\"z\":\"重庆农村商业银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"YY,AN,UP\"},{\"f\":\"cqsxyh\",\"z\":\"重庆三峡银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"cqyh\",\"z\":\"重庆银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"YY,AN,UP\"},{\"f\":\"zkyh\",\"z\":\"周口银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"zhhryh\",\"z\":\"珠海华润银行\",\"k\":\"\",\"d\":\"UP\",\"c\":\"\"},{\"f\":\"zmdyh\",\"z\":\"驻马店银行\",\"k\":\"\",\"d\":\"AN\",\"c\":\"\"},{\"f\":\"zgssyyh\",\"z\":\"自贡市商业银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"},{\"f\":\"zyssyyh\",\"z\":\"遵义市商业银行\",\"k\":\"\",\"d\":\"AN,UP\",\"c\":\"\"}]";
	public static final String[] HOT_BANK_NAMES = { "中国农业银行", "中国工商银行",
			"中国建设银行", "中国银行", "招商银行", "浦发银行", "交通银行", "中国邮政储蓄银行", "光大银行",
			"平安银行" };
	public static final int[] HOT_BANK_ICONS = { R.drawable.icon_bank_nongye,
			R.drawable.icon_bank_gongshang, R.drawable.icon_bank_jianshe,
			R.drawable.icon_bank_zhongguo, R.drawable.icon_bank_zhaoshang,
			R.drawable.icon_bank_pufa, R.drawable.icon_bank_jiaotong,
			R.drawable.icon_bank_youzheng, R.drawable.icon_bank_guangda,
			R.drawable.icon_bank_pingan };

	private BankListAdapter mBankListAdapter;
	private ListView mLvBank;

	private RelativeLayout mLayoutBack;
	private TextView mTvTitle;
	private EditText mEtBankName;

	private int mType;// 类型：信用卡，储蓄卡

	private static ArrayList<BankInfo> mBankList;// 显示的银行列表
	private ArrayList<BankInfo> mHotBankList;// 热门的银行列表
	private ArrayList<BankInfo> mDbBankList;// 数据库中的银行列表

	private TextWatcher textwahcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
		}

		@Override
		public void afterTextChanged(Editable arg0) {
			String searchContent = mEtBankName.getText().toString();

			if (TextUtils.isEmpty(searchContent)) {
				initBankListShow();
			} else {
				mControl.showSearchBank(searchContent, mBankList, mDbBankList);
			}

			mBankListAdapter.notifyDataSetChanged();
		}
	};

	private SelectBankControlBasic mControl;

	private MyHandler mHandler;

	private static class MyHandler extends
			WeakReferenceHandler<TopupSelectBank> {

		public MyHandler(TopupSelectBank reference) {
			super(reference);
		}

		@Override
		protected void handleMessage(TopupSelectBank activity, Message msg) {
			switch (msg.what) {
			case ControlMessage.SHOW_PROGRESS:
				activity.showProgress();
				break;

			case ControlMessage.DISMISS_PROGRESS:
				activity.dismissProgress();
				break;

			case ControlMessage.GET_BANK_LIST_VERSION_SUCCESS:
				String[] bankListInf = (String[]) msg.obj;
				activity.mControl.analyseBankListVersion(bankListInf[0],
						bankListInf[1], activity.mDbBankList);
				break;

			case ControlMessage.GET_BANK_LIST_VERSION_FAIL:
				ViewUtil.showTipsToast(activity, (String) msg.obj);
				break;

			case ControlMessage.GET_BANK_LIST_SUCCESS:
				if (activity.mDbBankList.size() == 0
						&& activity.mHotBankList.size() == 0) {
					activity.mControl.insertHotBank(activity.mDbBankList,
							activity.mHotBankList, HOT_BANK_NAMES,
							HOT_BANK_ICONS);
					activity.initBankListShow();
				}

				activity.mHandler
						.sendEmptyMessage(ControlMessage.DISMISS_PROGRESS);

//				activity.mControl.saveBankList(activity.mDbBankList);
				break;

			case ControlMessage.GET_BANK_LIST_FAIL:
				activity.mHandler
						.sendEmptyMessage(ControlMessage.DISMISS_PROGRESS);
				ViewUtil.showTipsToast(activity, (String) msg.obj);
				break;

			case ControlMessage.SAVE_BANK_LIST:
				activity.mHandler
						.sendEmptyMessage(ControlMessage.DISMISS_PROGRESS);
				activity.mBankListAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		}

	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		initData();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topup_select_bank);
		setupViews();
		init();
	}

	private void initData() {
		mHandler = new MyHandler(this);

		mBankList = new ArrayList<BankInfo>();
		mHotBankList = new ArrayList<BankInfo>();
		mDbBankList = new ArrayList<BankInfo>();

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mType = bundle.getInt("type");
			if (mType == TOPUP_DEBIT_CARD) {
				mControl = new UnionPayDebitSelectBankControl(this, mHandler);
			} else if (mType == TOPUP_CREDIT_CARD) {
				mControl = new UnionPayCreditSelectBankControl(this, mHandler);
			}
		} else {
			finish();
			return;
		}
	}

	private void setupViews() {
		mTvTitle = (TextView) this.findViewById(R.id.title);
		mLayoutBack = (RelativeLayout) findViewById(R.id.back_lin);
		mLayoutBack.setOnClickListener(this);
		mLvBank = (ListView) findViewById(R.id.bank_names_list);
		mEtBankName = (EditText) findViewById(R.id.edit);
		mEtBankName.addTextChangedListener(textwahcher);
	}

	private void init() {
		mControl.setTitle(mTvTitle);

		mBankListAdapter = new BankListAdapter(TopupSelectBank.this, mBankList);
		mBankListAdapter.setmBankSelectedListener(this);
		mLvBank.setAdapter(mBankListAdapter);
		if (mDbBankList.size() == 0 && mHotBankList.size() == 0){
			responseData(JSON_BANKS);
			insertHotBank(mDbBankList, mHotBankList, HOT_BANK_NAMES, HOT_BANK_ICONS);
		}else{
			mControl.initBankListInDb(mHotBankList, mDbBankList,
					HOT_BANK_NAMES, HOT_BANK_ICONS);
		}
		initBankListShow();
		mBankListAdapter.notifyDataSetChanged();
		mControl.getBankListVersion();
	}

	private void initBankListShow() {
		mBankList.clear();

		for (int i = 0; i < mHotBankList.size(); i++) {
			mBankList.add(mHotBankList.get(i));
		}

		for (int i = 0; i < mDbBankList.size(); i++) {
			mBankList.add(mDbBankList.get(i));
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.service_tv) {
			Intent intent = new Intent();
			intent.setClass(mContext, Feedback.class);
			startActivity(intent);
		} else if (v.getId() == R.id.back_lin) {
			TopupSelectBank.this.finish();
		}
	}

	@Override
	protected void submitData() {
		mControl.submitDataStatisticsOpen();
	}

	@Override
	public void onBankSelected(BankInfo bank) {
		mControl.selectedBank(bank);
		finish();
	}

	public void responseData(String result) {
		if (null != result) {
			try {
				mDbBankList.clear();

				JSONArray jsonArray = new JSONArray(result);
				int length = jsonArray.length();
				for (int i = 0; i < length; i++) {
					JSONObject jo = jsonArray.getJSONObject(i);
					addBanks(jo);
				}

				// setBankListVersionFlag();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 成功获取到银行数据后保存当前版本号
	// private void setBankListVersionFlag() {
	// Editor editor = ActionUtil.getEditor(mContext);
	// editor.putFloat("bank_list_version", mVersion);
	// editor.commit();
	// }

	public void addBanks(JSONObject jo) {
		BankInfo info = new BankInfo();
		try {
			info.setChinesename(jo.getString("z"));
			info.setCredit(jo.getString("c"));
			info.setDeposit(jo.getString("d"));
			info.setKey(jo.getString("k"));
			info.setShorthand(jo.getString("f"));
			info.setFirstchar(info.getShorthand().substring(0, 1));
			mDbBankList.add(info);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insertHotBank(ArrayList<BankInfo> bankList,
			ArrayList<BankInfo> hotBankList, String[] hotBankNames,
			int[] hotBankIcons) {
		for (int i = 0; i < hotBankNames.length; i++) {

			for (int j = 0; j < bankList.size(); j++) {
				BankInfo bank = (BankInfo) bankList.get(j).clone();

				if (bank.getChinesename().equals(hotBankNames[i])) {
					bank.setIconResource(hotBankIcons[i]);
					bank.setFirstchar("热门银行");
					hotBankList.add(bank);
				}
			}
		}
	}
}
