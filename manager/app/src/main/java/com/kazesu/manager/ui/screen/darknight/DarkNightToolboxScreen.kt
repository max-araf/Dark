package com.kazesu.manager.ui.screen.darknight

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kazesu.manager.ui.navigation3.LocalNavigator
import com.kazesu.manager.ui.navigation3.Route
import com.kazesu.manager.ui.theme.DarkNightAuraBackdrop
import com.kazesu.manager.ui.theme.DarkNightGlassCard
import com.kazesu.manager.ui.theme.DarkNightGradientButton
import com.kazesu.manager.ui.theme.DarkNightThemeStore
import com.kazesu.manager.ui.theme.DarkNightToastManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DarkNightToolboxScreen() {
    val navigator = LocalNavigator.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    DarkNightThemeStore.init(context)
    val themeState by DarkNightThemeStore.state.collectAsState()
    val palette = themeState.palette

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Performance", "Charging", "Integrity", "Monitor", "Rescue")

    var activeMode by remember { mutableStateOf(PerformanceMode.BALANCED) }
    var appRulesText by remember { mutableStateOf("com.dts.freefireth=gaming\ncom.dts.freefiremax=gaming") }
    var profileDaemonEnabled by remember { mutableStateOf(false) }

    var battery by remember { mutableStateOf(BatteryTelemetry()) }
    var integrity by remember { mutableStateOf(IntegrityStatus()) }
    var trickyTargets by remember { mutableStateOf("") }
    var telemetry by remember { mutableStateOf(LiveTelemetry()) }
    var bootloopGuard by remember { mutableStateOf(false) }
    var partitions by remember { mutableStateOf<List<PartitionEntry>>(emptyList()) }
    var busy by remember { mutableStateOf(false) }

    fun toast(msg: String) {
        DarkNightToastManager.show(msg)
    }

    suspend fun refreshAll() {
        activeMode = DarkNightToolboxRepository.getActivePerformanceMode()
        val existingRules = DarkNightToolboxRepository.readAppProfileRules()
        if (existingRules.isNotEmpty()) appRulesText = existingRules
        battery = DarkNightToolboxRepository.readBatteryTelemetry()
        integrity = DarkNightToolboxRepository.readIntegrityStatus()
        trickyTargets = DarkNightToolboxRepository.readTrickyTargets()
        telemetry = DarkNightToolboxRepository.readLiveTelemetry()
        bootloopGuard = DarkNightToolboxRepository.isBootloopGuardEnabled()
        partitions = DarkNightToolboxRepository.detectPartitions()
    }

    LaunchedEffect(Unit) { refreshAll() }

    LaunchedEffect(selectedTab) {
        while (selectedTab == 3) {
            telemetry = DarkNightToolboxRepository.readLiveTelemetry()
            delay(2500)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        DarkNightAuraBackdrop(
            palette = palette,
            animateOnStart = themeState.startupAnimEnabled,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top bar matching Zentra clean header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { navigator.pop() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("DarkNight Toolbox", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 19.sp)
                    Text("Kernel Control & Automation", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
                IconButton(onClick = { navigator.push(Route.DarkNightThemeStudio) }) {
                    Icon(Icons.Filled.Star, contentDescription = "Themes", tint = palette.secondaryGlow)
                }
                IconButton(onClick = { scope.launch { refreshAll(); toast("Refreshed") } }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh", tint = Color.White)
                }
            }

            // Pill-shaped segmented tab bar with auto-scroll & zero edge clipping
            val tabScrollState = rememberScrollState()
            LaunchedEffect(selectedTab) {
                val target = if (tabs.size <= 1) 0 else (tabScrollState.maxValue * selectedTab) / (tabs.size - 1)
                tabScrollState.animateScrollTo(target)
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(999.dp),
                color = Color(0xCC0D1017),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)),
            ) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(tabScrollState)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    tabs.forEachIndexed { index, title ->
                        val active = selectedTab == index
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .then(
                                    if (active) {
                                        Modifier.background(
                                            Brush.horizontalGradient(listOf(palette.buttonStart, palette.buttonEnd))
                                        )
                                    } else {
                                        Modifier
                                    }
                                )
                                .clickable { selectedTab = index }
                                .padding(horizontal = 16.dp, vertical = 9.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = title,
                                color = if (active) Color.White else Color.White.copy(alpha = 0.72f),
                                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 13.sp,
                                maxLines = 1,
                                softWrap = false,
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        DarkNightGlassCard(palette = palette) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Rounded.Memory, contentDescription = null, tint = palette.secondaryGlow)
                                Text("Global Kernel Profile", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            }
                            Text(
                                "Switch CPU/GPU governors & ZRAM swappiness instantly.",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                            )
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                PerformanceMode.entries.forEach { mode ->
                                    FilterChip(
                                        selected = activeMode == mode,
                                        onClick = {
                                            scope.launch {
                                                val (ok, msg) = DarkNightToolboxRepository.applyPerformanceMode(mode)
                                                if (ok) activeMode = mode
                                                toast(msg)
                                            }
                                        },
                                        label = { Text(mode.label) }
                                    )
                                }
                            }
                        }

                        DarkNightGlassCard(palette = palette) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Per-App Auto Profile Daemon", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                    Text("Format: package.name=gaming|balanced|battery", color = Color.White.copy(alpha = 0.68f), fontSize = 12.sp)
                                }
                                Switch(checked = profileDaemonEnabled, onCheckedChange = { profileDaemonEnabled = it })
                            }
                            OutlinedTextField(
                                value = appRulesText,
                                onValueChange = { appRulesText = it },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                maxLines = 6,
                                label = { Text("App Rules") }
                            )
                            DarkNightGradientButton(
                                text = "Save Rules & Update Daemon",
                                palette = palette,
                                onClick = {
                                    scope.launch {
                                        val (_, msg) = DarkNightToolboxRepository.saveAppProfileRules(appRulesText, profileDaemonEnabled)
                                        toast(msg)
                                    }
                                },
                            )
                        }
                    }

                    1 -> {
                        DarkNightGlassCard(palette = palette) {
                            Text("Battery Telemetry", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            Text("Level: ${battery.level}%  •  Status: ${battery.status}", color = Color.White)
                            Text("Temp: ${"%.1f".format(battery.tempC)}°C  •  Current: ${battery.currentMa} mA  •  Voltage: ${battery.voltageMv} mV", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                            Text(
                                if (battery.bypassSupported) "Control node: ${battery.bypassNode}" else "No kernel charging control node found on this device",
                                color = Color.White.copy(alpha = 0.65f),
                                fontSize = 12.sp,
                            )
                        }

                        DarkNightGlassCard(palette = palette) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Bypass Charging (Gaming Mode)", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                    Text("Pause battery charging while plugged in to keep thermals low.", color = Color.White.copy(alpha = 0.68f), fontSize = 12.sp)
                                }
                                Switch(
                                    checked = battery.bypassActive,
                                    enabled = battery.bypassSupported,
                                    onCheckedChange = { enable ->
                                        scope.launch {
                                            val (ok, msg) = DarkNightToolboxRepository.setBypassCharging(battery.bypassNode, enable)
                                            if (ok) battery = DarkNightToolboxRepository.readBatteryTelemetry()
                                            toast(msg)
                                        }
                                    }
                                )
                            }

                            Text("Smart Charge Limit", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(0 to "Off", 75 to "75%", 80 to "80%", 85 to "85%", 90 to "90%").forEach { (pct, label) ->
                                    FilterChip(
                                        selected = battery.chargeLimit == pct,
                                        enabled = battery.bypassSupported,
                                        onClick = {
                                            scope.launch {
                                                val (ok, msg) = DarkNightToolboxRepository.setChargeLimit(battery.bypassNode, pct)
                                                if (ok) battery = battery.copy(chargeLimit = pct)
                                                toast(msg)
                                            }
                                        },
                                        label = { Text(label) }
                                    )
                                }
                            }
                        }
                    }

                    2 -> {
                        DarkNightGlassCard(palette = palette) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Rounded.Security, contentDescription = null, tint = palette.secondaryGlow)
                                Text("Integrity & Stealth Status", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            }
                            Text("SELinux: ${integrity.selinuxMode}", color = Color.White)
                            Text("Verified Boot State: ${integrity.verifiedBootState}", color = Color.White.copy(alpha = 0.85f))
                            Text("SuSFS Detected: ${if (integrity.susfsDetected) "Active" else "Not found"}", color = Color.White.copy(alpha = 0.85f))
                            Text("TrickyStore: ${if (integrity.trickyStoreInstalled) "Installed" else "Not installed"}  •  Keybox: ${if (integrity.keyboxPresent) "Present" else "Missing"}", color = Color.White.copy(alpha = 0.85f))
                            Text("PIF Config: ${integrity.pifPath.ifEmpty { "Not found" }}", color = Color.White.copy(alpha = 0.65f), fontSize = 12.sp)
                        }

                        DarkNightGlassCard(palette = palette) {
                            Text("TrickyStore target.txt Editor", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            OutlinedTextField(
                                value = trickyTargets,
                                onValueChange = { trickyTargets = it },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4,
                                maxLines = 8,
                                label = { Text("/data/adb/tricky_store/target.txt") }
                            )
                            DarkNightGradientButton(
                                text = "Save target.txt",
                                palette = palette,
                                onClick = {
                                    scope.launch {
                                        val (_, msg) = DarkNightToolboxRepository.saveTrickyTargets(trickyTargets)
                                        toast(msg)
                                    }
                                },
                            )
                        }
                    }

                    3 -> {
                        DarkNightGlassCard(palette = palette) {
                            Text("Live Kernel Telemetry", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            Text("Kernel: ${telemetry.kernelRelease}", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                            Text("Uptime: ${telemetry.uptime}", color = Color.White)
                            Text("RAM: ${telemetry.memUsedMb} MB / ${telemetry.memTotalMb} MB  •  Peak Temp: ${"%.1f".format(telemetry.maxTempC)}°C", color = Color.White.copy(alpha = 0.9f))
                            Spacer(modifier = Modifier.height(2.dp))
                            telemetry.clusters.forEach { c ->
                                Text("${c.name}: ${c.curFreqMhz} MHz (${c.governor})", color = palette.secondaryGlow, fontSize = 12.sp)
                            }
                        }

                        DarkNightGlassCard(palette = palette) {
                            Text("1-Click Kernel & System Log Dumper", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            Text(
                                "Bundles dmesg, last_kmsg / console-ramoops, logcat, and getprop into /sdcard/Download/DarkNightSu/.",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                            )
                            DarkNightGradientButton(
                                text = if (busy) "Exporting Logs..." else "Export Full Log Archive (.tar.gz)",
                                palette = palette,
                                enabled = !busy,
                                onClick = {
                                    scope.launch {
                                        busy = true
                                        val (_, msg) = DarkNightToolboxRepository.dumpKernelLogs()
                                        busy = false
                                        toast(msg)
                                    }
                                },
                            )
                        }
                    }

                    4 -> {
                        DarkNightGlassCard(palette = palette) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("3-Strike Bootloop Rescue Guard", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                    Text("Auto-disables all modules if 3 consecutive boots fail.", color = Color.White.copy(alpha = 0.68f), fontSize = 12.sp)
                                }
                                Switch(
                                    checked = bootloopGuard,
                                    onCheckedChange = { enable ->
                                        scope.launch {
                                            val (ok, msg) = DarkNightToolboxRepository.setBootloopGuard(enable)
                                            if (ok) bootloopGuard = enable
                                            toast(msg)
                                        }
                                    }
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(
                                    onClick = {
                                        scope.launch {
                                            val (_, msg) = DarkNightToolboxRepository.createModuleSnapshot()
                                            toast(msg)
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    border = BorderStroke(1.dp, palette.primaryGlow),
                                ) {
                                    Icon(Icons.Rounded.Backup, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.padding(horizontal = 3.dp))
                                    Text("Snapshot", color = Color.White)
                                }
                                OutlinedButton(
                                    onClick = {
                                        scope.launch {
                                            val (_, msg) = DarkNightToolboxRepository.restoreModuleSnapshot()
                                            toast(msg)
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                                ) {
                                    Icon(Icons.Rounded.Restore, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.padding(horizontal = 3.dp))
                                    Text("Restore", color = Color.White)
                                }
                            }
                        }

                        DarkNightGlassCard(palette = palette) {
                            Text("Partition Backup (Boot / DTBO / VBMeta)", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            Text(
                                "Dumps raw partition images + SHA-256 to /sdcard/Download/DarkNightSu/backups/.",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                            )
                            if (partitions.isEmpty()) {
                                Text("No standard boot partitions detected (root required).", color = Color.White.copy(alpha = 0.65f), fontSize = 12.sp)
                            } else {
                                partitions.forEach { entry ->
                                    OutlinedButton(
                                        enabled = !busy,
                                        onClick = {
                                            scope.launch {
                                                busy = true
                                                val (_, msg) = DarkNightToolboxRepository.backupPartition(entry)
                                                busy = false
                                                toast(msg)
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)),
                                    ) {
                                        Text("Backup ${entry.name}", color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
