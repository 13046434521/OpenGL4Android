package com.jtl.opengl.menu;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jtl.opengl.R;
import com.socks.library.KLog;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 作者:jtl
 * 日期:Created in 2019/8/27 11:17
 * 描述:
 * 更改:
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuHolder> {
    private static final String TAG=MenuAdapter.class.getSimpleName();
    private List<MenuBean> mMenuBeanList;
    private Context mContext;
    private View.OnClickListener mOnClickListener;

    public MenuAdapter( Context context,List<MenuBean> menuBeanList, View.OnClickListener onClickListener) {
        mContext = context;
        mMenuBeanList = menuBeanList;
        mOnClickListener = onClickListener;
    }

    @NonNull
    @Override
    public MenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.layout_item_menu,parent,false);
        MenuHolder menuHolder=new MenuHolder(view);
        KLog.w(TAG,"onCreateViewHolder");
        return menuHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MenuHolder holder, final int position) {
        holder.mMenuText.setText(mMenuBeanList.get(position).getName());
        holder.mMenuText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class aa=mMenuBeanList.get(position).getClassData();
                Intent intent=new Intent(mContext,aa);
                mContext.startActivity(intent);
            }
        });

        KLog.w(TAG,"onBindViewHolder");
    }

    @Override
    public int getItemCount() {
        return mMenuBeanList.size();
    }

    protected class MenuHolder extends RecyclerView.ViewHolder{
        private TextView mMenuText;
        public MenuHolder(@NonNull View itemView) {
            super(itemView);

            mMenuText=itemView.findViewById(R.id.tv_item_menu);
        }
    }
}
