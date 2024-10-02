package com.example.androidapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText editTextTaskName, editTaskDesc;
    private Button buttonAddTask, buttonDueDate, logoutButton;
    private Spinner spinnerPriority;
    private RecyclerView recyclerViewTask;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private ProgressBar progressBar;
    private Calendar dueDate;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference tasksCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        tasksCollection = db.collection("tasks");

        initializeUIComponents();
        setupRecyclerView();
        loadTasks();
        buttonAddTask.setOnClickListener(view -> addNewTask(view));
        buttonDueDate.setOnClickListener(v -> showDatePickerDialog());
        logoutButton.setOnClickListener(v -> logoutUser());
    }

    private void initializeUIComponents() {
        editTextTaskName = findViewById(R.id.editTextTaskName);
        editTaskDesc = findViewById(R.id.editTextTaskDesc);
        buttonAddTask = findViewById(R.id.buttonAddTask);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        buttonDueDate = findViewById(R.id.buttonDueDate);
        progressBar = findViewById(R.id.progressBar);
        recyclerViewTask = findViewById(R.id.recyclerViewTasks);
        logoutButton = findViewById(R.id.logoutButton);
    }

    private void setupRecyclerView() {
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList);
        recyclerViewTask.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTask.setAdapter(taskAdapter);
        progressBar.setMax(100);
    }

    private void addNewTask(View view) {
        String taskName = editTextTaskName.getText().toString().trim();
        String taskDesc = editTaskDesc.getText().toString().trim();
        String priority = spinnerPriority.getSelectedItem().toString();

        if (!taskName.isEmpty() && !taskDesc.isEmpty()) {
            Task newTask = new Task(taskName, taskDesc, priority, dueDate);
            taskList.add(newTask);
            taskAdapter.notifyItemInserted(taskList.size() - 1);

            // Save task to Firestore
            Map<String, Object> taskMap = new HashMap<>();
            taskMap.put("name", taskName);
            taskMap.put("description", taskDesc);
            taskMap.put("priority", priority);
            taskMap.put("dueDate", dueDate != null ? dueDate.getTime() : null);
            taskMap.put("isCompleted", false);

            tasksCollection.add(taskMap)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("Firestore", "Task added with ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firestore", "Error adding task", e);
                    });

            updateProgressBar();
            clearFields();

            Snackbar.make(view, "Task added", Snackbar.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Please fill out both fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadTasks() {
        tasksCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                taskList.clear(); // Clear the list before adding new tasks
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    String description = document.getString("description");
                    String priority = document.getString("priority");
                    boolean isCompleted = document.getBoolean("isCompleted") != null && document.getBoolean("isCompleted");

                    // Handle the due date as Calendar object
                    Calendar dueDate = Calendar.getInstance();
                    if (document.getDate("dueDate") != null) {
                        dueDate.setTime(document.getDate("dueDate"));
                    }

                    Task taskItem = new Task(name, description, priority, dueDate);
                    taskItem.setCompleted(isCompleted);
                    taskList.add(taskItem);
                }
                taskAdapter.notifyDataSetChanged();
            } else {
                Log.w("Firestore", "Error getting tasks.", task.getException());
            }
        });
    }

    // Method to update progress bar based on task completion
    public void updateProgressBar() {
        int completedTasks = 0;
        for (Task task : taskList) {
            if (task.isCompleted()) {
                completedTasks++;
            }
        }

        int progressPercentage = (taskList.size() > 0) ?
                (int) ((completedTasks / (float) taskList.size()) * 100) : 0;

        progressBar.setProgress(progressPercentage);
    }

    private void clearFields() {
        editTextTaskName.setText("");
        editTaskDesc.setText("");
        dueDate = null;
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            dueDate = Calendar.getInstance();
            dueDate.set(year1, month1, dayOfMonth);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void logoutUser() {
        // Sign out the user from Firebase
        mAuth.signOut();

        // Redirect to LoginActivity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);

        // Finish current activity
        finish();
    }
}
