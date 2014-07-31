package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.ShengFuBetHistoryDetailItemData;

/**
 * person-centered success or failure pass of bet.
 * @author lugq
 *
 */
public class ShengFuAdapter extends  BaseAdapter {

    private Context context;
    private ArrayList<ShengFuBetHistoryDetailItemData> list;
    public String[] betRe = {"主胜","", "主负"};

    public ShengFuAdapter(Context context, ArrayList<ShengFuBetHistoryDetailItemData> list) {
        this.context = context;
        this.list = list;
    }

    public final class ViewHolder {
    	private Button win;
    	private Button lost;
        private TextView index;
        private TextView league;
        private TextView firstTeam;
        private TextView concedePoint;
        private TextView secondTeam;
        private Button[] btns = new Button[3];
        private ImageView[] imgs = new ImageView[3];
        private TextView[] tvs = new TextView[9];
        private String[] betRe = {"主胜","", "主负"};
        private int[] btns_id = {R.id.win,R.id.draw, R.id.lose};
        private int[] imgs_id = {R.id.img_01,R.id.img_02, R.id.img_03};
        private int[] tvs_id = {R.id.tv_01, R.id.tv_02, R.id.tv_03, R.id.tv_04, R.id.tv_05, R.id.tv_06,
                R.id.tv_07, R.id.tv_08, R.id.tv_09};
    }

    @Override
    public int getCount() {
        return list.size();
    }
    
	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
        final ViewHolder viewHolder;
        
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.sports_shengfu_item_view, null);
            viewHolder = new ViewHolder();
            
            viewHolder.league = (TextView) convertView.findViewById(R.id.league);
            viewHolder.firstTeam = (TextView) convertView.findViewById(R.id.first_team);
            viewHolder.concedePoint = (TextView) convertView.findViewById(R.id.concede_point);
            viewHolder.secondTeam = (TextView) convertView.findViewById(R.id.second_team);
            viewHolder.index = (TextView) convertView.findViewById(R.id.index);
            viewHolder.win = (Button) convertView.findViewById(R.id.win);
            viewHolder.lost = (Button) convertView.findViewById(R.id.lose);
            
            viewHolder.index = (TextView) convertView.findViewById(R.id.index);
            viewHolder.league = (TextView) convertView.findViewById(R.id.league);
            
            for (int i = 0; i < viewHolder.btns.length; i++) {
                viewHolder.btns[i] = (Button) convertView.findViewById(viewHolder.btns_id[i]);
                viewHolder.imgs[i] = (ImageView) convertView.findViewById(viewHolder.imgs_id[i]);
            }
            for (int i = 0; i < viewHolder.tvs.length; i++) {
                viewHolder.tvs[i] = (TextView) convertView.findViewById(viewHolder.tvs_id[i]);
            }
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.league.setText(list.get(position).getMatch_name());
        viewHolder.firstTeam.setText(list.get(position).getHome_team());
        
        if(Double.valueOf(list.get(position).getRemark()) > 0) {
        	
        	/*viewHolder.concedePoint.setText(Html.fromHtml("<font color=\"#008000\">(" +
                    sportsItem.getConcede() + ")</font>vs"));*/
        	
        	viewHolder.concedePoint.setText(Html.fromHtml("<font color=\"#FF3300\">(" + 
        	        list.get(position).getRemark() + ")</font>vs"));
        } else {
        	viewHolder.concedePoint.setText(Html.fromHtml("<font color=\"#339933\">(" + 
        			list.get(position).getRemark() + ")</font>vs"));
        }
        
        viewHolder.secondTeam.setText(list.get(position).getGuest_team());
        viewHolder.index.setText("00" + (position + 1));
        
        if(list.get(position).getResult().equals("胜")) {
        	viewHolder.win.setText(Html.fromHtml("<font color=\"#FFFFFF\">" + viewHolder.betRe[0] +
        			"&nbsp;&nbsp;" + list.get(position).getSp1() + "</font>"));
        	viewHolder.win.setBackgroundResource(R.drawable.custom_button_redside_normal);
        	
        	viewHolder.lost.setText(Html.fromHtml("<font color=\"#515151\">" + viewHolder.betRe[2] +
            		"</font>&nbsp;&nbsp;" + "<font color=\"#CA4843\">" + list.get(position).getSp2() + "</font>"));
        } else {
        	viewHolder.win.setText(Html.fromHtml("<font color=\"#515151\">" + viewHolder.betRe[0] +
            		"</font>&nbsp;&nbsp;" + "<font color=\"#CA4843\">" + list.get(position).getSp1() + "</font>"));
        	
        	viewHolder.lost.setText(Html.fromHtml("<font color=\"#FFFFFF\">" + viewHolder.betRe[2] +
        			"&nbsp;&nbsp;" + list.get(position).getSp2() + "</font>"));
        	viewHolder.lost.setBackgroundResource(R.drawable.custom_button_redside_normal);
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public interface OnBallClickListener {
        /**
         * 是否允许点击球队
         * 
         * @param groupPosition 球队所属组
         * @param childPosition 球队所属组中位置
         * @param index 选中胜平负中哪项
         * @return
         */
        public boolean checkClick(int groupPosition, int childPosition, int index);
    }

}
