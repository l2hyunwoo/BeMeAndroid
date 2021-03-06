package com.teambeme.beme.explore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teambeme.beme.explore.model.ResponseExplorationMinds
import com.teambeme.beme.explore.model.ResponseExplorationQuestionForFirstAnswer
import com.teambeme.beme.explore.model.ResponseExplorationQuestions
import com.teambeme.beme.explore.model.ResponseExplorationScrap
import com.teambeme.beme.explore.repository.ExploreRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExploreViewModel(private val exploreRepository: ExploreRepository) : ViewModel() {
    private val _otherMindsList = MutableLiveData<List<ResponseExplorationMinds.Data>>()
    val otherMindsList: LiveData<List<ResponseExplorationMinds.Data>>
        get() = _otherMindsList

    private var _userNickname: String = ""
    val userNickname: String
        get() = _userNickname

    private var tempOtherQuestionsList: MutableList<ResponseExplorationQuestions.Data.Answer?>? =
        mutableListOf()
    private val _otherQuestionsList =
        MutableLiveData<MutableList<ResponseExplorationQuestions.Data.Answer?>>()
    val otherQuestionsList: LiveData<MutableList<ResponseExplorationQuestions.Data.Answer?>>
        get() = _otherQuestionsList

    private var tempSameQuestionOtherAnswersList: MutableList<ResponseExplorationQuestions.Data.Answer?>? =
        mutableListOf()
    private val _sameQuestionOtherAnswersList =
        MutableLiveData<MutableList<ResponseExplorationQuestions.Data.Answer?>>()
    val sameQuestionOtherAnswersList: LiveData<MutableList<ResponseExplorationQuestions.Data.Answer?>>
        get() = _sameQuestionOtherAnswersList

    private var _questionForFirstAnswer =
        MutableLiveData<ResponseExplorationQuestionForFirstAnswer.Answer>()
    val questionForFirstAnswer: LiveData<ResponseExplorationQuestionForFirstAnswer.Answer>
        get() = _questionForFirstAnswer

    private var _scrapData = ResponseExplorationScrap("", 0, true)
    val scrapData: ResponseExplorationScrap
        get() = _scrapData

    private var _chipChecked = mutableListOf(false, false, false, false, false, false)
    val chipChecked: MutableList<Boolean>
        get() = _chipChecked

    private var _categoryNum: Int? = null
    val categoryNum: Int?
        get() = _categoryNum

    private var _sortingText: String = "??????"
    val sortingText: String
        get() = _sortingText

    private var _page: Int = 1
    val page: Int
        get() = _page

    private var _isMorePage = MutableLiveData(false)
    val isMorePage: LiveData<Boolean>
        get() = _isMorePage

    private var otherAnswersQuestionsID: Int = 0

    fun setCategoryNum(category: Int) {
        _page = 1
        chipChecked[category - 1] = !chipChecked[category - 1]
        if (chipChecked == listOf(false, false, false, false, false, false)) {
            _categoryNum = null
        } else {
            _chipChecked = mutableListOf(false, false, false, false, false, false)
            chipChecked[category - 1] = !chipChecked[category - 1]
            _categoryNum = category
        }
        requestOtherQuestionsWithCategorySorting(_categoryNum, _sortingText)
    }

    fun setSortingTextFromExplore(sorting: String) {
        _page = 1
        _sortingText = sorting
        requestOtherQuestionsWithCategorySorting(_categoryNum, _sortingText)
    }

    fun setSortingTextFromExploreDetail(questionId: Int, sorting: String) {
        _page = 1
        _sortingText = sorting
        requestSameQuestionsOtherAnswers(questionId, sorting)
    }

    fun requestOtherMinds() {
        exploreRepository.getExplorationAnother()
            .enqueue(
                object : Callback<ResponseExplorationMinds> {
                    override fun onResponse(
                        call: Call<ResponseExplorationMinds>,
                        response: Response<ResponseExplorationMinds>
                    ) {
                        if (response.isSuccessful)
                            _otherMindsList.value = response.body()!!.data?.toList()
                    }

                    override fun onFailure(call: Call<ResponseExplorationMinds>, t: Throwable) {
                        Log.d("network_requestOtherMinds", "????????????")
                    }
                }
            )
    }

    fun requestOtherQuestions() {
        exploreRepository.getExplorationOtherQuestions(
            _page,
            null,
            "??????"
        )
            .enqueue(
                object : Callback<ResponseExplorationQuestions> {
                    override fun onResponse(
                        call: Call<ResponseExplorationQuestions>,
                        response: Response<ResponseExplorationQuestions>
                    ) {
                        Log.d("abc", "?????? ??????")
                        if (response.isSuccessful) {
                            tempOtherQuestionsList =
                                response.body()!!.data?.answers?.toMutableList()
                            _otherQuestionsList.value = tempOtherQuestionsList?.toMutableList()

                            if (response.body()!!.data != null) {
                                if (response.body()!!.data?.pageLen > _page) {
                                    _page++
                                    _isMorePage.value = true
                                } else {
                                    _isMorePage.value = false
                                }
                            } else {
                                _isMorePage.value = false
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseExplorationQuestions>, t: Throwable) {
                        Log.d("network_requestOtherQuestions", "????????????")
                    }
                }
            )
    }

    fun requestOtherQuestionsWithCategorySorting(
        category: Int?,
        sorting: String,
        pageNum: Int = _page
    ) {
        exploreRepository.getExplorationOtherQuestions(
            pageNum,
            category,
            sorting
        )
            .enqueue(
                object : Callback<ResponseExplorationQuestions> {
                    override fun onResponse(
                        call: Call<ResponseExplorationQuestions>,
                        response: Response<ResponseExplorationQuestions>
                    ) {
                        if (response.isSuccessful) {
                            _page = pageNum
                            tempOtherQuestionsList =
                                response.body()!!.data?.answers?.toMutableList()
                            _otherQuestionsList.value = tempOtherQuestionsList?.toMutableList()
                            if (response.body()!!.data != null) {
                                if (response.body()!!.data?.pageLen > _page) {
                                    _page++
                                    _isMorePage.value = true
                                } else {
                                    _isMorePage.value = false
                                }
                            } else {
                                _isMorePage.value = false
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseExplorationQuestions>, t: Throwable) {
                        Log.d("network_requestOtherQuestionsWithCategorySorting", "????????????")
                    }
                }
            )
    }

    fun requestPlusOtherQuestions() {
        exploreRepository.getExplorationOtherQuestions(
            _page,
            _categoryNum,
            _sortingText
        )
            .enqueue(
                object : Callback<ResponseExplorationQuestions> {
                    override fun onResponse(
                        call: Call<ResponseExplorationQuestions>,
                        response: Response<ResponseExplorationQuestions>
                    ) {
                        if (response.isSuccessful) {
                            response.body()!!.data?.answers?.toMutableList()?.let {
                                tempOtherQuestionsList?.addAll(
                                    it
                                )
                            }
                            _otherQuestionsList.value = tempOtherQuestionsList?.toMutableList()

                            if (response.body()!!.data?.pageLen > _page) {
                                _page++
                                _isMorePage.value = true
                            } else {
                                _isMorePage.value = false
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseExplorationQuestions>, t: Throwable) {
                        Log.d("network_requestPlusOtherQuestions", "????????????")
                    }
                }
            )
    }

    fun requestSameQuestionsOtherAnswers(questionId: Int, sorting: String = "??????") {
        _page = 1
        otherAnswersQuestionsID = questionId
        exploreRepository.getExplorationSameQuestionOtherAnswers(
            otherAnswersQuestionsID,
            _page,
            sorting
        ).enqueue(
            object : Callback<ResponseExplorationQuestions> {
                override fun onResponse(
                    call: Call<ResponseExplorationQuestions>,
                    response: Response<ResponseExplorationQuestions>
                ) {
                    if (response.isSuccessful) {
                        tempSameQuestionOtherAnswersList =
                            response.body()!!.data?.answers?.toMutableList()
                        _sameQuestionOtherAnswersList.value =
                            tempSameQuestionOtherAnswersList?.toMutableList()
                        if (response.body()!!.data?.pageLen > _page) {
                            _page++
                            _isMorePage.value = true
                        } else {
                            _isMorePage.value = false
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseExplorationQuestions>, t: Throwable) {
                    Log.d("network_requestSameQuestionsOtherAnswers", "????????????")
                }
            }
        )
    }

    fun requestPlusSameQuestionOtherAnswers() {
        exploreRepository.getExplorationSameQuestionOtherAnswers(
            otherAnswersQuestionsID,
            _page,
            _sortingText
        ).enqueue(
            object : Callback<ResponseExplorationQuestions> {
                override fun onResponse(
                    call: Call<ResponseExplorationQuestions>,
                    response: Response<ResponseExplorationQuestions>
                ) {
                    if (response.isSuccessful) {
                        response.body()!!.data?.answers?.toMutableList()?.let {
                            tempSameQuestionOtherAnswersList?.addAll(
                                it
                            )
                        }
                        _sameQuestionOtherAnswersList.value =
                            tempSameQuestionOtherAnswersList?.toMutableList()

                        if (response.body()!!.data?.pageLen > _page) {
                            _page++
                            _isMorePage.value = true
                        } else {
                            _isMorePage.value = false
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseExplorationQuestions>, t: Throwable) {
                    Log.d("network_requestPlusSameQuestionsOtherAnswers", "????????????")
                }
            }
        )
    }

    fun requestQuestionForFirstAnswer() {
        exploreRepository.getQuestionForFirstAnswer().enqueue(
            object : Callback<ResponseExplorationQuestionForFirstAnswer> {
                override fun onResponse(
                    call: Call<ResponseExplorationQuestionForFirstAnswer>,
                    response: Response<ResponseExplorationQuestionForFirstAnswer>
                ) {
                    if (response.isSuccessful) {
                        _questionForFirstAnswer.value = response.body()!!.data
                    }
                }

                override fun onFailure(
                    call: Call<ResponseExplorationQuestionForFirstAnswer>,
                    t: Throwable
                ) {
                    Log.d("network_requestQuestionForFirstAnswer", "????????????")
                }
            }
        )
    }

    fun requestScrap(answerId: Int, answerData: ResponseExplorationQuestions.Data.Answer) {
        exploreRepository.putScrap(
            answerId
        ).enqueue(
            object : Callback<ResponseExplorationScrap> {
                override fun onResponse(
                    call: Call<ResponseExplorationScrap>,
                    response: Response<ResponseExplorationScrap>
                ) {
                    if (response.isSuccessful) {
                        Log.d("scrap_viewmodel", answerId.toString())
                        _scrapData = response.body()!!
                        Log.d("scrap_1", scrapData.message)
                    }
                }

                override fun onFailure(
                    call: Call<ResponseExplorationScrap>,
                    t: Throwable
                ) {
                    Log.d("network_requestQuestionForFirstAnswer", "????????????")
                }
            }
        )
    }
}