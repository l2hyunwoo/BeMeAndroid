package com.teambeme.beme.idsearchfollowing.repository

import com.teambeme.beme.data.remote.datasource.IdSearchDataSource
import com.teambeme.beme.idsearchfollowing.model.RequestFollowAndFollowing

class IdSearchRepositoryImpl(private val idSearchDataSource: IdSearchDataSource) :
    IdSearchRepository {
    override fun idSearch(query: String, range: String) = idSearchDataSource.idSearch(query, range)

    override fun getRecentSearchRecord() = idSearchDataSource.getRecentSearchRecord()

    override fun deleteRecentSearchRecord(searchedId: Int) =
        idSearchDataSource.deleteRecentSearchRecord(searchedId)

    override fun putFollowAndFollowing(body: RequestFollowAndFollowing) =
        idSearchDataSource.putFollowAndFollowing(body)
}