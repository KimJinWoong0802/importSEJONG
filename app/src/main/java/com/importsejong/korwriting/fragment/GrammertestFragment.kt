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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread

//Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GrammertestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@RequiresApi(Build.VERSION_CODES.O)
class GrammertestFragment : Fragment() {
    // Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mBinding: FragmentGrammertestBinding? = null
    private val binging get() = mBinding!!
    private var mainActivity: MainActivity? = null

    private var dao = Dao

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
    private lateinit var photoUri: Uri
    private lateinit var photoUrl: String

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
        mBinding = FragmentGrammertestBinding.inflate(inflater, container, false)
        val title = getString(R.string.grammertest_menu)+"\n"+getString(R.string.grammertest_title)
        binging.toolbar.title.text = title

        //?????? ??????
        popupOcrBinding = DialogPopupOcrBinding.inflate(inflater, container, false)
        builder1 = AlertDialog.Builder(requireContext()).setView(popupOcrBinding!!.root).setCancelable(false)
        popupView1 = builder1!!.create()
        setTextSizeOcr(mainActivity!!.textSize)

        popupOcr2Binding = DialogPopupOcr2Binding.inflate(inflater, container, false)
        builder2 = AlertDialog.Builder(requireContext()).setView(popupOcr2Binding!!.root).setCancelable(false)
        popupView2 = builder2!!.create()
        setTextSizeOcr2(mainActivity!!.textSize)

        popupResultBinding = DialogPopupResultBinding.inflate(inflater, container, false)
        builder3 = AlertDialog.Builder(requireContext()).setView(popupResultBinding!!.root).setCancelable(false)
        popupView3 = builder3!!.create()
        setTextSizeResult(mainActivity!!.textSize)

        //????????? ?????? ??????
        startCamera()

        setButton()

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        return binging.root
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
        //Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GrammertestFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun setButton() {
        //????????????
        binging.imageButton.setOnClickListener{
            takePhoto()
            popupView1!!.show()
        }

        //popupOcr ?????? ??????
        //???????????? ?????????
        popupOcrBinding!!.btnOcrYes.setOnClickListener {
            val beforeText: String = popupOcrBinding!!.txtOcrBefore.text.toString()
            popupResultBinding!!.txtResultBefore.text = beforeText
            spellCheck(beforeText)

            popupView1!!.dismiss()
            popupView3!!.show()

            //?????????
            popupOcrBinding!!.txtOcrBefore.text = getText(R.string.noText)
        }
        //???????????? ????????????
        popupOcrBinding!!.btnOcrNo.setOnClickListener{
            popupOcr2Binding!!.txtOcr2Before.setText(popupOcrBinding!!.txtOcrBefore.text)

            popupView1!!.dismiss()
            popupView2!!.show()

            //?????????
            popupOcrBinding!!.txtOcrBefore.text = getText(R.string.noText)
        }

        //popupOcr2 ?????? ??????
        //???????????? ??????
        popupOcr2Binding!!.btnOcr2Yes.setOnClickListener {
            val beforeText: String = popupOcr2Binding!!.txtOcr2Before.text.toString()
            popupResultBinding!!.txtResultBefore.text = beforeText
            spellCheck(beforeText)

            popupView2!!.dismiss()
            popupView3!!.show()

            //?????????
            popupOcr2Binding!!.txtOcr2Before.setText(getText(R.string.noText))
        }
        //???????????? ??????
        popupOcr2Binding!!.btnOcr2No.setOnClickListener {
            popupView2!!.dismiss()

            //?????????
            popupOcr2Binding!!.txtOcr2Before.setText(getText(R.string.noText))
        }

