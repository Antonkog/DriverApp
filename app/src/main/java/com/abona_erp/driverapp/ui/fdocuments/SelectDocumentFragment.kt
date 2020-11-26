package com.abona_erp.driverapp.ui.fdocuments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.Constant
import com.abona_erp.driverapp.data.Constant.OPEN_CAMERA_REQUEST_CODE
import com.abona_erp.driverapp.data.model.DMSDocumentType
import com.abona_erp.driverapp.databinding.SelectDocumentFragmentBinding
import com.abona_erp.driverapp.ui.base.BaseFragment
import com.abona_erp.driverapp.ui.utils.adapter.LazyAdapter
import com.abona_erp.driverapp.ui.utils.adapter.initWithLinLay
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileDescriptor
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class SelectDocumentFragment : BaseFragment(), LazyAdapter.OnItemClickListener<UploadDocumentItem>,
    OnSelectedDocumentTypeListener, DocumentRemoveListener {
    val TAG = "DocumentsFragment"
    private lateinit var selectDocumentFragmentBinding: SelectDocumentFragmentBinding
    var documentList = ArrayList<UploadDocumentItem>()
    private var documentEditAdapter = DocumentEditAdapter(this, this)
    var orderNo: Int = 0
    var taskId: Int = 0
    var mandantId: Int = 0
    val args: SelectDocumentFragmentArgs by navArgs()
    var documentType: DMSDocumentType = DMSDocumentType.POD_CMR
    var documentOption = DocumentOption.CAMERA

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.select_document_fragment, container, false)
        selectDocumentFragmentBinding = SelectDocumentFragmentBinding.bind(view)
        orderNo = args.taskData.orderNo ?: 0
        taskId = args.taskData.taskId ?: 0
        mandantId = args.taskData.mandantId ?: 0
        selectDocumentFragmentBinding.gallery.setOnClickListener {
            documentOption = DocumentOption.GALLERY
            chooseDocumentType()
        }
        selectDocumentFragmentBinding.camera.setOnClickListener {
            documentOption = DocumentOption.CAMERA
            chooseDocumentType()
        }
        selectDocumentFragmentBinding.done.setOnClickListener {

        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectDocumentFragmentBinding.documentList.initWithLinLay(
            LinearLayoutManager.HORIZONTAL,
            documentEditAdapter,
            listOf()
        )
        if (documentList.size > 0) {
            selectDocumentFragmentBinding.documentList.visibility = View.VISIBLE
        }
    }

    private fun chooseDocumentType() {
        val customDMSDocumentTypeDialog = CustomDMSDocumentTypeDialog(requireContext(), this)
        customDMSDocumentTypeDialog.show()
        customDMSDocumentTypeDialog.setCancelable(false)
    }

    private fun openDocumentPicker() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, Constant.OPEN_DOC_REQUEST_CODE)
    }

    private fun checkForPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 1)
        } else {
            openCamera()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == Constant.OPEN_DOC_REQUEST_CODE
            && resultCode == Activity.RESULT_OK
        ) {
            val uri = resultData?.data
            uri?.let {
                val imageBitmap = getBitmapFromUri(uri)
                imageBitmap?.let {
                    setPhotoList(it)
                }
            }
        } else if (requestCode == OPEN_CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = resultData?.extras?.get("data") as Bitmap
            setPhotoList(imageBitmap)
        }
    }

    private fun setPhotoList(imageBitmap: Bitmap) {
        val photoFile: File?
        try {
            photoFile = FileUtils.saveImage(
                requireContext(),
                imageBitmap,
                orderNo.toString(),
                taskId.toString(),
                mandantId.toString()
            )
            val currentTimestamp = Date()
            photoFile?.let {
                val currentPath = Uri.fromFile(it)
                val uploadItem = UploadDocumentItem(
                    currentPath,
                    false,
                    documentType,
                    currentTimestamp,
                    currentTimestamp
                )
                selectDocumentFragmentBinding.documentList.visibility = View.VISIBLE
                documentList.add(uploadItem)
                documentEditAdapter.swapData(documentList)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        val parcelFileDescriptor: ParcelFileDescriptor? =
            requireContext().contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor: FileDescriptor? = parcelFileDescriptor?.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor?.close()
        return image
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "To access we need your permission for camera",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, OPEN_CAMERA_REQUEST_CODE)
    }

    override fun onLazyItemClick(data: UploadDocumentItem) {
        val bundle =
            bundleOf(
                getString(R.string.key_upload_document) to data,
                getString(R.string.key_task_data) to args.taskData
            )
        findNavController().navigate(R.id.action_nav_document_to_editDocumentFragment, bundle)
    }

    override fun onSelectType(documentType: DMSDocumentType) {
        this.documentType = documentType
        when (documentOption) {
            DocumentOption.CAMERA -> {
                checkForPermission()
            }
            DocumentOption.GALLERY -> {
                openDocumentPicker()
            }
        }
    }

    override fun onRemove(uploadDocumentItem: UploadDocumentItem) {
        if (documentList.contains(uploadDocumentItem)) {
            documentList.remove(uploadDocumentItem)
        }

        if (documentList.size >= 0) {
            documentEditAdapter.swapData(documentList)
        } else {
            selectDocumentFragmentBinding.documentList.visibility = View.GONE
        }
    }
}