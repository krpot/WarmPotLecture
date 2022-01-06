package com.warmpot.android.stackoverflow.screen.question.list.viewmodel

import com.warmpot.android.stackoverflow.common.EventValue
import com.warmpot.android.stackoverflow.screen.common.adapter.LoadingState
import com.warmpot.android.stackoverflow.screen.question.model.Question
import com.warmpot.android.stackoverflow.domain.model.TagEntity

data class QuestionListUiState(
    val screenLoading: EventValue<Boolean> = EventValue(false),
    val loading: EventValue<List<LoadingState>> = EventValue(emptyList()),
    val error: EventValue<Throwable>? = null,
    val questions: List<Question>? = null,
    val tags: TagUiState? = null
)

data class TagUiState(
    val query: String,
    val tags: List<TagEntity>
)
