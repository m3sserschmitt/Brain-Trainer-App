package com.example.braintrainer.ui.game;

import static android.content.Context.MODE_PRIVATE;

import static java.lang.String.format;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.braintrainer.R;
import com.example.braintrainer.database.AppDatabase;
import com.example.braintrainer.database.Record;
import com.example.braintrainer.database.RecordDao;
import com.example.braintrainer.databinding.FragmentGameBinding;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayFragment extends Fragment {

    private FragmentGameBinding binding;

    private FragmentActivity activity;

    private SharedPreferences appSettings;
    private static AppDatabase databaseInstance;

    private CountDownTimer timer;

    private int correctAnswerPosition;

    private Random random;

    private int totalAnswers;
    private int correctAnswers;

    private void showScore()
    {
        binding.scoreTextView.setText(String.format("%d/%d", correctAnswers, totalAnswers));
    }

    private void resetScore()
    {
        totalAnswers = 0;
        correctAnswers = 0;

        showScore();
    }

    private void showTime(int time)
    {
        try {
            binding.timeTextView.setText(time + "s");
        } catch (Exception e)
        {
            timer.cancel();
        }
    }

    private void resetTime()
    {
        int time = appSettings.getInt("time", -1);

        showTime(time);
    }

    private void resetGame()
    {
        gameStartedLayout();
        resetScore();
        resetTime();
    }

    private void gameStartedLayout()
    {
        binding.startButton.setVisibility(View.INVISIBLE);
        binding.timeTextView.setVisibility(View.VISIBLE);
        binding.buttonsGridLayout.setVisibility(View.VISIBLE);
        binding.scoreTextView.setVisibility(View.VISIBLE);

        binding.resultTextView.setText("");
    }

    private void gameEndedLayout()
    {
        binding.startButton.setVisibility(View.VISIBLE);
        binding.timeTextView.setVisibility(View.INVISIBLE);
        binding.buttonsGridLayout.setVisibility(View.INVISIBLE);
        binding.scoreTextView.setVisibility(View.INVISIBLE);
        binding.problemTextView.setText(R.string.game_ended);
        binding.startButton.setText(R.string.play_again);
    }

    private void showNextAdditionSubtractionProblem(String problem, int correctAnswer)
    {
        binding.problemTextView.setText(problem);

        // generate 3 wrong answers;
        correctAnswerPosition = random.nextInt(4);
        int wrongAnswer;
        ArrayList<Integer> answers = new ArrayList<>();

        for(int i = 0; i < 4; i ++)
        {
            Log.i("positions", i + " " + correctAnswerPosition);

            if(i == correctAnswerPosition)
            {
                answers.add(correctAnswer);
                continue;
            }

            do {
                wrongAnswer = random.nextInt(201);
            } while (wrongAnswer == correctAnswer);

            answers.add(wrongAnswer);
            Log.i("answer", Integer.toString(answers.get(i)));
        }

        binding.button0.setText(format("%d",answers.get(0)));
        binding.button1.setText(format("%d",answers.get(1)));
        binding.button2.setText(format("%d",answers.get(2)));
        binding.button3.setText(format("%d",answers.get(3)));
    }

    private void generateAdditionSubtractionProblem()
    {
        String problemText = "";

        int a = random.nextInt(101);
        int b = random.nextInt(101);
        int op = random.nextInt(2);
        int result;

        if(op == 0) // addition
        {
            result = a + b;
            problemText = a + " + " + b;
        } else { // subtraction
            result = a - b;
            problemText = a + " - " + b;
        }

        problemText += " = ?";

        showNextAdditionSubtractionProblem(problemText, result);
    }

    private void generateProblem()
    {
        generateAdditionSubtractionProblem();
    }

    private void saveLastScore()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("New record");

        final EditText input = new EditText(activity);
        input.setHint("Enter your name");

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Ok", (dialog, which) -> {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executorService.execute(() -> {
                RecordDao recordDao = databaseInstance.recordDao();

                int currentTime = appSettings.getInt("time", -1);

                Record newRecord = new Record();
                newRecord.setScore(correctAnswers, currentTime);
                newRecord.setTitle(input.getText().toString());

                recordDao.insertAll(newRecord);

                handler.post(() -> {
                    Toast.makeText(activity, "New record saved", Toast.LENGTH_SHORT).show();
                });
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void checkRecords()
    {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            final int currentTime = appSettings.getInt("time", -1);

            RecordDao recordDao = databaseInstance.recordDao();
            Record best = recordDao.getBest(currentTime);

            handler.post(() -> {
                if(correctAnswers != 0 &&
                        (best == null || best.smallerThan(correctAnswers, currentTime)))
                {
                    saveLastScore();
                }
            });
        });
    }

    private void startTimer()
    {
        int time = appSettings.getInt("time", -1);

        if(time > 0)
        {
            timer = new CountDownTimer((long)time * 1000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    showTime((int)millisUntilFinished / 1000);
                }

                @Override
                public void onFinish() {
                    showTime(0);
                    gameEndedLayout();
                    checkRecords();
                }
            };

            timer.start();
        }
    }

    public void startGame()
    {
        resetGame();
        generateProblem();
        startTimer();
    }

    public void chooseAnswer(@NonNull View view)
    {
        String pressedButtonTag = view.getTag().toString();

        if(pressedButtonTag.equals(Integer.toString(correctAnswerPosition)))
        {
            binding.resultTextView.setText(R.string.correct);
            correctAnswers += 1;
        } else {
            binding.resultTextView.setText(R.string.wrong);
        }

        totalAnswers += 1;

        showScore();
        generateProblem();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentGameBinding.inflate(inflater, container, false);


        activity = requireActivity();

        databaseInstance = AppDatabase.getInstance(activity);
        appSettings = activity.getSharedPreferences("com.example.braintrainer", MODE_PRIVATE);

        random = new Random();

        binding.startButton.setOnClickListener(v -> startGame());

        binding.button0.setOnClickListener(this::chooseAnswer);

        binding.button1.setOnClickListener(this::chooseAnswer);

        binding.button2.setOnClickListener(this::chooseAnswer);

        binding.button3.setOnClickListener(this::chooseAnswer);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}