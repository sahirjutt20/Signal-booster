package com.example.ui

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SignalLog
import com.example.viewmodel.OptimizationState
import com.example.viewmodel.SignalViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BoosterApp(viewModel: SignalViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    
    // Core parameters from state flow
    val operatorName by viewModel.operatorName.collectAsStateWithLifecycle()
    val isSimulationMode by viewModel.isSimulationMode.collectAsStateWithLifecycle()
    val realDbm by viewModel.realSignalStrengthDbm.collectAsStateWithLifecycle()
    val stability by viewModel.connectionStability.collectAsStateWithLifecycle()
    val currentPing by viewModel.currentPingMs.collectAsStateWithLifecycle()
    val cellTelemetry by viewModel.cellTelemetry.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1C1B1F)) // Slate background
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Brand block [w-10 h-10 rounded-full bg-[#4A4458] flex items-center justify-center text-[#D0BCFF]]
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4A4458)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = imageNameForConnection(realDbm),
                                contentDescription = "SignalPro logo",
                                tint = Color(0xFFD0BCFF),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "SignalPro X",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.25).sp
                                ),
                                color = Color(0xFFE6E1E5)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${operatorName} • LTE-A / 5G NR Active",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = Color(0xFFAEAAAE)
                                )
                            }
                        }
                    }

                    // Simulation Mode Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSimulationMode) Color(0xFF8C1D40).copy(alpha = 0.35f)
                                else Color(0xFF4A4458).copy(alpha = 0.35f)
                            )
                            .border(
                                1.dp,
                                if (isSimulationMode) Color(0xFFF2B8B5)
                                else Color(0xFFD0BCFF),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isSimulationMode) "SIMULATOR" else "HARDWARE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = if (isSimulationMode) Color(0xFFF2B8B5) else Color(0xFFD0BCFF)
                        )
                    }
                }
            }
        },
        bottomBar = {
            CustomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        containerColor = Color(0xFF1C1B1F)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> BoosterScreen(viewModel)
                1 -> TelemetryScreen(viewModel)
                2 -> LabsScreen(viewModel)
                3 -> HistoryScreen(viewModel)
            }
        }
    }
}

