package com.importsejong.korwriting.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import com.importsejong.korwriting.MainActivity
import com.importsejong.korwriting.R
import com.importsejong.korwriting.databinding.FragmentWritingtestTwoBinding
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WritingtestTwoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class WritingtestTwoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mBinding: FragmentWritingtestTwoBinding? = null
    private val binging get() = mBinding!!
    private var mainActivity: MainActivity? = null

    private lateinit var storageReference: StorageReference
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference: DatabaseReference = firebaseDatabase.reference

    private lateinit var outputDirectory: File

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
        mBinding = FragmentWritingtestTwoBinding.inflate(inflater, container, false)
        outputDirectory = getOutputDirectory()
        val getStringData = arguments?.getString("dataString")
        binging.textView18.text = getStringData
        binging.textView17.text = getStringData

        setTextSize(mainActivity!!.textSize)

        setButton()
        setImage()

        return binging.root
        //return inflater.inflate(R.layout.fragment_writingtest, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WritingtestTwoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WritingtestTwoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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

    private fun setImage() {
        val photoFile = File(
            outputDirectory,
            resources.getString(R.string.grammertest_fileName))
        val savedUri = Uri.fromFile(photoFile)
        binging.imageView5.setImageURI(savedUri)
    }

    //글씨 크기 변경
    fun setTextSize(textSize :Int) {
        val size20 :Float = 16.0f + textSize*2
        val size24 :Float = 20.0f + textSize*2
        val size34 :Float = 30.0f + textSize*2
        val size14 :Float = 10.0f + textSize*2

        binging.textView18.textSize = size20
        binging.textView21.textSize = size20
        binging.textView16.textSize = size24
        binging.textView19.textSize = size24
        binging.textView20.textSize = size34
        binging.button2.textSize = size14
    }

    private fun setButton() {
        binging.button2.setOnClickListener {
            //프래그먼트 이동
            val transaction = mainActivity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, WritingtestFragment())
            transaction.commit()
        }
    }
}