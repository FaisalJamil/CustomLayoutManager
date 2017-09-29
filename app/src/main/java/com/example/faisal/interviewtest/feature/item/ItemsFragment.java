/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.interviewtest.feature.item;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.example.faisal.domain.Item;
import com.example.faisal.domain.ListOfItems;
import com.example.faisal.interviewtest.AndroidApplication;
import com.example.faisal.interviewtest.R;
import com.example.faisal.interviewtest.feature.item.adapter.ItemAdapter;
import com.example.faisal.interviewtest.internal.di.components.DaggerFragmentComponent;
import com.example.faisal.interviewtest.internal.di.components.FragmentComponent;
import com.example.faisal.interviewtest.internal.di.modules.FragmentModule;
import com.example.faisal.interviewtest.internal.mvp.BaseFragment;
import com.example.faisal.interviewtest.util.Navigator;
import com.example.faisal.interviewtest.util.Utils;
import com.example.faisal.interviewtest.view.decorator.ItemTouchHelper.ItemTouchHelperCallback;
import com.example.faisal.interviewtest.view.decorator.ItemTouchHelper.OnStartDragListener;
import com.example.faisal.interviewtest.view.decorator.SpannedGridLayoutManager;

import java.util.UUID;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemsFragment extends BaseFragment<ItemsPresenter> implements ItemsView<ItemsPresenter>{

    @BindView(R.id.post_list)
    RecyclerView rvPosts;
    @BindView(R.id.loading)
    ProgressBar loading;

    @Inject
    Navigator navigator;
    ItemAdapter itemAdapter;
    ItemTouchHelper itemTouchHelper;

    //To bind with the add dialog view using butterKnife
    public class DialogViewHolder {
        @BindView(R.id.new_item)
        EditText newItem;

        DialogViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
    public DialogViewHolder dialogViewHolder;

    public static ItemsFragment newInstance() {
        return new ItemsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initInjector();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(R.string.post_list);
        initList();
    }

    private void initInjector() {
        FragmentComponent component = DaggerFragmentComponent.builder()
                .applicationComponent(AndroidApplication.getComponent(getContext()))
                .fragmentModule(new FragmentModule(this)) // Support for future providers
                .build();

        component.inject(this);
    }

    private void initList() {
        itemAdapter = new ItemAdapter(getContext(), initClickListener(), initDragListener());
        rvPosts.setAdapter(itemAdapter);

        rvPosts.setHasFixedSize(true);

        SpannedGridLayoutManager spannedGridLayoutManager = new SpannedGridLayoutManager(
                new SpannedGridLayoutManager.GridSpanLookup() {
                    @Override
                    public SpannedGridLayoutManager.SpanInfo getSpanInfo(int position) {
                        if (position == 0) {
                            return new SpannedGridLayoutManager.SpanInfo(2, 2);
                        } else {
                            return new SpannedGridLayoutManager.SpanInfo(1, 1);
                        }

                    }

                },
                3 /* Three columns */,
                1f /* We want our items to be 1:1 ratio */);
        rvPosts.setLayoutManager(spannedGridLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvPosts.addItemDecoration(itemDecoration);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(itemAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rvPosts);

        //Can be used to handle clicks on item cards
//        ItemClickSupport.addTo(rvPosts).setOnItemClickListener(
//                (recyclerView, position, v) -> {
//
//                }
//        );
    }

    private ItemAdapter.OnItemClickListener initClickListener (){
        return new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                // if whole item clicked
                if(itemAdapter.getItemCount()-1 == position) { // it is add item
                        showAddDialog();
                } else if(view.getId() == R.id.delete){
                    Item item = itemAdapter.getItemByPosition(position);
                    Utils.showDialog(getContext(), getString(R.string.alert), String.format(getString(R.string.item_delete), item.uuid), null)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    presenter.deleteItem(position);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.cancel();
                                }
                            }).show();
                }else { // if item clicked but not add view or delete
                    Item item = itemAdapter.getItemByPosition(position);
                    Utils.showDialog(getContext(), getString(R.string.alert), String.format(getString(R.string.item_clicked), item.uuid), null)
                            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            }
        };
    }

    private OnStartDragListener initDragListener (){
        return new OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
//                itemTouchHelper.startDrag(viewHolder);
            }

            @Override
            public void onItemMove(int fromPosition, int toPosition) {
                presenter.moveItem(fromPosition, toPosition);
            }
        };
    }

    private void showAddDialog(){
        View newItemDialog = View.inflate(getContext(), R.layout.dialog_new_item, null);
        dialogViewHolder = new DialogViewHolder(newItemDialog);

        Utils.showDialog(getContext(), getString(R.string.add_new_item), "", newItemDialog)
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        presenter.addItem(UUID.randomUUID().toString(), dialogViewHolder.newItem.getText().toString());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                }).show();
    }

    @Override
    public void add(Item data) {
        itemAdapter.addItem(data);
    }

    @Override
    public void delete(int position) {
        itemAdapter.deleteItem(position);
    }

    @Override
    public void render(ListOfItems itemList) {
        itemAdapter.setItemList(itemList.items);
        itemAdapter.notifyItemRangeInserted(0, itemList.items.size());
    }

    @Inject
    @Override
    public void injectPresenter(ItemsPresenter presenter) {
        super.injectPresenter(presenter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.post_list_fragment;
    }

    @Override
    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
        ViewCompat.setAlpha(loading, 0);
        ViewCompat.animate(rvPosts).alpha(0);
        ViewCompat.animate(loading).alpha(1);
    }

    @Override
    public void hideLoading() {
        ViewCompat.animate(rvPosts).alpha(1);
        ViewCompat.animate(loading).alpha(0);
    }
}
