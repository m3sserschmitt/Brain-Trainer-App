package com.example.braintrainer.ui.records;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.braintrainer.R;
import com.example.braintrainer.database.AppDatabase;
import com.example.braintrainer.database.RecordDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecordAdapter extends
        RecyclerView.Adapter<RecordAdapter.RecordViewHolder> implements Filterable {

    private final AppDatabase databaseInstance;

    private final List<RecordItem> recordList;
    private final List<RecordItem> recordListFull;

    static class RecordViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView title;
        private final Button deleteButton;
        private final Button shareButton;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.recordTitleTextView);
            deleteButton = itemView.findViewById(R.id.deleteRecordButton);
            shareButton = itemView.findViewById(R.id.shareRecordButton);
        }
    }

    RecordAdapter(Context context, List<RecordItem> records)
    {
        this.recordList = records;
        recordListFull = new ArrayList<>(records);

        databaseInstance = AppDatabase.getInstance(context);
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_layout, parent,
                false);

        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        RecordItem currentItem = recordList.get(position);
        holder.title.setText(currentItem.getTitle());

        holder.deleteButton.setTag(currentItem.getId());
        holder.deleteButton.setOnClickListener(v -> {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executorService.execute(() -> {
                RecordDao recordDao = databaseInstance.recordDao();

                final int itemId = Integer.parseInt(v.getTag().toString());

                recordDao.deleteOne(itemId);

                handler.post(() -> {
                    recordList.clear();

                    for(RecordItem recordItem: recordListFull)
                    {
                        if(recordItem.getId() == itemId)
                        {
                            recordListFull.remove(recordItem);
                            break;
                        }
                    }

                    recordList.addAll(recordListFull);
                    notifyItemRemoved(position);
                });
            });
        });

        holder.shareButton.setTag("Hi there! I've got " + currentItem.getCorrectAnswers()
                + " answers in " + currentItem.getTime() + " seconds in BrainTrainer App!");
        holder.shareButton.setOnClickListener(v -> {
            String textToShare = v.getTag().toString();

            Log.i("share_button_pressed", textToShare);

            // ToDo: implement data sharing functionality
        });
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    @Override
    public Filter getFilter() {
        return recordFilter;
    }

    private final Filter recordFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<RecordItem> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0)
            {
                filteredList.addAll(recordListFull);
            }else {
                String filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim();

                for(RecordItem item : recordListFull)
                {
                    if(item.getTitle().toLowerCase(Locale.ROOT).contains(filterPattern))
                    {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            recordList.clear();
            recordList.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };
}
