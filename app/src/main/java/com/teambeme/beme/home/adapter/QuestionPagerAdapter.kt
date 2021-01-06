package com.teambeme.beme.home.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.teambeme.beme.R
import com.teambeme.beme.answer.view.AnswerActivity
import com.teambeme.beme.databinding.ItemHomeQuestionBinding
import com.teambeme.beme.home.model.Question

class QuestionPagerAdapter :
    RecyclerView.Adapter<QuestionPagerAdapter.QuestionViewHolder>() {
    private var questionList = mutableListOf<Question>()

    class QuestionViewHolder(private val context: Context, private val binding: ItemHomeQuestionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(question: Question) {
            binding.question = question
            binding.btnHomeAnswer.setOnClickListener {
                val intent = Intent(context, AnswerActivity::class.java)
                intent.putExtra("id", question.id)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemHomeQuestionBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.item_home_question, parent, false)
        return QuestionViewHolder(parent.context, binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.onBind(questionList[position])
    }

    override fun getItemCount() = questionList.size

    fun replaceQuestionList(list: List<Question>) {
        questionList = list.toMutableList()
        notifyDataSetChanged()
    }
}