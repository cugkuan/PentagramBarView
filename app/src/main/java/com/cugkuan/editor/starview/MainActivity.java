package com.cugkuan.editor.starview;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.cugkuan.widget.PentagramBarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);

        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 50;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {


                if (convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.list,null);
                }
                PentagramBarView starBarView = convertView.findViewById(R.id.star);

                switch (position % 3){
                    case  0:
                        starBarView.setProgress(20);
                        starBarView.setMax(100);
                        starBarView.setCrRatio(PentagramBarView.REGULAR_RATIO);
                        break;
                    case  1:
                        starBarView.setProgress(30);
                        starBarView.setMax(100);
                        starBarView.setCrRatio(0.6f);
                        break;
                    case  2:
                        starBarView.setProgress(70);
                        starBarView.setMax(100);
                        starBarView.setCrRatio(2f);
                        break;
                }

                return convertView;
            }
        });

    }

}
