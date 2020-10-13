package com.mancel.yann.whereismycar

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mancel.yann.whereismycar.databases.AppDatabase
import com.mancel.yann.whereismycar.models.POI
import com.mancel.yann.whereismycar.repositories.DatabaseRepository
import com.mancel.yann.whereismycar.repositories.RoomDatabaseRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Created by Yann MANCEL on 13/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.WhereIsMyCar
 *
 * An android test on [DatabaseRepository].
 */
@RunWith(AndroidJUnit4::class)
class DatabaseRepositoryTest {

    // FIELDS --------------------------------------------------------------------------------------

    private lateinit var _database: AppDatabase
    private lateinit var _repository: DatabaseRepository

    private val _poi = POI(_latitude = 0.0, _longitude = 0.0)

    // METHODS -------------------------------------------------------------------------------------

    @Before
    @Throws(Exception::class)
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        this._database =
            Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()

        this._repository = RoomDatabaseRepository(this._database.poiDAO())
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        this._database.close()
    }

    // -- Create --

    @Test
    fun insertPointsOfInterest_shouldBeSuccess() = runBlocking {
        // BEFORE: Add element
        val ids = this@DatabaseRepositoryTest._repository
            .insertPointsOfInterest(this@DatabaseRepositoryTest._poi)

        // TEST
        assertEquals(1, ids.size)
        assertEquals(1L, ids.first())
    }

    // -- Read --

    @Test
    fun getPointsOfInterest_shouldBeEmpty() = runBlocking {
        // BEFORE: Retrieve data
        val data: List<POI>
        val flow = this@DatabaseRepositoryTest._repository.getPointsOfInterest()

        val deferred = async {
            flow.take(1)
                .single()
        }
        data = deferred.await()

        // TEST: No data
        assertEquals(0, data.size)
    }

    @Test
    fun getPointsOfInterest_shouldBeSuccess() = runBlocking {
        // BEFORE: Add element
        this@DatabaseRepositoryTest._repository
            .insertPointsOfInterest(this@DatabaseRepositoryTest._poi)

        // THEN: Retrieve data
        val data: List<POI>
        val flow = this@DatabaseRepositoryTest._repository.getPointsOfInterest()

        val deferred = async {
            flow.take(1)
                .single()
        }
        data = deferred.await()

        // TEST
        assertEquals(1, data.size)
        assertEquals(1L, data.first()._id)
        assertEquals(this@DatabaseRepositoryTest._poi._latitude, data.first()._latitude, 0.01)
        assertEquals(this@DatabaseRepositoryTest._poi._longitude, data.first()._longitude, 0.01)
    }

    // -- Update --

    @Test
    fun updatePointsOfInterest_shouldBeSuccess() = runBlocking {
        // BEFORE: Add element
        this@DatabaseRepositoryTest._repository
            .insertPointsOfInterest(this@DatabaseRepositoryTest._poi)

        // THEN: Update element (POI is data class) -> Id is the same with element in database
        val updatedElement = this@DatabaseRepositoryTest._poi.copy(_id = 1L,_latitude = 1.0)
        val updatedRows = this@DatabaseRepositoryTest._repository
            .updatePointsOfInterest(updatedElement)

        // THEN: Retrieve data
        val data: List<POI>
        val flow = this@DatabaseRepositoryTest._repository.getPointsOfInterest()

        val deferred = async {
            flow.take(1)
                .single()
        }
        data = deferred.await()

        // TEST
        assertEquals(1, updatedRows)
        assertEquals(updatedElement._id, data.first()._id)
        assertNotEquals(this@DatabaseRepositoryTest._poi._latitude, data.first()._latitude, 0.01)
    }

    @Test
    fun updatePointsOfInterest_shouldBeFail() = runBlocking {
        // BEFORE: Add element
        this@DatabaseRepositoryTest._repository
            .insertPointsOfInterest(this@DatabaseRepositoryTest._poi)

        // THEN: Update element (POI is data class) -> Id is different with element in database
        val updatedElement = this@DatabaseRepositoryTest._poi.copy(_latitude = 1.0)
        val updatedRows = this@DatabaseRepositoryTest._repository
            .updatePointsOfInterest(updatedElement)

        // TEST
        assertEquals(0, updatedRows)
    }

    // -- Delete --

    @Test
    fun removePointsOfInterest_shouldBeSuccess() = runBlocking {
        // BEFORE: Add element
        this@DatabaseRepositoryTest._repository
            .insertPointsOfInterest(this@DatabaseRepositoryTest._poi)

        // THEN: Retrieve data
        val data: List<POI>
        val flow = this@DatabaseRepositoryTest._repository.getPointsOfInterest()

        val deferred = async {
            flow.take(1)
                .single()
        }
        data = deferred.await()

        // THEN: Remove element
        val deletedRows = this@DatabaseRepositoryTest._repository
            .removePointsOfInterest(data.first())

        // TEST
        assertEquals(1, deletedRows)
    }

    @Test
    fun removePointsOfInterest_shouldBeFail() = runBlocking {
        // BEFORE: Remove element
        val deletedRows =
            this@DatabaseRepositoryTest._repository
                .removePointsOfInterest(this@DatabaseRepositoryTest._poi)

        // TEST
        assertEquals(0, deletedRows)
    }
}