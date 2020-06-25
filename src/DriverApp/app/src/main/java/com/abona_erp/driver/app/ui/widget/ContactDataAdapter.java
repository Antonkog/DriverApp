package com.abona_erp.driver.app.ui.widget;

import android.graphics.Color;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.model.ContactItem;
import com.abona_erp.driver.app.data.model.EnumContactType;
import com.abona_erp.driver.app.data.model.EnumNumberType;
import com.abona_erp.driver.core.base.ContextUtils;

import java.util.List;

public class ContactDataAdapter extends RecyclerView.Adapter<ContactDataAdapter.ViewHolder> {
  
  private List<ContactItem> mDataset;
  
  public ContactDataAdapter(List<ContactItem> items) {
    this.mDataset = items;
  }
  
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
    ViewHolder vh = new ViewHolder(v);
    return vh;
  }
  
  @Override
  public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
    if (i % 2 == 1) {
      // regular row/item:
      viewHolder.ll_root_item.setBackgroundColor(Color.parseColor("#f5f6fa"));
    } else {
      // alternate row/item:
      viewHolder.ll_root_item.setBackgroundColor(Color.parseColor("#dcdde1"));
    }
    if (mDataset.get(i).getName() != null)
      viewHolder.tv_contact_name.setText(mDataset.get(i).getName());
    if (mDataset.get(i).getNumber() != null) {
      viewHolder.tv_contact_number.setText(mDataset.get(i).getNumber());
      Linkify.addLinks(viewHolder.tv_contact_number, Linkify.ALL);
    }
    
    if (mDataset.get(i).getNumberType().equals(EnumNumberType.EMAIL)) {
      viewHolder.iv_number_type.setImageDrawable(ContextCompat.getDrawable(ContextUtils.getApplicationContext(), android.R.drawable.ic_dialog_email));
    } else if (mDataset.get(i).getNumberType().equals(EnumNumberType.MOBILE)) {
      viewHolder.iv_number_type.setImageDrawable(ContextCompat.getDrawable(ContextUtils.getApplicationContext(), R.drawable.ic_phone_wh));
    } else if (mDataset.get(i).getNumberType().equals(EnumNumberType.PHONE)) {
      viewHolder.iv_number_type.setImageDrawable(ContextCompat.getDrawable(ContextUtils.getApplicationContext(), R.drawable.ic_phone_wh));
    }
    
    if (mDataset.get(i).getContactType().equals(EnumContactType.VERFOLGER)) {
      viewHolder.tv_contact_type.setText(ContextUtils.getApplicationContext().getResources().getString(R.string.contact_type_persecutor));
    } else if (mDataset.get(i).getContactType().equals(EnumContactType.BACKUP_VERFOLGER)) {
      viewHolder.tv_contact_type.setText(ContextUtils.getApplicationContext().getResources().getString(R.string.contact_type_second_persecutor));
    } else if (mDataset.get(i).getContactType().equals(EnumContactType.CUSTOMER)) {
      viewHolder.tv_contact_type.setText(ContextUtils.getApplicationContext().getResources().getString(R.string.contact_type_customer));
    }
  }
  
  @Override
  public int getItemCount() {
    return mDataset.size();
  }
  
  public class ViewHolder extends RecyclerView.ViewHolder {
    
    public AsapTextView tv_contact_name;
    public AsapTextView tv_contact_number;
    public AsapTextView tv_contact_type;
    public AppCompatImageView iv_number_type;
    public LinearLayout ll_root_item;
    
    public ViewHolder(View v) {
      super(v);
      tv_contact_name = (AsapTextView)v.findViewById(R.id.tv_contact_name);
      tv_contact_number = (AsapTextView)v.findViewById(R.id.tv_contact_number);
      tv_contact_type = (AsapTextView)v.findViewById(R.id.tv_contact_type);
      iv_number_type = (AppCompatImageView)v.findViewById(R.id.iv_number_type);
      ll_root_item = (LinearLayout)v.findViewById(R.id.ll_root_item);
    }
  }
}
