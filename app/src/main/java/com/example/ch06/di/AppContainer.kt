package com.example.ch06.di

import com.example.ch06.data.ArticleRepository
import com.example.ch06.data.FakeArticleRepository

// Manual DI: satu tempat yang tahu cara merakit dependency
interface AppContainer {
    val articleRepository: ArticleRepository
    // TODO [T4.6] Tambahkan `val profileRepository: ProfileRepository` di sini, lalu
    //   rakit implementasinya (FakeProfileRepository) di DefaultAppContainer.
}

class DefaultAppContainer : AppContainer {
    override val articleRepository: ArticleRepository by lazy {
        FakeArticleRepository()
    }
}
