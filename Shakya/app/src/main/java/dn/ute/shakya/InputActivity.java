package dn.ute.shakya;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class InputActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_cancel, btn_done;
    EditText edt_data;
    ArrayList<String> lstData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        addView();
        addEvent();
        receiveData();
        if(lstData.size() > 0){
            edt_data.setText("");
            for(int i = 0; i < lstData.size(); i++){
                edt_data.setText(edt_data.getText().toString() + lstData.get(i) + "\n");
            }
        }
    }

    private void receiveData(){
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getSerializableExtra("lstData") != null) {
                lstData = (ArrayList<String>) getIntent().getSerializableExtra("lstData");
                return;
            }
            return;
        }
    }

    private void addView(){
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_done = findViewById(R.id.btn_done);
        edt_data = findViewById(R.id.edt_data);
    }

    private void addEvent(){
        btn_cancel.setOnClickListener(this);
        btn_done.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.btn_cancel:
                intent = new Intent();
                setResult(Activity.RESULT_CANCELED, intent);
                this.finish();
                break;
            case R.id.btn_done:
                if(edt_data.getText().toString().trim().length() == 0){
                    Toast.makeText(this, "Empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String[] arr = edt_data.getText().toString().trim().split("\n");
                lstData.clear();
                for(int i = 0; i < arr.length; i++) {
                    if(arr[i].trim().length() > 0)
                        lstData.add(arr[i].trim());
                }
                intent = new Intent();
                intent.putExtra("lstData", lstData);
                setResult(Activity.RESULT_OK, intent);
                this.finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            Intent intent = new Intent();
            setResult(Activity.RESULT_CANCELED, intent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
