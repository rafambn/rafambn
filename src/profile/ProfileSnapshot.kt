package com.rafambn.profilebanner.profile

import com.rafambn.profilebanner.pinnedRepos

data class ProfileSnapshot(
    val repositories: List<RepositorySnapshot>,
    val name: String = "rafambn",
    val bio: String = "Originally an Android developer, now a solutions architect. " +
        "I enjoy solving problems, making complex things simple and learning new things.",
    val updatedAt: java.time.Instant? = null
)

fun fallbackProfile(): ProfileSnapshot =
    ProfileSnapshot(pinnedRepos.map(::fallbackRepository))
