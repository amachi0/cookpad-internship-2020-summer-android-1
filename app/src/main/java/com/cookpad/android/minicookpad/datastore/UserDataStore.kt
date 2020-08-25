package com.cookpad.android.minicookpad.datastore

import com.cookpad.android.minicookpad.datasource.UserEntity

interface UserDataStore {
    fun currentUser(): UserEntity? // 現在のユーザ状態(ログインしていなければ null)

    fun fetch(name: String, onSuccess: (UserEntity?) -> Unit, onFailed: (Throwable) -> Unit)

    fun register(user: UserEntity, onSuccess: () -> Unit, onFailed: (Throwable) -> Unit)
}