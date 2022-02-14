package com.example.ryangraphsudoku.ui.activegame

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.ryangraphsudoku.R
import com.example.ryangraphsudoku.common.makeToast
import com.example.ryangraphsudoku.ui.GraphSudokuTheme
import com.example.ryangraphsudoku.ui.activegame.buildlogic.buildActiveGameLogic

class ActiveGameActivity : AppCompatActivity(), ActiveGameContainer {

    private lateinit var logic: ActiveGameLogic


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ActiveGameViewModel()

        setContent {
            GraphSudokuTheme {
                ActiveGameScreen(
                    onEventHandler = logic::onEvent,
                    vaiewModel = viewModel
                )
            }
        }

        logic = buildActiveGameLogic(this, viewModel, applicationContext)
    }

    override fun onStart() {
        super.onStart()
        logic.onEvent(ActiveGameEvent.OnStart)
    }

    override fun onStop() {
        super.onStop()
        logic.onEvent(ActiveGameEvent.OnStop)
    }

    override fun showError() = makeToast(getString(R.string.generic_error))

    override fun onNewGameClick() {
        startActivity(
            Intent(this, NewGameActivity::class.java)
        )
    }
}