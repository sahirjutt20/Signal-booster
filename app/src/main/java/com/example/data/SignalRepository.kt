package com.example.data

import kotlinx.coroutines.flow.Flow

class SignalRepository(private val signalLogDao: SignalLogDao) {
    val allLogs: Flow<List<SignalLog>> = signalLogDao.getAllLogs()

    suspend fun insert(log: SignalLog) {
        signalLogDao.insertLog(log)
    }

    suspend fun clearAll() {
        signalLogDao.clearLogs()
    }
}
