package com.rafambn.profilebanner

import com.rafambn.profilebanner.counter.ViewStats

data class PortfolioStats(
    val profile: ViewStats,
    val repositories: List<RepositoryStats>
)
