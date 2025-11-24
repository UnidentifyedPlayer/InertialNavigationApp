package com.example.inertialnavigationapp

import org.junit.Assert.assertEquals
import org.junit.Test

class MathUtilsUnitTests {

    @Test
    fun testNormalization(){
        var vector: FloatArray = floatArrayOf(0f, 0f, 3f);
        var newVector = FloatArray(3);
        MathUtils.normalizeVector(vector,newVector);
        assertEquals(1f,newVector[2]);
    }

    @Test
    fun testIdentityMatrixRotation(){
        var vector: FloatArray = floatArrayOf(0f, 0f, 3f)
        var newVector = FloatArray(3)
        var rotationMatrix = floatArrayOf(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
        MathUtils.rotateVector(rotationMatrix,vector,newVector)

        assertEquals(vector[0],newVector[0])
        assertEquals(vector[1],newVector[1])
        assertEquals(vector[2],newVector[2])
    }

    @Test
    fun testTransposeMatrix(){

        var rotationMatrix = floatArrayOf(1f, 1f, 2f, 0f, 1f, 3f, 0f, 0f, 1f)
        MathUtils.transpose(rotationMatrix)
        var excpectedMatrix = floatArrayOf(1f, 0f, 0f, 1f, 1f, 0f, 2f, 3f, 1f)

        for(i in 0..8){
            assertEquals(excpectedMatrix[i], rotationMatrix[i])
        }
    }


    @Test
    fun testIdentityMatrixConstruction(){
        var expectedRotationMatrix = floatArrayOf(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)

        val rotationVector = MathUtils.constructRotationVector(0f, floatArrayOf(0f,0f,1f));
        var rotationMatrix = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

        MathUtils.rotationMatrixFromRotationVector(rotationVector,rotationMatrix)

        for(index in 0..<rotationMatrix.size)
            assertEquals(expectedRotationMatrix[0],rotationMatrix[0])
    }

    @Test
    fun testLeviCivita(){
        assert(MathUtils.LeviCivita(1, 1, 1) ==  0)
        assert(MathUtils.LeviCivita(1, 1, 2) ==  0)
        assert(MathUtils.LeviCivita(1, 1, 3) ==  0)
        assert(MathUtils.LeviCivita(1, 2, 1) ==  0)
        assert(MathUtils.LeviCivita(1, 2, 2) ==  0)
        assert(MathUtils.LeviCivita(1, 2, 3) ==  1)
        assert(MathUtils.LeviCivita(1, 3, 1) ==  0)
        assert(MathUtils.LeviCivita(1, 3, 2) == -1)
        assert(MathUtils.LeviCivita(1, 3, 3) ==  0)
        assert(MathUtils.LeviCivita(2, 1, 1) ==  0)
        assert(MathUtils.LeviCivita(2, 1, 2) ==  0)
        assert(MathUtils.LeviCivita(2, 1, 3) == -1)
        assert(MathUtils.LeviCivita(2, 2, 1) ==  0)
        assert(MathUtils.LeviCivita(2, 2, 2) ==  0)
        assert(MathUtils.LeviCivita(2, 2, 3) ==  0)
        assert(MathUtils.LeviCivita(2, 3, 1) ==  1)
        assert(MathUtils.LeviCivita(2, 3, 2) ==  0)
        assert(MathUtils.LeviCivita(2, 3, 3) ==  0)
        assert(MathUtils.LeviCivita(3, 1, 1) ==  0)
        assert(MathUtils.LeviCivita(3, 1, 2) ==  1)
        assert(MathUtils.LeviCivita(3, 1, 3) ==  0)
        assert(MathUtils.LeviCivita(3, 2, 1) == -1)
        assert(MathUtils.LeviCivita(3, 2, 2) ==  0)
        assert(MathUtils.LeviCivita(3, 2, 3) ==  0)
        assert(MathUtils.LeviCivita(3, 3, 1) ==  0)
        assert(MathUtils.LeviCivita(3, 3, 2) ==  0)
        assert(MathUtils.LeviCivita(3, 3, 3) ==  0)
    }
}