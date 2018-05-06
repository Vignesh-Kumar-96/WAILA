package org.tensorflow.demo;

/**
 * Created by vignesh on 5/5/18.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;


public class DynamicAdapter extends RecyclerView.Adapter<DynamicAdapter.DynamicViewHolder> {
    private List<PhotoObject> mData;
    private Context context;
    private LayoutInflater theInflater = null;
    private PhotoManager photoManager = PhotoManager.getInstance();


    public DynamicAdapter(List<PhotoObject> photoRecords, Context _context) {
        mData = photoRecords;
        context = _context;
    }


    public class DynamicViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageView image;
        View container;

        public DynamicViewHolder(View theView) {
            super(theView);
            text = (TextView) theView.findViewById(R.id.picTextRowText);
            image = (ImageView) theView.findViewById(R.id.picTextRowPic);
        }
    }

    @Override
    public DynamicAdapter.DynamicViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.pic_text_row,parent,false);
        DynamicViewHolder vh = new DynamicViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(DynamicViewHolder holder, int position) {
        PhotoObject rr = mData.get(position);
        holder.text.setText(rr.title);

        byte[] decoded = android.util.Base64.decode(rr.getEncodedBytes(), android.util.Base64.DEFAULT);
        Bitmap image = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
        holder.image.setImageBitmap(image);


    }


    public void add(final PhotoObject item, int position) {
        mData.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    /*
    public void action(int position){
        Intent i = new Intent(this.context, OnePost.class);
        Bundle myExtras = new Bundle();
        PhotoObject rr = mData.get(position);
        String text = rr.title;
        String url = rr.imageURL.toString();
        myExtras.putString("titleString", text);
        myExtras.putString("urlString", url);
        i.putExtras(myExtras);
        context.startActivity(i);
    }
    */

    @Override
    public int getItemCount() {
        return mData.size();
    }
    // This one is important and not obvious
    @Override
    public long getItemId(int position) {
        return mData.get(position).hashCode();
    }


}

