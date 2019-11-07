package com.dataexpo.facedataexpo.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.dataexpo.facedataexpo.R;

public class RegistUserDialog extends Dialog {
    private Context mContext;
    public EditText et_name;
    private LoginDialog.OnDialogClickListener dialogClickListener;

    public RegistUserDialog(Context context) {
        this(context, 0);
    }

    public RegistUserDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View view = inflater.inflate(R.layout.layout_regist_user_dialog, null);
        setContentView(view);

        view.findViewById(R.id.btn_comfirm_regist_user_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogClickListener != null) {
                    dialogClickListener.onConfirmClick(view);
                }
            }
        });

        view.findViewById(R.id.btn_cancel_regist_user_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogClickListener != null) {
                    dialogClickListener.onModifierClick(view);
                }
            }
        });

        et_name = view.findViewById(R.id.et_regist_user_dialog_name);
    }

    public void setDialogClickListener(LoginDialog.OnDialogClickListener listener) {
        this.dialogClickListener = listener;
    }

    public interface OnDialogClickListener {
        void onConfirmClick(View view);

        void onModifierClick(View view);
    }
}
