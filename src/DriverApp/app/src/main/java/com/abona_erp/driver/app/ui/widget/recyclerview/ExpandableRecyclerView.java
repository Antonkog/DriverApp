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

import com.abona_erp.driver.app.ui.feature.main.adapter.CommonItemClickListener;

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
    private CommonItemClickListener<T> listener;

    protected CommonItemClickListener<T> getListener() {
      return listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
      private Map<Integer, View> views;
      
      public ViewHolder(View view, CommonItemClickListener sListener) {
        super(view);
        listener = sListener;
        views = new HashMap<>();
        views.put(0, view);
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
    
    public Adapter(Context context, CommonItemClickListener<T> listener) {
      this.context = context;
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
