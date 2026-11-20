package com.kazesu.manager.ui.theme

/**
 * Central place for DarkNightSu user-facing links so upstream personal links
 * (Patreon / OpenCollective / KazeSU repo) never leak into the UI.
 */
object DarkNightLinks {
    const val REPO_URL = "https://github.com/max-araf/DarkNightSu"
    const val KERNEL_INTEGRATION_GUIDE_URL = "https://github.com/max-araf/DarkNightSu/blob/main/DARKNIGHT_SETUP.md"

    // Set SHOW_SUPPORT_CARD = true and fill SUPPORT_URL when you want to show your own donation/support link.
    const val SHOW_SUPPORT_CARD = false
    const val SUPPORT_URL = ""
}
