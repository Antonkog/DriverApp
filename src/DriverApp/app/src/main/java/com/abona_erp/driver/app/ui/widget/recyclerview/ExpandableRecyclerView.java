package com.abona_erp.driver.app.ui.widget.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpandableRecyclerView extends RecyclerView {
  
  
  public ExpandableRecyclerView(@NonNull Context context) {
    this(context, null);
  }
  
  public ExpandableRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }
  
  public ExpandableRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }
  
  @Override
  public void setLayoutManager(RecyclerView.LayoutManager layout) {
    if (!(layout instanceof LinearLayoutManager)) {
      throw new IllegalArgumentException("layoutManager manager must be an instance of LinearLayoutManager!");
    }
    super.setLayoutManager(layout);
  }
  
  @Override
  public void setAdapter(RecyclerView.Adapter adapter) {
    if (!(adapter instanceof Adapter)) {
      throw new IllegalArgumentException("adapter must be an instance of ExpandableRecyclerView.Adapter!");
    }
    super.setAdapter(adapter);
  }
  
  public static abstract class Adapter<T> extends RecyclerView.Adapter<Adapter.ViewHolder> {
    
    private List<T> mItems;
    private Context context;
    private OnViewHolderClick<T> listener;
    @NonNull private final LinearLayoutManager layoutManager;
    private int currentPosition = -1;
    
    public interface OnViewHolderClick<T> {
      void onClick(View view, int position, T item);
      void onDblClick(View view, int position, T item);
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder  {
      private Map<Integer, View> views;
      
      public ViewHolder(View view, OnViewHolderClick listener) {
        super(view);
        views = new HashMap<>();
        views.put(0, view);
      }
      
      public void initViewList(int[] idList) {
        for (int id : idList)
          initViewById(id);
      }
      
      public void initViewById(int id) {
        View view = (getView() != null ? getView().findViewById(id) : null);
        
        if (view != null) {
          views.put(id, view);
        }
      }
      
      public View getView() {
        return getView(0);
      }
      
      public View getView(int id) {
        if (views.containsKey(id)) {
          return views.get(id);
        } else {
          initViewById(id);
        }
        return views.get(id);
      }
    }
    
    protected abstract View createView(Context context, ViewGroup viewGroup, int viewType);
    
    protected abstract void bindView(T item, Adapter.ViewHolder viewHolder);
    
    public Adapter(Context context, @NonNull LinearLayoutManager layoutManager, OnViewHolderClick<T> listener) {
      this.context = context;
      this.layoutManager = layoutManager;
      this.listener = listener;
      mItems = new ArrayList<>();
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
      return new ViewHolder(createView(context, viewGroup, viewType), listener);
    }
    
    @Override
    @CallSuper
    public void onBindViewHolder(@NonNull final Adapter.ViewHolder holder, int position) {
      final ExpandableItem expandableItem = holder.itemView.findViewWithTag(ExpandableItem.TAG);
      if (expandableItem == null) {
        throw new RuntimeException("Item of this adapter must contain ExpandableItem!");
      }
      expandableItem.setOnClickListener(new DoubleClickListener() {
        
        @Override
        public void onSingleClick(View v) {
          currentPosition = holder.getLayoutPosition();
          for (int index = 0; index < layoutManager.getChildCount(); ++index) {
            if (index != (currentPosition - layoutManager.findFirstVisibleItemPosition())) {
              final ExpandableItem currentExpandableItem =
                layoutManager.getChildAt(index).findViewWithTag(ExpandableItem.TAG);
              currentExpandableItem.hide();
            }
          }
          final ExpandableItem item = layoutManager
            .getChildAt(currentPosition - layoutManager.findFirstVisibleItemPosition())
            .findViewWithTag(ExpandableItem.TAG);
          if (expandableItem.isOpened()) {
            expandableItem.hide();
          } else {
            expandableItem.show();
          }
          
          listener.onClick(v, holder.getAdapterPosition(), getItem(holder.getAdapterPosition()));
        }
  
        @Override
        public void onDoubleClick(View v) {
          if (listener != null) {
            listener.onDblClick(v, holder.getAdapterPosition(), getItem(holder.getAdapterPosition()));
          }
        }
      });
      if (currentPosition != position && expandableItem.isOpened()) {
        expandableItem.hideNow();
      } else if (currentPosition == position && !expandableItem.isOpened() && !expandableItem.isClosedByUser()) {
        expandableItem.showNow();
      }
      
      bindView(getItem(position), holder);
    }
    
    @Override
    public int getItemCount() {
     if (mItems != null) {
       return mItems.size();
     } else return 0;
    }
    
    public T getItem(int index) {
      return ((mItems != null && index  < mItems.size()) ? mItems.get(index) : null);
    }
    
    public Context getContext() {
      return context;
    }
    
    public void setList(List<T> list) {
      reset();
      addItems(list);
    }
    
    public List<T> getList() {
      return mItems;
    }
    
    public void setClickListener(OnViewHolderClick listener) {
      this.listener = listener;
    }
    
    private void addItems(List<T> list) {
      int startPosition = mItems.size();
      mItems = list;
      notifyItemRangeInserted(startPosition, list.size());
    }
    
    private void reset() {
      int count = mItems.size();
      mItems.clear();
      notifyItemRangeRemoved(0, count);
    }
  }
}
