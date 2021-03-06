package com.teambeme.beme.home.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.teambeme.beme.R
import com.teambeme.beme.answer.model.IntentAnswerData
import com.teambeme.beme.answer.view.AnswerActivity
import com.teambeme.beme.databinding.ItemHomeMoreQuestionBinding
import com.teambeme.beme.databinding.ItemHomeQuestionBinding
import com.teambeme.beme.home.model.Answer
import com.teambeme.beme.home.view.AnswerSuggestFragment
import com.teambeme.beme.home.view.InfoChangeFragment
import com.teambeme.beme.home.view.InfoChangeFragment.InfoChangeClickListener
import com.teambeme.beme.home.view.TransitionPublicFragment
import com.teambeme.beme.home.viewmodel.HomeViewModel

class QuestionPagerAdapter(
    private val fragmentManager: FragmentManager,
    private val homeViewModel: HomeViewModel,
    private val questionButtonClickListener: QuestionButtonClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var answerList = mutableListOf<Answer>()

    inner class QuestionViewHolder(
        private val context: Context,
        private val binding: ItemHomeQuestionBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(answer: Answer, position: Int) {
            binding.answer = answer

            binding.btnHomeAnswer.setOnClickListener {
                questionButtonClickListener.onAnswerButtonClick(answer, position - 1)
            }

            binding.imgQuestionLock.setOnClickListener {
                TransitionPublicFragment(answerList[position - 1].publicFlag,
                    object : TransitionPublicFragment.ChangePublicClickListener {
                        override fun onClick() {
                            homeViewModel.changePublic(position - 1)
                        }
                    }
                ).show(fragmentManager, "TransitionPublic")
            }

            binding.txtHomeEdit.setOnClickListener {
                InfoChangeFragment(object : InfoChangeClickListener {
                    override fun modifyAnswer() {
                        val currentAnswer = answerList[position - 1]
                        val intentAnswerData = IntentAnswerData(
                            questionId = currentAnswer.questionId,
                            answerId = currentAnswer.id,
                            title = currentAnswer.questionTitle,
                            category = currentAnswer.questionCategoryName,
                            categoryIdx = currentAnswer.answerIdx?.toInt(),
                            createdAt = currentAnswer.createdAt,
                            content = currentAnswer.content ?: "",
                            isPublic = transformIntToBoolean(currentAnswer.publicFlag),
                            isCommentBlocked = transformIntToBoolean(currentAnswer.commentBlockedFlag)
                        )
                        val isModify = 100
                        val intent = Intent(context, AnswerActivity::class.java)
                        intent.putExtra("intentAnswerData", intentAnswerData)
                        intent.putExtra("isChange", 100)
                        context.startActivity(intent)
                    }

                    override fun deleteAnswer() {
                        homeViewModel.deleteAnswer(position - 1)
                    }
                }).show(
                    fragmentManager,
                    "InfoChangeBottomSheet"
                )
            }

            binding.txtHomeChangeQuestion.setOnClickListener {
                homeViewModel.changeQuestion(position)
            }
        }
    }

    inner class MoreQuestionViewHolder(
        private val binding: ItemHomeMoreQuestionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(supportFragmentManager: FragmentManager) {
            binding.btnHomeMoreQuestion.setOnClickListener {
                Log.d("Home", "${answerList.all { answer -> answer.content != null }}")
                if (answerList.all { answer -> answer.content != null }) {
                    homeViewModel.getMoreQuestion()
                } else {
                    val answerSuggestFragment = AnswerSuggestFragment()
                    answerSuggestFragment.show(supportFragmentManager, "CustomDialog")
                }
            }
        }
    }

    inner class AddPastQuestionViewHolder(
        private val binding: ItemHomeMoreQuestionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind() {
            binding.txtHomeMoreQuestion.text = "?????? ???????????? ?????? ?????????\n???????????? ???????????? ?????????"
            binding.btnHomeMoreQuestion.text = "????????? ?????? ??????"
            binding.btnHomeMoreQuestion.setOnClickListener {
                homeViewModel.getMoreAnswers()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_HEADER
            answerList.size + 1 -> TYPE_FOOTER
            else -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> {
                val binding: ItemHomeMoreQuestionBinding =
                    DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.item_home_more_question,
                        parent,
                        false
                    )
                AddPastQuestionViewHolder(binding)
            }
            TYPE_FOOTER -> {
                val binding: ItemHomeMoreQuestionBinding =
                    DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.item_home_more_question,
                        parent,
                        false
                    )
                MoreQuestionViewHolder(binding)
            }
            else -> {
                val binding: ItemHomeQuestionBinding =
                    DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.item_home_question,
                        parent,
                        false
                    )
                QuestionViewHolder(parent.context, binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (position) {
            0 -> {
                with(holder as AddPastQuestionViewHolder) { holder.onBind() }
            }
            answerList.size + 1 -> {
                with(holder as MoreQuestionViewHolder) { holder.onBind(fragmentManager) }
            }
            else -> {
                with(holder as QuestionViewHolder) {
                    holder.onBind(
                        answerList[position - 1],
                        position
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return answerList.size + 2
    }

    fun replaceQuestionList(list: List<Answer>) {
        answerList = list.toMutableList()
        Log.d("Home", answerList.toString())
        notifyDataSetChanged()
    }

    interface QuestionButtonClickListener {
        fun onAnswerButtonClick(answer: Answer, position: Int)
    }

    fun transformIntToBoolean(value: Int?): Boolean {
        return when (value) {
            0 -> false
            else -> true
        }
    }

    companion object {
        const val TYPE_FOOTER = 0
        const val TYPE_ITEM = 1
        const val TYPE_HEADER = 2
    }
}