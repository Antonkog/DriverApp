package com.abona_erp.driver.app.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.ContactItem;
import com.abona_erp.driver.app.data.model.DangerousGoods;
import com.abona_erp.driver.app.data.model.DangerousGoodsClass;
import com.abona_erp.driver.app.data.model.EnumPalletExchangeType;
import com.abona_erp.driver.app.data.model.NotesItem;
import com.abona_erp.driver.app.data.model.PalletExchange;
import com.abona_erp.driver.app.util.AppUtils;

import java.util.List;

public class CustomTaskInfoDialog extends Dialog
  implements View.OnClickListener {
  
  public CustomTaskInfoDialog(Context context, int themeResId) {
    super(context, themeResId);
  }
  
  public CustomTaskInfoDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
    super(context, cancelable, cancelListener);
  }
  
  public Activity activity;
  public Dialog dialog;
  Resources _resources;
  public AppCompatButton btn_ok;
  public AppCompatButton btn_cancel;
  
  AsapTextView tv_title;
  AsapTextView tv_order_no;
  View vw_action_type;
  
  private CommItem mCommItem;
  private int mActionType;
  
  private AsapTextView tv_name;
  private AsapTextView tv_info_reference;
  private AsapTextView tv_info_reference_title;
  private AsapTextView tv_ref_id_1;
  private AsapTextView tv_ref_id_1_title;
  private AsapTextView tv_ref_id_2;
  private AsapTextView tv_ref_id_2_title;
  private AsapTextView tv_client;
  private AsapTextView tv_client_title;
  private AsapTextView tv_geo_data;
  private AsapTextView tv_loading_order;
  private AsapTextView tv_loading_order_title;
  private AsapTextView tv_adr_class;
  private AsapTextView tv_adr_class_title;
  private AsapTextView tv_un_no;
  private AsapTextView tv_un_no_title;
  private AsapTextView tv_number_of_paletts;
  private AsapTextView tv_number_of_paletts_title;
  private AsapTextView tv_dpl;
  private AsapTextView tv_dpl_title;
  private AsapTextView tv_contact_title;
  private AsapTextView tv_contact;
  private AsapTextView tv_notes;
  private AsapTextView tv_notes_title;
  
  private AppCompatImageView iv_dangerous_goods;
  private AppCompatImageView iv_palette_info;
  private AppCompatImageView iv_notes;
  private AppCompatImageView iv_contacts;
  
  private LinearLayout ll_dangerous_goods;
  private LinearLayout ll_paletts;
  private LinearLayout ll_notes;
  private LinearLayout ll_contacts;
  
  public CustomTaskInfoDialog(Activity a, CommItem commItem, int actionType) {
    super(a);
    this.activity = a;
    this.mCommItem = commItem;
    this.mActionType = actionType;
    
    this._resources = getContext().getResources();
  }
  
  private void setupLayout() {
    if (mCommItem == null) return;
    if (mCommItem.getTaskItem() == null);
    
    if (mCommItem.getTaskItem().getOrderNo() != null) {
      tv_order_no.setText(AppUtils.parseOrderNo(mCommItem.getTaskItem().getOrderNo()));
    }
    
    // Mandant Name and ID:
    if (mCommItem.getTaskItem().getMandantId() != null || mCommItem.getTaskItem().getMandantName() != null) {
      tv_client.setVisibility(View.VISIBLE);
      tv_client_title.setVisibility(View.VISIBLE);
      
      String client = "";
      if (mCommItem.getTaskItem().getMandantName() != null) {
        client += mCommItem.getTaskItem().getMandantName() + " ";
      }
      if (mCommItem.getTaskItem().getMandantId() != null) {
        client += "(" + mCommItem.getTaskItem().getMandantId() + ")";
      }
      tv_client.setText(client);
    } else {
      tv_client.setVisibility(View.GONE);
      tv_client_title.setVisibility(View.GONE);
    }
    
    // Name:
    if (mCommItem.getTaskItem().getAddress().getName1() != null || mCommItem.getTaskItem().getAddress().getName2() != null) {
      tv_name.setVisibility(View.VISIBLE);
      String name = "";
      if (mCommItem.getTaskItem().getAddress().getName1() != null) {
        name += mCommItem.getTaskItem().getAddress().getName1();
      }
      if (mCommItem.getTaskItem().getAddress().getName2() != null) {
        name += " (" + mCommItem.getTaskItem().getAddress().getName2() + ")";
      }
      tv_name.setText(name);
    } else {
      tv_name.setVisibility(View.GONE);
    }
    
    // Geodata:
    if (mCommItem.getTaskItem().getAddress().getLatitude() != null || mCommItem.getTaskItem().getAddress().getLongitude() != null) {
      tv_geo_data.setVisibility(View.VISIBLE);
  
      String lat_lng = "(lat: ";
      if (mCommItem.getTaskItem().getAddress().getLatitude() != null) {
        lat_lng += mCommItem.getTaskItem().getAddress().getLatitude() + ", lng: ";
      } else {
        lat_lng += ", lng: ";
      }
      if (mCommItem.getTaskItem().getAddress().getLongitude() != null) {
        lat_lng += mCommItem.getTaskItem().getAddress().getLongitude() + ")";
      } else {
        lat_lng += ")";
      }
      tv_geo_data.setText(lat_lng);
    } else {
      tv_geo_data.setVisibility(View.GONE);
    }
    
    // Load Data:
    if (mCommItem.getTaskItem().getTaskDetails() != null || mCommItem.getTaskItem().getDescription() != null) {
      // Waren:
      if (mCommItem.getTaskItem().getTaskDetails() != null && mCommItem.getTaskItem().getTaskDetails().getDescription() != null) {
        applyInfoReference(mCommItem.getTaskItem().getTaskDetails().getDescription());
      } else if (mCommItem.getTaskItem().getDescription() != null) {
        applyInfoReference(mCommItem.getTaskItem().getDescription());
      } else {
        applyInfoReference(null);
      }
  
      // Loading Order:
      if (mCommItem.getTaskItem().getTaskDetails() != null && mCommItem.getTaskItem().getTaskDetails().getLoadingOrder() != null) {
        applyLoadingOrder(mCommItem.getTaskItem().getTaskDetails().getLoadingOrder());
      } else {
        applyLoadingOrder(null);
      }
    } else {
      applyInfoReference(null);
      applyLoadingOrder(null);
    }
  
    if (mCommItem.getTaskItem().getTaskDetails() != null && mCommItem.getTaskItem().getTaskDetails().getReferenceId1() != null) {
      applyRefId1(mCommItem.getTaskItem().getTaskDetails().getReferenceId1()/*commItem.getTaskItem().getReferenceIdCustomer1()*/);
    }
  
    applyRefId2(mCommItem.getTaskItem().getReferenceIdCustomer2());
    applyDangerousGoods(mCommItem.getTaskItem().getDangerousGoods());
    applyPallets(mCommItem.getTaskItem().getPalletExchange());
    applyNotes(mCommItem.getTaskItem().getNotes());
    applyContacts(mCommItem.getTaskItem().getContacts());
    //applyDocuments(mCommItem.getDocumentItem());
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.custom_task_info);
  
    btn_ok = (AppCompatButton)findViewById(R.id.btn_ok);
    btn_ok.setOnClickListener(this);
    
    tv_order_no = (AsapTextView)findViewById(R.id.tv_order_no);
    
    tv_name = (AsapTextView)findViewById(R.id.tv_name);
    tv_info_reference = (AsapTextView)findViewById(R.id.tv_info_reference);
    tv_info_reference_title = (AsapTextView)findViewById(R.id.tv_info_reference_title);
    tv_ref_id_1 = (AsapTextView)findViewById(R.id.tv_ref_id_1);
    tv_ref_id_1_title = (AsapTextView)findViewById(R.id.tv_ref_id_1_title);
    tv_ref_id_2 = (AsapTextView)findViewById(R.id.tv_ref_id_2);
    tv_ref_id_2_title = (AsapTextView)findViewById(R.id.tv_ref_id_2_title);
    tv_client = (AsapTextView)findViewById(R.id.tv_client);
    tv_client_title = (AsapTextView)findViewById(R.id.tv_client_title);
    tv_geo_data = (AsapTextView)findViewById(R.id.tv_geo_data);
    tv_loading_order = (AsapTextView)findViewById(R.id.tv_loading_order);
    tv_loading_order_title = (AsapTextView)findViewById(R.id.tv_loading_order_title);
    tv_adr_class = (AsapTextView)findViewById(R.id.tv_adr_class);
    tv_adr_class_title = (AsapTextView)findViewById(R.id.tv_adr_class_title);
    tv_un_no = (AsapTextView)findViewById(R.id.tv_un_no);
    tv_un_no_title = (AsapTextView)findViewById(R.id.tv_un_no_title);
    tv_number_of_paletts = (AsapTextView)findViewById(R.id.tv_number_of_paletts);
    tv_number_of_paletts_title = (AsapTextView)findViewById(R.id.tv_number_of_paletts_title);
    tv_dpl = (AsapTextView)findViewById(R.id.tv_dpl);
    tv_dpl_title = (AsapTextView)findViewById(R.id.tv_dpl_title);
    tv_contact = (AsapTextView)findViewById(R.id.tv_contact);
    tv_contact_title = (AsapTextView)findViewById(R.id.tv_contact_title);
    tv_notes = (AsapTextView)findViewById(R.id.tv_notes);
    tv_notes_title = (AsapTextView)findViewById(R.id.tv_notes_title);
    
    iv_dangerous_goods = (AppCompatImageView)findViewById(R.id.iv_dangerous_goods);
    iv_palette_info = (AppCompatImageView)findViewById(R.id.iv_palette_info);
    iv_contacts = (AppCompatImageView)findViewById(R.id.iv_contacts);
    iv_notes = (AppCompatImageView)findViewById(R.id.iv_notes);
    
    ll_dangerous_goods = (LinearLayout)findViewById(R.id.ll_dangerous_goods);
    ll_paletts = (LinearLayout)findViewById(R.id.ll_paletts);
    ll_notes = (LinearLayout)findViewById(R.id.ll_notes);
    ll_contacts = (LinearLayout)findViewById(R.id.ll_contacts);
    
    setupLayout();
  }
  
  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_ok:
      default:
        dismiss();
        break;
    }
  }
  
  private void applyRefId1(String refId1) {
    if (refId1 == null) {
      tv_ref_id_1.setVisibility(View.GONE);
      tv_ref_id_1_title.setVisibility(View.GONE);
    } else {
      tv_ref_id_1.setVisibility(View.VISIBLE);
      tv_ref_id_1_title.setVisibility(View.VISIBLE);
      tv_ref_id_1.setText(refId1);
    }
  }
  
  private void applyRefId2(String refId2) {
    if (refId2 == null) {
      tv_ref_id_2.setVisibility(View.GONE);
      tv_ref_id_2_title.setVisibility(View.GONE);
    } else {
      tv_ref_id_2.setVisibility(View.VISIBLE);
      tv_ref_id_2_title.setVisibility(View.VISIBLE);
      tv_ref_id_2.setText(refId2);
    }
  }
  
  private void applyInfoReference(String infoReference) {
    if (infoReference == null) {
      tv_info_reference_title.setVisibility(View.GONE);
      tv_info_reference.setVisibility(View.GONE);
    } else {
      tv_info_reference_title.setVisibility(View.VISIBLE);
      tv_info_reference.setVisibility(View.VISIBLE);
      tv_info_reference.setText(infoReference);
    }
  }
  
  private void applyLoadingOrder(Integer loadingOrder) {
    if (loadingOrder == null) {
      tv_loading_order_title.setVisibility(View.GONE);
      tv_loading_order.setVisibility(View.GONE);
    } else {
      tv_loading_order_title.setVisibility(View.VISIBLE);
      tv_loading_order.setVisibility(View.VISIBLE);
      tv_loading_order.setText(String.valueOf(loadingOrder.intValue()));
    }
  }
  
  private void applyDangerousGoods(DangerousGoods dangerousGoods) {
    if (dangerousGoods == null) {
      
      iv_dangerous_goods.setVisibility(View.GONE);
      iv_dangerous_goods.setImageDrawable(null);
      tv_adr_class_title.setVisibility(View.GONE);
      tv_adr_class.setVisibility(View.GONE);
      tv_un_no_title.setVisibility(View.GONE);
      tv_un_no.setVisibility(View.GONE);
      
      ll_dangerous_goods.setVisibility(View.GONE);
      return;
    }
    if (dangerousGoods.isGoodsDangerous() != null) {
      if (dangerousGoods.isGoodsDangerous()) {
        ll_dangerous_goods.setVisibility(View.VISIBLE);
        
        iv_dangerous_goods.setVisibility(View.VISIBLE);
        iv_dangerous_goods.setImageDrawable(null);
        
        if (dangerousGoods.getDangerousGoodsClassType() != null) {
          iv_dangerous_goods.setImageDrawable(getDangerousGoodsClass(dangerousGoods.getDangerousGoodsClassType()));
        }
        tv_adr_class_title.setVisibility(View.VISIBLE);
        tv_adr_class.setVisibility(View.VISIBLE);
        if (dangerousGoods.getAdrClass() != null) {
          tv_adr_class.setText(dangerousGoods.getAdrClass());
        }
        tv_un_no_title.setVisibility(View.VISIBLE);
        tv_un_no.setVisibility(View.VISIBLE);
        if (dangerousGoods.getUnNo() != null) {
          tv_un_no.setText(dangerousGoods.getUnNo());
        }
      } else {
        
        iv_dangerous_goods.setVisibility(View.GONE);
        iv_dangerous_goods.setImageDrawable(null);
        tv_adr_class_title.setVisibility(View.GONE);
        tv_adr_class.setVisibility(View.GONE);
        tv_un_no_title.setVisibility(View.GONE);
        tv_un_no.setVisibility(View.GONE);
        
        ll_dangerous_goods.setVisibility(View.GONE);
      }
    }
  }
  
  private void applyPallets(PalletExchange palletExchange) {
    if (palletExchange == null) {
      iv_palette_info.setVisibility(View.GONE);
      tv_number_of_paletts_title.setVisibility(View.GONE);
      tv_number_of_paletts.setVisibility(View.GONE);
      tv_dpl_title.setVisibility(View.GONE);
      tv_dpl.setVisibility(View.GONE);
      
      ll_paletts.setVisibility(View.GONE);
      return;
    }
    if(palletExchange.getPalletExchangeType().equals(EnumPalletExchangeType.YES)) {
      ll_paletts.setVisibility(View.VISIBLE);
      
      tv_number_of_paletts_title.setVisibility(View.VISIBLE);
      if (palletExchange.getPalletsAmount() != null) {
        tv_number_of_paletts.setVisibility(View.VISIBLE);
        tv_number_of_paletts.setText(String.valueOf(palletExchange.getPalletsAmount().intValue()));
      } else {
        tv_number_of_paletts.setVisibility(View.GONE);
      }
      
      tv_dpl_title.setVisibility(View.VISIBLE);
      if (palletExchange.isDPL() != null) {
        tv_dpl.setVisibility(View.VISIBLE);
        tv_dpl.setText(palletExchange.isDPL() ? _resources.getString(R.string.yes) : _resources.getString(R.string.no));
      } else {
        tv_dpl.setVisibility(View.GONE);
      }
    } else {
      ll_paletts.setVisibility(View.GONE);
    }
  }
  
  private void applyNotes(List<NotesItem> notes) {
    if (notes == null || notes.size() <= 0) {
      iv_notes.setVisibility(View.GONE);
      tv_notes_title.setVisibility(View.GONE);
      tv_notes.setVisibility(View.GONE);
      ll_notes.setVisibility(View.GONE);
      return;
    }
    
    ll_notes.setVisibility(View.VISIBLE);
    
    iv_notes.setVisibility(View.VISIBLE);
    tv_notes_title.setVisibility(View.VISIBLE);
    tv_notes.setVisibility(View.VISIBLE);
    
    String comment_text = _resources.getQuantityString(R.plurals.numberOfNotesAvailable, notes.size(), notes.size());
    tv_notes.setText(comment_text);
  }
  
  private void applyContacts(List<ContactItem> contacts) {
    if (contacts == null || contacts.size() <= 0) {
      iv_contacts.setVisibility(View.GONE);
      tv_contact_title.setVisibility(View.GONE);
      
      ll_contacts.setVisibility(View.GONE);
      return;
    }
    
    ll_contacts.setVisibility(View.VISIBLE);
    iv_contacts.setVisibility(View.VISIBLE);
    tv_contact_title.setVisibility(View.VISIBLE);
    tv_contact.setVisibility(View.VISIBLE);
    String contact_text = String.valueOf(contacts.size()) + " " + _resources.getString(R.string.contacts_available);
    tv_contact.setText(contact_text);
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
