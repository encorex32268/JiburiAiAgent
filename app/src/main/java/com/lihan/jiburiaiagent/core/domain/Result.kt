package com.lihan.jiburiaiagent.core.domain

interface RootError

sealed interface Result<out D, out E: RootError> {
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Error<out E: RootError>(val error: E) : Result<Nothing, E>
}

inline fun <T, E: RootError, R> Result<T, E>.map(transform: (T) -> R): Result<R, E> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> Result.Error(error)
    }
}

inline fun <T, E: RootError> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    if (this is Result.Success) {
        action(data)
    }
    return this
}

inline fun <T, E: RootError> Result<T, E>.onFailure(action: (E) -> Unit): Result<T, E> {
    if (this is Result.Error) {
        action(error)
    }
    return this
}

sealed interface DataError : RootError {
    enum class Network : DataError {
        NO_INTERNET,      // 無網路連線
        REQUEST_TIMEOUT,  // 請求逾時
        UNAUTHORIZED,     // 401 未授權
        FORBIDDEN,        // 403 拒絕存取
        NOT_FOUND,        // 404 找不到資源
        SERVER_ERROR,     // 5xx 伺服器異常
        SERIALIZATION,    // JSON 解析失敗
        UNKNOWN           // 未知網路異常
    }

    enum class Local : DataError {
        DISK_FULL,        // 磁碟空間不足
        COLUMN_NOT_FOUND, // 資料庫欄位不存在
        UNKNOWN           // 未知本地儲存異常
    }
}
