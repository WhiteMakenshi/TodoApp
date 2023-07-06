package com.example.todoapp.addtasks.ui.model

sealed interface TasksUiState {
    object Loading: TasksUiState
    data class Error(val throwable: Throwable): TasksUiState
    data class Success(val tasks:List<TaskModel>): TasksUiState
}