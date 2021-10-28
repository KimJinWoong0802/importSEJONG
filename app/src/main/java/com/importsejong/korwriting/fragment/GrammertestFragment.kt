package com.importsejong.korwriting.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import com.importsejong.korwriting.MainActivity
import com.importsejong.korwriting.R
import com.importsejong.korwriting.databinding.DialogPopupOcr2Binding
import com.importsejong.korwriting.databinding.DialogPopupOcrBinding
import com.importsejong.korwriting.databinding.DialogPopupResultBinding
import com.importsejong.korwriting.databinding.FragmentGrammertestBinding
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GrammertestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class GrammertestFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mBinding: FragmentGrammertestBinding? = null
    private val binging get() = mBinding!!
    private var mainActivity: MainActivity? = null

    private var popupResultBinding : DialogPopupResultBinding? = null
    private var popupOcrBinding : DialogPopupOcrBinding? = null
    private var popupOcr2Binding : DialogPopupOcr2Binding? = null
    private var builder1 : AlertDialog.Builder? = null
    private var builder2 : AlertDialog.Builder? = null
    private var builder3 : AlertDialog.Builder? = null
    private var popupView1 : AlertDialog? = null
    private var popupView2 : AlertDialog? = null
    private var popupView3 : AlertDialog? = null

    private lateinit var storageReference: StorageReference
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference: DatabaseReference = firebaseDatabase.reference

    private var preview : Preview? = null
    private var imageCapture: ImageCapture? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    var getIntData :Int? = null
    var getStringData :String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentGrammertestBinding.inflate(inflater, container, false)

        //팝업 설정
        popupResultBinding = DialogPopupResultBinding.inflate(inflater, container, false)
        popupOcrBinding = DialogPopupOcrBinding.inflate(inflater, container, false)
        popupOcr2Binding = DialogPopupOcr2Binding.inflate(inflater, container, false)
        builder1 = AlertDialog.Builder(requireContext()).setView(popupOcrBinding!!.root).setCancelable(false)
        builder2 = AlertDialog.Builder(requireContext()).setView(popupOcr2Binding!!.root).setCancelable(false)
        builder3 = AlertDialog.Builder(requireContext()).setView(popupResultBinding!!.root).setCancelable(false)
        popupView1 = builder1!!.create()
        popupView2 = builder2!!.create()
        popupView3 = builder3!!.create()

        getIntData = arguments?.getInt("dataInt")
        getStringData = arguments?.getString("dataString")

        //카메라 화면 설정
        startCamera()
        binging.txtPractice.text = getStringData

        //맞춤법검사? 글씨교정?
        when(getIntData) {
            //맞춤법 검사 버튼 이벤트
            1 -> {
                binging.toolbar.title.text = resources.getString(R.string.grammertest_title)
                setButtonGrammertest()
            }
            //글씨 교정 버튼 이벤트
            2 -> {
                binging.toolbar.title.text = resources.getString(R.string.writingtest_title_two)
                setButtonWritingtest()
            }
        }

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        return binging.root
        //return inflater.inflate(R.layout.fragment_grammertest, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GrammertestFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GrammertestFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    //맞춤법 검사 버튼 이벤트

    private fun setButtonGrammertest() {
        //사진찍기
        binging.imageButton.setOnClickListener{
            takePhoto()
            popupView1!!.show()
        }

        //popupOcr 팝업 버튼
        //인식결과 맞아요
        popupOcrBinding!!.btnOcrYes.setOnClickListener {
            popupResultBinding!!.txtResultBefore.text = popupOcrBinding!!.txtOcrBefore.text

            popupView1!!.dismiss()
            popupView3!!.show()
        }
        //인식결과 아니에요
        popupOcrBinding!!.btnOcrNo.setOnClickListener{
            popupOcr2Binding!!.txtOcr2Before.setText(popupOcrBinding!!.txtOcrBefore.text)

            popupView1!!.dismiss()
            popupView2!!.show()
        }

        //popupOcr2 팝업 버튼
        //글씨수정 확인
        popupOcr2Binding!!.btnOcr2Yes.setOnClickListener {
            popupResultBinding!!.txtResultBefore.text = popupOcr2Binding!!.txtOcr2Before.text

            popupView2!!.dismiss()
            popupView3!!.show()
        }
        //글씨수정 취소
        popupOcr2Binding!!.btnOcr2No.setOnClickListener {
            popupView2!!.dismiss()
        }

        //popupResult 팝업 버튼
        //맞춤법결과 확인
        popupResultBinding!!.btnResultOk.setOnClickListener {
            popupView3!!.dismiss()
        }
        //맞춤법결과 책갈피에넣기
        popupResultBinding!!.btnResultBookmark.setOnClickListener {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
            val formatted = current.format(formatter)

            databaseReference.child("사용자").child(mainActivity!!.kakaoId).child("카카오")
                .child("맞춤법 검사").child(popupResultBinding!!.txtResultBefore.text.toString()).child("일자").setValue(formatted)

            databaseReference.child("사용자").child(mainActivity!!.kakaoId)
                .child("카카오").child("맞춤법 검사").child(popupResultBinding!!.txtResultBefore.text.toString()).child("입력 문장 내용")
                .setValue(popupResultBinding!!.txtResultBefore.text.toString())

            databaseReference.child("사용자").child(mainActivity!!.kakaoId)
                .child("카카오").child("맞춤법 검사").child(popupResultBinding!!.txtResultBefore.text.toString()).child("고친 문장 내용")
                .setValue(popupResultBinding!!.txtAfter.text.toString())

            databaseReference.child("사용자").child(mainActivity!!.kakaoId)
                .child("카카오").child("맞춤법 검사").child(popupResultBinding!!.txtResultBefore.text.toString()).child("촬영 사진")
                .setValue("촬영사진")

            databaseReference.child("사용자").child(mainActivity!!.kakaoId)
                .child("카카오").child("맞춤법 검사").child(popupResultBinding!!.txtResultBefore.text.toString()).child("맞춤법 검사 정보")
                .setValue("맞춤법검사정보")

            popupView3!!.dismiss()

        }
    }

    private fun setButtonWritingtest() {
        //사진찍기
        binging.imageButton.setOnClickListener {
            takePhoto()
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
            val formatted = current.format(formatter)

            val testString = arguments?.getString("dataString")

            databaseReference.child("사용자").child(mainActivity!!.kakaoId).child("카카오")
                .child("글씨 교정").child(testString!!).child("일자").setValue(formatted)

            databaseReference.child("사용자").child(mainActivity!!.kakaoId)
                .child("카카오").child("글씨 교정").child(testString).child("기준 문장 내용")
                .setValue(testString)

            databaseReference.child("사용자").child(mainActivity!!.kakaoId)
                .child("카카오").child("글씨 교정").child(testString).child("촬영 사진")
                .setValue("촬영사진")

            databaseReference.child("사용자").child(mainActivity!!.kakaoId)
                .child("카카오").child("글씨 교정").child(testString).child("최고 점수")
                .setValue("최고 점수")

        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = File(
            outputDirectory,
            resources.getString(R.string.grammertest_fileName))
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) { }
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Toast.makeText(requireContext(), "캡처성공", Toast.LENGTH_SHORT).show()
                    if(getIntData == 2) mainActivity!!.openWritingtestFragment(3, getStringData)
                }
            })
    }

    // 카메라 화면 설정
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binging.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder()
                .build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture)
            } catch(exc: Exception) { }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireContext().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.grammertest_folderName)).apply {
                mkdirs()
            }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir
        else requireContext().filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}