package com.abona_erp.driverapp.ui.fdocuments

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.databinding.AddTextDialogBinding
import com.abona_erp.driverapp.ui.utils.adapter.LazyAdapter
import com.abona_erp.driverapp.ui.utils.adapter.initWithLinLay
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

class TextEditorDialogFragment : DialogFragment(), LazyAdapter.OnItemClickListener<Int> {
    private lateinit var textBinding: AddTextDialogBinding
    private var mInputMethodManager: InputMethodManager? = null
    private var mColorCode = 0
    private var mTextEditor: TextEditor? = null

    interface TextEditor {
        fun onDone(inputText: String?, colorCode: Int)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_text_dialog, container, false)
        textBinding = AddTextDialogBinding.bind(view)
        return view
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        mInputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val colorPickerAdapter = ColorPickerAdapter(this)
        textBinding.addTextColorPickerRecyclerView.initWithLinLay(
            LinearLayoutManager.HORIZONTAL,
            colorPickerAdapter,
            DocumentEditDataOption.getDefaultColors(requireContext())
        )
        textBinding.addTextEditText.setText(requireArguments().getString(EXTRA_INPUT_TEXT))
        mColorCode = requireArguments().getInt(EXTRA_COLOR_CODE)
        textBinding.addTextEditText.setTextColor(mColorCode)
        mInputMethodManager!!.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            0
        )

        textBinding.addTextDoneTv.setOnClickListener { _ ->
            mInputMethodManager!!.hideSoftInputFromWindow(view.windowToken, 0)
            dismiss()
            val inputText = textBinding.addTextEditText.text.toString()
            if (!TextUtils.isEmpty(inputText) && mTextEditor != null) {
                mTextEditor!!.onDone(inputText, textBinding.addTextEditText.currentTextColor)
            }
        }
    }

    fun setOnTextEditorListener(textEditor: TextEditor?) {
        mTextEditor = textEditor
    }

    override fun onResume() {
        super.onResume()
        dialog?.let {
            val params = it.window!!.attributes
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.window!!.attributes = params as android.view.WindowManager.LayoutParams
        }
    }

    companion object {
        val TAG = TextEditorDialogFragment::class.java.simpleName
        const val EXTRA_INPUT_TEXT = "extra_input_text"
        const val EXTRA_COLOR_CODE = "extra_color_code"
    }

    override fun onLazyItemClick(data: Int) {
        textBinding.addTextEditText.setTextColor(data)
    }
}