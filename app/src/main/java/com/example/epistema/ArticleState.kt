package com.example.epistema

object ArticleState {
    private var currentPageId: Int = -1
    private var currentArticleId: Int = -1

    fun setCurrentPage(pageId: Int) {
        currentPageId = pageId
        currentArticleId = -1
    }

    fun setCurrentArticle(articleId: Int) {
        currentArticleId = articleId
        currentPageId = -1
    }

    fun getCurrentPageId() = currentPageId
    fun getCurrentArticleId() = currentArticleId
}