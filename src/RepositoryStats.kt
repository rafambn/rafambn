package com.rafambn.profilebanner

import com.rafambn.profilebanner.counter.ViewStats

data class RepositoryStats(
    val name: String,
    val stars: Long?,
    val views: ViewStats
)
