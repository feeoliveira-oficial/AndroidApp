package com.example.androidapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTaskName, textViewTaskDesc, textViewDueDate, textViewPriority;
        public CardView cardView;
        public Switch switchTaskStatus;

        public TaskViewHolder(View itemView) {
            super(itemView);
            textViewTaskName = itemView.findViewById(R.id.textViewTaskName);
            textViewTaskDesc = itemView.findViewById(R.id.textViewTaskDescription);
            textViewDueDate = itemView.findViewById(R.id.textViewDueDate);
            textViewPriority = itemView.findViewById(R.id.textViewPriority);
            cardView = itemView.findViewById(R.id.cardViewTask);
            switchTaskStatus = itemView.findViewById(R.id.switchTaskStatus);
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.textViewTaskName.setText(task.getTaskName());
        holder.textViewTaskDesc.setText(task.getTaskDescription());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (task.getDueDate() != null) {
            holder.textViewDueDate.setText(sdf.format(task.getDueDate().getTime()));
        }

        holder.textViewPriority.setText(task.getPriority());
        updateTaskCardColor(task, holder);

        holder.switchTaskStatus.setChecked(task.isCompleted());
        holder.switchTaskStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            ((MainActivity) holder.itemView.getContext()).updateProgressBar();
            updateTaskCardColor(task, holder);
        });
    }

    private void updateTaskCardColor(Task task, TaskViewHolder holder) {
        if (task.isCompleted()) {
            holder.cardView.setCardBackgroundColor(Color.GRAY);
        } else {
            switch (task.getPriority()) {
                case "Low":
                    holder.cardView.setCardBackgroundColor(Color.parseColor("#AED581"));
                    break;
                case "Medium":
                    holder.cardView.setCardBackgroundColor(Color.parseColor("#FFEB3B"));
                    break;
                case "High":
                    holder.cardView.setCardBackgroundColor(Color.parseColor("#EF5350"));
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}