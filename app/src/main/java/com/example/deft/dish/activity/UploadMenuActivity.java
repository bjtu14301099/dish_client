package com.example.deft.dish.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import com.example.deft.dish.R;

public class UploadMenuActivity extends AppCompatActivity {

    private String[] areas1 = new String[]{"初级","中级","高级" };
    private String[] areas2 = new String[]{"10分钟以内","10分钟到1小时","1小时以上" };
    private Button bt_level;
    private Button bt_time;
    private Button bt_add;
    private Button back;
    private Button next;
    private LinearLayout ll;
    private ScrollView scroll;
    private EditText name;
    private EditText et[];
    private int et_num;
    private int width;

    private boolean checkIfNull()
    {
        int i;
        for(i=0;i<et_num;i+=2)
        {
            if(et[i].getText().toString().equals("")&&et[i+1].getText().toString().equals(""))
            {
                Toast.makeText(this,"请先填写完之前的材料信息",Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int et_num=0;
        et=new EditText[40];

        setContentView(R.layout.activity_main);
        ll=(LinearLayout)findViewById(R.id.linear);
        scroll=(ScrollView)findViewById(R.id.scrollView);
        bt_level=(Button)findViewById(R.id.button3);
        bt_time=(Button)findViewById(R.id.button4);
        bt_add=(Button)findViewById(R.id.button5);
        back=(Button)findViewById(R.id.button);
        next=(Button)findViewById(R.id.button2);
        bt_level.setOnClickListener(new btlListener());
        bt_time.setOnClickListener(new bttListener());
        bt_add.setOnClickListener(new btaListener());
        back.setOnClickListener(new backListener());
        next.setOnClickListener(new nextListener());
        name=(EditText)findViewById(R.id.editText);
        bt_add.callOnClick();

    }
    class btaListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(checkIfNull()) {
                Point size = new Point();
                getWindowManager().getDefaultDisplay().getSize(size);
                width = size.x;
                et[et_num] = new EditText(UploadMenuActivity.this);
                et[et_num + 1] = new EditText(UploadMenuActivity.this);
                LinearLayout ll1 = new LinearLayout(UploadMenuActivity.this);
                ll1.setOrientation(LinearLayout.HORIZONTAL);
                et[et_num + 1].setLeft(10);
                int w1=width/2;
                int w2=width/2;

                et[et_num].setWidth(w1);
                et[et_num + 1].setWidth(w2);
                et[et_num].setHint("用料：例如五花肉");
                et[et_num + 1].setHint("用量：例如250g");
                ll1.addView(et[et_num]);
                ll1.addView(et[et_num + 1]);
                ll.addView(ll1);
                int offset = scroll.getHeight() + 20;
                scroll.scrollTo(0, offset);
                et_num += 2;
            }
        }
    }
    class btlListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            new AlertDialog.Builder(UploadMenuActivity.this).setTitle("难度选择").setItems(areas1,new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    Toast.makeText(UploadMenuActivity.this, "您已经选择了: " +":" + areas1[which],Toast.LENGTH_LONG).show();
                    bt_level.setText(areas1[which]);
                    dialog.dismiss();
                }
            }).show();
        }
    }
    class bttListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            new AlertDialog.Builder(UploadMenuActivity.this).setTitle("时间选择").setItems(areas2,new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    Toast.makeText(UploadMenuActivity.this, "您已经选择了: " +":" + areas2[which],Toast.LENGTH_LONG).show();
                    bt_time.setText(areas2[which]);
                    dialog.dismiss();
                }
            }).show();
        }
    }
    class backListener implements View.OnClickListener{
        @Override
        public void onClick(View v)
        {

        }
    }

    class nextListener implements View.OnClickListener{
        @Override
        public void onClick(View v)
        {
            Intent intent=new Intent();
            intent.setClass(UploadMenuActivity.this,ProcessActivity.class);

            Bundle bundle=new Bundle();
            bundle.putString("name",name.getText().toString());
            bundle.putStringArray("ingredient",getIngredientString(et,et_num));
            bundle.putStringArray("amount",getAmountString(et,et_num));
            bundle.putInt("num",et_num);
            bundle.putString("level",bt_level.getText().toString());
            bundle.putString("time",bt_time.getText().toString());
            intent.putExtras(bundle);

            startActivity(intent);


        }
        public String[] getIngredientString(EditText et[],int et_num)
        {
            String []a;
            a=new String[20];
            for(int i=0;i<et_num;i+=2)
            {
                a[i/2]=new String();
                a[i/2]=et[i].getText().toString();
            }
            return a;
        }
        public String[] getAmountString(EditText et[],int et_num)
        {
            String []a;
            a=new String[20];
            for(int i=0;i<et_num;i+=2)
            {
                a[i/2]=new String();
                a[i/2]=et[i+1].getText().toString();
            }
            return a;
        }
    }

}


