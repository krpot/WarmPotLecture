package com.warmpot.android.stackoverflow.domain.usecase

import com.warmpot.android.stackoverflow.common.OneOf
import com.warmpot.android.stackoverflow.common.tryOneOf
import com.warmpot.android.stackoverflow.data.schema.QuestionSchema
import com.warmpot.android.stackoverflow.data.schema.QuestionsResponse
import com.warmpot.android.stackoverflow.network.StackoverflowApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class GetQuestionsUseCase {

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor())
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.stackexchange.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val stackOverflowApi: StackoverflowApi by lazy { retrofit.create() }

    private val cache = arrayListOf<QuestionSchema>()
    private var page: Int = 1
    private var hasMoreData = false

    private suspend fun fetchQuestions(page: Int): QuestionFetchResult =
        withContext(Dispatchers.IO) {
            getQuestionsBy(page)
        }

    suspend fun loadFirstPage() = fetchQuestions(1)

    suspend fun loadNext(): QuestionFetchResult {
        return fetchQuestions(nextPage())
    }

    suspend fun refresh(): QuestionFetchResult {
        cache.clear()
        return fetchQuestions(page = this.page)
    }

    suspend fun retry() = fetchQuestions(page = this.page)

    private suspend fun getQuestionsBy(page: Int): QuestionFetchResult {
        return when (val result = tryOneOf { stackOverflowApi.getQuestions(page) }) {
            is OneOf.Error -> QuestionFetchResult.Failure(result.e)
            is OneOf.Success -> {
                handleSuccess(page, result.data)
            }
        }
    }

    private fun handleSuccess(pageNo: Int, response: QuestionsResponse): QuestionFetchResult {
        this.page = pageNo
        this.hasMoreData = response.hasMore

        if (response.items.isEmpty()) {
            hasMoreData = false
            return if (cache.isEmpty())
                QuestionFetchResult.Empty(cache)
            else
                QuestionFetchResult.EndOfData(cache)
        }

        cache.addAll(response.items)
        return QuestionFetchResult.HasData(cache)
    }

    private fun nextPage() = page.inc()
}
