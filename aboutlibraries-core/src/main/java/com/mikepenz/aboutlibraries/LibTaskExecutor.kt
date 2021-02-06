package com.mikepenz.aboutlibraries

import java.io.Serializable

/**
 * This Class was created by Patrick J
 * on 14.12.15. For more Details and Licensing
 * have a look at the README.md
 */
enum class LibTaskExecutor : Serializable {
    DEFAULT_EXECUTOR,
    THREAD_POOL_EXECUTOR,
    SERIAL_EXECUTOR
}
