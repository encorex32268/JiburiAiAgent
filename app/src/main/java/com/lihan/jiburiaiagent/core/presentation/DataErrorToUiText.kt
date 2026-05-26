package com.lihan.jiburiaiagent.core.presentation

import com.lihan.jiburiaiagent.R
import com.lihan.jiburiaiagent.core.domain.DataError

fun DataError.Network.toUiText(): UiText {
    return when (this) {
        DataError.Network.NO_INTERNET -> UiText.StringResource(R.string.error_no_internet)
        DataError.Network.REQUEST_TIMEOUT -> UiText.StringResource(R.string.error_timeout)
        DataError.Network.UNAUTHORIZED -> UiText.StringResource(R.string.error_unauthorized)
        DataError.Network.FORBIDDEN -> UiText.StringResource(R.string.error_forbidden)
        DataError.Network.NOT_FOUND -> UiText.StringResource(R.string.error_not_found)
        DataError.Network.SERVER_ERROR -> UiText.StringResource(R.string.error_server)
        DataError.Network.SERIALIZATION -> UiText.StringResource(R.string.error_serialization)
        DataError.Network.UNKNOWN -> UiText.StringResource(R.string.error_unknown)
    }
}

fun DataError.Local.toUiText(): UiText {
    return when (this) {
        DataError.Local.DISK_FULL -> UiText.StringResource(R.string.error_disk_full)
        DataError.Local.COLUMN_NOT_FOUND -> UiText.StringResource(R.string.error_database_unknown)
        DataError.Local.UNKNOWN -> UiText.StringResource(R.string.error_database_unknown)
    }
}

fun DataError.toUiText(): UiText {
    return when (this) {
        is DataError.Network -> this.toUiText()
        is DataError.Local -> this.toUiText()
    }
}
