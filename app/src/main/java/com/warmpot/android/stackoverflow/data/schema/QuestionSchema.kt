package com.warmpot.android.stackoverflow.data.schema


import com.google.gson.annotations.SerializedName

data class QuestionSchema(
    @SerializedName(" question_id")
    val questionId: Int = 0,

    @SerializedName("title")
    val title: String = "",

    @SerializedName("creation_date")
    val creationDate: Long = 0L,

    @SerializedName("last_activity_date")
    val lastActivityDate: Long = 0L,

    @SerializedName("last_edit_date")
    val lastEditDate: Long = 0L,

    @SerializedName("link")
    val link: String = "",

    @SerializedName("owner")
    val owner: OwnerSchema? = null,

    @SerializedName("answer_count")
    val answerCount: Int = 0,

    @SerializedName("score")
    val score: Int = 0,

    @SerializedName("up_vote_count")
    val upvoteCount: Int = 0,

    @SerializedName("view_count")
    val viewCount: Int = 0
)
