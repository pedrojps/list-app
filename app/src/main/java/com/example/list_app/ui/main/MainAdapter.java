package com.example.list_app.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.adapters.ListenerUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.list_app.R;
import com.example.list_app.common.time.AppDownloadImage;
import com.example.list_app.data.entities.Item;
import com.example.list_app.databinding.ItemMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private ArrayList<Item> localDataSet = new ArrayList<>();

    public final ObservableField<Item> dataOnClick = new ObservableField<Item>();

    public MainAdapter(ArrayList<Item> dataSet) {
        localDataSet = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_main, viewGroup, false);

        return new ViewHolder(Objects.requireNonNull(DataBindingUtil.bind(view)));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Item item = localDataSet.get(position);

        viewHolder.bind(item);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataOnClick.set(item);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void updateDataSet(List<Item> data, boolean b) {
        if(!localDataSet.containsAll(data)) {
            localDataSet.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void clearData() {
        localDataSet = new ArrayList<>();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemMainBinding mBinding;

        public ViewHolder(ItemMainBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bind(Item item) {

            mBinding.title.setText(item.name);
            mBinding.descricao.setText(item.description);
            mBinding.username.setText(item.owner.login);
            mBinding.forkNumber.setText(String.valueOf(item.forks_count));
            mBinding.starNumber.setText(String.valueOf(item.stargazers_count));

            AppDownloadImage.GetAppDownloadImage().getImage(item.owner.avatar_url,mBinding.image);
        }

    }
}
