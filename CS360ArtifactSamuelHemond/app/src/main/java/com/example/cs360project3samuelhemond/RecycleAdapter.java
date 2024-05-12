package com.example.cs360project3samuelhemond;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

//recycle Adapter for recycle view
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.MyViewHolder> {
    private static final String TAG = "RecycleAdapter";
    //variables for recycle view.
    Context context;
    ArrayList<DailyWeight> weights;
    LayoutInflater layoutInflater;
    ClickListener listener;

    //constructor for RecycleAdapter
    public RecycleAdapter(Context context, ArrayList<DailyWeight> weights, ClickListener listener){
        this.context = context;
        this.weights = weights;
        this.layoutInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    //located recycler view
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.recycle_item,parent,false);
        return new MyViewHolder(view, listener);
    }

    //set values is recycle_item.xml
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.weight.setText(String.valueOf(weights.get(position).getWeight() + " Lbs"));
        holder.date.setText(String.valueOf(weights.get(position).getDate()));
    }

    //return number of items in recycle view
    @Override
    public int getItemCount() {
        return weights.size();
    }

    //ViewHolder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView weight, date;
        Button deleteButton;
        private WeakReference<ClickListener> listenerRef;

        //constructor for ViewHolder
        public MyViewHolder(@NonNull View itemView, ClickListener listener) {
            super(itemView);
            Log.i(TAG, "MyViewHolder");

            //store listeners
            listenerRef = new WeakReference<>(listener);
            //get views
            weight =  itemView.findViewById(R.id.weightValue);
            date =  itemView.findViewById(R.id.dateValue);
            deleteButton = (Button) itemView.findViewById(R.id.deleteRow);

            deleteButton.setOnClickListener(this);

        }
        //when button is clicked set positions using ClickListener class
        @Override
        public void onClick(View v) {

            if (v.getId() == deleteButton.getId()) {
                Log.i(TAG, "ITEM PRESSED = " + String.valueOf(getAdapterPosition()));
                listenerRef.get().onClick(getAdapterPosition());
            }

        }

    }
}
