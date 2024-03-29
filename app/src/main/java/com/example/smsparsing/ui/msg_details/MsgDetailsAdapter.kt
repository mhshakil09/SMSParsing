package com.example.smsparsing.ui.msg_details

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.smsparsing.R
import com.example.smsparsing.api.model.sms_model.SmsModel
import com.example.smsparsing.databinding.ItemViewMsgDetailsBinding
import timber.log.Timber
import java.util.*

class MsgDetailsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<SmsModel> = mutableListOf()
    private val checkList: MutableList<String> = mutableListOf()
    private val creditList: MutableList<String> = mutableListOf()
    private val debitList: MutableList<String> = mutableListOf()

    private var totalMessageCount = 0
    private var totalCreditMessageCount = 0
    private var totalDebitMessageCount = 0
    private var totalCreditAmount = 0.00
    private var totalDebitAmount = 0.00

    var onTotalMessageCount: ((totalMessageCount: Int) -> Unit)? = null
    var onTotalCreditMessageCount: ((creditMessageCount: Int) -> Unit)? = null
    var onTotalDebitMessageCount: ((debitMessageCount: Int) -> Unit)? = null
    var onTotalCreditAmount: ((creditAmount: Double) -> Unit)? = null
    var onTotalDebitAmount: ((debitAmount: Double) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(ItemViewMsgDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {

            val model = dataList[position]
            val binding = holder.binding

            checkList.clear()
            checkList.addAll(listOf("tk", "bdt"))

            creditList.clear()
            creditList.addAll(listOf("received", "credited", "deposit"))

            debitList.clear()
            debitList.addAll(listOf("payment", "debited", "purchase", "deducted"))

            val amountCheck = """(\d+(,\d+)*(.\d+)*)""".toRegex()
            if (((model.body)?.contains(amountCheck) == true) ) {
                for (i in checkList) {
                    if (((model.body)?.lowercase(Locale.getDefault())?.contains(i) == true)) {
                        binding.msgAddress.text = model.address
                        binding.serviceCenter.text = model.serviceCenter
                        binding.msgBody.text = model.body
                        binding.msgBody.setTextColor(ContextCompat.getColor( binding.msgBody.context, R.color.black_80))

                        totalCreditMessageCount += 1

                        val amountCheckTest = """((\d+,\d+)*(\d+\.\d+))""".toRegex()
                        //val tempData : MatchResult? = amountCheckTest.find(model.body.toString())
                        //Timber.d("requestBody single ${tempData?.value}")

                        val tempData : Sequence<MatchResult> = amountCheckTest.findAll(model.body.toString())
                        var tempAmount = 0.00
                        tempData.forEach()
                        {
                            Timber.d("requestBody ${it.value}")
                            if (it.value.contains(",")) {
                                var tempString = it.value
                                Timber.d("requestBody string $tempString")
                                tempString = tempString.replace(",", "")
                                Timber.d("requestBody string $tempString")
                                tempAmount = tempString.toDouble()
                            }
                            //matchResult -> Timber.d("requestBody ${matchResult.value}")
                        }

                        totalCreditAmount += tempAmount

                        break
                    } else {
                        binding.msgAddress.text = model.address
                        binding.serviceCenter.text = model.serviceCenter
                        binding.msgBody.text = model.body
                        binding.msgBody.setTextColor(Color.BLUE)
                    }
                }
            } else {
                binding.msgAddress.text = model.address
                binding.serviceCenter.text = model.serviceCenter
                binding.msgBody.text = model.body
                binding.msgBody.setTextColor(Color.BLUE)
                //dataList.removeAt(position)
            }


        }
    }

    private inner class ViewHolder(val binding: ItemViewMsgDetailsBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            onTotalCreditAmount?.invoke(totalCreditAmount)
            onTotalCreditMessageCount?.invoke(totalCreditMessageCount)

            /*binding.nextQuestion.setOnClickListener {
                if (onOptionsSelected != null) {
                    nextQuestion?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
                }

            }*/
        }

    }

    fun loadInitData(list: MutableList<SmsModel>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun clearData() {
        dataList.clear()
        notifyDataSetChanged()
    }
}