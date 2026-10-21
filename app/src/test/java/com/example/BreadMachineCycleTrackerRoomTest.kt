package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.BreadCycleTrackingDao
import com.example.data.local.BreadMachineCycleTrackerEntity
import com.example.data.model.BakingStageType
import com.example.data.model.PreloadedBreadMachineCycles
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BreadMachineCycleTrackerRoomTest {

    private lateinit var db: AppDatabase
    private lateinit var trackerDao: BreadCycleTrackingDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        trackerDao = db.cycleTrackingDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndQueryActiveCycleTrackerInRoom() = runBlocking {
        val entity = BreadMachineCycleTrackerEntity.fromCycle(
            cycle = PreloadedBreadMachineCycles.WHITE_BREAD,
            cycleId = BreadMachineCycleTrackerEntity.DEFAULT_TRACKER_ID
        )

        trackerDao.insertOrUpdate(entity)

        val retrieved = trackerDao.getCycle(BreadMachineCycleTrackerEntity.DEFAULT_TRACKER_ID)
        assertNotNull(retrieved)
        assertEquals("Basic White Bread Cycle", retrieved!!.cycleName)
        assertEquals(1, retrieved.courseNumber)
        assertEquals(10, retrieved.stages.size)
        assertEquals(BreadMachineCycleTrackerEntity.STATUS_IDLE, retrieved.status)
        assertEquals(0, retrieved.currentStageIndex)
        assertEquals(225 * 60, retrieved.totalDurationSeconds)

        // Verify Flow emission
        val flowValue = trackerDao.getCycleFlow(BreadMachineCycleTrackerEntity.DEFAULT_TRACKER_ID).first()
        assertNotNull(flowValue)
        assertEquals(retrieved.cycleName, flowValue!!.cycleName)
    }

    @Test
    fun testMultiStageCycleProgressUpdatingInRoom() = runBlocking {
        val entity = BreadMachineCycleTrackerEntity.fromCycle(
            cycle = PreloadedBreadMachineCycles.WHITE_BREAD
        )
        trackerDao.insertOrUpdate(entity)

        // Advance to stage 3 (Knead 2: twin-blade vigorous mix)
        trackerDao.updateProgress(
            cycleId = entity.cycleId,
            stageIndex = 3,
            stageElapsed = 450,
            totalElapsed = 2850
        )

        val updated = trackerDao.getCycle(entity.cycleId)
        assertNotNull(updated)
        assertEquals(3, updated!!.currentStageIndex)
        assertEquals(450, updated.stageElapsedSeconds)
        assertEquals(2850, updated.totalElapsedSeconds)
        assertEquals(BakingStageType.KNEAD_2, updated.currentStage?.type)
    }

    @Test
    fun testAddInAlarmForRaisinsAndNutsInRoom() = runBlocking {
        val entity = BreadMachineCycleTrackerEntity.fromCycle(
            cycle = PreloadedBreadMachineCycles.WHITE_BREAD
        )
        trackerDao.insertOrUpdate(entity)

        // Initially alarm is not triggered and not acknowledged
        val initial = trackerDao.getCycle(entity.cycleId)!!
        assertFalse(initial.addInAlarmTriggered)
        assertFalse(initial.addInAlarmAcknowledged)
        assertTrue(initial.addInItems.any { it.contains("Raisins", ignoreCase = true) })
        assertTrue(initial.addInItems.any { it.contains("Walnuts", ignoreCase = true) })

        // 1. Trigger add-in alarm when cycle enters dispenser mark in Knead 2
        trackerDao.updateAddInAlarm(
            cycleId = entity.cycleId,
            triggered = true,
            acknowledged = false
        )

        val ringing = trackerDao.getCycle(entity.cycleId)!!
        assertTrue(ringing.addInAlarmTriggered)
        assertFalse(ringing.addInAlarmAcknowledged)

        // 2. User acknowledges add-in alarm ("Added raisins & nuts")
        trackerDao.updateAddInAlarm(
            cycleId = entity.cycleId,
            triggered = false,
            acknowledged = true
        )

        val acknowledged = trackerDao.getCycle(entity.cycleId)!!
        assertFalse(acknowledged.addInAlarmTriggered)
        assertTrue(acknowledged.addInAlarmAcknowledged)
    }

    @Test
    fun testCustomizingAddInMixInsInRoom() = runBlocking {
        val entity = BreadMachineCycleTrackerEntity.fromCycle(
            cycle = PreloadedBreadMachineCycles.WHITE_BREAD
        )
        trackerDao.insertOrUpdate(entity)

        val customAddIns = listOf(
            "Organic Thompson Raisins (100g)",
            "Roasted California Pecans (60g)",
            "Semi-Sweet Chocolate Chips (50g)",
            "Cinnamon Spice Swirl (15g)"
        )

        trackerDao.updateAddInItems(entity.cycleId, customAddIns)

        val retrieved = trackerDao.getCycle(entity.cycleId)!!
        assertEquals(4, retrieved.addInItems.size)
        assertEquals("Organic Thompson Raisins (100g)", retrieved.addInItems[0])
        assertEquals("Roasted California Pecans (60g)", retrieved.addInItems[1])
        assertEquals("Semi-Sweet Chocolate Chips (50g)", retrieved.addInItems[2])
    }

    @Test
    fun testStatusAndSpeedTransitionsInRoom() = runBlocking {
        val entity = BreadMachineCycleTrackerEntity.fromCycle(
            cycle = PreloadedBreadMachineCycles.WHITE_BREAD
        )
        trackerDao.insertOrUpdate(entity)

        // Set running
        trackerDao.updateStatus(entity.cycleId, BreadMachineCycleTrackerEntity.STATUS_RUNNING)
        assertEquals(BreadMachineCycleTrackerEntity.STATUS_RUNNING, trackerDao.getCycle(entity.cycleId)!!.status)

        // Set simulation speed to 30x
        trackerDao.updateSimulationSpeed(entity.cycleId, 30)
        assertEquals(30, trackerDao.getCycle(entity.cycleId)!!.simulationSpeed)

        // Set paused
        trackerDao.updateStatus(entity.cycleId, BreadMachineCycleTrackerEntity.STATUS_PAUSED)
        assertEquals(BreadMachineCycleTrackerEntity.STATUS_PAUSED, trackerDao.getCycle(entity.cycleId)!!.status)

        // Set completed
        trackerDao.updateStatus(entity.cycleId, BreadMachineCycleTrackerEntity.STATUS_COMPLETED)
        assertEquals(BreadMachineCycleTrackerEntity.STATUS_COMPLETED, trackerDao.getCycle(entity.cycleId)!!.status)
    }
}
