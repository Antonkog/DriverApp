package com.abona_erp.driverapp.ui.fdocuments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.databinding.DocumentEditFragmentBinding
import com.abona_erp.driverapp.ui.RxBus
import com.abona_erp.driverapp.ui.base.BaseFragment
import com.abona_erp.driverapp.ui.events.RxBusEvent
import com.abona_erp.driverapp.ui.utils.adapter.LazyAdapter
import com.abona_erp.driverapp.ui.utils.adapter.initWithLinLay
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import ja.burhanrashid52.photoeditor.*
import ja.burhanrashid52.photoeditor.PhotoEditor.OnSaveListener
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DocumentEditFragment : BaseFragment(), PropertiesListener,
    TextEditorDialogFragment.TextEditor, LazyAdapter.OnItemClickListener<EditOptionEntity> {
    private lateinit var documentEditFragmentBinding: DocumentEditFragmentBinding
    private val args: DocumentEditFragmentArgs by navArgs()
    lateinit var photoEditor: PhotoEditor
    private lateinit var propertiesFragment: PropertiesFragment
    private var editOptionAdapter = EditOptionAdapter(this)
    private lateinit var uploadDocumentItem: UploadDocumentItem
    private lateinit var taskData: TaskData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.document_edit_fragment, container, false)
        documentEditFragmentBinding = DocumentEditFragmentBinding.bind(view)
        documentEditFragmentBinding.lifecycleOwner = this.viewLifecycleOwner
        uploadDocumentItem = args.uploadDocument
        taskData = args.taskData
        documentEditFragmentBinding.photoEditorView.source.setImageURI(uploadDocumentItem.uri)
        photoEditor =
            PhotoEditor.Builder(requireContext(), documentEditFragmentBinding.photoEditorView)
                .setPinchTextScalable(true)
                .build()
        propertiesFragment = PropertiesFragment()
        propertiesFragment.setPropertiesChangeListener(this)
        documentEditFragmentBinding.editOptions.initWithLinLay(
            LinearLayoutManager.HORIZONTAL,
            editOptionAdapter,
            DocumentEditDataOption.getEditOption()
        )
        val filterViewAdapter =
            FilterViewAdapter(object : LazyAdapter.OnItemClickListener<FilterOptionEntity> {
                override fun onLazyItemClick(data: FilterOptionEntity) {
                    photoEditor.setFilterEffect(data.photoFilter)
                }
            })
        documentEditFragmentBinding.filterOptions.initWithLinLay(
            LinearLayoutManager.HORIZONTAL,
            filterViewAdapter,
            DocumentEditDataOption.getFilterOptions()
        )
        documentEditFragmentBinding.close.setOnClickListener {
            hideFilter()
        }
        RxBus.listen(RxBusEvent.DocumentCropMessage::class.java).subscribe { event ->
            documentEditFragmentBinding.photoEditorView.source.setImageURI(event.uri)
        }
        hideFilter()
        return view
    }

    private fun showBottomSheetDialogFragment(fragment: BottomSheetDialogFragment?) {
        if (fragment == null || fragment.isAdded) {
            return
        }
        fragment.show(requireActivity().supportFragmentManager, fragment.tag)
    }

    override fun onColorChanged(colorCode: Int) {
        photoEditor.brushColor = colorCode
    }

    override fun onOpacityChanged(opacity: Int) {
        photoEditor.setOpacity(opacity)
    }

    override fun onBrushSizeChanged(brushSize: Int) {
        photoEditor.brushSize = brushSize.toFloat()
    }

    override fun onDone(inputText: String?, colorCode: Int) {
        val styleBuilder = TextStyleBuilder()
        styleBuilder.withTextColor(colorCode)
        photoEditor.addText(inputText, styleBuilder)
    }

    override fun onLazyItemClick(data: EditOptionEntity) {
        when (data.type) {
            EditOptionEnum.BRUSH -> {
                photoEditor.setBrushDrawingMode(true)
                showBottomSheetDialogFragment(propertiesFragment)
            }
            EditOptionEnum.TEXT -> {
                val args = Bundle()
                args.putString(TextEditorDialogFragment.EXTRA_INPUT_TEXT, "")
                args.putInt(
                    TextEditorDialogFragment.EXTRA_COLOR_CODE,
                    ContextCompat.getColor(requireContext(), R.color.white)
                )
                val fragment = TextEditorDialogFragment()
                fragment.arguments = args
                fragment.show(childFragmentManager.beginTransaction(), TextEditorDialogFragment.TAG)
                fragment.setOnTextEditorListener(this)
            }
            EditOptionEnum.CROP_ROTATE -> {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    val timeStamp =
                        SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
                    val fileName = "EditCrop_$timeStamp"
                    val storageDir = requireContext().cacheDir
                    val file = File.createTempFile(fileName, ".png", storageDir)
                    try {
                        val saveSettings = SaveSettings.Builder()
                            .setClearViewsEnabled(true)
                            .setTransparencyEnabled(true)
                            .build()
                        photoEditor.saveAsFile(
                            file.absolutePath,
                            saveSettings,
                            object : OnSaveListener {
                                override fun onSuccess(imagePath: String) {
                                    val saveImageUri = Uri.fromFile(File(imagePath))
                                    documentEditFragmentBinding.photoEditorView.source.setImageURI(
                                        saveImageUri
                                    )
                                    val currentTimeStamp =
                                        SimpleDateFormat(
                                            "yyyyMMddHHmmss",
                                            Locale.getDefault()
                                        ).format(Date())
                                    val destinationFileName = "CropImage_$currentTimeStamp.png"
                                    val destinationUri = Uri.fromFile(
                                        File(
                                            requireActivity().cacheDir,
                                            destinationFileName
                                        )
                                    )
                                    val uCrop = UCrop.of(saveImageUri, destinationUri)
                                    val options = UCrop.Options()
                                    options.setCompressionFormat(Bitmap.CompressFormat.PNG)
                                    options.setFreeStyleCropEnabled(true)
                                    uCrop.withOptions(options)
                                    uCrop.start(requireActivity())
                                }

                                override fun onFailure(exception: Exception) {
                                    exception.printStackTrace()
                                }
                            })
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            EditOptionEnum.FILTER -> {
                showFilter()
            }
            EditOptionEnum.UNDO -> {
                photoEditor.undo()
            }
            EditOptionEnum.REDO -> {
                photoEditor.redo()
            }
        }
    }

    private fun showFilter() {
        documentEditFragmentBinding.filterGroup.visibility = View.VISIBLE
        documentEditFragmentBinding.editOptions.visibility = View.GONE
    }

    private fun hideFilter() {
        documentEditFragmentBinding.filterGroup.visibility = View.GONE
        documentEditFragmentBinding.editOptions.visibility = View.VISIBLE
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.action_edit_doc).let {
            it?.setVisible(true)
        }
        menu.findItem(R.id.action_log_out).let {
            it?.setVisible(false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit_doc -> {
                saveEditImage()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveEditImage() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val fileName = FileUtils.getFileName(
                taskData.orderNo.toString(),
                taskData.taskId.toString(),
                taskData.mandantId.toString()
            )
            val file = FileUtils.createImageFile(requireContext(), fileName)
            try {
                val saveSettings = SaveSettings.Builder()
                    .setClearViewsEnabled(true)
                    .setTransparencyEnabled(true)
                    .build()
                photoEditor.saveAsFile(
                    file.absolutePath,
                    saveSettings,
                    object : OnSaveListener {
                        override fun onSuccess(imagePath: String) {
                            val saveImageUri =
                                Uri.fromFile(File(imagePath))
                            uploadDocumentItem.uri = saveImageUri
                            uploadDocumentItem.ModifiedAt = Date()
                            requireActivity().onBackPressed()
                        }

                        override fun onFailure(exception: Exception) {
                            exception.printStackTrace()
                        }
                    })
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val TAG = "DocumentEditFragment"
    }

}