package com.teambeme.beme.otherpage.repository

import com.teambeme.beme.data.remote.datasource.OtherPageDataSource
import com.teambeme.beme.otherpage.model.ResponseFollow
import com.teambeme.beme.otherpage.model.ResponseOtherInfo
import com.teambeme.beme.otherpage.model.ResponseScrap
import retrofit2.Call

class OtherPageRepositoryImpl(private val otherDataSource: OtherPageDataSource) :
    OtherPageRepository {
    override fun getProfileAnswer(userId: Int, page: Int) =
        otherDataSource.getProfileAnswer(userId, page)

    override fun getOtherInfo(userId: Int): Call<ResponseOtherInfo> =
        otherDataSource.getOtherInfo(userId)

    override fun putScrap(answerId: Int): Call<ResponseScrap> =
        otherDataSource.putScrap(answerId)

    override fun putFollow(userId: Int): Call<ResponseFollow> =
        otherDataSource.putFollow(userId)
}
