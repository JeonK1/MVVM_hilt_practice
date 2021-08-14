package com.example.mvvm_hilt_practice.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.mvvm_hilt_practice.data.TaskDao

class TasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao
): ViewModel() {
    // orientation 변화에도 영향주지 않도록 여기서 task를 잡아두자
    val tasks = taskDao.getTasks().asLiveData()
}