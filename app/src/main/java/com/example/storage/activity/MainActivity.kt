package com.example.storage.activity

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.activity.result.launch
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.storage.R
import com.example.storage.utils.Utils
import java.io.*
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    var isPersistent:Boolean = false
    var isInternal:Boolean = true
    var readPermissionGranted:Boolean = false
    var writePermissionGranted:Boolean = false
    var fineLocationPermissionGranted:Boolean = false
    var cameraPermissionGranted:Boolean = false
    var coarseLocationPermissionGranted:Boolean = false
    lateinit var bt_save_int:Button
    lateinit var bt_read_int:Button
    lateinit var bt_delete_int:Button
    lateinit var bt_save_ext:Button
    lateinit var bt_read_ext:Button
    lateinit var bt_delete_ext:Button
    lateinit var bt_take_photo:Button
    lateinit var bt_open_photo_int:Button
    lateinit var bt_open_photo_ext:Button
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkStoragePaths()
        createInternalFile()
        createExternalFile()
        initViews()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initViews() {
        bt_save_int = findViewById(R.id.bt_save_int)
        bt_read_int = findViewById(R.id.bt_read_int)
        bt_delete_int = findViewById(R.id.bt_delete_int)
        bt_save_ext = findViewById(R.id.bt_save_ext)
        bt_read_ext = findViewById(R.id.bt_read_ext)
        bt_delete_ext = findViewById(R.id.bt_delete_ext)
        bt_take_photo = findViewById(R.id.bt_take_photo)
        bt_open_photo_int = findViewById(R.id.bt_open_photo_int)
        bt_open_photo_ext = findViewById(R.id.bt_open_photo_ext)

        bt_save_int.setOnClickListener { saveInternalFile("PDP_HW") }
        bt_read_int.setOnClickListener { readInternalFile() }
        bt_delete_int.setOnClickListener { deleteInternalFile() }
        bt_save_ext.setOnClickListener { saveExternalFile("Khurshidbek Kurbanov") }
        bt_read_ext.setOnClickListener { readExternalFile() }
        bt_delete_ext.setOnClickListener { deleteExternalFile() }
        bt_take_photo.setOnClickListener { takePhoto.launch() }
        bt_open_photo_ext.setOnClickListener { callImagesActivity() }


        requestCameraPermissions()
        requestLocationPermissions()
        requestPermissions()
    }

    private fun callImagesActivity() {
        var intent = Intent(this, ImagesActivity::class.java)
        startActivity(intent)
    }


    private fun deleteInternalFile() {
        val fileName = "pdp_internal.txt"
        val file:File
        file = if (isPersistent) {
            File(filesDir, fileName)
        } else {
            File(cacheDir, fileName)
        }
        if (file.exists()) {
            try {
                file.delete()
                Toast.makeText(applicationContext,String.format("File %s has been deleted", fileName), Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Toast.makeText(applicationContext,String.format("File %s delete failed", fileName), Toast.LENGTH_SHORT).show()
            }
        }else {
            Toast.makeText(applicationContext,String.format("File %s doesn't exists", fileName), Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteExternalFile() {
        val fileName = "pdp_internal.txt"
        val file:File
        file = if (isPersistent) {
            File(getExternalFilesDir(null),fileName)
        } else {
            File(externalCacheDir, fileName)
        }
        if (file.exists()) {
            try {
                file.delete()
                Toast.makeText(applicationContext,String.format("File %s has been deleted", fileName), Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Toast.makeText(applicationContext,String.format("File %s delete failed", fileName), Toast.LENGTH_SHORT).show()
            }
        }else {
            Toast.makeText(applicationContext,String.format("File %s doesn't exists", fileName), Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveInternalFile(data: String) {
        val fileName = "pdp_internal.txt"
        try {
            val fileOutpuStream:FileOutputStream
            fileOutpuStream = if (isPersistent) {
                openFileOutput(fileName, MODE_PRIVATE)
            } else {
                val file = File(cacheDir, fileName)
                FileOutputStream(file)
            }
            fileOutpuStream.write(data.toByteArray(Charset.forName("UTF-8")))
            Toast.makeText(applicationContext,String.format("Write to %s successful", fileName), Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(applicationContext,String.format("Write to file %s failed", fileName), Toast.LENGTH_SHORT).show()
        }
    }

    private fun readInternalFile() {
        val fileName = "pdp_internal.txt"
        try {
            val fileInpuStream:FileInputStream
            fileInpuStream = if (isPersistent) {
                openFileInput(fileName)
            } else {
                val file = File(cacheDir, fileName)
                FileInputStream(file)
            }
            val inputStreamReader = InputStreamReader(fileInpuStream, Charset.forName("UTF-8"))
            val lines: MutableList<String?> = ArrayList()
            val reader = BufferedReader(inputStreamReader)
            var line = reader.readLine()
            while (line != null) {
                lines.add(line)
                line = reader.readLine()
            }
            val readText = TextUtils.join("\n", lines)
            Toast.makeText(applicationContext,String.format("Read from file %s successful", fileName), Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(applicationContext,String.format("Read from file %s failed", fileName), Toast.LENGTH_SHORT).show()

        }
    }

    private fun saveExternalFile(data: String) {
        val fileName = "pdp_internal.txt"
        val file:File
        file = if (isPersistent) {
            File(getExternalFilesDir(null),fileName)
        } else {
            File(externalCacheDir, fileName)
        }
        try {
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(data.toByteArray(Charset.forName("UTF-8")))
            Toast.makeText(applicationContext,String.format("Write to %s successful", fileName), Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(applicationContext,String.format("Write to file %s failed", fileName), Toast.LENGTH_SHORT).show()
        }
    }

    private fun readExternalFile() {
        val fileName = "pdp_internal.txt"
        val file:File
        file = if (isPersistent) {
            File(getExternalFilesDir(null),fileName)
        } else {
            File(externalCacheDir, fileName)
        }
        try {
            val fileInpuStream = FileInputStream(file)
            val inputStreamReader = InputStreamReader(fileInpuStream, Charset.forName("UTF-8"))
            val lines: MutableList<String?> = ArrayList()
            val reader = BufferedReader(inputStreamReader)
            var line = reader.readLine()
            while (line != null) {
                lines.add(line)
                line = reader.readLine()
            }
            val readText = TextUtils.join("\n", lines)
            Toast.makeText(applicationContext,String.format("Read from file %s successful", fileName), Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(applicationContext,String.format("Read from file %s failed", fileName), Toast.LENGTH_SHORT).show()

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun requestPermissions() {
        val hasReadPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val hasWritePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        readPermissionGranted = hasReadPermission
        readPermissionGranted = hasWritePermission || minSdk29

        val permissionToRequest = mutableListOf<String>()
        if (!readPermissionGranted)
            permissionToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        if (!writePermissionGranted)
            permissionToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionToRequest.toTypedArray())
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun requestLocationPermissions() {
       val fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        fineLocationPermissionGranted = fineLocationPermission
        coarseLocationPermissionGranted = coarseLocationPermission

        val permissionToRequest = mutableListOf<String>()
        if (!fineLocationPermissionGranted)
            permissionToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        if (!coarseLocationPermissionGranted)
            permissionToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (permissionToRequest.isNotEmpty()) {
            locationPermissionLauncher.launch(permissionToRequest.toTypedArray())
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun requestCameraPermissions() {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        cameraPermissionGranted = cameraPermission

        val permissionToRequest = mutableListOf<String>()
        if (!cameraPermissionGranted)
            permissionToRequest.add(Manifest.permission.CAMERA)

        if (permissionToRequest.isNotEmpty()) {
            cameraPermissionLauncher.launch(permissionToRequest.toTypedArray())
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        readPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermissionGranted
        writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermissionGranted

        if (readPermissionGranted) Toast.makeText(applicationContext,"READ_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show()
        if (writePermissionGranted) Toast.makeText(applicationContext,"WRITE_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show()

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private val locationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        fineLocationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: fineLocationPermissionGranted
        coarseLocationPermissionGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: coarseLocationPermissionGranted

        if (fineLocationPermissionGranted) Toast.makeText(applicationContext,"ACCESS_FINE_LOCATION", Toast.LENGTH_SHORT).show()
        if (coarseLocationPermissionGranted) Toast.makeText(applicationContext,"ACCESS_COARSE_LOCATION", Toast.LENGTH_SHORT).show()

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        cameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: cameraPermissionGranted

        if (cameraPermissionGranted) Toast.makeText(applicationContext,"CAMERA", Toast.LENGTH_SHORT).show()

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        val filename = UUID.randomUUID().toString()

        val isPhotoSaved = if (isInternal) {
            savePhotoToInternalStorage(filename, bitmap!!)
        } else {
            if (writePermissionGranted) {
                savePhotoToExternalStorage(filename, bitmap!!)
            } else {
                false
            }
        }

        if (isPhotoSaved) {
            Toast.makeText(applicationContext,"Photo saved successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext,"Photo saved successfully", Toast.LENGTH_SHORT).show()
        }

    }

    private fun savePhotoToExternalStorage(filename: String, bitmap: Bitmap): Boolean {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$filename.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.WIDTH, bitmap.width)
            put(MediaStore.Images.Media.HEIGHT, bitmap.height)
        }
        return try {
            contentResolver.insert(collection, contentValues)?.also { uri ->
                contentResolver.openOutputStream(uri).use { outputStream ->
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                        throw IOException("Couldn't save bitmap")
                    }
                }
            } ?: throw IOException("Couldn't create MediaStore entry")
            true
        } catch (e:IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun savePhotoToInternalStorage(filename: String, bitmap: Bitmap): Boolean {
        return try {
            openFileOutput("$filename.jpg", MODE_PRIVATE).use { stream ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG,95, stream)) {
                    throw IOException("Couldn't save bitmap.")
                }
            }
            true
        } catch (e:IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun createInternalFile() {
        val fileName = "pdp_internal.txt"
        val file:File
        file = if (isPersistent) {
            File(filesDir, fileName)
        } else {
            File(cacheDir, fileName)
        }

        if (!file.exists()) {
            try {
                file.createNewFile()
                Toast.makeText(applicationContext,String.format("File %s has been created", fileName), Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Toast.makeText(applicationContext,String.format("File %s creation failed", fileName), Toast.LENGTH_SHORT).show()

            }
        }else {
            Toast.makeText(applicationContext,String.format("File %s already exists", fileName), Toast.LENGTH_SHORT).show()
        }
    }

    private fun createExternalFile() {
        val fileName = "pdp_external.txt"
        val file: File
        file = if (isPersistent) {
            File(getExternalFilesDir(null), fileName)
        } else {
            File(externalCacheDir, fileName)
        }
        Log.d("@@@", "absolutePath: " + file.absolutePath)
        if (!file.exists()) {
            try {
                file.createNewFile()
                Utils.fireToast(
                    this, String.format
                        ("File %s has been created", fileName)
                )
            } catch (e: IOException) {
                Utils.fireToast(
                    this, String.format
                        ("File %s creation failed", fileName)
                )
            }
        } else {
            Utils.fireToast(
                this, String.format
                    ("File %s already exists", fileName)
            )
        }
    }

    private fun checkStoragePaths() {
        val internal_m1 = getDir("custom",0)
        val internal_m2 = filesDir

        val external_m1 = getExternalFilesDir(null)
        val external_m2 = externalCacheDir
        val external_m3 = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        Log.d("@@StorageActivity ", internal_m1.absolutePath)
        Log.d("@@StorageActivity ", internal_m2.absolutePath)
        Log.d("@@StorageActivity ", external_m1!!.absolutePath)
        Log.d("@@StorageActivity ", external_m2!!.absolutePath)
        Log.d("@@StorageActivity ", external_m3!!.absolutePath)
    }

}