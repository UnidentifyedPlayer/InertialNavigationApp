package com.example.inertialnavigationapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Surface
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var linAccelSensor: Sensor? = null

    private var rotationVectorSensor: Sensor? = null
    private var stepCountSensor: Sensor? = null
    private var stepSensorType: Int = 0
    private var sensorManager: SensorManager? = null

    private var tracker: PositionTracker? = null

    private var cumulativeLinAcceleration = FloatArray(3, {0f})
    private var n = 0;
    private var lastAccelerometer = FloatArray(3, {0f})
    private var lastRotationVector = FloatArray(5, {0f})
    private var lastStepCount:Int = 0;
    private var stepsSet:Boolean = false;


    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val locationPermissionId = 2
    private val activityPermissionId = 3

    private var calculationsInfo : String? = "test write"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        linAccelSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        rotationVectorSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)


        // Check and request permission for Activity Recognition (Android 10+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (checkActivityPermissions()) {
                setupStepSensor()
            }
            else{
                requestActivityPermission()
            }
        }



        tracker = PositionTracker();


        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val getLocationButton: Button = findViewById(R.id.getLocationBtn)

        getLocationButton.setOnClickListener {
            getLocation()
            for(i in 0..2){
                cumulativeLinAcceleration[i] = 0f
            }
            n = 0
        }
    }

    private fun setupStepSensor(){
        stepCountSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        if(stepCountSensor == null){
            stepCountSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            if(stepCountSensor != null) {
                Toast.makeText(this, "Using high latency step sensor", Toast.LENGTH_LONG).show()
                stepSensorType = 2 //step counter sensor
            }
        }
        else
            stepSensorType = 1 //step detector sensor

        if((stepSensorType == 0) &&
            !packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER) &&
            !packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR)){
            Toast.makeText(this, "No step sensors found on the device", Toast.LENGTH_LONG).show()
        }
    }

    private fun getSensorOutput(event: SensorEvent?): Int{
        if(stepSensorType == 1){ //step detector sensor
            return event!!.values[0].toInt()
        }
        if(stepSensorType == 2){ //step counter sensor
            return event!!.values[0].toInt() - lastStepCount
        }
        return 0
    }

    private  fun constructRotVectorString(lastRotationVector: FloatArray): String{
        var str : String = ""
        for(whateever in lastRotationVector.asList()){
            str = str + whateever + "\n";
        }
        return str
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor == linAccelSensor) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.size)
            var rotationMatrix = getRotationMatrix()
            tracker!!.updateDirectionVector(lastAccelerometer,rotationMatrix)
            //updateCumulativeAcceleration()
        } else if (event.sensor == rotationVectorSensor) {
            System.arraycopy(event.values, 0, lastRotationVector, 0, 4)
            updatePhoneYAxis()
        } else if (event.sensor == stepCountSensor) {
            if(stepsSet){
                if(tracker!!.areCoordinatesSet()){
                    val stepsPassed = getSensorOutput(event)
                    var rotationMatrix = getRotationMatrix()
                    calculationsInfo = tracker!!.updatePosition(lastAccelerometer, rotationMatrix, stepsPassed) +
                            "rotVector:\n" + constructRotVectorString(lastRotationVector)
                    //        "rotVector: " + lastRotationVector.asList() +";\n"
                    //writeAppSpecificExternalFile(applicationContext, true)
                    updateLocationDisplay()
                }
            }
            else{
                stepsSet = true
            }
            lastStepCount = event.values[0].toInt()

        }
    }

    fun getOrientation(context: Context): Int {
        return context.display?.rotation ?: 0
    }

    fun getRotationMatrix(): FloatArray{
        var rotationMatrix = FloatArray(9)
        SensorManager.getRotationMatrixFromVector(rotationMatrix,lastRotationVector)
        var rotationMatrixAdjusted = FloatArray(9)
        when (getOrientation(baseContext)) {
            Surface.ROTATION_0 -> rotationMatrixAdjusted = rotationMatrix.clone()
            Surface.ROTATION_90 -> SensorManager.remapCoordinateSystem(
                rotationMatrix,
                SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X,
                rotationMatrixAdjusted
            )

            Surface.ROTATION_180 -> SensorManager.remapCoordinateSystem(
                rotationMatrix,
                SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y,
                rotationMatrixAdjusted
            )

            Surface.ROTATION_270 -> SensorManager.remapCoordinateSystem(
                rotationMatrix,
                SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X,
                rotationMatrixAdjusted
            )
        }
        return rotationMatrixAdjusted
    }

    fun usingKotlinStringFormat(input: Float, scale: Int) = "%.${scale}f".format(input)

    fun updatePhoneYAxis(){
        var rotationMatrix = getRotationMatrix()
        //MathUtils.transpose(rotationMatrix)
        var yAxis = floatArrayOf(0f,1f,0f)
        var yAxisInWorld = FloatArray(3)
        MathUtils.rotateVector(rotationMatrix,yAxis,yAxisInWorld)
        val rawLinAccTextView: TextView = findViewById(R.id.rawLinAccTextView)
        rawLinAccTextView.text =
            "Phone YAxis points to: " +
                    "\nx: ${usingKotlinStringFormat(yAxisInWorld[0],7)}" +
                    "\ny: ${usingKotlinStringFormat(yAxisInWorld[1],7)}" +
                    "\nz: ${usingKotlinStringFormat(yAxisInWorld[2],7)}"
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(this, linAccelSensor, SensorManager.SENSOR_DELAY_GAME)
        sensorManager?.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_GAME)
        sensorManager?.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    private fun updateLocationDisplay(){
        if(!tracker!!.areCoordinatesSet()){
            return
        }
        val latitudeTextView: TextView = findViewById(R.id.latitudeText)
        val longitudeTextView: TextView = findViewById(R.id.longitudeText)
        val sensorInfoTextView: TextView = findViewById(R.id.sensorInfoTextView)

        latitudeTextView.text = "Latitude: ${tracker!!.currentCoordinates[0]}"
        longitudeTextView.text = "Longitude: ${tracker!!.currentCoordinates[1]}"
        sensorInfoTextView.text = calculationsInfo
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkLocationPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        tracker!!.setStartCoordinates(location.latitude.toFloat(), location.longitude.toFloat());
                        updateLocationDisplay()
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestLocationPermissions()
        }
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private fun checkLocationPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun checkActivityPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
        )
            return true
        return false
    }
    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            locationPermissionId
        )
    }

    private fun requestActivityPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACTIVITY_RECOGNITION
            ),
            activityPermissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == locationPermissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                getLocation()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == activityPermissionId){
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                setupStepSensor()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}