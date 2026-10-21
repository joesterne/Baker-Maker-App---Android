package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object for reading and mutating multi-stage bread machine cycles,
 * stage progression, and 'add-ins' (raisins, nuts) alarms.
 */
@Dao
interface BreadCycleTrackingDao {

    @Query("SELECT * FROM cycle_tracking WHERE cycleId = :cycleId LIMIT 1")
    fun getCycleFlow(cycleId: String = BreadMachineCycleTrackerEntity.DEFAULT_TRACKER_ID): Flow<BreadMachineCycleTrackerEntity?>

    @Query("SELECT * FROM cycle_tracking WHERE cycleId = :cycleId LIMIT 1")
    suspend fun getCycle(cycleId: String = BreadMachineCycleTrackerEntity.DEFAULT_TRACKER_ID): BreadMachineCycleTrackerEntity?

    @Query("SELECT * FROM cycle_tracking ORDER BY lastUpdatedTimestamp DESC")
    fun getAllTrackedCycles(): Flow<List<BreadMachineCycleTrackerEntity>>

    @Query("SELECT COUNT(*) FROM cycle_tracking")
    suspend fun getCycleCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: BreadMachineCycleTrackerEntity)

    @Query("UPDATE cycle_tracking SET status = :status, lastUpdatedTimestamp = :timestamp WHERE cycleId = :cycleId")
    suspend fun updateStatus(
        cycleId: String,
        status: String,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("UPDATE cycle_tracking SET currentStageIndex = :stageIndex, stageElapsedSeconds = :stageElapsed, totalElapsedSeconds = :totalElapsed, lastUpdatedTimestamp = :timestamp WHERE cycleId = :cycleId")
    suspend fun updateProgress(
        cycleId: String,
        stageIndex: Int,
        stageElapsed: Int,
        totalElapsed: Int,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("UPDATE cycle_tracking SET addInAlarmTriggered = :triggered, addInAlarmAcknowledged = :acknowledged, lastUpdatedTimestamp = :timestamp WHERE cycleId = :cycleId")
    suspend fun updateAddInAlarm(
        cycleId: String,
        triggered: Boolean,
        acknowledged: Boolean,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("UPDATE cycle_tracking SET addInItems = :items, lastUpdatedTimestamp = :timestamp WHERE cycleId = :cycleId")
    suspend fun updateAddInItems(
        cycleId: String,
        items: List<String>,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("UPDATE cycle_tracking SET addInChimeSound = :chimeSound, addInVibrate = :vibrate, lastUpdatedTimestamp = :timestamp WHERE cycleId = :cycleId")
    suspend fun updateAddInChimeSettings(
        cycleId: String,
        chimeSound: Boolean,
        vibrate: Boolean,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("UPDATE cycle_tracking SET simulationSpeed = :speed, lastUpdatedTimestamp = :timestamp WHERE cycleId = :cycleId")
    suspend fun updateSimulationSpeed(
        cycleId: String,
        speed: Int,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("DELETE FROM cycle_tracking WHERE cycleId = :cycleId")
    suspend fun deleteCycle(cycleId: String)

    @Query("DELETE FROM cycle_tracking")
    suspend fun clearAll()
}
