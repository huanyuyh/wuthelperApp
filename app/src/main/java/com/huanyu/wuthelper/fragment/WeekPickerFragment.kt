package com.huanyu.wuthelper.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.huanyu.wuthelper.databinding.FragmentWeekPickerBinding
import com.huanyu.wuthelper.utils.SPTools.Companion.getWeekCount


class WeekPickerFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentWeekPickerBinding? = null
    private val binding get() = _binding!!

    var onWeekSelected: ((Int) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWeekPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var numWeek = getWeekCount(requireContext())
        // 假设一个学期有20周
        val totalWeeks = numWeek
        val weeksArray = Array(totalWeeks) { i -> "第${i + 1}周" }

        binding.numberPicker.apply {
            minValue = 1 // 设置最小值
            maxValue = totalWeeks // 设置最大值为20
            displayedValues = weeksArray // 设置显示的字符串数组
            value = 1 // 假设当前周为1，应从ViewModel获取实际值
        }

        binding.buttonConfirm.setOnClickListener {
            onWeekSelected?.invoke(binding.numberPicker.value)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "WeekPickerFragment"
        private const val LOG_WeekPickerFragment = "WeekPickerFragment:"
    }
}
