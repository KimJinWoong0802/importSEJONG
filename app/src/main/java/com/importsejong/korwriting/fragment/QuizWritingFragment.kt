package com.importsejong.korwriting.fragment

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import com.google.firebase.database.*
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
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

//Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

//DB에서 받아올 문제 클래스
data class WritingQuiz(
    var quizText :String? =null,
    var answer :String? = null
)

/**
 * A simple [Fragment] subclass.
 * Use the [QuizWritingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@RequiresApi(Build.VERSION_CODES.O)
class QuizWritingFragment : Fragment() {
    //Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mBinding: FragmentQuizWritingBinding? = null
    private val binding get() = mBinding!!
    private var mainActivity: MainActivity? = null

    private var dao = Dao

    //*/ quizback, ocr, ocr2, Writingquiz팝업창 변수
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

    //퀴즈에서 사용되는 변수
    private var progressNumber :Int = 0 //퀴즈의 진행상황 0~9
    private var quizList = arrayListOf<WritingQuiz>()
    private var scoreAnswer = 0 //정답개수
    private var scoreWorng = 0  //오답개수
    private var score = 0   //획득점수

    private lateinit var databaseReference : DatabaseReference


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
        binding.toolbar.title.text = getString(R.string.quiz_writing_main)
        //글씨 크기 변경
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
        popupViewWritingquiz = builderWritingquiz!!.create()

        //카메라 화면 설정
        startCamera()

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        setButton()

        setQuiz()


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
        //Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            QuizWritingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    //퀴즈를 랜덤으로 10개 뽑기
    private fun randomList(allList: ArrayList<WritingQuiz>) {
        val randomInt10 = mutableSetOf<Int>()
        val size = allList.size
        val random = Random()


        //입력값 10 이하는 Error출력
        if(size < 10) {
            val temp = WritingQuiz("Error","Error")

            for(i in 0..9) {
                quizList.add(temp)
            }

            return
        }

        while (true) {
            //랜덤으로 숫자 10개 리스트 생성
            randomInt10.add(random.nextInt(size))

            //랜덤숫자10개에 맞는 문제를 리턴
            if(randomInt10.size >= 10) {
                for(i in randomInt10) {
                    quizList.add(allList[i])
                }

                return
            }
        }
    }

    //글씨 크기 변경
    private fun setTextSize(textSize :Int) {
        val size24 :Float = 20.0f + textSize*2

        binding.txtQuiz.textSize = size24
        binding.txtCount.textSize = size24
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
            val getScore: Int
            val txtMain: String
            val color: Int

            //writingquiz 팝업 내용
            if(quizList[progressNumber].answer == beforeText) {
                txtMain = getString(R.string.quiz_popup_next_correct)
                scoreAnswer += 1
                getScore = 100
                color = ContextCompat.getColor(requireContext(), R.color.color_correct)
            }
            else {
                txtMain = getString(R.string.quiz_popup_next_incorrect)
                scoreWorng += 1
                getScore = 0
                color = ContextCompat.getColor(requireContext(), R.color.color_incorrect)
            }
            score += getScore

            popupWritingquizBinding!!.txtMain.text = txtMain
            popupWritingquizBinding!!.txtMain.setBackgroundColor(color)
            popupWritingquizBinding!!.txtAnswer.text = quizList[progressNumber].answer
            popupWritingquizBinding!!.txtScore.text = getString(R.string.quiz_popup_score, getScore)
            popupWritingquizBinding!!.txtScore2.text = getString(R.string.quiz_popup_score2, score)

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
            val getScore: Int
            val txtMain: String
            val color: Int

            //writingquiz 팝업 내용
            if(quizList[progressNumber].answer == beforeText) {
                txtMain = getString(R.string.quiz_popup_next_correct)
                scoreAnswer += 1
                getScore = 100
                color = ContextCompat.getColor(requireContext(), R.color.color_correct)
            }
            else {
                txtMain = getString(R.string.quiz_popup_next_incorrect)
                scoreWorng += 1
                getScore = 0
                color = ContextCompat.getColor(requireContext(), R.color.color_incorrect)
            }
            score += getScore

            popupWritingquizBinding!!.txtMain.text = txtMain
            popupWritingquizBinding!!.txtMain.setBackgroundColor(color)
            popupWritingquizBinding!!.txtAnswer.text = quizList[progressNumber].answer
            popupWritingquizBinding!!.txtScore.text = getString(R.string.quiz_popup_score, getScore)
            popupWritingquizBinding!!.txtScore2.text = getString(R.string.quiz_popup_score2, score)

            popupViewOcr2!!.dismiss()
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
                binding.txtQuiz.text = quizList[progressNumber].quizText
                binding.txtCount.text = getString(R.string.quiz_count,progressNumber+1)

                popupViewWritingquiz!!.dismiss()
            }
            else {
                popupViewWritingquiz!!.dismiss()

                //결과 프래그먼트로 이동
                val transaction = mainActivity!!.supportFragmentManager.beginTransaction()
                val fragment = QuizResultFragment()
                val bundle = Bundle()

                bundle.putString("name","QuizWriting")
                bundle.putInt("scoreAnswer", scoreAnswer)
                bundle.putInt("scoreWorng", scoreWorng)
                bundle.putInt("score", score)
                fragment.arguments = bundle
                transaction.replace(R.id.frame, fragment)
                transaction.commit()
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


        //뒤로가기 팝업 버튼
        //계속하기
        popupQuizbackBinding!!.txtContinue.setOnClickListener {
            popupViewQuizBack!!.dismiss()
        }
        //끝내기
        popupQuizbackBinding!!.txtEnd.setOnClickListener {
            popupViewWritingquiz!!.dismiss()
            popupViewQuizBack!!.dismiss()

            cameraExecutor.shutdown()

            var rankscore = 0

            databaseReference = FirebaseDatabase.getInstance().getReference().child("사용자").child(mainActivity!!.kakaoId)

            databaseReference.child("rankscore").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    rankscore = score + snapshot.getValue().toString().toInt()
                    databaseReference.child("rankscore").setValue(rankscore)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
            //프래그먼트 이동
            val transaction = mainActivity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, QuizFragment())
            transaction.commit()
        }
    }


    private fun setQuiz() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("퀴즈").child("손글씨 단어 퀴즈")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val allList = arrayListOf<WritingQuiz>()

                    for (quizSnapshot in snapshot.children) {
                        val writingquiz = quizSnapshot.getValue(WritingQuiz::class.java)
                        allList.add(writingquiz!!)
                    }

                    randomList(allList)

                    binding.txtQuiz.text = quizList[0].quizText
                    binding.txtCount.text = getString(R.string.quiz_count,1)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
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
                    OCR(photoUri, photoFile)
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

    private fun OCR(uri : Uri, file: File) {
        var bitmap = MediaStore.Images.Media.getBitmap(context?.getContentResolver(), uri)
        var rotateMatrix = Matrix()
        rotateMatrix.postRotate(90F)
        bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(), bitmap.getHeight(), rotateMatrix, true)
        bitmap = Bitmap.createScaledBitmap(bitmap,300,300,true)

        var out: OutputStream? = null
        try{
            out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out?.flush()
            out?.close()
        }finally {
            out?.close()
        }

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