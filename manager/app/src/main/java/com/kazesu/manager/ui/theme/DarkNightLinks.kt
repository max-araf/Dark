package com.kazesu.manager.ui.theme

/**
 * Central place for DarkNightSu user-facing links so upstream personal links
 * (Patreon / OpenCollective / KazeSU Telegram / upstream contributors) never leak into the UI.
 */
object DarkNightLinks {
    const val REPO_URL = "https://github.com/max-araf/DarkNightSu"
    const val KERNEL_INTEGRATION_GUIDE_URL = "https://github.com/max-araf/DarkNightSu/blob/main/DARKNIGHT_SETUP.md"

    // Set these flags to true and fill your own URLs whenever ready.
    const val SHOW_SUPPORT_CARD = false
    const val SUPPORT_URL = ""

    const val SHOW_TELEGRAM_LINK = false
    const val TELEGRAM_URL = ""

    const val SHOW_UPSTREAM_CONTRIBUTORS = false
}
