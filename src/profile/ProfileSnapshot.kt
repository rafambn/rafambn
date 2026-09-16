package com.rafambn.profilebanner.profile

import com.rafambn.profilebanner.pinnedRepos

data class ProfileSnapshot(
    val repositories: List<RepositorySnapshot>
)

fun fallbackProfile(): ProfileSnapshot =
    ProfileSnapshot(pinnedRepos.map(::fallbackRepository))
