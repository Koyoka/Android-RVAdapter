package com.eleven.rvadapterdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eleven.rvadapter.ComAdapterFactory;
import com.eleven.rvadapter.CommonRcyAdapter;
import com.eleven.rvadapter.base.BindResId;
import com.eleven.rvadapter.base.OnRecyclerItemClickListen;
import com.eleven.rvadapter.event.RcyBaseViewHolder;
import com.eleven.rvadapter.event.RcyHolderViewEventMng;
import com.eleven.rvadapter.event.RcyHolderViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private ArrayList<MyViewModel> source;
    private CommonRcyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);


        source = new ArrayList<>();
        for(int i = 0;  i< 150; i++){
            source.add(new MyViewModel("00"+i));
        }

        adapter = ComAdapterFactory.newInstance()
                .injectRecyclerView(this, source, recyclerView, MyViewHolder.class);
        adapter.setOnRecyclerItemClickListen(position -> {
            Toast.makeText(this, source.get(position).text, Toast.LENGTH_LONG).show();
        });

    }


    //region recyclerView

    public static class MyViewModel extends RcyHolderViewModel{
        public String text;
        public MyViewModel(String text){
            this.text = text;
        }
    }

    @BindResId(R.layout.item_text_view)
    public static class MyViewHolder extends RcyBaseViewHolder<MyViewModel>  implements RcyHolderViewEventMng.RegisterItemClick{
        private TextView txtView;
        public MyViewHolder(View itemView) {
            super(itemView);
            txtView = $(itemView, R.id.txtView);
        }

        @Override
        public void bindData(MyViewModel vm) {
            txtView.setText(vm.text);
        }

        @Override
        public View getClickItemView() {
            return txtView;
        }
    }
    //endregion
}
