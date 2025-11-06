package com.example.inertialnavigationapp

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Objects

class PositionTrackerUnitTests {

    //device aligned with "world" coordinates as defined in Android documentation
    //(screen facing up, y axis points to the north, x axis points to the east),
    //movement along positive direction of the device's x-axis
    @Test
    fun testUpdateLocationPosXMovement(){


        val tracker = PositionTracker()
        tracker.setStartCoordinates(50f,0f)
        val accelVector = floatArrayOf(1f, 0f, 0f)
        var rotationMatrix = floatArrayOf(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
        val rotationVector = MathUtils.constructRotationVector(0f, floatArrayOf(0f,0f,1f))

        var steps = 1428
        tracker.updatePosition(accelVector,rotationMatrix,steps)
        var newCoordinates = tracker.currentCoordinates;
        assertEquals(0.013970014f, newCoordinates[1])
        assertEquals(50f, newCoordinates[0])
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
        MathUtils.rotationMatrixFromRotationVector(rotationVector,rotationMatrix)
        var steps = 1428
        tracker.updatePosition(accelVector,rotationMatrix,steps)
        var newCoordinates = tracker.currentCoordinates;
        assertEquals(-0.013970014f, newCoordinates[1])
        assertEquals(50f, newCoordinates[0])
    }

}