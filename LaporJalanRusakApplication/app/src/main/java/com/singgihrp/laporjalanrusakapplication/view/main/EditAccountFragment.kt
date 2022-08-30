package com.singgihrp.laporjalanrusakapplication.view.main

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.singgihrp.laporjalanrusakapplication.R
import com.singgihrp.laporjalanrusakapplication.databinding.FragmentEditAccountBinding
import com.singgihrp.laporjalanrusakapplication.model.entity.User
import com.singgihrp.laporjalanrusakapplication.util.Status
import com.singgihrp.laporjalanrusakapplication.view.support.Dialog
import com.singgihrp.laporjalanrusakapplication.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*


class EditAccountFragment : Fragment(), View.OnClickListener {

    private var _binding : FragmentEditAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private val cal = Calendar.getInstance()

    private lateinit var dialog: Dialog

    private val dateSetListener =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditAccountBinding.inflate(inflater, container, false)
        dialog = Dialog(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        getDataUser()

        binding.imgArrowback.setOnClickListener(this)
        binding.btnUpdateProfile.setOnClickListener(this)
        binding.etUpdateDate.setOnClickListener(this)
        binding.etfieldUpdateDate.setOnClickListener(this)
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun getDataUser(){
        homeViewModel.getUserData().observe(viewLifecycleOwner){event->
            event.peekContent().let {
                when(it.status){
                    Status.SUCCESS -> {
                        dialog.dismissDialog()
                        val kelamin = resources.getStringArray(R.array.jenis_kelamin)
                        binding.etfieldUpdateEmail.setText(it.data?.email.toString())
                        binding.etfieldUpdateDate.setText(it.data?.date.toString())
                        binding.etfieldUpdateName.setText(it.data?.name.toString())
                        binding.etfieldUpdateNohp.setText(it.data?.nohp?.removePrefix("+62"))
                        if(it.data?.kelamin == "Laki - laki"){
                            binding.tvKelamin.setText(kelamin[0])
                            binding.tvKelamin.isEnabled = false
                        }else if(it.data?.kelamin == "Perempuan"){
                            binding.tvKelamin.setText(kelamin[1])
                            binding.tvKelamin.isEnabled = false
                        }
                        if(it.data?.verified == true){
                            binding.etUpdateEmail.isHelperTextEnabled = false
                        }
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

    private fun updateDateInView(){
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.etfieldUpdateDate.setText(sdf.format(cal.time))
    }

    private fun updateUser(user: User){
        homeViewModel.updateUserData(user).observe(viewLifecycleOwner){event->
            event.getContentIfNotHandled().let {
                if(it!=null){
                    when(it.status){
                        Status.SUCCESS -> {
                            dialog.dismissDialog()
                            Toast.makeText(requireContext(), "Data berhasil diubah", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_editAccountFragment_to_editProfilFragment)
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

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.img_arrowback -> {
                findNavController().navigate(R.id.action_editAccountFragment_to_editProfilFragment)
            }
            R.id.btn_updateProfile -> {
                val user = User(
                    date = binding.etfieldUpdateDate.text.toString().trim(),
                    kelamin = binding.tvKelamin.text.toString(),
                    nohp = "+62"+binding.etfieldUpdateNohp.text.toString().trim(),
                    name = binding.etfieldUpdateName.text.toString().trim()
                )
                if(validation()){
                    updateUser(user)
                }
            }
            R.id.et_updateDate -> {
                DatePickerDialog(requireActivity(),
                dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
            R.id.etfield_updateDate -> {
                DatePickerDialog(requireActivity(),
                    dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val kelamin = resources.getStringArray(R.array.jenis_kelamin)
        val arrayAdapter = ArrayAdapter(requireActivity(), R.layout.dropdown_menu, kelamin)
        binding.tvKelamin.setAdapter(arrayAdapter)
        binding.tvKelamin.inputType = EditorInfo.TYPE_NULL
        binding.tvKelamin.onFocusChangeListener = View.OnFocusChangeListener { _: View?, hasfocus ->
            if(hasfocus){
                hideKeyboard()
            }
        }
    }

    private fun validation(): Boolean{
        var validasi = true
        val namabox = binding.etUpdateName
        val namafield = binding.etfieldUpdateName.text.toString()
        val jenisKelaminBox = binding.etUpdateJenisKelamin
        val jenisKelamin = binding.tvUpdateJenisKelamin.text.toString()
        val notelp = binding.etUpdateNoHp
        val notelpField = binding.etfieldUpdateNohp.text.toString()
        val date = binding.etUpdateDate
        val dateField = binding.etfieldUpdateDate.text.toString()
        if(namafield.isEmpty()){
            validasi = false
            namabox.error = "Nama Wajib diisi"
            namabox.isErrorEnabled = true
            return validasi
        }else{
            namabox.error = null
            namabox.isErrorEnabled = false
        }
        if(jenisKelamin.isEmpty()){
            validasi = false
            jenisKelaminBox.error = "Pilih salah satu"
            return validasi
        }else{
            jenisKelaminBox.error = null
            jenisKelaminBox.isErrorEnabled = false
        }
        if(notelpField.length < 10){
            validasi = false
            notelp.error = "Nomor Telepon Minimal 10"
            return validasi
        }else{
            notelp.error = null
            notelp.isErrorEnabled = false
        }
        if(dateField.isEmpty()){
            validasi = false
            date.error = "Pilih Tanggal Lahir"
            return validasi
        }else{
            date.error = null
            date.isErrorEnabled = false
        }
        return validasi
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}