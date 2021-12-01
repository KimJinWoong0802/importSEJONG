package com.importsejong.korwriting.fragment

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.importsejong.korwriting.Dao
import com.importsejong.korwriting.MainActivity
import com.importsejong.korwriting.R
import com.importsejong.korwriting.databinding.*
import com.importsejong.korwriting.network.data.ocrdata
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

//DB에서 받아올 문제 클래스
data class WritingQuiz(
    val quiz :String,
    val answer :String
)

/**
 * A simple [Fragment] subclass.
 * Use the [QuizWritingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@RequiresApi(Build.VERSION_CODES.O)
class QuizWritingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mBinding: FragmentQuizWritingBinding? = null
    private val binding get() = mBinding!!
    private var mainActivity: MainActivity? = null

    private var dao = Dao

    //*/ quizback, ocr, ocr2팝업창 변수
    private var popupQuizbackBinding :DialogPopupQuizbackBinding? = null
    private var builderQuizBack : AlertDialog.Builder? = null
    private var popupViewQuizBack : AlertDialog? = null

    private var popupOcrBinding : DialogPopupOcrBinding? = null
    private var builderOcr : AlertDialog.Builder? = null
    private var popupViewOcr : AlertDialog? = null

    private var popupOcr2Binding : DialogPopupOcr2Binding? = null
    private var builderOcr2 : AlertDialog.Builder? = null
    private var popupViewOcr2 : AlertDialog? = null

    private var popupWritingquizBinding :DialogPopupWritingquizBinding? = null
    private var builderWritingquiz : AlertDialog.Builder? = null
    private var popupViewWritingquiz : AlertDialog? = null
    //*/

    private var preview : Preview? = null
    private var imageCapture: ImageCapture? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var photoUri: Uri
    private lateinit var photoUrl: String

    //퀴즈에서 사용되는 변수
    private var progressNumber :Int = 0 //퀴즈의 진행상황 0~9
    private lateinit var quizList :List<WritingQuiz>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentQuizWritingBinding.inflate(inflater, container, false)

        // TODO : 텍스트크기 변경
        setTextSize(mainActivity!!.textSize)

        //팝업 설정
        popupQuizbackBinding = DialogPopupQuizbackBinding.inflate(inflater, container, false)
        builderQuizBack = AlertDialog.Builder(requireContext()).setView(popupQuizbackBinding!!.root).setCancelable(false)
        popupViewQuizBack = builderQuizBack!!.create()

        popupOcrBinding = DialogPopupOcrBinding.inflate(inflater, container, false)
        builderOcr = AlertDialog.Builder(requireContext()).setView(popupOcrBinding!!.root).setCancelable(false)
        popupViewOcr = builderOcr!!.create()

        popupOcr2Binding = DialogPopupOcr2Binding.inflate(inflater, container, false)
        builderOcr2 = AlertDialog.Builder(requireContext()).setView(popupOcr2Binding!!.root).setCancelable(false)
        popupViewOcr2 = builderOcr2!!.create()

        popupWritingquizBinding = DialogPopupWritingquizBinding.inflate(inflater, container, false)
        builderWritingquiz = AlertDialog.Builder(requireContext()).setView(popupWritingquizBinding!!.root).setCancelable(false)
        popupViewWritingquiz = builderOcr2!!.create()

        //카메라 화면 설정
        startCamera()

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        setButton()

        val maxQuizNumber = 20  // TODO : DB에서 문제 개수 가져오기
        val quizNumberList:List<Int> = randomInt10(maxQuizNumber)

        //TODO : 번호에 맞는 퀴즈 가져와서 quizList에 저장
        setQuiz(quizNumberList)

        //첫번째 퀴즈 표시
        binding.txtQuiz.text = quizList[0].quiz

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment QuizWritingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            QuizWritingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    //겹치지 않는 랜덤숫자리스트 10개 생성
    private fun randomInt10(count: Int): List<Int>{
        val set = mutableSetOf<Int>()
        val random = Random()

        //입력값 10 이하는 -1 출력
        if(count <= 10) {
            return List(10) { -1 }
        }

        while (true) {
            set.add(random.nextInt(count) + 1)

            //Set을 List로 변환, 정렬, 출력
            if(set.size >= 10) {
                val list = ArrayList(set)
                list.sort()

                return list
            }
        }
    }

    //글씨 크기 변경
    private fun setTextSize(textSize :Int) {
        //TODO : 글씨 크기 변경

        //TODO : 팝업 글씨 크기 변경
    }

    private fun setButton() {

        //사진찍기
        binding.imageButton.setOnClickListener{
            takePhoto()
            popupViewOcr!!.show()
        }


        //popupOcr 팝업 버튼
        //인식결과 맞아요
        popupOcrBinding!!.btnOcrYes.setOnClickListener {
            val beforeText: String = popupOcrBinding!!.txtOcrBefore.text.toString()

            //writingquiz 팝업 내용
            if(quizList[progressNumber].answer == beforeText) {
                popupWritingquizBinding!!.txtMain.text = getString(R.string.quiz_popup_next_correct)
            }
            else {
                popupWritingquizBinding!!.txtMain.text = getString(R.string.quiz_popup_next_incorrect)
            }
            popupWritingquizBinding!!.txtAnswer.text = quizList[progressNumber].answer
            //TODO : 점수판


            popupViewOcr!!.dismiss()
            popupViewWritingquiz!!.show()
        }
        //인식결과 아니에요
        popupOcrBinding!!.btnOcrNo.setOnClickListener{
            popupOcr2Binding!!.txtOcr2Before.setText(popupOcrBinding!!.txtOcrBefore.text)

            popupViewOcr!!.dismiss()
            popupViewOcr2!!.show()
        }


        //popupOcr2 팝업 버튼
        //글씨수정 확인
        popupOcr2Binding!!.btnOcr2Yes.setOnClickListener {
            val beforeText: String = popupOcr2Binding!!.txtOcr2Before.text.toString()

            //writingquiz 팝업 내용
            if(quizList[progressNumber].answer == beforeText) {
                popupWritingquizBinding!!.txtMain.text = getString(R.string.quiz_popup_next_correct)
            }
            else {
                popupWritingquizBinding!!.txtMain.text = getString(R.string.quiz_popup_next_incorrect)
            }
            popupWritingquizBinding!!.txtAnswer.text = quizList[progressNumber].answer
            //TODO : 점수판


            popupViewOcr!!.dismiss()
            popupViewWritingquiz!!.show()
        }
        //글씨수정 취소
        popupOcr2Binding!!.btnOcr2No.setOnClickListener {
            popupViewOcr2!!.dismiss()
        }

        //popupWritingquiz 팝업 버튼
        //다음문제 버튼
        popupWritingquizBinding!!.txtNext.setOnClickListener {
            progressNumber++

            if(progressNumber <= 9) {
                binding.txtQuiz.text = quizList[progressNumber].quiz

                popupViewWritingquiz!!.dismiss()
            }
            else {
                //TODO : 프래그먼트로 이동
                //val transaction = mainActivity!!.supportFragmentManager.beginTransaction()
                //transaction.replace(R.id.frame, QuizFragment())
                //transaction.commit()
            }
        }
        //끝내기 버튼
        popupWritingquizBinding!!.txtEnd.setOnClickListener {
            popupViewQuizBack!!.show()
        }


        //뒤로가기
        binding.toolbar.btnBack.setOnClickListener {
            popupViewQuizBack!!.show()
        }

        //뒤로가기 계속하기
        popupQuizbackBinding!!.txtContinue.setOnClickListener {
            popupViewQuizBack!!.dismiss()
        }

        //뒤로가기 끝내기
        popupQuizbackBinding!!.txtEnd.setOnClickListener {
            popupViewQuizBack!!.dismiss()

            //프래그먼트 이동
            val transaction = mainActivity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, QuizFragment())
            transaction.commit()
        }
    }

    //번호에 맞는 퀴즈 가져오기
    private fun setQuiz(listInt :List<Int>) {
        //TODO : 번호에 맞는 퀴즈 가져와서 quizList에 저장
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = File(
            outputDirectory,
            resources.getString(R.string.grammertest_fileName))
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        photoUri = getfile(photoFile).toUri()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) { }
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Toast.makeText(requireContext(), "캡처성공", Toast.LENGTH_SHORT).show()

                    //OCR 실행
                    OCR(photoFile)
                }
            })
    }

    private fun getfile(file:File):File{ return file }
    // 카메라 화면 설정
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
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

    private fun OCR(file :File) {
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

        dao.ocr(
            "KakaoAK daf1f2ea535740044cc45bb025b6b041",
            body,
            object : Callback<ocrdata> {
                override fun onResponse(
                    call: Call<ocrdata>,
                    response: Response<ocrdata>
                ) {
                    Log.d("결과", "성공 : ${response.raw()}")
                    Log.d("결과", "성공 : ${response.body()}")
                    popupOcrBinding!!.txtOcrBefore.text =
                        response.body().toString().replace(("[^\\D]").toRegex(), "").replace("[, ]","").replace("=[","")
                            .replace(("[a-z]").toRegex(), "").replace(("[A-Z]").toRegex(), "").replace(("_").toRegex(), " ")
                            .replace(", , , ],","").replace("]),","").replace("( ","").replace("(","")
                            .replace("])])","")
                }
                override fun onFailure(call: Call<ocrdata>, t: Throwable) {
                    Log.d("결과:", "실패 : $t")
                    popupOcrBinding!!.txtOcrBefore.text = t.toString()
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}