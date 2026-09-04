package com.milo.app.service

import android.content.Context
import androidx.work.*
import com.milo.app.data.local.MiloDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Battery-Aware Background Worker (Section 10, 11, 13)
 * Runs batched, deferred, network-aware sync queue drain only when conditions are optimal.
 */
class BatteryAwareSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val db = MiloDatabase.getInstance(applicationContext)
            val pending = db.syncDao().getPendingSyncItems()

            // Drain sync queue in batches
            pending.forEach { item ->
                // Simulate cloud synchronization with server
                db.syncDao().dequeue(item.syncId)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        fun schedulePeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<BatteryAwareSyncWorker>(
                12, TimeUnit.HOURS // Low-frequency battery-conscious schedule
            )
            .setConstraints(constraints)
            .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "MiloBatteryAwareSync",
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
        }
    }
}