@Composable
fun CustomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .navigationBarsPadding()
            .border(width = 1.dp, color = Color(0xFF4A4458)),
        containerColor = Color(0xFF2B2930),
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = { Icon(Icons.Filled.SignalCellularAlt, contentDescription = "Monitor", tint = if (selectedTab == 0) Color(0xFFD0BCFF) else Color(0xFFAEAAAE)) },
            label = { Text("Monitor", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = if (selectedTab == 0) Color(0xFFE6E1E5) else Color(0xFFAEAAAE)) }
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = { Icon(Icons.Filled.Hub, contentDescription = "Network", tint = if (selectedTab == 1) Color(0xFFD0BCFF) else Color(0xFFAEAAAE)) },
            label = { Text("Network", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = if (selectedTab == 1) Color(0xFFE6E1E5) else Color(0xFFAEAAAE)) }
        )
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            icon = { Icon(Icons.Filled.Science, contentDescription = "Advanced", tint = if (selectedTab == 2) Color(0xFFD0BCFF) else Color(0xFFAEAAAE)) },
            label = { Text("Advanced", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = if (selectedTab == 2) Color(0xFFE6E1E5) else Color(0xFFAEAAAE)) }
        )
        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            icon = { Icon(Icons.Filled.Analytics, contentDescription = "History", tint = if (selectedTab == 3) Color(0xFFD0BCFF) else Color(0xFFAEAAAE)) },
            label = { Text("History", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = if (selectedTab == 3) Color(0xFFE6E1E5) else Color(0xFFAEAAAE)) }
        )
    }
}@Composable
fun BoosterScreen(viewModel: SignalViewModel) {
    val realDbm by viewModel.realSignalStrengthDbm.collectAsStateWithLifecycle()
    val stability by viewModel.connectionStability.collectAsStateWithLifecycle()
    val currentPing by viewModel.currentPingMs.collectAsStateWithLifecycle()
    val cellTelemetry by viewModel.cellTelemetry.collectAsStateWithLifecycle()
    val optimizationState by viewModel.optimizationState.collectAsStateWithLifecycle()
    val devPrefs by viewModel.devPreferences.collectAsStateWithLifecycle()
    val operatorName by viewModel.operatorName.collectAsStateWithLifecycle()

    // Dual SIM states for live selector cards
    val activeSimSlot by viewModel.activeSimSlot.collectAsStateWithLifecycle()
    val sim1Dbm by viewModel.sim1Dbm.collectAsStateWithLifecycle()
    val sim2Dbm by viewModel.sim2Dbm.collectAsStateWithLifecycle()
    val sim1OperatorName by viewModel.sim1OperatorName.collectAsStateWithLifecycle()
    val sim2OperatorName by viewModel.sim2OperatorName.collectAsStateWithLifecycle()
    val dataSimSlot by viewModel.dataSimSlot.collectAsStateWithLifecycle()

    var showSuccessDialog by remember { mutableStateOf(false) }
    var latencySaved by remember { mutableStateOf(0) }
    var showRilResetNotify by remember { mutableStateOf(false) }

    // Dialog triggering setup
    LaunchedEffect(optimizationState) {
        if (optimizationState is OptimizationState.Complete) {
            latencySaved = (optimizationState as OptimizationState.Complete).latencyReducedMs
            showSuccessDialog = true
        }
    }

    // Temporary animated show for RIL resets
    LaunchedEffect(showRilResetNotify) {
        if (showRilResetNotify) {
            kotlinx.coroutines.delay(2000)
            showRilResetNotify = false
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // High Density Signal Gauge Container with flashing green dot
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2B2930) // #2B2930 Slate Surface
                ),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFF4A4458)) // High density border
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Flashing telemetry dot in top-right
                    val infiniteTransition = rememberInfiniteTransition(label = "Blink transition")
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "Blink dot"
                    )
                    
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(10.dp)
                            .graphicsLayer { this.alpha = alpha }
                            .clip(CircleShape)
                            .background(Color(0xFF00FFCC)) // green-500 equivalent neon
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "RSRP SIGNAL INTENSITY",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = Color(0xFFAEAAAE)
                        )

                        // --- NEW: SIDE-BY-SIDE DUAL SIM STATUS SELECTORS ---
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // SIM 1 Selector Tile
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.selectActiveSimSlot(0) },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (activeSimSlot == 0) Color(0xFF4A4458) else Color(0xFF1C1B1F)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (activeSimSlot == 0) Color(0xFFD0BCFF) else Color(0xFF4A4458)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "SIM 1",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (activeSimSlot == 0) Color(0xFF00FFCC) else Color(0xFFAEAAAE)
                                        )
                                        if (dataSimSlot == 0) {
                                            Icon(
                                                imageVector = Icons.Filled.CellTower,
                                                contentDescription = "Active Mobile Data Priority",
                                                tint = Color(0xFF00FFCC),
                                                modifier = Modifier.size(11.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = sim1OperatorName.substringBefore(" ("),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "$sim1Dbm dBm",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = colorForSignal(sim1Dbm)
                                    )
                                }
                            }

                            // SIM 2 Selector Tile
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.selectActiveSimSlot(1) },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (activeSimSlot == 1) Color(0xFF4A4458) else Color(0xFF1C1B1F)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (activeSimSlot == 1) Color(0xFFEFB8C8) else Color(0xFF4A4458)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "SIM 2",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (activeSimSlot == 1) Color(0xFFFFD600) else Color(0xFFAEAAAE)
                                        )
                                        if (dataSimSlot == 1) {
                                            Icon(
                                                imageVector = Icons.Filled.CellTower,
                                                contentDescription = "Active Mobile Data Priority",
                                                tint = Color(0xFFFFD600),
                                                modifier = Modifier.size(11.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = sim2OperatorName.substringBefore(" ("),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "$sim2Dbm dBm",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = colorForSignal(sim2Dbm)
                                    )
                                }
                            }
                        }

                        // Circular Glowing Gauge
                        CircularSignalGauge(dbm = realDbm, stability = stability)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "${cellTelemetry.radioType} • ${cellTelemetry.signalQualityIndicator}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = colorForSignal(realDbm)
                        )
                    }
                }
            }
        }

        // Action Status / Reset Notification Alert Banner
        if (showRilResetNotify) {
            item {
                AnimatedVisibility(
                    visible = showRilResetNotify,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF4A4458).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFFD0BCFF), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Resetting",
                                tint = Color(0xFFD0BCFF),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "RIL Core Reinitialized: Cleared socket routing limits",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFD0BCFF)
                            )
                        }
                    }
                }
            }
        }

        // Quick Dual Optimization Controls Row
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            ) {
                when (val state = optimizationState) {
                    is OptimizationState.Idle, is OptimizationState.Complete -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Max Boost Button
                            Button(
                                onClick = { viewModel.startAutomaticOptimization() },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFD0BCFF), // primary lavender
                                    contentColor = Color(0xFF381E72) // onPrimary
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Filled.Bolt, contentDescription = "Boost", modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "MAX BOOST",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                    )
                                }
                            }

                            // Reset RIL Button
                            Button(
                                onClick = {
                                    viewModel.refreshActiveNetworkTelemetry()
                                    showRilResetNotify = true
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4A4458),
                                    contentColor = Color(0xFFE8DEF8)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Filled.Refresh, contentDescription = "Reset RIL", modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "RESET RIL",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                    is OptimizationState.Optimizing -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF2B2930))
                                .border(1.dp, Color(0xFF4A4458), RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.stage,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFD0BCFF),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { state.progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = Color(0xFFD0BCFF),
                                trackColor = Color(0xFF4A4458)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${(state.progress * 100).toInt()}% Optimized",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFAEAAAE)
                            )
                        }
                    }
                }
            }
        }

        // High Density Developer Panel (Grid)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1C1B1F) // Deep solid carbon background
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF4A4458)) // High density divider borders
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Panel Header Strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF4A4458))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DEV PROTOCOL MONITOR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = Color(0xFFD0BCFF)
                        )
                        Text(
                            text = "VER 4.1.2-LIVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFFD0BCFF)
                        )
                    }

                    // 2-Column Grid of statistics
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Row 1
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .border(width = 0.5.dp, color = Color(0xFF4A4458))
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Carrier",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium, fontSize = 9.sp),
                                    color = Color(0xFFAEAAAE)
                                )
                                Text(
                                    text = operatorName.uppercase(),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .border(width = 0.5.dp, color = Color(0xFF4A4458))
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Bandwidth",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium, fontSize = 9.sp),
                                    color = Color(0xFFAEAAAE)
                                )
                                Text(
                                    text = "20MHz (${cellTelemetry.frequencyBand.substringBefore(" (")})",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Row 2
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .border(width = 0.5.dp, color = Color(0xFF4A4458))
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "TAC / PCI",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium, fontSize = 9.sp),
                                    color = Color(0xFFAEAAAE)
                                )
                                Text(
                                    text = "${cellTelemetry.areaCode} / ${cellTelemetry.cellId.take(3)}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .border(width = 0.5.dp, color = Color(0xFF4A4458))
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "SINR / RSRQ",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium, fontSize = 9.sp),
                                    color = Color(0xFFAEAAAE)
                                )
                                Text(
                                    text = "12dB / -14.0dB",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = Color.White
                                )
                            }
                        }

                        // Row 3
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .border(width = 0.5.dp, color = Color(0xFF4A4458))
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "PLMN ID",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium, fontSize = 9.sp),
                                    color = Color(0xFFAEAAAE)
                                )
                                Text(
                                    text = "${cellTelemetry.mcc}-${cellTelemetry.mnc}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = Color.White
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .border(width = 0.5.dp, color = Color(0xFF4A4458))
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "TX Power",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium, fontSize = 9.sp),
                                    color = Color(0xFFAEAAAE)
                                )
                                Text(
                                    text = "+23.0 dBm",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = Color(0xFFEFB8C8)
                                )
                            }
                        }
                    }

                    // Bottom list of tags of protocol properties
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("TCP_FAST_OPEN", "BBRv2_CONGESTION", "LOW_LATENCY_MTU").forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF2B2930))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFFD0BCFF),
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Secondary Telemetry Cards Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Pin latency card
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "DNS PING",
                    value = "${currentPing}ms",
                    icon = Icons.Filled.Speed,
                    color = when {
                        currentPing < 35 -> Color(0xFF00E676)
                        currentPing < 70 -> Color(0xFFFFD600)
                        else -> Color(0xFFFF1744)
                    }
                )

                // Stability score card
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "STABILITY",
                    value = "$stability%",
                    icon = Icons.Filled.NetworkCheck,
                    color = when {
                        stability > 80 -> Color(0xFF00E676)
                        stability > 50 -> Color(0xFFFFD600)
                        else -> Color(0xFFFF1744)
                    }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Preferred DNS Server card
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "TUNED DNS",
                    value = devPrefs.preferredDns.substringBefore(" ("),
                    icon = Icons.Filled.Dns,
                    color = Color(0xFFD0BCFF)
                )

                // Simulated interference warning indicator
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "DATA MTU",
                    value = "${devPrefs.customMtu} B",
                    icon = Icons.Filled.Router,
                    color = Color(0xFFEFB8C8)
                )
            }
        }
    }

    // Success dialog shown when optimization is complete
    if (showSuccessDialog) {
        Dialog(onDismissRequest = {
            showSuccessDialog = false
            viewModel.resetOptimizationState()
        }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "NETWORK OPTIMIZED",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = Color(0xFF00E676)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Signal strength successfully realigned. System connected routes optimized to reduce structural delay.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Savings Metric Banner
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Latency Reduced By ~$latencySaved ms",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    TextButton(
                        onClick = {
                            showSuccessDialog = false
                            viewModel.resetOptimizationState()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "DISMISS",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CircularSignalGauge(dbm: Int, stability: Int) {
    // Map DBM to progress arc (dbm ranges roughly from -115 to -50)
    val targetProgress = ((dbm - (-115f)) / (-50f - (-115f))).coerceIn(0f, 1f)
    
    val animationProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "Dbm Gauge Anim"
    )

    val color = colorForSignal(dbm)

    Box(
        modifier = Modifier
            .size(200.dp)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val canvasSize = size
            val center = Offset(canvasSize.width / 2, canvasSize.height / 2)
            val radius = (canvasSize.width - strokeWidth) / 2

            // Base Track
            drawArc(
                color = Color.White.copy(alpha = 0.08f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Dynamic Signal level colored Arc
            drawArc(
                color = color,
                startAngle = 135f,
                sweepAngle = 270f * animationProgress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Inner stats
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$dbm",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "dBm Strength",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "$stability% STABLE",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                    color = color
                )
            }
        }
    }
}

@Composable
fun TelemetryScreen(viewModel: SignalViewModel) {
    val cellTelemetry by viewModel.cellTelemetry.collectAsStateWithLifecycle()
    val dbmHistory by viewModel.dbmHistory.collectAsStateWithLifecycle()
    val realDbm by viewModel.realSignalStrengthDbm.collectAsStateWithLifecycle()
    val stability by viewModel.connectionStability.collectAsStateWithLifecycle()

    // Dual SIM states for telemetry comparison
    val sim1Dbm by viewModel.sim1Dbm.collectAsStateWithLifecycle()
    val sim2Dbm by viewModel.sim2Dbm.collectAsStateWithLifecycle()
    val sim1OperatorName by viewModel.sim1OperatorName.collectAsStateWithLifecycle()
    val sim2OperatorName by viewModel.sim2OperatorName.collectAsStateWithLifecycle()
    val sim1Telemetry by viewModel.sim1Telemetry.collectAsStateWithLifecycle()
    val sim2Telemetry by viewModel.sim2Telemetry.collectAsStateWithLifecycle()
    val activeSimSlot by viewModel.activeSimSlot.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "ADVANCED CELL TELEMETRY",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                ),
                color = Color(0xFFD0BCFF)
            )
        }

        // Real-Time Signal Chart Card (Custom scrolling Canvas!)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2B2930)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF4A4458))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Real-Time RSRP Waveform",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFE6E1E5)
                        )
                        Text(
                            text = "${realDbm} dBm",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = colorForSignal(realDbm)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Draw the custom beautiful signal graph
                    SignalHistoryChart(history = dbmHistory, modifier = Modifier.fillMaxWidth().height(160.dp))

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Poor (-120 dBm)", style = MaterialTheme.typography.labelSmall, color = Color(0xFFAEAAAE))
                        Text(text = "Excellent (-50 dBm)", style = MaterialTheme.typography.labelSmall, color = Color(0xFFAEAAAE))
                    }
                }
            }
        }

        // Active Dual-SIM Live Telemetry Comparison Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2930)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF4A4458))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Active Dual-SIM Live Telemetry Comparison",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFD0BCFF),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // SIM 1 Column
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (activeSimSlot == 0) Color(0xFF4A4458).copy(alpha = 0.2f) else Color.Transparent)
                                .border(1.dp, if (activeSimSlot == 0) Color(0xFFD0BCFF).copy(alpha = 0.5f) else Color.Transparent, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF00FFCC)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("1", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "SIM SLOT 1",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFD0BCFF)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = sim1OperatorName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(text = "$sim1Dbm dBm", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace), color = colorForSignal(sim1Dbm))
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            Text(text = "Radio Link", fontSize = 10.sp, color = Color(0xFFAEAAAE))
                            Text(text = sim1Telemetry.radioType, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(text = "MCC/MNC", fontSize = 10.sp, color = Color(0xFFAEAAAE))
                            Text(text = "${sim1Telemetry.mcc}/${sim1Telemetry.mnc}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(text = "Cell ID / Area", fontSize = 10.sp, color = Color(0xFFAEAAAE))
                            Text(text = "${sim1Telemetry.cellId} / ${sim1Telemetry.areaCode}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(text = "Freq Band", fontSize = 10.sp, color = Color(0xFFAEAAAE))
                            Text(text = sim1Telemetry.frequencyBand.substringBefore(" ("), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                        // SIM 2 Column
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (activeSimSlot == 1) Color(0xFF4A4458).copy(alpha = 0.2f) else Color.Transparent)
                                .border(1.dp, if (activeSimSlot == 1) Color(0xFFEFB8C8).copy(alpha = 0.5f) else Color.Transparent, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFD600)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("2", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "SIM SLOT 2",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFEFB8C8)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = sim2OperatorName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(text = "$sim2Dbm dBm", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace), color = colorForSignal(sim2Dbm))
                            Spacer(modifier = Modifier.height(6.dp))

                            Text(text = "Radio Link", fontSize = 10.sp, color = Color(0xFFAEAAAE))
                            Text(text = sim2Telemetry.radioType, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(text = "MCC/MNC", fontSize = 10.sp, color = Color(0xFFAEAAAE))
                            Text(text = "${sim2Telemetry.mcc}/${sim2Telemetry.mnc}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(text = "Cell ID / Area", fontSize = 10.sp, color = Color(0xFFAEAAAE))
                            Text(text = "${sim2Telemetry.cellId} / ${sim2Telemetry.areaCode}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(text = "Freq Band", fontSize = 10.sp, color = Color(0xFFAEAAAE))
                            Text(text = sim2Telemetry.frequencyBand.substringBefore(" ("), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }

        // Core RF Registry Codes
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2B2930)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF4A4458))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Core RF Baseband Registers",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFD0BCFF)
                    )

                    HorizontalDivider(color = Color(0xFF4A4458))

                    TelemetryLine(label = "Mobile Country Code (MCC)", value = cellTelemetry.mcc)
                    TelemetryLine(label = "Mobile Network Code (MNC)", value = cellTelemetry.mnc)
                    TelemetryLine(label = "Primary Cell ID (CID)", value = cellTelemetry.cellId)
                    TelemetryLine(label = "Tracking Area Code (TAC/LAC)", value = cellTelemetry.areaCode)
                    TelemetryLine(label = "Interface RF Frequency Band", value = cellTelemetry.frequencyBand)
                    TelemetryLine(label = "Cellular Antenna Stability Index", value = "$stability%")
                }
            }
        }

        // Diagnostic Console log simulation
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1C1B1F)
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF4A4458))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "LIVE DIAGNOSTIC BUS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = Color(0xFF00FFCC)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "[SYS] Listening for Telephony Callback updates...\n" +
                                "[SYS] Current signal level evaluated to ${realDbm} dBm: ${cellTelemetry.signalQualityIndicator}.\n" +
                                "[RF] Radio Access technology set dynamically to ${cellTelemetry.radioType}.\n" +
                                "[DNS] Stability resolved. Estimated transport loss: ${(100 - stability) / 4}%.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp
                        ),
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun SignalHistoryChart(history: List<Int>, modifier: Modifier = Modifier) {
    val signalColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier) {
        if (history.isEmpty()) return@Canvas

        val maxDbm = -50f
        val minDbm = -115f
        val dbmRange = maxDbm - minDbm

        val width = size.width
        val height = size.height
        val stepX = width / (history.size - 1).coerceAtLeast(1)

        val points = history.mapIndexed { index, dbm ->
            val x = index * stepX
            // Map dbm to dynamic axis y
            val normalizedVal = ((dbm.toFloat() - minDbm) / dbmRange).coerceIn(0f, 1f)
            val y = height - (normalizedVal * height)
            Offset(x, y)
        }

        // Create Bezier Path
        val path = Path()
        if (points.isNotEmpty()) {
            path.moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                val pPrev = points[i - 1]
                val pCurr = points[i]
                val controlX = (pPrev.x + pCurr.x) / 2
                path.quadraticTo(pPrev.x, pPrev.y, controlX, (pPrev.y + pCurr.y) / 2)
            }
            path.lineTo(points.last().x, points.last().y)
        }

        // Area Path for fill under-curve
        val areaPath = Path().apply {
            addPath(path)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        // Draw Fill Gradient
        drawPath(
            path = areaPath,
            brush = Brush.verticalGradient(
                colors = listOf(signalColor.copy(alpha = 0.35f), Color.Transparent),
                startY = 0f,
                endY = height
            )
        )

        // Draw line curve
        drawPath(
            path = path,
            color = signalColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw node circles at peak coordinates
        points.forEachIndexed { idx, point ->
            if (idx == points.size - 1) {
                // Glow point
                drawCircle(color = signalColor, radius = 6.dp.toPx(), center = point)
                drawCircle(color = Color.White, radius = 3.dp.toPx(), center = point)
            }
        }
    }
}

