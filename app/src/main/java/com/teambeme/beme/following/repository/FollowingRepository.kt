package com.teambeme.beme.following.repository

import com.teambeme.beme.explore.model.ResponseExplorationQuestions
import com.teambeme.beme.following.model.ResponseFollowingList
import com.teambeme.beme.following.model.ResponseFollowingSearchId
import retrofit2.Call

interface FollowingRepository {
    fun getFollowingAnswers(
        page: Int
    ): Call<ResponseExplorationQuestions>

    fun getFollowingFollowerList(): Call<ResponseFollowingList>

    fun getSearchMyFollowingFollower(
        query: String,
        range: String
    ): Call<ResponseFollowingSearchId>
}