package com.dataexpo.facedataexpo.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dataexpo.facedataexpo.R;
import com.dataexpo.facedataexpo.listener.OnDialogClickListener;

public class ImportDialog extends Dialog {
    private Context mContext;
    public TextView tv_status;

    private OnDialogClickListener dialogClickListener;

    public ImportDialog(Context context) {
        this(context, R.style.LoginDialogStyle);
    }

    public ImportDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View view = inflater.inflate(R.layout.layout_import_status, null);
        setContentView(view);

        view.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogClickListener != null) {
                    dialogClickListener.onConfirmClick(view);
                }
            }
        });

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogClickListener != null) {
                    dialogClickListener.onModifierClick(view);
                }
            }
        });

        tv_status = view.findViewById(R.id.tv_import_status);
    }

    @Override
    public void show() {
        super.show();
    }

    public void setDialogClickListener(OnDialogClickListener listener) {
        this.dialogClickListener = listener;
    }

    public interface OnDialogClickListener {
        void onConfirmClick(View view);

        void onModifierClick(View view);
    }

}