package com.gns.billing.session

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SessionEvents {
    private val _timeoutFlow = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val timeoutFlow = _timeoutFlow.asSharedFlow()

    fun emitTimeout() {
        _timeoutFlow.tryEmit(Unit)
    }
}
