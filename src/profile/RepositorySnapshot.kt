package com.rafambn.profilebanner.profile

data class RepositorySnapshot(
    val name: String,
    val description: String,
    val stars: Long,
    val language: String?
)

fun fallbackRepository(name: String): RepositorySnapshot =
    RepositorySnapshot(
        name = name,
        description = "Pinned repository",
        stars = 0,
        language = null
    )
