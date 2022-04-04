package com.example.braintrainer.ui.records;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.braintrainer.R;
import com.example.braintrainer.database.AppDatabase;
import com.example.braintrainer.database.Record;
import com.example.braintrainer.database.RecordDao;
import com.example.braintrainer.databinding.FragmentRecordsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecordsFragment extends Fragment {

    private FragmentRecordsBinding binding;

    private AppDatabase databaseInstance;

    private RecordAdapter recordAdapter;
    private List<RecordItem> recordItemList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        databaseInstance = AppDatabase.getInstance(requireActivity());
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRecordsBinding.inflate(inflater, container, false);

        populateRecordList();

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.record_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.actionSearch);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                recordAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void populateRecordList()
    {
        recordItemList = new ArrayList<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            RecordDao recordDao = databaseInstance.recordDao();

            List<Record> records = recordDao.getAll();

            handler.post(() -> {

                if(records.size() == 0)
                {
                    Toast.makeText(requireActivity(), "No records yet",
                            Toast.LENGTH_SHORT).show();
                }else {
                    for(Record record : records)
                    {
                        String title = record.getTitle() + " - " + record.getCorrectAnswers()
                                + " correct answers - " + record.getTime() + "s";

                        recordItemList.add(new RecordItem(title, record.getId(),
                                record.getCorrectAnswers(), record.getTime()));
                    }
                    setUpRecyclerView();
                }
            });
        });
    }

    private void setUpRecyclerView()
    {
        recordAdapter = new RecordAdapter(requireActivity(), recordItemList);
        RecyclerView recyclerView = binding.recordsRecyclerView;
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recordAdapter);
    }

}