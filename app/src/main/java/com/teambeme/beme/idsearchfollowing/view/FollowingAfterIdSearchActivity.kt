package com.teambeme.beme.idsearchfollowing.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.teambeme.beme.R
import com.teambeme.beme.base.BindingActivity
import com.teambeme.beme.databinding.ActivityFollowingAfterIdSearchBinding
import com.teambeme.beme.idsearchfollowing.adapter.IdSearchAdapter
import com.teambeme.beme.idsearchfollowing.adapter.RecentSearchAdapter
import com.teambeme.beme.idsearchfollowing.viewmodel.IdSearchViewModel
import com.teambeme.beme.util.StatusBarUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowingAfterIdSearchActivity :
    BindingActivity<ActivityFollowingAfterIdSearchBinding>(R.layout.activity_following_after_id_search) {
    private val idSearchViewModel: IdSearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setStatusBar(this, Color.WHITE)
        LifeCycleEventLogger(javaClass.name).registerLogger(lifecycle)
        binding.idSearchViewModel = idSearchViewModel
        binding.lifecycleOwner = this
        setRecentSearchAdapter(binding)
        idSearchViewModel.requestRecentSearchData()

        val idSearchAdapter = IdSearchAdapter(idSearchViewModel)
        binding.rcvFollowingAfterIdsearch.adapter = idSearchAdapter

        setIdSearchListObserve()
        setQueryTextListener()
        backBtnWorking()
    }

    private fun setIdSearchListObserve() {
        idSearchViewModel.idSearchData.observe(this) { idSearchData ->
            idSearchData?.let {
                if (binding.rcvFollowingAfterIdsearch.adapter != null) with(binding.rcvFollowingAfterIdsearch.adapter as IdSearchAdapter) {
                    submitList(idSearchData)
                }
                if (idSearchData[0].isFollowed == null) {
                    binding.noticeWhenNoSearchData.visibility = View.VISIBLE
                    binding.constraintViewFollowingAfterIdsearch.visibility = View.INVISIBLE
                } else {
                    binding.noticeWhenNoSearchData.visibility = View.INVISIBLE
                    binding.constraintViewFollowingAfterIdsearch.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setRecentSearchAdapter(binding: ActivityFollowingAfterIdSearchBinding) {
        val recentSearchAdapter = RecentSearchAdapter(idSearchViewModel)
        binding.rcvRecentSearch.apply {
            adapter = recentSearchAdapter
            layoutManager = LinearLayoutManager(this@FollowingAfterIdSearchActivity)
        }
        idSearchViewModel.recentSearchData.observe(this) { list ->
            recentSearchAdapter.replaceRecentSearchList(list)
        }
        idSearchViewModel.deletePosition.observe(this) {
            deleteListener()
        }
    }

    private fun deleteListener() = idSearchViewModel.deleteRecentSearch()

    private fun backBtnWorking() {
        binding.btnBackFollowingIdsearch.setOnClickListener { onBackPressed() }
    }

    private fun setQueryTextListener() {
        binding.searchViewFollowingIdsearch.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                val queryText = newText ?: ""
                if (queryText.count() > 0) {
                    binding.viewRecentSearch.visibility = View.GONE
                    binding.constraintViewFollowingAfterIdsearch.visibility = View.VISIBLE
                } else {
                    idSearchViewModel.deleteSearchRecord()
                    binding.viewRecentSearch.visibility = View.VISIBLE
                    binding.constraintViewFollowingAfterIdsearch.visibility = View.GONE
                    binding.noticeWhenNoSearchData.visibility = View.GONE
                }
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    idSearchViewModel.setSearchQuery(query)
                    idSearchViewModel.requestIdSearchData()
                    Log.d("search_semin", "${idSearchViewModel.idSearchData.value}")
                }
                return false
            }
        })
    }
}