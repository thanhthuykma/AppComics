package com.example.appcomics.Adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appcomics.Model.Catergory;

import java.util.ArrayList;
import java.util.List;
import com.example.appcomics.R;

public class CatergoryAdapter extends RecyclerView.Adapter<CatergoryAdapter.ViewHolder> {

    Context context;
    List<Catergory> catergoryList;
    SparseBooleanArray itemStateArray = new SparseBooleanArray();
    List<Catergory> selected_category = new ArrayList<>();

    public CatergoryAdapter(Context context, List<Catergory> catergoryList) {
        this.context = context;
        this.catergoryList = catergoryList;
    }
    public List<Catergory> getSelectedCategories() {
        return selected_category;
    }


    @NonNull
    @Override
    public CatergoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.check_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatergoryAdapter.ViewHolder holder, int position) {
        holder.ckb_options.setText(catergoryList.get(position).getName());
        holder.ckb_options.setChecked(itemStateArray.get(position));
    }

    @Override
    public int getItemCount() {
        return catergoryList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox ckb_options;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ckb_options = (CheckBox) itemView.findViewById(R.id.check_options);
            ckb_options.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int adapterPosition = getAdapterPosition();
                    itemStateArray.put(adapterPosition,isChecked);
                    if (isChecked){
                        selected_category.add(catergoryList.get(adapterPosition));
                    }
                    else
                        selected_category.remove(catergoryList.get(adapterPosition));
                }
            });

        }
    }
}
