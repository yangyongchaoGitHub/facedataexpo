package com.dataexpo.facedataexpo.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dataexpo.facedataexpo.R;
import com.dataexpo.facedataexpo.listener.OnDialogClickListener;

public class RegistSelectDialog extends Dialog {
    public static final int REGIST_BY_PHOTO = 1;
    public static final int REGIST_BY_GALLERY = 2;
    public static final int REGIST_BY_DEPOSITORY = 3;
    private Context mContext;
    Button btn_photo;
    Button btn_gallery;
    Button btn_depository;

    private OnDialogClickListener dialogClickListener;

    public RegistSelectDialog(Context context) {
        this(context, R.style.LoginDialogStyle);
    }

    public RegistSelectDialog(Context context, int themeResId) {
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
        final View view = inflater.inflate(R.layout.layout_regist_select, null);
        setContentView(view);

        view.findViewById(R.id.btn_regist_by_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogClickListener != null) {
                    dialogClickListener.onDialogItemClick(REGIST_BY_PHOTO);
                }
            }
        });

        view.findViewById(R.id.btn_regist_by_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogClickListener != null) {
                    dialogClickListener.onDialogItemClick(REGIST_BY_GALLERY);
                }
            }
        });

        view.findViewById(R.id.btn_regist_by_depository).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogClickListener != null) {
                    dialogClickListener.onDialogItemClick(REGIST_BY_DEPOSITORY);
                }
            }
        });
    }

    @Override
    public void show() {
        super.show();
    }

    public void setDialogClickListener(OnDialogClickListener listener) {
        this.dialogClickListener = listener;
    }
}