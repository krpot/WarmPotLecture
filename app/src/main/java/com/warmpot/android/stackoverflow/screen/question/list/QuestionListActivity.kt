package com.warmpot.android.stackoverflow.screen.question.list

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import com.warmpot.android.stackoverflow.databinding.ActivityQuestionListBinding
import com.warmpot.android.stackoverflow.screen.common.adapter.LoadingState
import com.warmpot.android.stackoverflow.screen.common.adapter.LoadingStateAdapter
import com.warmpot.android.stackoverflow.screen.common.constants.IntentConstant
import com.warmpot.android.stackoverflow.screen.common.listener.FastClickHandler
import com.warmpot.android.stackoverflow.screen.common.recyclerview.LoadMoreListener
import com.warmpot.android.stackoverflow.screen.common.recyclerview.RecyclerViewHelper
import com.warmpot.android.stackoverflow.screen.question.details.QuestionDetailsActivity
import com.warmpot.android.stackoverflow.screen.question.list.adapter.QuestionAdapter
import com.warmpot.android.stackoverflow.screen.question.list.adapter.QuestionViewHolder
import com.warmpot.android.stackoverflow.screen.question.list.viewmodel.QuestionListViewModel
import com.warmpot.android.stackoverflow.screen.question.list.viewmodel.QuestionListViewState
import com.warmpot.android.stackoverflow.screen.question.model.Question
import com.warmpot.android.stackoverflow.screen.user.UserActivity
import com.warmpot.android.stackoverflow.screen.user.model.User
import com.warmpot.android.stackoverflow.utils.RecyclerViewDivider
import com.warmpot.android.stackoverflow.utils.hide
import com.warmpot.android.stackoverflow.utils.viewModel

class QuestionListActivity : AppCompatActivity() {

    private val viewModel by viewModel<QuestionListViewModel>()

    private val binding by lazy { ActivityQuestionListBinding.inflate(layoutInflater) }

    private val loadingStateAdapter by lazy { LoadingStateAdapter() }
    private val questionAdapter by lazy { QuestionAdapter() }
    private val concatAdapter by lazy { ConcatAdapter(questionAdapter, loadingStateAdapter) }

    private val fastClickHandler by lazy { FastClickHandler() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupViews()
        setupViewModel()

        loadFirstPageQuestions()
    }

    private lateinit var loadMoreListener: LoadMoreListener
    private var lastItemClicked = 0L

    private fun setupViews() {
        binding.apply {
            RecyclerViewHelper(
                recyclerView = questionRcv,
                adapter = concatAdapter,
                divider = RecyclerViewDivider.Vertical,
                onLoadMore = ::triggerLoadMore
            ).also { helper ->
                loadMoreListener = requireNotNull(helper.loadMoreListener)
            }

            questionAdapter.listener = object : QuestionViewHolder.Listener {
                override fun onItemClicked(question: Question) {
                    questionClicked(question)
                }

                override fun onOwnerClicked(user: User) {
                    ownerClicked(user)
                }

                override fun onTagClicked(tag: String) {
                    tagClicked(tag)
                }
            }

            loadingStateAdapter.onRetryClicked {
                viewModel.loadMoreRetryClicked()
            }

            swipeRefresh.setOnRefreshListener {
                pullToRefresh()
            }
        }
    }

    private fun tagClicked(tag: String) {
        // TODO : Open tag activity
        fastClickHandler.performClick {

        }
    }

    private fun ownerClicked(user: User) {
        fastClickHandler.performClick {
            navigateToUser(user)
        }
    }

    private fun questionClicked(question: Question) {
        fastClickHandler.performClick {
            navigateToDetails(question)
        }
    }

    private fun navigateToDetails(question: Question) {
        val intent = Intent(this, QuestionDetailsActivity::class.java)
        intent.putExtra(IntentConstant.INTENT_PARAM_KEY, question)
        startActivity(intent)
    }

    private fun navigateToUser(user: User) {
        val intent = Intent(this, UserActivity::class.java)
        intent.putExtra(IntentConstant.INTENT_PARAM_KEY, user.userId)
        startActivity(intent)
    }

    private fun pullToRefresh() {
        viewModel.pullToRefresh()
    }

    private fun loadFirstPageQuestions() {
        viewModel.loadFirstPageQuestions()
    }

    private fun triggerLoadMore() {
        viewModel.triggerLoadMore()
    }

    private fun loadMoreDone() {
        binding.loadingBar.hide()
        binding.swipeRefresh.isRefreshing = false
        loadMoreListener.loadMoreDone()
    }

    private fun setupViewModel() {
        viewModel.observe(this) { viewState ->
            when (viewState) {
                is QuestionListViewState.Loading -> handleLoadMore(viewState.data)
                is QuestionListViewState.ListItem -> handleQuestionFetched(viewState.data)
            }
        }
    }

    private fun handleQuestionFetched(questions: List<Question>?) {
        questionAdapter.submitList(questions) {
            loadMoreDone()
        }
    }

    private fun handleLoadMore(loadingStates: List<LoadingState>?) {
        loadingStateAdapter.submitList(loadingStates)
    }
}
