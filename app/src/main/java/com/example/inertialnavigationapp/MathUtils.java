package com.example.inertialnavigationapp;

public class MathUtils {

    public static float AVERAGE_STEP_LENGTH = 0.7f;
    public static float AVERAGE_EARTH_RADIUS = 6378000;

    public static void updateCoordinates(float[] prevCoords, float dy, float dx) {
        prevCoords[0] = (float) (prevCoords[0] + (dy / AVERAGE_EARTH_RADIUS) * (180 / Math.PI));
        prevCoords[1] = (float) (prevCoords[1] + (dx / AVERAGE_EARTH_RADIUS) * (180 / Math.PI) / Math.cos(prevCoords[0] * Math.PI / 180));
    }

    public static void rotateVector(float[] R, float[] vector, float[] resultVector) {
        resultVector[0] = R[0] * vector[0] + R[1] * vector[1] + R[2] * vector[2];
        resultVector[1] = R[3] * vector[0] + R[4] * vector[1] + R[5] * vector[2];
        resultVector[2] = R[6] * vector[0] + R[7] * vector[1] + R[8] * vector[2];
    }

    public static void normalizeVector(float[] vector, float[] resultVector) {
        float absolute = (float) Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2) + Math.pow(vector[2], 2));
        if (absolute == 0){
            resultVector[0] = 0;
            resultVector[1] = 0;
            resultVector[2] = 0;
            return;
        }
        resultVector[0] = vector[0] / absolute;
        resultVector[1] = vector[1] / absolute;
        resultVector[2] = vector[2] / absolute;
    }

    public static float[] addVectors(float[] a, float[] b){
        if(a.length != b.length){
            throw new ArrayIndexOutOfBoundsException("vectors are different lengths");
        }

        float[] newVector = new float[a.length];

        for(int i = 0; i<a.length; i++){
            newVector[i] = a[i] + b[i];
        }
        return newVector;
    }

    public static void transpose(float[] matrix){
        float temp = 0;
        if(matrix.length == 9){
            temp = matrix[1];
            matrix[1] = matrix[3];
            matrix[3] = temp;

            temp = matrix[2];
            matrix[2] = matrix[6];
            matrix[6] = temp;

            temp = matrix[5];
            matrix[5] = matrix[7];
            matrix[7] = temp;
        }
    }


    //Methods below are used only in testing for now

    public static void getRotationMatrixFromAxisAndAngle(float angle, float[] axisVector, float[] rotationMatrix){
        float[] rotationVector = MathUtils.constructRotationVector(angle, axisVector);
        MathUtils.rotationMatrixFromRotationVector(rotationVector,rotationMatrix);
    }

    //vector contains values as described in Android documentaion
    //https://developer.android.com/develop/sensors-and-location/sensors/sensors_motion#sensors-motion-rotate
    //Rotation matrix is constructed from it as described in a link below
    //https://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle
    public static void rotationMatrixFromRotationVector(float[] vector, float[] rotationMatrix){
        int l = 0;
        for(int k = 0; k<3; k++){
            for(int j = 0; j<3; j++){
                if(j==k){
                    rotationMatrix[j+k*3] = (float)(2*Math.pow(vector[j],2)+2*Math.pow(vector[3],2)-1);
                }
                else{
                    l = 3 - j - k;//index of value in vector variable that is not j or k
                    rotationMatrix[j+k*3] = 2*(vector[j]*vector[k]) - 2*LeviCivita(j,k,l)*vector[l]*vector[3];
                }
            }
        }
        MathUtils.transpose(rotationMatrix);
    }

    //used in calculating rotation matrix, implemented as follows
    //https://codegolf.stackexchange.com/questions/160359/levi-civita-symbol
    public static int LeviCivita(int i, int j, int k){
        return (i-j)*(j-k)*(k-i)/2;
    }

    //constructs rotation vector from axis and angle of rotation around it
    public static float[] constructRotationVector(float angle, float[] axisVector){
        float magnitude = (float)Math.sin( (angle/2) * Math.PI / 180);
        float rotationCos = (float)Math.cos( (angle/2) * Math.PI / 180);
        return new float[] { axisVector[0]*magnitude, axisVector[1]*magnitude,
                axisVector[2]*magnitude, rotationCos};
    }


}
