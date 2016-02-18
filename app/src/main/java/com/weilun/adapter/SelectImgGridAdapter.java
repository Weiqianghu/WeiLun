package com.weilun.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.weilun.bean.Constant;
import com.weilun.bean.SelectImgBean;
import com.weilun.util.ImgUtil;
import com.weilun.weilun.R;

import java.util.List;

import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by 胡伟强 on 2015/12/22.
 */
public class SelectImgGridAdapter extends BaseAdapter{

    private GridView mSelectImgGridView;
    private List<SelectImgBean> imgList;

    private LayoutInflater layoutInflater;

    private Context mContext;


    public SelectImgGridAdapter(Context context,GridView gridView,List<SelectImgBean> data){
        this.mSelectImgGridView=gridView;
        this.imgList=data;

        this.mContext=context;

        layoutInflater=LayoutInflater.from(context);
    }



    @Override
    public int getCount() {
        return imgList.size();
    }

    @Override
    public Object getItem(int position) {
        return imgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView=layoutInflater.inflate(R.layout.select_img_item,null);
            viewHolder.img= (ImageView) convertView.findViewById(R.id.img);
            viewHolder.deleteImg= (ImageView) convertView.findViewById(R.id.delete_img);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder= (ViewHolder) convertView.getTag();
        }


        if(imgList.get(position)!=null){
            Bitmap bitmap=ImgUtil.getSmallBitmap(imgList.get(position).getImgPath());
            viewHolder.img.setImageBitmap(bitmap);
            viewHolder.deleteImg.setImageResource(R.drawable.delete_icon);
            viewHolder.deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imgList.remove(position);
                    notifyDataSetChanged();
                    Intent intent=new Intent();
                    intent.setAction(Constant.FLAG_SELECT_IMG);
                    mContext.sendBroadcast(intent);
                }
            });
        }
        return convertView;
    }

    private class ViewHolder{
        private ImageView img;
        private ImageView deleteImg;
    }
}
