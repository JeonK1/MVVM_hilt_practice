package com.example.mvvm_hilt_practice.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import com.example.mvvm_hilt_practice.ui.tasks.SortOrder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesManager"

data class FilterPreference(val sortOrder: SortOrder, val hideCompleted: Boolean)

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {
    private val datastore = context.createDataStore("user_preferences")

    val preferencesFlow = datastore.data // outside 로 보내줄 Flow
        .catch { exception ->
            // exception이 발생할 수 있음
            if(exception is IOException){
                // data reading 하는 시점에서 문제 발생
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences()) // default (BY_DATE, false) 로 설정
            } else {
                throw exception
            }
        }
        .map { preferences ->
            // Flow<Preference> to
            val sortOrder = SortOrder.valueOf(
                preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.BY_DATE.name // default date, (?: 는 좌측이 null 이면 우측 값이 대입되는 Elvis 연산자)
            )
            val hideCompleted = preferences[PreferencesKeys.HIDE_COMPLETED] ?: false
            FilterPreference(sortOrder, hideCompleted) // 이렇게 FilterPreference로 만들었으니 ViewModel에서 굳이 변환 안해줘 됨
        }

    // update sortOrder async function
    suspend fun updateSortOrder(sortOrder: SortOrder) {
        datastore.edit { preferences ->
            preferences[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    // update hideCompleted async function
    suspend fun updateHideCompleted(hideCompleted: Boolean){
        datastore.edit { preferences ->
            preferences[PreferencesKeys.HIDE_COMPLETED] = hideCompleted
        }
    }


    private object PreferencesKeys {
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_COMPLETED = preferencesKey<Boolean>("hide_completed")
    }
}
