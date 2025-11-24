package com.example.inertialnavigationapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PositionTracker {
    private float[] currentCoords;

    private float[] cumulativeStepDirectionVector;

    private float[] cumulativeDirectionVector;

    private List<float[]> vectors;

    private boolean coordinatesSet = false;


    public PositionTracker() {
        currentCoords = new float[2];
        cumulativeStepDirectionVector = new float[3];
        cumulativeDirectionVector = new float[3];
        vectors = new ArrayList<float[]>();
    }

    public void setStartCoordinates(float latitude, float longitude) {
        currentCoords[0] = latitude;
        currentCoords[1] = longitude;
        coordinatesSet = true;
        cumulativeStepDirectionVector = new float[3];
        cumulativeDirectionVector = new float[3];
        vectors = new ArrayList<float[]>();

    }

    public boolean areCoordinatesSet() {
        return coordinatesSet;
    }

    public float[] getCurrentCoordinates() {
        return currentCoords;
    }

    public void updateDirectionVector(float[] linAcceleration, float[] rotationMatrix){
        float[] worldLinearAcceleration = new float[3];
        MathUtils.rotateVector(rotationMatrix, linAcceleration, worldLinearAcceleration);

        for(int i = 0; i<linAcceleration.length; i++){
            cumulativeStepDirectionVector[i] = cumulativeStepDirectionVector[i] + worldLinearAcceleration[i];
            cumulativeDirectionVector[i] = cumulativeDirectionVector[i] + worldLinearAcceleration[i];
        }
        vectors.add(worldLinearAcceleration);

    }

    public float[] getDispersion(){
        float[] dispersion = new float[3];
        float[] averages = new float[3];
        float[] temp = new float[3];
        int n = vectors.size();

        for(int i = 0; i < 3;i++){
            averages[i] = cumulativeStepDirectionVector[i]/n;
        }
        for(int i =0; i < n; i++){
            temp = vectors.get(i);
            for(int j = 0; j < 3; j++){
                dispersion[j] = (temp[j] - averages[j]) * (temp[j] - averages[j]);
            }
        }
        for(int i = 0; i < 3;i++){
            dispersion[i] = (float)Math.sqrt(dispersion[i]/n);
        }
        return dispersion;
    }

    public String updatePosition(float[] linAcceleration, float[] rotationMatrix, int steps) {

        if(vectors.isEmpty()){
            updateDirectionVector(linAcceleration,rotationMatrix);
        }


        float[] normWorldAccelDirection = new float[3];
        MathUtils.normalizeVector(cumulativeStepDirectionVector, normWorldAccelDirection);

        float[] temp = new float[3];
        System.arraycopy(cumulativeStepDirectionVector,0,temp,0,3);
        float[] horizontalDirection = new float[3];
        temp[2] = 0;
        MathUtils.normalizeVector(temp, horizontalDirection);

        float distance = MathUtils.AVERAGE_STEP_LENGTH * steps;
        MathUtils.updateCoordinates(currentCoords, distance * horizontalDirection[1],
                distance * horizontalDirection[0]);
        String str = getStatusString(getDispersion(), cumulativeStepDirectionVector, normWorldAccelDirection);
        cumulativeStepDirectionVector = new float[3];
        vectors.clear();
        return str;
    }

    public String getStatusString(float[] dispersion, float[] WorldAcc, float[] normWorldAcc){
        return "normDirection: " +
                "\nx: " + normWorldAcc[0] +
                "\ny: " + normWorldAcc[1] +
                "\nz: " + normWorldAcc[2]+
                "\nDispersion: " +
                "\nx: " + normWorldAcc[0] +
                "\ny: " + normWorldAcc[1] +
                "\nz: " + normWorldAcc[2]+
                "\nn:" + vectors.size() +
                "\nCumulativeDirection: "+
                "\nx: " + cumulativeDirectionVector[0] +
                "\ny: " + cumulativeDirectionVector[1] +
                "\nz: " + cumulativeDirectionVector[2]+
                "\n";

    }





}
