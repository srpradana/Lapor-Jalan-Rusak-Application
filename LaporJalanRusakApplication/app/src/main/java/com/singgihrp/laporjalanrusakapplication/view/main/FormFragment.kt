package com.singgihrp.laporjalanrusakapplication.view.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.singgihrp.laporjalanrusakapplication.view.support.Dialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.adevinta.leku.LATITUDE
import com.adevinta.leku.LONGITUDE
import com.adevinta.leku.LocationPickerActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.Timestamp
import com.singgihrp.laporjalanrusakapplication.R
import com.singgihrp.laporjalanrusakapplication.databinding.FragmentBottomSheetBinding
import com.singgihrp.laporjalanrusakapplication.databinding.FragmentFormBinding
import com.singgihrp.laporjalanrusakapplication.model.entity.Report
import com.singgihrp.laporjalanrusakapplication.util.Status
import com.singgihrp.laporjalanrusakapplication.viewmodel.FormViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FormFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentFormBinding? = null
    private val binding get() = _binding!!
    private lateinit var formViewModel: FormViewModel

    private var location = ""
    private var city = ""

    private var image: Uri? = null
    private var tempImagePath = ""

    private var countReport: String? = ""
    private var checkImage: Int = 0
    private var checkLocation: Int = 0

    private lateinit var dialog: Dialog

    /*
    Get Image From Gallery
     */
    private val galleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            if (result != null) {
                Glide.with(requireContext())
                    .asBitmap()
                    .load(result)
                    .into(binding.imgFoto)
                image = result
                checkImage = 1
            } else if (image != null) {
                checkImage = 1
            } else {
                checkImage = 0
            }
            checkButtonSend(checkImage, checkLocation)
        }

    /*
    Get Image From Camera
     */
    private val cameraResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            checkImage = if (success) {
                Glide.with(requireContext())
                    .asBitmap()
                    .load(image)
                    .into(binding.imgFoto)
                1
            } else if (image != null) {
                1
            } else {
                0
            }
            checkButtonSend(checkImage, checkLocation)
        }

    companion object {
        const val REQUEST_CODE_PERMISSION_GALLERY = 111
        const val REQUEST_CODE_PERMISSION_CAMERA = 110
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFormBinding.inflate(inflater, container, false)
        dialog = Dialog(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        formViewModel = ViewModelProvider(this)[FormViewModel::class.java]

        getUserData()
        getCountReport()
        checkButtonSend(checkImage, checkLocation)

        binding.btnPilihFoto.setOnClickListener(this)
        binding.btnSend.setOnClickListener(this)
        binding.btnPickLokasi.setOnClickListener(this)
        binding.imgArrowback.setOnClickListener(this)
    }

    private fun getCountReport() {
        formViewModel.getDataCountReport()
        formViewModel.countFormData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled().let {
                if (it != null) {
                    countReport = it
                }
            }
        }
    }

    private fun getUserData() {
        formViewModel.getUserData().observe(viewLifecycleOwner) { event ->
            event.peekContent().let {
                when (it.status) {
                    Status.SUCCESS -> {
                        dialog.dismissDialog()
                        binding.etfieldNama.setText(it.data?.name)
                        binding.etfieldEmail.setText(it.data?.email)
                        binding.etfieldNohp.setText(it.data?.nohp)
                    }
                    Status.ERROR -> {
                        dialog.dismissDialog()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    Status.LOADING -> dialog.startDialog()
                }
            }
        }
    }

    private fun sendReport(report: Report, reportid: String) {
        formViewModel.sendReport(report, reportid).observe(viewLifecycleOwner){event->
            event.getContentIfNotHandled().let {
                if(it!=null){
                    when(it.status) {
                        Status.SUCCESS -> {
                            dialog.dismissDialog()
                            Toast.makeText(requireActivity(), "Berhasil mengirim laporan", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_formFragment_to_homeFragment)
                        }
                        Status.ERROR -> {
                            dialog.dismissDialog()
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        Status.LOADING -> dialog.startDialog()
                    }
                }else{
                    Toast.makeText(requireContext(), it?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkGalleryPermission() {
        if (isGranted(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE_PERMISSION_GALLERY,
            )
        ) {
            galleryResult.launch("image/*")
        }
    }

    private fun checkCameraPermission() {
        if (isGranted(
                requireActivity(),
                Manifest.permission.CAMERA,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ),
                REQUEST_CODE_PERMISSION_CAMERA
            )
        ) {
            image = FileProvider.getUriForFile(requireContext(),
                "com.singgihrp.laporjalanrusakapplication.provider",
                createImageFile()!!.also {
                    tempImagePath = it.absolutePath
                }
            )
            cameraResult.launch(image)
        }
    }

    private fun createImageFile(): File? {
        val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("temp_image", ".jpg", storageDir)
    }

    private fun getCurrentLocation() {
        val intent = LocationPickerActivity.Builder()
            .withSearchZone("id_ID")
            .withSatelliteViewHidden()
            .withGoogleTimeZoneEnabled()
            .withLegacyLayout()
            .withGooglePlacesApiKey("AIzaSyA4zIoYxe7N1GAr6oVXBg0X_TTO1RvRg6I")
            .withVoiceSearchHidden()
            .withUnnamedRoadHidden()
            .build(requireContext())
        resultLauncher.launch(intent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val data: Intent? = result.data
                val latitude = data?.getDoubleExtra(LATITUDE, 0.0)
                Log.d("Split", "latitudekota: ${latitude.toString()} ")
                val longitude = data?.getDoubleExtra(LONGITUDE, 0.0)
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geocoder.getFromLocation(latitude!!, longitude!!, 1)
                val address = addresses[0].getAddressLine(0)

                val splitAddress = address.split(",").toTypedArray()
                val kota = splitCity(splitAddress)
                val splitKota = kota.split(" ").toTypedArray()
                Log.d("Split", "splitkota: $splitKota ")


                location = address
                city = if (splitKota[1] == "Kabupaten" || splitKota[1] == "Kota" || splitKota[1] == "Kabupatén" || splitKota[1] == "Kab.") {
                    splitKota[2]
                } else if (splitKota[2] == "Regency" || splitKota[2] == "City") {
                    splitKota[1]
                } else {
                    kota
                }
                binding.etfieldAlamat.setText(location)
                checkLocation = 1
                Log.d("Lokasi", ": $location $city")
            } else if (binding.etfieldAlamat.text != null) {
                checkLocation = 1
            } else {
                checkLocation = 0
            }
            checkButtonSend(checkImage, checkLocation)
        }

    private fun availableLocation(location: String){
        formViewModel.getLocationData(location)
        formViewModel.availableLocation.observe(this){event->
            event.getContentIfNotHandled().let {
                if(it==true){
                    Toast.makeText(requireContext(), "Lokasi sudah digunakan dalam laporan lain", Toast.LENGTH_SHORT).show()
                }else if(it==false) {
                    uploadedImage()
                }
            }
        }
    }

    private fun uploadedImage() {
        val date = getDateString(Timestamp.now().seconds, "dd/MM/yyyy")
        val time = getDateString(Timestamp.now().seconds, "HH:mm")
        val bitmap = getBitmapFromView(binding.imgFoto)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        formViewModel.getImageUrl(data).observe(viewLifecycleOwner){event->
            event.getContentIfNotHandled().let {
                if(it!=null && it.data != "Gagal"){
                    when(it.status) {
                        Status.SUCCESS -> {
                            dialog.dismissDialog()
                            val report = Report(
                                nama = binding.etfieldNama.text.toString().trim(),
                                email = binding.etfieldEmail.text.toString().trim(),
                                nohp = binding.etfieldNohp.text.toString().trim(),
                                lokasi = binding.etfieldAlamat.text.toString().trim(),
                                rusak = binding.tvKerusakan.text.toString().trim(),
                                images = it.data,
                                date = date,
                                city = city,
                                time = "$time WIB",
                                timestamp = Timestamp.now(),
                                status = "Terkirim"
                            )
                            sendReport(report, getRandomString())
                        }
                        Status.ERROR -> {
                            dialog.dismissDialog()
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        Status.LOADING -> dialog.startDialog()
                    }
                }else{
                    Toast.makeText(requireContext(), it?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getRandomString(): String{
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..20)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun getDateString(seconds: Long, outputPattern: String): String {
        return try {
            val dateFormat = SimpleDateFormat(outputPattern, Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = seconds * 1000
            val date = calendar.time
            dateFormat.format(date)
        } catch (e: Exception) {
            Log.e("utils", "Date format", e)
            ""
        }
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width, view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_send -> {
                if(binding.tvKerusakan.text.isEmpty()){
                    binding.ddTingkatRusak.error = "Pilih salah satu"
                    binding.ddTingkatRusak.isErrorEnabled = true
                }else{
                    val location = binding.etfieldAlamat.text.toString().trim()
                    availableLocation(location)
                }
            }
            R.id.btn_pickLokasi -> {
                getCurrentLocation()
            }
            R.id.btn_pilihFoto -> {
                showDialog()
            }
            R.id.img_arrowback -> {
                findNavController().navigate(R.id.action_formFragment_to_homeFragment)
            }
        }
    }

    private fun checkButtonSend(countImage: Int, countLocation: Int) {
        binding.btnSend.isEnabled = countImage == 1 && countLocation == 1
    }

    private fun splitCity(splitAddress: Array<String>): String {
        var city = ""

        when (splitAddress.size) {
            9 -> {
                city = splitAddress[6]
            }
            8 -> {
                city = splitAddress[5]
            }
            7 -> {
                city = splitAddress[4]
            }
            6 -> {
                city = splitAddress[3]
            }
            5 -> {
                city = splitAddress[2]
            }
            4 -> {
                city = splitAddress[1]
            }
            3 -> {
                city = splitAddress[0]
            }
        }
        return city
    }

    private fun showDialog() {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val view = FragmentBottomSheetBinding.inflate(layoutInflater)
        dialog.setContentView(view.root)
        view.btnGallery.setOnClickListener {
            checkGalleryPermission()
            dialog.dismiss()
        }
        view.btnTakePic.setOnClickListener {
            checkCameraPermission()
            dialog.dismiss()
        }
        dialog.setCancelable(true)
        dialog.show()
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onResume() {
        super.onResume()
        val damage = resources.getStringArray(R.array.tingkat_kerusakan)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu, damage)
        binding.tvKerusakan.setAdapter(arrayAdapter)
        binding.tvKerusakan.inputType = EditorInfo.TYPE_NULL
        binding.tvKerusakan.onFocusChangeListener = View.OnFocusChangeListener { _: View?, hasfocus ->
            if(hasfocus){
                hideKeyboard()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /*
    Permissions
     */

    private fun isGranted(
        activity: Activity,
        permission: String,
        permissions: Array<String>,
        request: Int,
    ): Boolean {
        val permissionCheck = ActivityCompat.checkSelfPermission(activity, permission)
        return if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(activity, permissions, request)
            }
            false
        } else {
            true
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, Please allow permissions from App Settings.")
            .setPositiveButton(
                "App Settings"
            ) { _, _ ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", "packageName", null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .show()
    }
}