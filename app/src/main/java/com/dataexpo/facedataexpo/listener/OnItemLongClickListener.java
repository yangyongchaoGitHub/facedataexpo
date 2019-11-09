package com.dataexpo.facedataexpo.listener;

import android.view.View;

@FunctionalInterface
public interface OnItemLongClickListener {
    void onLongItemClick(View view, int position);
}
