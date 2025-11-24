package com.example.inertialnavigationapp

import android.hardware.SensorManager
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    val AVERAGE_STEPS_IN_A_KILOMETER = 1428;

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.inertialnavigationapp", appContext.packageName)
    }

    @Test
    fun getRotationMatrixTest(){
        var angle: Float = (Math.random()*90).toFloat();
        var axisVector: FloatArray = FloatArray(3);
        var normalAxisVector: FloatArray = FloatArray(3);
        for(i in 0..2)
            axisVector[i] = (Math.random()*2 - 1).toFloat();
        MathUtils.normalizeVector(axisVector, normalAxisVector)
        var rotationVector: FloatArray = MathUtils.constructRotationVector(angle,normalAxisVector)
        var androidRotationMatrix: FloatArray = FloatArray(9);
        SensorManager.getRotationMatrixFromVector(androidRotationMatrix,rotationVector)
        var implementedRotationMatrix: FloatArray = FloatArray(9);
        MathUtils.rotationMatrixFromRotationVector(rotationVector, implementedRotationMatrix)
        println(androidRotationMatrix.asList())
        println(implementedRotationMatrix.asList())
        for(i in 0..8)
            assertEquals(androidRotationMatrix[i],implementedRotationMatrix[i])

    }

    @Test
    fun testUpdateLocationPosXMovement(){


        val tracker = PositionTracker()
        tracker.setStartCoordinates(50f,0f)
        val accelVector = floatArrayOf(1f, 0f, 0f)
        var rotationMatrix = floatArrayOf(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
        val rotationVector = MathUtils.constructRotationVector(0f, floatArrayOf(0f,0f,1f))
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector)
        tracker.updatePosition(accelVector,rotationMatrix,AVERAGE_STEPS_IN_A_KILOMETER)
        var newCoordinates = tracker.currentCoordinates;
        assertEquals(50f, newCoordinates[0])
        assertEquals(0.013970014f, newCoordinates[1])
    }

    //same as previous test, but with phone rotated upside down
    //(screen facing up, y axis points to the south, x axis points to the west),
    //movement along positive direction of the device's x-axis
    @Test
    fun testUpdateLocationNegativeXMovement(){
        val tracker = PositionTracker()
        tracker.setStartCoordinates(50f,0f)
        var accelVector = floatArrayOf(1f, 0f, 0f)
        var rotationMatrix = floatArrayOf(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
        val rotationVector = MathUtils.constructRotationVector(180f, floatArrayOf(0f,0f,1f))
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector)
        var str = tracker.updatePosition(accelVector,rotationMatrix,AVERAGE_STEPS_IN_A_KILOMETER)
        var newCoordinates = tracker.currentCoordinates;
        println(str)
        assertEquals(50f, newCoordinates[0])
        assertEquals(-0.013970014f, newCoordinates[1])
    }

    @Test
    fun testUpdateLocationPosYMovement(){


        val tracker = PositionTracker()
        tracker.setStartCoordinates(50f,0f)
        val accelVector = floatArrayOf(0f, 1f, 0f)
        var rotationMatrix = floatArrayOf(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
        val rotationVector = MathUtils.constructRotationVector(0f, floatArrayOf(0f,0f,1f))
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector)
        tracker.updatePosition(accelVector,rotationMatrix,AVERAGE_STEPS_IN_A_KILOMETER)
        var newCoordinates = tracker.currentCoordinates;
        assertEquals(50.00898f, newCoordinates[0])
        assertEquals(0.0f, newCoordinates[1])
    }

    @Test
    fun testUpdateLocationNegYMovement(){


        val tracker = PositionTracker()
        tracker.setStartCoordinates(50f,0f)
        val accelVector = floatArrayOf(0f, 1f, 0f)
        var rotationMatrix = floatArrayOf(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
        val rotationVector = MathUtils.constructRotationVector(180f, floatArrayOf(0f,0f,1f))
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector)

        tracker.updatePosition(accelVector,rotationMatrix,AVERAGE_STEPS_IN_A_KILOMETER)
        var newCoordinates = tracker.currentCoordinates;
        assertEquals(49.99102f, newCoordinates[0])
        assertEquals(1.710514E-18, newCoordinates[1])
    }

    @Test
    fun testUpdateLocationPosXMovementPhonePointsEast(){
        val tracker = PositionTracker()
        tracker.setStartCoordinates(50f,0f)
        val accelVector = floatArrayOf(1f, 0f, 0f)
        var rotationMatrix = floatArrayOf(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
        val rotationVector = MathUtils.constructRotationVector(-90f, floatArrayOf(0f,0f,1f))
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector)
        tracker.updatePosition(accelVector,rotationMatrix,AVERAGE_STEPS_IN_A_KILOMETER)
        var newCoordinates = tracker.currentCoordinates;
        assertEquals(49.99102f, newCoordinates[0])
        assertEquals(1.710514E-18, newCoordinates[1])

    }

    @Test
    fun testUpdateLocationPosXMovementPhonePointsWest(){
        val tracker = PositionTracker()
        tracker.setStartCoordinates(50f,0f)
        val accelVector = floatArrayOf(1f, 0f, 0f)
        var rotationMatrix = floatArrayOf(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
        val rotationVector = MathUtils.constructRotationVector(90f, floatArrayOf(0f,0f,1f))
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector)
        tracker.updatePosition(accelVector,rotationMatrix,AVERAGE_STEPS_IN_A_KILOMETER)
        var newCoordinates = tracker.currentCoordinates;
        assertEquals(50.00898f, newCoordinates[0])
        assertEquals(0.0f, newCoordinates[1])

    }

    @Test
    fun testUpdateLocationPosYMovementPhonePointsEast(){
        val tracker = PositionTracker()
        tracker.setStartCoordinates(50f,0f)
        val accelVector = floatArrayOf(0f, 1f, 0f)
        var rotationMatrix = floatArrayOf(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
        val rotationVector = MathUtils.constructRotationVector(-90f, floatArrayOf(0f,0f,1f))
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector)
        tracker.updatePosition(accelVector,rotationMatrix,AVERAGE_STEPS_IN_A_KILOMETER)
        var newCoordinates = tracker.currentCoordinates;
        assertEquals(50f, newCoordinates[0])
        assertEquals(0.013970014f, newCoordinates[1])
    }

    @Test
    fun testUpdateLocationPosYMovementPhonePointsWest(){
        val tracker = PositionTracker()
        tracker.setStartCoordinates(50f,0f)
        val accelVector = floatArrayOf(0f, 1f, 0f)
        var rotationMatrix = floatArrayOf(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
        val rotationVector = MathUtils.constructRotationVector(90f, floatArrayOf(0f,0f,1f))
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector)
        tracker.updatePosition(accelVector,rotationMatrix,AVERAGE_STEPS_IN_A_KILOMETER)
        var newCoordinates = tracker.currentCoordinates;
        assertEquals(50f, newCoordinates[0])
        assertEquals(-0.013970014f, newCoordinates[1])

    }



}