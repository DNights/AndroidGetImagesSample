package dev.dnights.androidgetimagessample

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import dev.dnights.androidgetimagessample.databinding.ActivityMainBinding
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var imageCaptureFile: File? = null
    private var imageCaptureUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnOpenCamera.setOnClickListener {
            openCamera()
        }

        binding.btnOpenGallery.setOnClickListener {
            openGallery()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("DEBUG_TAG", "onActivityResult requestCode - $requestCode / resultCode - $resultCode")

        if(requestCode == OPEN_CAMERA){
            if (resultCode == RESULT_OK) {
                try {
                    val parcelFileDescriptor = contentResolver.openFileDescriptor(imageCaptureUri!!, "r", null)
                    val ins = FileInputStream(parcelFileDescriptor?.fileDescriptor!!)
                    val img = BitmapFactory.decodeStream(ins)
                    ins?.close()
                    // 이미지 표시
                    binding.ivSelectedImage.setImageBitmap(img)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else if(requestCode == OPEN_OPEN_GALLERY) {
            if (resultCode == RESULT_OK) {
                try {
                    val ins = contentResolver.openInputStream(data?.data!!)
                    val img = BitmapFactory.decodeStream(ins)
                    ins?.close()
                    // 이미지 표시
                    binding.ivSelectedImage.setImageBitmap(img)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun openCamera() {
        imageCaptureFile = createImageCaptureFile()
        imageCaptureUri = FileProvider.getUriForFile(this@MainActivity,applicationContext.packageName + ".fileprovider", imageCaptureFile!!)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureUri)
        startActivityForResult(intent, OPEN_CAMERA)
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(intent, OPEN_OPEN_GALLERY)
    }

    private fun createImageCaptureFile(): File? {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val prefix = sdf.format(Date())
        val fileName = "$prefix.jpg"
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            try {
                File.createTempFile(
                        prefix,  /* prefix */
                        ".jpeg",  /* suffix */
                        storageDir /* directory */
                )
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        } else {
            val dir = getExternalFilesDir(Environment.DIRECTORY_DCIM) //카메라용
            val camera = File(dir, "Camera")
            camera.mkdir()
            File(camera, fileName)
        }
    }

    companion object {
        private const val OPEN_CAMERA = 1000
        private const val OPEN_OPEN_GALLERY = 1001

    }
}