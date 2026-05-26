package com.lihan.jiburiaiagent.detail.presentation

import com.lihan.jiburiaiagent.core.presentation.UiText

sealed interface MovieDetailEvent {
    data object NavigateBack : MovieDetailEvent
    data class ShowToast(val message: UiText) : MovieDetailEvent
}
