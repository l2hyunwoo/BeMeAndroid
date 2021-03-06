package com.teambeme.beme.home.repository

import com.teambeme.beme.data.remote.datasource.HomeDataSource
import com.teambeme.beme.home.model.RequestModifyPublic
import com.teambeme.beme.home.model.ResponseAnswer
import com.teambeme.beme.home.model.ResponseAnswers
import com.teambeme.beme.home.model.ResponseModifyData

class HomeRepositoryImpl(private val homeDataSource: HomeDataSource) : HomeRepository {
    override suspend fun modifyPublic(answerId: Int, publicFlag: Int): ResponseModifyData {
        return homeDataSource.modifyPublic(RequestModifyPublic(answerId, publicFlag))
    }

    override suspend fun getAnswers(page: Int): ResponseAnswers = homeDataSource.getAnswers(page)

    override suspend fun getNewAnswer(): ResponseAnswer = homeDataSource.getNewAnswer()

    override suspend fun changeQuestion(answerId: Int): ResponseAnswer =
        homeDataSource.changeQuestion(answerId)

    override suspend fun deleteAnswer(answerId: Int): ResponseModifyData =
        homeDataSource.deleteAnswer(answerId)
}