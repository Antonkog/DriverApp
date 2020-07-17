package com.abona_erp.driver.app.ui.widget;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.model.DangerousGoods;
import com.abona_erp.driver.app.data.model.DangerousGoodsClass;
import com.abona_erp.driver.core.base.ContextUtils;

import java.util.List;

public class DangerousGoodsDataAdapter extends RecyclerView.Adapter<DangerousGoodsDataAdapter.ViewHolder> {
  
  private List<DangerousGoods> mDataset;
  private Resources _resources;
  
  public DangerousGoodsDataAdapter(List<DangerousGoods> items) {
    this.mDataset = items;
    this._resources = ContextUtils.getApplicationContext().getResources();
  }
  
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dangerous_goods_item, parent, false);
    ViewHolder vh = new ViewHolder(v);
    return vh;
  }
  
  @Override
  public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
    if (i % 2 == 1) {
      viewHolder.ll_root_item.setBackgroundColor(Color.parseColor("#dcdde1"));
    } else {
      viewHolder.ll_root_item.setBackgroundColor(Color.parseColor("#f5f6fa"));
    }
    
    if (mDataset.get(i).getDangerousGoodsClassType() != null)
      viewHolder.iv_dangerous_goods.setImageDrawable(getDangerousGoodsClass(mDataset.get(i).getDangerousGoodsClassType()));
    if (mDataset.get(i).getAdrClass() != null)
      viewHolder.tv_adr_class.setText(mDataset.get(i).getAdrClass());
    if (mDataset.get(i).getUnNo() != null)
      viewHolder.tv_un_no.setText(mDataset.get(i).getUnNo());
  }
  
  @Override
  public int getItemCount() {
    return mDataset.size();
  }
  
  public class ViewHolder extends RecyclerView.ViewHolder {
    
    public AppCompatImageView iv_dangerous_goods;
    public AsapTextView tv_adr_class;
    public AsapTextView tv_un_no;
    public LinearLayout ll_root_item;
    
    public ViewHolder(View v) {
      super(v);
      iv_dangerous_goods = (AppCompatImageView)v.findViewById(R.id.iv_dangerous_goods);
      tv_adr_class = (AsapTextView)v.findViewById(R.id.tv_adr_class);
      tv_un_no = (AsapTextView)v.findViewById(R.id.tv_un_no);
      ll_root_item = (LinearLayout)v.findViewById(R.id.ll_root_item);
    }
  }
  
  private Drawable getDangerousGoodsClass(DangerousGoodsClass dangerousGoodsClass) {
    switch (dangerousGoodsClass) {
      case CLASS_1_EXPLOSIVES: return _resources.getDrawable(R.drawable.ic_class_1_explosives);
      case CLASS_1_1_EXPLOSIVES: return _resources.getDrawable(R.drawable.ic_class_1_explosives_1_1);
      case CLASS_1_2_EXPLOSIVES: return _resources.getDrawable(R.drawable.ic_class_1_explosives_1_2);
      case CLASS_1_3_EXPLOSIVES: return _resources.getDrawable(R.drawable.ic_class_1_explosives_1_3);
      case CLASS_1_4_EXPLOSIVES: return _resources.getDrawable(R.drawable.ic_class_1_explosives_1_4);
      case CLASS_1_5_EXPLOSIVES: return _resources.getDrawable(R.drawable.ic_class_1_explosives_1_5);
      case CLASS_1_6_EXPLOSIVES: return _resources.getDrawable(R.drawable.ic_class_1_explosives_1_6);
      case CLASS_2_FLAMMABLE_GAS: return _resources.getDrawable(R.drawable.ic_class_2_flammable_gas);
      case CLASS_2_NON_FLAMMABLE_GAS: return _resources.getDrawable(R.drawable.ic_class_2_non_flammable_gas);
      case CLASS_2_POISON_GAS: return _resources.getDrawable(R.drawable.ic_class_2_poison_gas);
      case CLASS_3_FLAMMABLE_LIQUID: return _resources.getDrawable(R.drawable.ic_class_3_flammable_liquid);
      case CLASS_4_1_FLAMMABLE_SOLIDS: return _resources.getDrawable(R.drawable.ic_class_4_flammable_solid);
      case CLASS_4_2_SPONTANEOUSLY_COMBUSTIBLE: return _resources.getDrawable(R.drawable.ic_class_4_spontaneously_combustible);
      case CLASS_4_3_DANGEROUSE_WHEN_WET: return _resources.getDrawable(R.drawable.ic_class_4_dangerous_when_wet);
      case CLASS_5_1_OXIDIZER: return _resources.getDrawable(R.drawable.ic_class_5_1_oxidizer);
      case CLASS_5_2_ORAGNIC_PEROXIDES: return _resources.getDrawable(R.drawable.ic_class_5_2_organic_peroxides);
      case CLASS_6_1_POISON: return _resources.getDrawable(R.drawable.ic_class_6_poison);
      case CLASS_6_2_INFECTIOUS_SUBSTANCE: return _resources.getDrawable(R.drawable.ic_class_6_2_infectious_substance);
      case CLASS_7_FISSILE: return _resources.getDrawable(R.drawable.ic_class_7_fissile);
      case CLASS_7_RADIOACTIVE_I: return _resources.getDrawable(R.drawable.ic_class_7_radioactive_i);
      case CLASS_7_RADIOACTIVE_II: return _resources.getDrawable(R.drawable.ic_class_7_radioactive_ii);
      case CLASS_7_RADIOACTIVE_III: return _resources.getDrawable(R.drawable.ic_class_7_radioactive_iii);
      case CLASS_8_CORROSIVE: return _resources.getDrawable(R.drawable.ic_class_8_corrosive);
      case CLASS_9_MISCELLANEOUS: return _resources.getDrawable(R.drawable.ic_class_9_miscellaneus);
      default: return _resources.getDrawable(R.drawable.ic_risk);
    }
  }
}
