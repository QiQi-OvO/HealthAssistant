package com.example.healthassistant.Admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.healthassistant.R;

import java.util.ArrayList;

public class AbnormalAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> account_list;
    private ArrayList<String> name_list;
    private ArrayList<String> phone_list;
    private ArrayList<String> temp_list;
    private ArrayList<String> time_list;
    private ArrayList<String> transport_list;
    private ArrayList<String> remarks_list;
    private LayoutInflater mLayoutInflater;



    AbnormalAdapter(Context context,ArrayList<String> account_list,ArrayList<String> name_list,ArrayList<String> phone_list,ArrayList<String> temp_list,ArrayList<String> time_list,ArrayList<String> transport_list,ArrayList<String> remarks_list){
        this.mContext = context;
        this.account_list = account_list;
        this.name_list = name_list;
        this.phone_list = phone_list;
        this.temp_list = temp_list;
        this.time_list = time_list;
        this.transport_list = transport_list;
        this.remarks_list = remarks_list;
        mLayoutInflater = LayoutInflater.from(context);
    }



    @Override
    public int getCount() {
        return account_list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class AbViewHolder{
        public TextView mTv2Account,mTv2Name,mTv2Phone,mTv2Temp,mTv2Time,mTv2Transport,mTv2Remarks;
    }





    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AbViewHolder holder = null;
        //得到list的数据
        String account = account_list.get(position);
        String name = name_list.get(position);
        String phone = phone_list.get(position);
        String temp = temp_list.get(position);
        String time = time_list.get(position);
        String transport = transport_list.get(position);
        String remarks = remarks_list.get(position);

        if (convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.abnormal_item,null);
            holder = new AbViewHolder();
            holder.mTv2Account = (TextView) convertView.findViewById(R.id.ab_user_account);
            holder.mTv2Name = (TextView)convertView.findViewById(R.id.ab_user_name);
            holder.mTv2Phone = (TextView) convertView.findViewById(R.id.ab_user_phone);
            holder.mTv2Temp = (TextView) convertView.findViewById(R.id.ab_temp) ;
            holder.mTv2Time = (TextView) convertView.findViewById(R.id.ab_time) ;
            holder.mTv2Transport = (TextView) convertView.findViewById(R.id.ab_transport) ;
            holder.mTv2Remarks = (TextView) convertView.findViewById(R.id.ab_remarks) ;
            convertView.setTag(holder);
        }else{
            holder = (AbViewHolder) convertView.getTag();
        }
        //控件赋值
        holder.mTv2Account.setText(account);
        holder.mTv2Name.setText(name);
        holder.mTv2Phone.setText(phone);
        holder.mTv2Temp.setText(temp);
        holder.mTv2Time.setText(time);
        holder.mTv2Transport.setText(transport);
        holder.mTv2Remarks.setText(remarks);
        return convertView;
    }




}
