package com.swsnack.catchhouse.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerViewAdapter<T, H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // FIXME 복붙코드의 냄새가 납니다. lint warning으로 뜨는 내용들을 다 없애서 반영해주세요. 접근자, lambda식, unchecked 등
    protected List<T> arrayList;
    OnItemClickListener onItemClickListener;
    OnItemLongClickListener onItemLongClickListener;
    private Context context;


    public BaseRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public BaseRecyclerViewAdapter(Context context, List<T> arrayList) {
        this.context = context;
        this.arrayList = arrayList;

    }

    public Context getContext() {
        return context;
    }

    @Override
    public int getItemCount() {
        if (arrayList == null)
            return 0;


        return arrayList.size();
    }

    public T getItem(int position) {
        if (arrayList == null)
            return null;


        return arrayList.get(position);
    }

    public void updateItems(List<T> items) {

        if (this.arrayList == null) {
            arrayList = new ArrayList<>();
        }
        this.arrayList.clear();
        this.arrayList.addAll(items);

        notifyDataSetChanged();
    }

    public void addItems(List<T> items) {

        if (this.arrayList == null) {
            this.arrayList = items;
        } else {
            this.arrayList.addAll(items);
        }

        notifyDataSetChanged();


    }

    public void clearItems() {
        if (arrayList != null) {

            arrayList.clear();
            notifyDataSetChanged();
        }


    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(holder, position);
                }

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (onItemLongClickListener != null) {
                    onItemLongClickListener.onItemLongClick(holder, position);
                }

                return false;
            }
        });


        onBindView((H) holder, position);


    }

    abstract public void onBindView(H holder, int position);

    public void setOnItemClickListener(
            OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(
            OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemClickListener {

        public void onItemClick(RecyclerView.ViewHolder viewHolder, int position);
    }


    public interface OnItemLongClickListener {

        public void onItemLongClick(RecyclerView.ViewHolder viewHolder, int position);
    }
}
