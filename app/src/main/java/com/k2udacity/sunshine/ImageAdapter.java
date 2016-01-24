package com.k2udacity.sunshine;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * Created by kchandra on 23/01/16.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    String baseImgUrl = "http://image.tmdb.org/t/p/";
    String size = "w185";
    private Movie[] images ;

    public Movie[] getImages() {
        return images;
    }

    public void setImages(Movie[] images) {
        this.images = images;
        notifyDataSetChanged();
    }

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        if(images!=null)
            return images.length;
        else
            return 0;
    }

    public Object getItem(int position) {
        return baseImgUrl + size + images[position].getPosterPath();
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        Toast.makeText(mContext, "In getview", Toast.LENGTH_SHORT).show();
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        //String path = baseImgUrl + size + images[position].getPosterPath();
        Picasso.with(mContext).load((String)getItem(position)).into(imageView);
//        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    // references to our images

}
