package com.example.todoapp.addtasks.data

import com.example.todoapp.addtasks.ui.model.TaskModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDAO: TaskDAO
) {
    val tasks: Flow<List<TaskModel>> =
        taskDAO.getTasks().map {items -> items.map {TaskModel(it.id, it.task, it.selected)}}

    suspend fun add(taskModel: TaskModel) {
        taskDAO.addTask(taskModel.toData())
    }

    suspend fun update(taskModel: TaskModel) {
        taskDAO.updateTask(taskModel.toData())
    }

    suspend fun delete(taskModel: TaskModel) {
        taskDAO.deleteTask(taskModel.toData())
    }
}

fun TaskModel.toData():TaskEntity{
    return TaskEntity(this.id, this.task, this.selected)
}