@Composable
fun TelemetryLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun LabsScreen(viewModel: SignalViewModel) {
    val devPrefs by viewModel.devPreferences.collectAsStateWithLifecycle()
    val dnsBenchmarks by viewModel.dnsBenchmarks.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Dual SIM states for option settings
    val sim1NetworkMode by viewModel.sim1NetworkMode.collectAsStateWithLifecycle()
    val sim2NetworkMode by viewModel.sim2NetworkMode.collectAsStateWithLifecycle()
    val dataSimSlot by viewModel.dataSimSlot.collectAsStateWithLifecycle()
    val sim1RoamingEnabled by viewModel.sim1RoamingEnabled.collectAsStateWithLifecycle()
    val sim2RoamingEnabled by viewModel.sim2RoamingEnabled.collectAsStateWithLifecycle()
    val sim1ProfileType by viewModel.sim1ProfileType.collectAsStateWithLifecycle()
    val sim2ProfileType by viewModel.sim2ProfileType.collectAsStateWithLifecycle()
    val sim1OperatorName by viewModel.sim1OperatorName.collectAsStateWithLifecycle()
    val sim2OperatorName by viewModel.sim2OperatorName.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "DEVELOPER LABS & CONTROLS",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                ),
                color = Color(0xFFD0BCFF)
            )
            Text(
                text = "Fine-tune cellular search heuristics and run DNS speeds directly.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFAEAAAE)
            )
        }

        // --- NEW: ADVANCED DUAL SIM DECKS AND ROUTING SETS ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2B2930)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF4A4458))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Dual-SIM Carrier Control Deck",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFD0BCFF)
                    )

                    HorizontalDivider(color = Color(0xFF4A4458))

                    // 1. Mobile Data Routing Segment Button
                    Column {
                        Text(
                            text = "Active Mobile Data Gateway Routing",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFE6E1E5)
                        )
                        Text(
                            text = "Dynamically toggles primary modem RF IP pipeline assignment",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFAEAAAE)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.selectDataSimSlot(0) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (dataSimSlot == 0) Color(0xFFD0BCFF) else Color(0xFF1C1B1F),
                                    contentColor = if (dataSimSlot == 0) Color.Black else Color(0xFFE6E1E5)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("SIM 1 (Verizon)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { viewModel.selectDataSimSlot(1) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (dataSimSlot == 1) Color(0xFFD0BCFF) else Color(0xFF1C1B1F),
                                    contentColor = if (dataSimSlot == 1) Color.Black else Color(0xFFE6E1E5)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("SIM 2 (T-Mobile)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFF4A4458).copy(alpha = 0.5f))

                    // 2. SIM SLOT 1 PANEL
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Slot 1: $sim1OperatorName",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFD0BCFF)
                            )
                            // Card Type Badge (Physical vs eSIM)
                            Text(
                                text = sim1ProfileType,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF4A4458))
                                    .clickable {
                                        val nextType = if (sim1ProfileType.contains("Physical")) "eSIM (Electronic SIM)" else "Physical SIM"
                                        viewModel.updateSimProfileType(0, nextType)
                                    }
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE6E1E5)
                            )
                        }

                        // Network Mode Chooser
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Preferred RF Network Mode", fontSize = 12.sp, color = Color(0xFFAEAAAE))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("5G/LTE Auto", "LTE Only", "3G Legacy").forEach { mode ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .border(1.dp, if (sim1NetworkMode == mode) Color(0xFFD0BCFF) else Color.Transparent)
                                            .background(if (sim1NetworkMode == mode) Color(0xFF4A4458) else Color(0xFF1C1B1F))
                                            .clickable { viewModel.updateSimNetworkMode(0, mode) }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(mode.replace(" Legacy", ""), fontSize = 10.sp, color = Color.White)
                                    }
                                }
                            }
                        }

                        // Data Roaming Toggle Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Cell Data Roaming Service", fontSize = 12.sp, color = Color(0xFFE6E1E5))
                                Text("Permits network access when roaming nationally", fontSize = 10.sp, color = Color(0xFFAEAAAE))
                            }
                            Switch(
                                checked = sim1RoamingEnabled,
                                onCheckedChange = { viewModel.toggleSimRoaming(0) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFFD0BCFF),
                                    checkedTrackColor = Color(0xFF4A4458),
                                    uncheckedThumbColor = Color(0xFFAEAAAE),
                                    uncheckedTrackColor = Color(0xFF1C1B1F)
                                )
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFF4A4458).copy(alpha = 0.5f))

                    // 3. SIM SLOT 2 PANEL
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Slot 2: $sim2OperatorName",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFEFB8C8)
                            )
                            // Card Type Badge (Physical vs eSIM)
                            Text(
                                text = sim2ProfileType,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF4A4458))
                                    .clickable {
                                        val nextType = if (sim2ProfileType.contains("Physical")) "eSIM (Electronic SIM)" else "Physical SIM"
                                        viewModel.updateSimProfileType(1, nextType)
                                    }
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE6E1E5)
                            )
                        }

                        // Network Mode Chooser
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Preferred RF Network Mode", fontSize = 12.sp, color = Color(0xFFAEAAAE))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("5G/LTE Auto", "LTE Only", "3G Legacy").forEach { mode ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .border(1.dp, if (sim2NetworkMode == mode) Color(0xFFEFB8C8) else Color.Transparent)
                                            .background(if (sim2NetworkMode == mode) Color(0xFF4A4458) else Color(0xFF1C1B1F))
                                            .clickable { viewModel.updateSimNetworkMode(1, mode) }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(mode.replace(" Legacy", ""), fontSize = 10.sp, color = Color.White)
                                    }
                                }
                            }
                        }

                        // Data Roaming Toggle Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Cell Data Roaming Service", fontSize = 12.sp, color = Color(0xFFE6E1E5))
                                Text("Permits network access when roaming nationally", fontSize = 10.sp, color = Color(0xFFAEAAAE))
                            }
                            Switch(
                                checked = sim2RoamingEnabled,
                                onCheckedChange = { viewModel.toggleSimRoaming(1) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFFEFB8C8),
                                    checkedTrackColor = Color(0xFF4A4458),
                                    uncheckedThumbColor = Color(0xFFAEAAAE),
                                    uncheckedTrackColor = Color(0xFF1C1B1F)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Lab Fader: Simulated Path Interference
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2B2930)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF4A4458))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Simulated Path Interference",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFE6E1E5)
                            )
                            Text(
                                text = "Simulates concrete structures and path loss",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFAEAAAE)
                            )
                        }
                        Text(
                            text = "${devPrefs.simulateInterference.toInt()}%",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
                            color = Color(0xFFD0BCFF)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Slider(
                        value = devPrefs.simulateInterference,
                        onValueChange = { viewModel.updateSimulatedInterference(it) },
                        valueRange = 0f..100f,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFD0BCFF),
                            activeTrackColor = Color(0xFFD0BCFF),
                            inactiveTrackColor = Color(0xFF4A4458)
                        )
                    )
                }
            }
        }

        // Manual packet tuning
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2B2930)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF4A4458))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Dynamic Packet MTU",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFE6E1E5)
                            )
                            Text(
                                text = "Tuning frame size mitigates structural delay",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFAEAAAE)
                            )
                        }
                        Text(
                            text = "${devPrefs.customMtu} B",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
                            color = Color(0xFFEFB8C8)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Slider(
                        value = devPrefs.customMtu.toFloat(),
                        onValueChange = { viewModel.updateCustomMtu(it.toInt()) },
                        valueRange = 1400f..1500f,
                        steps = 9,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFEFB8C8),
                            activeTrackColor = Color(0xFFEFB8C8),
                            inactiveTrackColor = Color(0xFF4A4458)
                        )
                    )
                }
            }
        }

        // Toggles Box
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2B2930)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF4A4458))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Laboratory Parameters",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFD0BCFF),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Auto-Reconnect Threshold",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFE6E1E5)
                            )
                            Text(
                                text = "Triggers automatic optimization when signal drops",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFAEAAAE)
                            )
                        }
                        Switch(
                            checked = devPrefs.autoReconnect,
                            onCheckedChange = { viewModel.updateAutoReconnect(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFD0BCFF),
                                checkedTrackColor = Color(0xFF4A4458),
                                uncheckedThumbColor = Color(0xFFAEAAAE),
                                uncheckedTrackColor = Color(0xFF1C1B1F)
                            )
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFF4A4458))

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Aggressive Handover Mode",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFE6E1E5)
                            )
                            Text(
                                text = "Triggers aggressive nearby cell tower handshakes",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFAEAAAE)
                            )
                        }
                        Switch(
                            checked = devPrefs.aggressiveHandover,
                            onCheckedChange = { viewModel.updateAggressiveHandover(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFD0BCFF),
                                checkedTrackColor = Color(0xFF4A4458),
                                uncheckedThumbColor = Color(0xFFAEAAAE),
                                uncheckedTrackColor = Color(0xFF1C1B1F)
                            )
                        )
                    }
                }
            }
        }

        // Real-world DNS Benchmarks inside Laboratory!
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2B2930)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF4A4458))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Live DNS Latency Benchmarks",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFE6E1E5)
                        )
                        Button(
                            onClick = { viewModel.triggerDnsBenchmark() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4A4458),
                                contentColor = Color(0xFFD0BCFF)
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(32.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("PING ALL", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    dnsBenchmarks.forEach { dns ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.updateSelectedDns("${dns.providerName} (${dns.primaryIp})") }
                                .background(
                                    if (devPrefs.preferredDns.contains(dns.primaryIp)) Color(0xFF4A4458).copy(alpha = 0.4f)
                                    else Color.Transparent
                                )
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = devPrefs.preferredDns.contains(dns.primaryIp),
                                    onClick = { viewModel.updateSelectedDns("${dns.providerName} (${dns.primaryIp})") },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Color(0xFFD0BCFF),
                                        unselectedColor = Color(0xFFAEAAAE)
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Column {
                                    Text(
                                        text = dns.providerName,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFFE6E1E5)
                                    )
                                    Text(
                                        text = dns.primaryIp,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFAEAAAE)
                                    )
                                }
                            }

                            // Speed gauge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when {
                                            dns.currentLatencyMs <= 0 -> Color(0xFF4A4458)
                                            dns.currentLatencyMs < 30 -> Color(0xFF00FFCC).copy(alpha = 0.15f)
                                            dns.currentLatencyMs < 60 -> Color(0xFFFFD600).copy(alpha = 0.15f)
                                            else -> Color(0xFFFF1744).copy(alpha = 0.15f)
                                        }
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = dns.status,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = when {
                                        dns.currentLatencyMs <= 0 -> Color(0xFFAEAAAE)
                                        dns.currentLatencyMs < 30 -> Color(0xFF00FFCC)
                                        dns.currentLatencyMs < 60 -> Color(0xFFFFD600)
                                        else -> Color(0xFFFF1744)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryScreen(viewModel: SignalViewModel) {
    val history by viewModel.logHistory.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "OPTIMIZATION REGINES LOG",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color(0xFFD0BCFF)
                )
                Text(
                    text = "${history.size} historical diagnostic events recorded",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFAEAAAE)
                )
            }

            if (history.isNotEmpty()) {
                IconButton(onClick = { viewModel.clearLogHistory() }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Clear logs",
                        tint = Color(0xFFF2B8B5)
                    )
                }
            }
        }

        // 📲 APK Packaging & Installation Service Guide Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2B2930)
            ),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Color(0xFF00FFCC).copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Android,
                        contentDescription = "APK Icon",
                        tint = Color(0xFF00FFCC),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "HOW TO COMPILE & DOWNLOAD THE APK",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        color = Color(0xFF00FFCC)
                    )
                }

                Text(
                    text = "Since you are on an Android mobile device, you can package this complete app and install it locally without a PC:",
                    fontSize = 11.sp,
                    color = Color(0xFFE6E1E5)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    listOf(
                        "⚙️  Locate the Settings Button: Look at the very top right of your Google AI Studio browser interface.",
                        "📦  Request Build Task: Tap on the Gear Icon/dropdown menu.",
                        "📲  Select 'Generate APK file': Tap this option. Our secure server-side compiler will automatically compile all code structures into a stable, direct installable Android .apk file.",
                        "🛠️  Tap To Install: Once the download indicator completes, tap the file in your mobile notification bar and select Install (enable 'Allow Unknown Sources' if requested by Android Package Installer)."
                    ).forEach { step ->
                        Text(
                            text = step,
                            fontSize = 10.5.sp,
                            color = Color(0xFFAEAAAE),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        if (history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4A4458).copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DoneAll,
                            contentDescription = "No Logs yet",
                            tint = Color(0xFFD0BCFF),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Logs Database Empty",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFE6E1E5)
                    )
                    Text(
                        text = "Trigger an Auto-Boost cycle from the dashboard.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFAEAAAE),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(history) { log ->
                    LogItemRow(log)
                }
            }
        }
    }
}

