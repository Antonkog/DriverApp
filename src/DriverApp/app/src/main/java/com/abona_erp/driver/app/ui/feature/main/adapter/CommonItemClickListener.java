package com.abona_erp.driver.app.ui.feature.main.adapter;

import android.view.View;

import com.abona_erp.driver.app.data.entity.Notify;

public interface CommonItemClickListener<T> {
    void onClick(View view, int position, T item, boolean selected);

    void onDblClick(View view, int position, T item);

    void onProgressItemClick(Notify notify);

    void onMapClick(Notify notify);

    void onCameraClick(Notify notify);

    void onDocumentClick(Notify notify);
}