        //popupResult ?????? ??????
        //??????????????? ??????
        popupResultBinding!!.btnResultOk.setOnClickListener {
            popupView3!!.dismiss()

            //?????????
            popupResultBinding!!.txtResultBefore.text = getText(R.string.noText)
            popupResultBinding!!.txtAfter.text = getText(R.string.noText)
            popupResultBinding!!.txtAfter2.text = getText(R.string.noText)
            popupResultBinding!!.txtAfter3.text = getText(R.string.noText)
        }
        //??????????????? ??????????????????
        popupResultBinding!!.btnResultBookmark.setOnClickListener {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy??? MM??? dd??? HH??? mm??? ss???")
            val formatted = current.format(formatter)

            storageReference = FirebaseStorage.getInstance().reference.child("images/${mainActivity!!.kakaoId}/????????? ??????/$formatted.jpg")

            storageReference.putFile(photoUri).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    storageReference.downloadUrl.addOnSuccessListener {
                        photoUrl = it.toString()
                        databaseReference.child("?????????").child(mainActivity!!.kakaoId)
                            .child("????????? ??????").child(formatted).child("photourl")
                            .setValue(photoUrl)
                    }
                }
            }

            databaseReference.child("?????????").child(mainActivity!!.kakaoId)
                .child("????????? ??????").child(formatted).child("inputsentence")
                .setValue(popupResultBinding!!.txtResultBefore.text.toString())

            databaseReference.child("?????????").child(mainActivity!!.kakaoId)
                .child("????????? ??????").child(formatted).child("date")
                .setValue(formatted)

            databaseReference.child("?????????").child(mainActivity!!.kakaoId)
                .child("????????? ??????").child(formatted).child("parttofix")
                .setValue(popupResultBinding!!.txtAfter.text.toString())

            databaseReference.child("?????????").child(mainActivity!!.kakaoId)
                .child("????????? ??????").child(formatted).child("fixedpart")
                .setValue(popupResultBinding!!.txtAfter2.text.toString())

            databaseReference.child("?????????").child(mainActivity!!.kakaoId)
                .child("????????? ??????").child(formatted).child("help")
                .setValue(popupResultBinding!!.txtAfter3.text)

            //?????????
            popupResultBinding!!.txtResultBefore.text = getText(R.string.noText)
            popupResultBinding!!.txtAfter.text = getText(R.string.noText)
            popupResultBinding!!.txtAfter2.text = getText(R.string.noText)
            popupResultBinding!!.txtAfter3.text = getText(R.string.noText)

            popupView3!!.dismiss()
        }
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
                    Toast.makeText(requireContext(), "????????????", Toast.LENGTH_SHORT).show()

                    //OCR??????
                    OCR(photoUri,photoFile)
                }
            })
    }

    private fun getfile(file:File):File{ return file }
    // ????????? ?????? ??????
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

    //?????? ?????? ??????
    private fun setTextSizeOcr(textSize :Int) {
        val size34 :Float = 30.0f + textSize*2
        val size24 :Float = 20.0f + textSize*2
        val size20 :Float = 16.0f + textSize*2

        popupOcrBinding!!.txtOcrMain.textSize = size34
        popupOcrBinding!!.txtOcrContent.textSize = size20
        popupOcrBinding!!.txtOcrBefore.textSize = size24
        popupOcrBinding!!.btnOcrYes.textSize = size20
        popupOcrBinding!!.btnOcrNo.textSize = size20
    }

    private fun setTextSizeOcr2(textSize :Int) {
        val size34 :Float = 30.0f + textSize*2
        val size20 :Float = 16.0f + textSize*2

        popupOcr2Binding!!.txtOcr2Main.textSize = size34
        popupOcr2Binding!!.txtOcr2Content.textSize = size20
        popupOcr2Binding!!.txtOcr2Before.textSize = size20
        popupOcr2Binding!!.btnOcr2Yes.textSize = size20
        popupOcr2Binding!!.btnOcr2No.textSize = size20
    }

    private fun setTextSizeResult(textSize :Int) {
        val size34 :Float = 30.0f + textSize*2
        val size24 :Float = 20.0f + textSize*2

        popupResultBinding!!.txtResultMain.textSize = size34
        popupResultBinding!!.txtResultContent.textSize = size24
        popupResultBinding!!.txtResultBefore.textSize = size24
        popupResultBinding!!.textView11.textSize = size24
        popupResultBinding!!.txtAfter.textSize = size24
        popupResultBinding!!.txtAfter2.textSize = size24
        popupResultBinding!!.textView24.textSize = size24
        popupResultBinding!!.btnResultOk.textSize = size24
        popupResultBinding!!.btnResultBookmark.textSize = size24
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
                    Log.d("??????", "?????? : ${response.raw()}")
                    Log.d("??????", "?????? : ${response.body()}")
                    popupOcrBinding!!.txtOcrBefore.text =
                        response.body().toString().replace(("[^\\D]").toRegex(), "").replace("[, ]","").replace("=[","")
                            .replace(("[a-z]").toRegex(), "").replace(("[A-Z]").toRegex(), "").replace(("_").toRegex(), " ")
                            .replace(", , , ],","").replace("]),","").replace("( ","").replace("(","")
                            .replace("])])","")
                }
                override fun onFailure(call: Call<ocrdata>, t: Throwable) {
                    Log.d("??????:", "?????? : $t")
                    popupOcrBinding!!.txtOcrBefore.text = t.toString()
                }
            }
        )
    }

    private fun spellCheck(spell:String) {
        thread(start = true) {
            try {
                val urlText = "https://speller.cs.pusan.ac.kr/results?text1=$spell"
                val url = URL(urlText)
                val netConn = url.openConnection() as HttpURLConnection
                netConn.requestMethod = "POST"
                netConn.setRequestProperty("Accept", "application/json")
                //?????? ?????????????
                netConn.doOutput
                //response code ????????????
                if(netConn.responseCode == HttpURLConnection.HTTP_OK) {
                    val streamReader = InputStreamReader(netConn.inputStream)
                    val buffered = BufferedReader(streamReader)

                    val content = StringBuilder()
                    var i = 1

                    while(true) {
                        val line = buffered.readLine() ?: break
                        content.append("$i. ")
                        content.append(line)
                        content.append("\n")
                        i++
                    }
                    buffered.close()
                    netConn.disconnect()

                    mainActivity!!.runOnUiThread {
                        val testString : String = content.toString()
                        Log.d("tset",testString)
                        try{
                            val splitArray : ArrayList<String> = testString.split("\"orgStr\":\"") as ArrayList<String>
                            Log.d("split",splitArray.toString())
                            var testArray :ArrayList<String>
                            val len = splitArray.size
                            val resultArray = ArrayList<String>()
                            for (n: Int in 1..len step 2){
                                try {
                                    testArray = splitArray[n].split("\",") as ArrayList<String>
                                    Log.d("List ckeck",testArray.toString())
                                    resultArray.add(testArray[0])

                                }catch (e:Exception){
                                    break
                                }
                            }
                            popupResultBinding!!.txtAfter.text = resultArray.toString().replace("[","").replace("]","")

                        }catch (e: Exception){
                            popupResultBinding!!.txtAfter.text = "???????????? ?????? ????????? ?????? ???????????????, ????????? ????????? ?????? ?????? ????????? ????????? ?????? ????????? ?????? ??? ????????????."
                        }
                        try{
                            val splitArray : ArrayList<String> = testString.split("\"candWord\":\"") as ArrayList<String>
                            Log.d("split",splitArray.toString())
                            var testArray :ArrayList<String>
                            val len = splitArray.size
                            val resultArray = ArrayList<String>()
                            for (n: Int in 1..len step 2){
                                try {
                                    testArray = splitArray[n].split("\"}]") as ArrayList<String>
                                    Log.d("List ckeck",testArray.toString())
                                    resultArray.add(testArray[0])
                                }catch (e:Exception){
                                    break
                                }
                            }
                            popupResultBinding!!.txtAfter2.text = resultArray.toString().replace("[","").replace("]","")
                        }catch (e: Exception){
                            popupResultBinding!!.txtAfter.text = "???????????? ?????? ????????? ?????? ???????????????, ????????? ????????? ?????? ?????? ????????? ????????? ?????? ????????? ?????? ??? ????????????."
                        }
                        try{
                            val helpArray : ArrayList<String> = testString.split("\"help\":\"") as ArrayList<String>
                            Log.d("split",helpArray.toString())
                            var testArray :ArrayList<String>
                            val len = helpArray.size
                            val helpresultArray = ArrayList<String>()
                            for (n: Int in 1..len step 2){
                                try {
                                    testArray = helpArray[n].split("\",\"") as ArrayList<String>
                                    Log.d("List ckeck",testArray.toString())
                                    helpresultArray.add(testArray[0])
                                }catch (e:Exception){
                                    break
                                }
                            }

                            popupResultBinding!!.txtAfter3.text = helpresultArray.toString().replace("&apos;","").replace("[","").replace("]","")
                        }catch (e: Exception){
                            popupResultBinding!!.txtAfter3.text = "???????????? ?????? ????????? ?????? ???????????????."
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}