@Composable
fun LogItemRow(log: SignalLog) {
    val formatter = remember { SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault()) }
    val formattedDate = remember(log.timestamp) { formatter.format(Date(log.timestamp)) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2B2930)
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF4A4458))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = log.optimizationType,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFD0BCFF)
                )
                Text(
                    text = "${log.latencyMs} ms latency",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = Color(0xFF00FFCC)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = log.resultMessage,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFE6E1E5)
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = Color(0xFF4A4458))

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${log.operatorName} • ${log.networkType} • ${log.dbm} dBm",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFAEAAAE)
                )
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFAEAAAE)
                )
            }
        }
    }
}


@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2B2930)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFF4A4458))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = Color(0xFFAEAAAE)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                ),
                color = Color.White
            )
        }
    }
}

// Utility formatting functions
fun colorForSignal(dbm: Int): Color {
    return when {
        dbm >= -75 -> Color(0xFF00FFCC) // Glow Cyan
        dbm >= -88 -> Color(0xFF00E676) // Bright Green
        dbm >= -98 -> Color(0xFFFFD600) // Amber
        dbm >= -110 -> Color(0xFFFFD180) // Orange
        else -> Color(0xFFFF1744) // Neon Red
    }
}

fun imageNameForConnection(dbm: Int): androidx.compose.ui.graphics.vector.ImageVector {
    return when {
         dbm >= -75 -> Icons.Filled.SignalCellularAlt
         dbm >= -98 -> Icons.Filled.SignalCellularAlt
         else -> Icons.Filled.SignalCellularConnectedNoInternet4Bar
    }
}
