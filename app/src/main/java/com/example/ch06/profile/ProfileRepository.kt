package com.example.ch06.profile

data class UserProfile(
    val username: String,
    val notificationsEnabled: Boolean
)

// Sumber data profil; sinkron karena datanya lokal (tanpa suspend)
interface ProfileRepository {
    fun getProfile(): UserProfile
}

class FakeProfileRepository : ProfileRepository {
    private val profile = UserProfile(
        username = "Christopher G",
        notificationsEnabled = true
    )

    override fun getProfile(): UserProfile = profile
}
