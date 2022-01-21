package com.example.ryangraphsudoku.ui.activegame

import com.example.ryangraphsudoku.common.BaseLogic
import com.example.ryangraphsudoku.common.DispatcherProvider
import com.example.ryangraphsudoku.domain.IGameRepository
import com.example.ryangraphsudoku.domain.IStatisticsRepository
import com.example.ryangraphsudoku.domain.SudokuPuzzle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ActiveGameLogic(
    private val container: ActiveGameContainer?,
    private val viewModel: ActiveGameViewModel,
    private val gameRepo: IGameRepository,
    private val statsRepo: IStatisticsRepository,
    private val dispatcher: DispatcherProvider,
) : BaseLogic<ActiveGameEvent>(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = dispatcher.provideUIContext() + jobTracker

    init {
        jobTracker = Job()
    }

    inline fun startCoroutineTimer(
        crossinline action: () -> Unit
    ) = launch {
        while (true) {
            action()
            delay(1000)
        }
    }

    private var timerTracker: Job? = null

    private val Long.toOffset: Long
        get() = if (this <= 0) 0 else this - 1


    override fun onEvent(event: ActiveGameEvent) {
        when (event) {
            is ActiveGameEvent.OnInput -> onInput(event.input, viewModel.timerState)
            ActiveGameEvent.OnNewGameClicked -> onNewGameClick()
            ActiveGameEvent.OnStart -> onStart()
            ActiveGameEvent.OnStop -> onStop()
            is ActiveGameEvent.OnTileFocused -> onTileFocus(event.x, event.y)
        }
    }

    private fun onTileFocus(x: Int, y: Int) {
        viewModel.updateFocusState(x, y)
    }

    private fun onStop() {
        if (!viewModel.isCompleteState) {
            launch {
                gameRepo.saveGame(viewModel.timerState.toOffset,
                    { cancelStuff() },
                    {
                        cancelStuff()
                        container?.showError()
                    }
                )
            }
        }
    }

    private fun onStart() = launch {
        gameRepo.getCurrentGame(
            { puzzle, isComplete ->
                viewModel.initializeBoardState(puzzle, isComplete)
                if (!isComplete) timerTracker = startCoroutineTimer {
                    viewModel.updateTimerState()
                }
            },
            {
                container?.onNewGameClick()
            }
        )
    }

    private fun onNewGameClick() = launch {
        viewModel.showLoadingState()
        if (viewModel.isCompleteState) {
            gameRepo.getCurrentGame(
                { puzzle, _ ->
                    updateWithTime(puzzle)
                },
                {
                    container?.showError()
                }
            )
        } else {
            navigateToNewGame()
        }
    }

    private fun updateWithTime(puzzle: SudokuPuzzle) = launch {
        gameRepo.updateGame(
            puzzle.copy(elapsedTime = viewModel.timerState.toOffset),
            { navigateToNewGame() },
            {
                container?.showError()
                navigateToNewGame()
            }
        )
    }

    private fun navigateToNewGame() {
        cancelStuff()
        container?.onNewGameClick()
    }

    private fun cancelStuff() {
        if (timerTracker?.isCancelled == false) timerTracker?.cancel()
        jobTracker.cancel()
    }


    private fun onInput(input: Int, elapsedTime: Long) = launch {
        viewModel.boardState.values
            .firstOrNull { it.hasFocus }
            ?.let {
                gameRepo.updateNode(
                    it.x,
                    it.y,
                    input,
                    elapsedTime,
                    { isCompleted ->
                        it.let {
                            viewModel.updateBoardState(it.x, it.y, input, false)
                            if (isCompleted) {
                                timerTracker?.cancel()
                                checkIfNewRecord()
                            }

                        }
                    },
                    { container?.showError() }
                )
            }
    }

    private fun checkIfNewRecord() = launch {
        statsRepo.updateStatistic(
            viewModel.timerState,
            viewModel.difficulty,
            viewModel.boundary,
            { isRecord ->
                viewModel.isNewRecordState = isRecord
                viewModel.updateCompletedState()
            },
            {
                container?.showError()
                viewModel.updateCompletedState()
            }
        )
    }
}