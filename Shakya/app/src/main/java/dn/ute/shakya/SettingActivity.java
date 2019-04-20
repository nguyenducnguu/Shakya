package dn.ute.shakya;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import dn.ute.shakya.common.Const;

public class SettingActivity extends AppCompatActivity{
    Button btn_back;
    TextView tv_duration;
    LinearLayout ln_setDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        addView();
        addEvent();

        tv_duration.setText(this.getSharedPreferences(Const.DURATION, Context.MODE_PRIVATE).getInt(Const.DURATION, 1) + " seconds");
    }

    private void addView(){
        btn_back = findViewById(R.id.btn_back);
        tv_duration = findViewById(R.id.tv_duration);
        ln_setDuration = findViewById(R.id.ln_setDuration);
    }

    private void addEvent(){
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ln_setDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogNumberPicker();
            }
        });
    }

    public void showDialogNumberPicker()
    {
        final Dialog d = new Dialog(SettingActivity.this);
        d.setTitle("Duration (seconds)");
        d.setContentView(R.layout.dialog_set_duration);
        Button btn_set = d.findViewById(R.id.btn_set);
        Button btn_cancel = d.findViewById(R.id.btn_cancel);
        final NumberPicker np = d.findViewById(R.id.numberPicker);
        np.setMaxValue(180);
        np.setMinValue(1);
        SharedPreferences sharedPref = this.getSharedPreferences(Const.DURATION, Context.MODE_PRIVATE);
        np.setValue(sharedPref.getInt(Const.DURATION, 1));
        np.setWrapSelectorWheel(false);
        final Activity mActivity = this;
        btn_set.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                int duration = np.getValue();
                SharedPreferences.Editor editor = mActivity.getSharedPreferences(Const.DURATION, Context.MODE_PRIVATE).edit();
                editor.putInt(Const.DURATION, duration);
                editor.commit();
                d.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                tv_duration.setText(mActivity.getSharedPreferences(Const.DURATION, Context.MODE_PRIVATE).getInt(Const.DURATION, 1) + " seconds");
            }
        });
        d.show();

        //Grab the window of the dialog, and change the width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = d.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }
}
