/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.interviewtest.feature.item.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.faisal.domain.Item;
import com.example.faisal.interviewtest.R;
import com.example.faisal.interviewtest.view.decorator.ItemTouchHelper.ItemTouchHelperAdapter;
import com.example.faisal.interviewtest.view.decorator.ItemTouchHelper.OnStartDragListener;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

// Create the basic Post adapter extending from RecyclerView.Adapter
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private final OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(int position, View view);
    }
    private final OnStartDragListener dragListener;
    // Store a member variable for the Posts
    private List<Item> mItemList = new ArrayList<>();
    // Store the context for easy access
    private Context mContext;
    private int parentHeight = 0;
    private int parentWidth = 0;

    // General constructor
    public ItemAdapter(Context context, OnItemClickListener listener, OnStartDragListener dragListener) {
        mContext = context;
        this.listener = listener;
        this.dragListener = dragListener;
    }

    public void setItemList(List<Item> ItemList) {
        this.mItemList = ItemList;
        this.mItemList.add(new Item()); // to add new items
    }

    public Item getItemByPosition(int position) {
        return mItemList.get(position);
    }

    public void addItem(Item data) {
        mItemList.remove(mItemList.size()-1);
        mItemList.add(data);
        mItemList.add(new Item()); // to add new items
        notifyItemRangeChanged(mItemList.size()-2, 2);
    }

    public void deleteItem(int position) {
        mItemList.remove(position);
        int itemsToUpdate = mItemList.size() - (position - 1);
        // Because we need to update positions for all items after deleted item
        notifyItemRangeChanged(position, itemsToUpdate);
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.pic_item, parent, false);
        parentHeight = parent.getMeasuredHeight();
        parentWidth = parent.getMeasuredWidth();
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ItemAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Item item = mItemList.get(position);

        //bind click listener with item
        viewHolder.bind(position, listener);

        // Set item views based on your views and data model
        if(item.imageUrlString == null && position == mItemList.size()-1){
            Uri uri = new Uri.Builder()
                    .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                    .path(String.valueOf(R.drawable.ic_library_add_blue_900_48dp))
                    .build();
            viewHolder.pic.setImageURI(uri);
            viewHolder.delete.setVisibility(View.GONE);
            viewHolder.dragHandle.setVisibility(View.GONE);
        }else{
            Uri uri;
            if(item.imageUrlString != null){
                uri = Uri.parse(item.imageUrlString);
                viewHolder.pic.setImageURI(uri);
                viewHolder.delete.setVisibility(View.VISIBLE);
                viewHolder.dragHandle.setVisibility(View.VISIBLE);
            }
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public long getItemId(int position) {
        // here code for getting the right itemID,
        // i.e. return super.getItemId(mPosition);
        // where mPosition is the Position in the Collection.
        Item item = getItemByPosition(position);
        int index = mItemList.indexOf(item);
        return super.getItemId(mItemList.indexOf(item));
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        // add view should not be moved
        if(toPosition != mItemList.size()-1 && fromPosition != mItemList.size()-1){
            Collections.swap(mItemList, fromPosition, toPosition);
            // Need to do below, because NotifyItemMove only handle one sided move
            Item fromItem = mItemList.get(fromPosition);
            Item toItem = mItemList.get(toPosition);
            notifyItemChanged(fromPosition, toItem);
            notifyItemChanged(toPosition, fromItem);
            dragListener.onItemMove(fromPosition, toPosition);
            return true;
        }
        else
            return false;
    }

    @Override
    public void onItemDismiss(int position) {
        mItemList.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.pic)
        SimpleDraweeView pic;
        @BindView(R.id.delete)
        ImageButton delete;
        @BindView(R.id.handle)
        ImageView dragHandle;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final int position, final OnItemClickListener listener) {
            // if whole item clicked
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(position, view);
                }
            });
            // if delete button clicked
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(position, view);
                }
            });
        }
    }
}
