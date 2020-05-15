package com.example.healthassistant.Admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.healthassistant.R;

import java.util.ArrayList;

public class AllUserAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> account_list;
    private ArrayList<String> name_list;
    private ArrayList<String> phone_list;
    private ArrayList<String> temp_list;
    private LayoutInflater mLayoutInflater;



    AllUserAdapter(Context context,ArrayList<String> account_list,ArrayList<String> name_list,ArrayList<String> phone_list,ArrayList<String> temp_list){
        this.mContext = context;
        this.account_list = account_list;
        this.name_list = name_list;
        this.phone_list = phone_list;
        this.temp_list = temp_list;
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
    static class AllViewHolder{
        public TextView mTv2Account,mTv2Name,mTv2Phone,mTv2Temp;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AllViewHolder holder = null;
        //得到list的数据
        String account = account_list.get(position);
        String name = name_list.get(position);
        String phone = phone_list.get(position);
        String temp = temp_list.get(position);

        if (convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.all_user_item,null);
            holder = new AllViewHolder();
            holder.mTv2Account = (TextView) convertView.findViewById(R.id.all_user_account);
            holder.mTv2Name = (TextView)convertView.findViewById(R.id.all_user_name);
            holder.mTv2Phone = (TextView) convertView.findViewById(R.id.all_user_phone);
            holder.mTv2Temp = (TextView) convertView.findViewById(R.id.all_user_temp) ;
            convertView.setTag(holder);
        }else{
            holder = (AllViewHolder) convertView.getTag();
        }
        //控件赋值
        holder.mTv2Account.setText(account);
        holder.mTv2Name.setText(name);
        holder.mTv2Phone.setText(phone);
        holder.mTv2Temp.setText(temp);
        return convertView;
    }
}
