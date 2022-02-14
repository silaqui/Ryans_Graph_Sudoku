package com.example.ryangraphsudoku.ui.activegame.buildlogic

import android.content.Context
import com.example.ryangraphsudoku.common.ProductionDispatcherProvider
import com.example.ryangraphsudoku.persistence.*
import com.example.ryangraphsudoku.ui.activegame.ActiveGameContainer
import com.example.ryangraphsudoku.ui.activegame.ActiveGameLogic
import com.example.ryangraphsudoku.ui.activegame.ActiveGameViewModel

internal fun buildActiveGameLogic(
    container: ActiveGameContainer,
    viewModel: ActiveGameViewModel,
    context: Context
): ActiveGameLogic = ActiveGameLogic(
    container, viewModel,
    GameRepositoryImpl(
        LocalGameStorageImpl(context.filesDir.path),
        LocalSettingsStorageImpl(context.settingsDataStore)
    ),
    LocalStatisticsStorageImpl(context.statsDataStore),
    ProductionDispatcherProvider
)