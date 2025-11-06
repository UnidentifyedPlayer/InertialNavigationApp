package com.example.inertialnavigationapp;

import android.app.Activity;
import android.hardware.SensorManager;
import android.hardware.Sensor;

public class PositionTracker {
    private float[] currentCoords;

    private boolean coordinatesSet = false;


    public PositionTracker() {
        currentCoords = new float[2];
    }

    public void setStartCoordinates(float latitude, float longitude) {
        currentCoords[0] = latitude;
        currentCoords[1] = longitude;
        coordinatesSet = true;
    }

    public boolean areCoordinatesSet() {
        return coordinatesSet;
    }

    public float[] getCurrentCoordinates() {
        return currentCoords;
    }

    public void updatePosition(float[] linAcceleration, float[] rotationMatrix, int steps) {

        float[] worldLinearAcceleration = new float[3];
        MathUtils.rotateVector(rotationMatrix, linAcceleration, worldLinearAcceleration);

        float[] normWorldAccelDirection = new float[3];
        MathUtils.normalizeVector(worldLinearAcceleration, normWorldAccelDirection);

        float distance = MathUtils.AVERAGE_STEP_LENGTH * steps;
        MathUtils.updateCoordinates(currentCoords, distance * normWorldAccelDirection[1],
                distance * normWorldAccelDirection[0]);
    }





}
