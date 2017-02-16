package com.realsoc.pipong;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.realsoc.pipong.utils.DataUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Hugo on 16/02/2017.
 */

public class RankingActivity extends AppCompatActivity{
    private static final String LOG_TAG = "RANKING_ACTIVITY";
    private HashMap<String,Long> playersWithPonctuation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        DataUtils dataUtils = DataUtils.getInstance(this);
        ListView listView=(ListView)findViewById(R.id.ranking_list);
        playersWithPonctuation = dataUtils.getPlayersWithPonctuation();
        List<Map.Entry<String, Long>> list =
                new LinkedList<>( playersWithPonctuation.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<String, Long>>()
        {
            public int compare( Map.Entry<String, Long> o1, Map.Entry<String, Long> o2 )
            {
                return (o2.getValue()).compareTo( o1.getValue() );
            }
        }
        );
        ListViewAdapter adapter=new ListViewAdapter(this, list);
        listView.setAdapter(adapter);

    }
    private class ListViewAdapter extends BaseAdapter {

        ArrayList<HashMap<String,String>> list = new ArrayList<>();;
        Activity activity;
        TextView txtFirst;
        TextView txtSecond;
        TextView txtThird;
        public ListViewAdapter(Activity activity,List<Map.Entry<String,Long>> entry){
            super();
            this.activity=activity;
            list.clear();
            list=processList(entry);
        }
        private ArrayList<HashMap<String,String>> processList(List<Map.Entry<String,Long>> entry){
            list.clear();
            long precVal = 10000000L;
            int currentCount=0;
            int straightCount = 0;
            HashMap<String,String> base = new HashMap<>();
            base.put("name","Name");
            base.put("coef","Coefficient");
            base.put("posi","Rank");
            list.add(base);
            for(Map.Entry<String,Long> el : entry){
                straightCount++;
                if(el.getValue()<precVal){
                    precVal = el.getValue();
                    currentCount = straightCount;
                }
                HashMap<String,String> item = new HashMap<>();
                item.put("name",el.getKey());
                item.put("coef",String.valueOf(el.getValue()));
                item.put("posi",String.valueOf(currentCount));
                list.add(item);
            }
            return list;
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            RankViewHolder rankViewHolder;


            LayoutInflater inflater=activity.getLayoutInflater();

            if(convertView == null){

                convertView=inflater.inflate(R.layout.ranking_item, null);
                rankViewHolder = new RankViewHolder();
                rankViewHolder.nameTextView=(TextView) convertView.findViewById(R.id.ranking_name);
                rankViewHolder.coefTextView=(TextView) convertView.findViewById(R.id.ranking_coef);
                rankViewHolder.posiTextView=(TextView) convertView.findViewById(R.id.ranking_position);
                convertView.setTag(rankViewHolder);
            }else {
                rankViewHolder = (RankViewHolder)convertView.getTag();
            }

            HashMap<String, String> map=getItem(position);

            rankViewHolder.nameTextView.setText(map.get("name"));
            rankViewHolder.coefTextView.setText(map.get("coef"));
            rankViewHolder.posiTextView.setText(map.get("posi"));
            if(position == 0){
                rankViewHolder.nameTextView.setTypeface(null, Typeface.BOLD);
                rankViewHolder.coefTextView.setTypeface(null, Typeface.BOLD);
                rankViewHolder.posiTextView.setTypeface(null, Typeface.BOLD);
            }

            return convertView;
        }
        private class RankViewHolder {
            TextView nameTextView;
            TextView coefTextView;
            TextView posiTextView;

        }
    }
}
