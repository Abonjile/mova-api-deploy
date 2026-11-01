/**
 * =====================================================================================
 * COMPOSABLE: AnalyticsScreen
 * =====================================================================================
 * Goal setting, calorie progress, trend and volume charts, and the muscle-group donut.
 * Every label comes from strings.xml, so the whole screen translates.
 * =====================================================================================
 */
package com.example.mova.ui.screens.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mova.R
import java.util.Calendar

/**
 * X-axis labels for the trend charts.
 * Weekly shows the first letter of each of the last seven weekdays, ending today.
 * Monthly shows four weeks, oldest first. Uses java.util.Calendar, which works on API 24.
 */
@Composable
private fun trendLabels(period: String): List<String> {
    if (period != "Weekly") {
        return listOf("Wk 1", "Wk 2", "Wk 3", "Wk 4")
    }
    val locale = java.util.Locale.getDefault()
    return (6 downTo 0).map { daysAgo ->
        val day = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -daysAgo) }
        (day.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, locale) ?: "").take(1)
    }
}

/** The translated name of a period: "Weekly" or "Monthly". */
@Composable
private fun periodName(period: String): String =
    if (period == "Weekly") stringResource(R.string.period_weekly)
    else stringResource(R.string.period_monthly)

/** Turns an error from the ViewModel into the matching translated message. */
@Composable
private fun analyticsErrorText(error: AnalyticsError): String = when (error) {
    AnalyticsError.NETWORK -> stringResource(R.string.network_error)
    AnalyticsError.LOAD_FAILED -> stringResource(R.string.error_load_analytics)
    AnalyticsError.SAVE_FAILED -> stringResource(R.string.error_save_goal)
    AnalyticsError.INVALID_GOAL -> stringResource(R.string.error_invalid_goal)
}

@Composable
fun AnalyticsScreen(
    userId: String,
    viewModel: AnalyticsViewModel = viewModel()
) {
    var selectedPeriod by remember { mutableStateOf("Weekly") }
    var showEditDialog by remember { mutableStateOf(false) }

    val analyticsData by viewModel.analyticsData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Dialog fields, filled from the saved goal each time the dialog opens.
    var inputGoalTitle by remember { mutableStateOf("") }
    var inputTargetCalories by remember { mutableStateOf("") }

    LaunchedEffect(userId, selectedPeriod) {
        if (userId.isNotBlank()) {
            viewModel.loadAnalytics(userId, selectedPeriod)
        }
    }

    val labels = trendLabels(selectedPeriod)
    val currentError = error

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // ---- Header ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.analytics_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A237E)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(Color.Black, CircleShape))
                Spacer(modifier = Modifier.width(4.dp))
                Box(modifier = Modifier.size(8.dp).background(Color.Red, CircleShape))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "MOVA", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A237E))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ---- Weekly / Monthly switch ----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE9ECEF), RoundedCornerShape(10.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Weekly", "Monthly").forEach { period ->
                val isSelected = selectedPeriod == period
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color.White else Color.Transparent)
                        .clickable { selectedPeriod = period }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = periodName(period),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color(0xFF1A1C1E) else Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        currentError?.let { problem ->
            Text(text = analyticsErrorText(problem), color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            text = stringResource(R.string.analytics_goal_setting),
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color(0xFF1A237E)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // ---- Goal card ----
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.analytics_active_goal, periodName(selectedPeriod)),
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = analyticsData?.goalTitle ?: stringResource(R.string.analytics_no_goal),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    TextButton(onClick = {
                        inputGoalTitle = analyticsData?.goalTitle ?: ""
                        inputTargetCalories = analyticsData?.targetCalories?.toString() ?: "2000"
                        showEditDialog = true
                    }) {
                        Text(
                            text = stringResource(R.string.analytics_edit),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD9820B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                val burned = analyticsData?.burnedCalories ?: 0
                val target = analyticsData?.targetCalories ?: 2000
                // Keeps the bar between 0 and full, even when the goal is zero or the data is old.
                val progress = if (target > 0) (burned.toFloat() / target.toFloat()).coerceIn(0f, 1f) else 0f

                LinearProgressIndicator(
                    progress =  progress ,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFFD9820B),
                    trackColor = Color(0xFFF1F3F5)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.analytics_current_status),
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = stringResource(R.string.analytics_calories_status, burned, target),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.analytics_performance_trends),
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color(0xFF1A237E)
        )
        Spacer(modifier = Modifier.height(10.dp))

        // ---- Calorie trend (line chart) ----
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.analytics_calories_trend, periodName(selectedPeriod)).uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))

                Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                    val points: List<Float> = analyticsData?.calorieTrend ?: emptyList()
                    if (points.isNotEmpty()) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // coerceAtLeast(1f) prevents dividing by zero when every point is 0.
                            val maxVal = (points.maxOrNull() ?: 1f).coerceAtLeast(1f)
                            val widthStep = size.width / (points.size - 1).coerceAtLeast(1)
                            val path = Path()

                            points.forEachIndexed { index, fl ->
                                val x = index * widthStep
                                // Canvas y increases downward, so high values are flipped to the top.
                                val y = size.height * (1f - (fl / maxVal).coerceIn(0f, 1f))
                                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            }
                            drawPath(path, color = Color(0xFF26A69A), style = Stroke(width = 3.dp.toPx()))
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.analytics_no_trend),
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    labels.forEach { label ->
                        Text(text = label, fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ---- Training volume (bar chart) ----
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.analytics_training_volume).uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().height(70.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val volumes: List<Float> = analyticsData?.trainingVolume ?: emptyList()
                    val maxVol = (volumes.maxOrNull() ?: 100f).coerceAtLeast(1f)
                    volumes.forEach { vol ->
                        // The 0.1 minimum keeps a zero-minute day visible as a small stub.
                        val heightFraction = (vol / maxVol).coerceIn(0.1f, 1f)
                        Box(
                            modifier = Modifier
                                .width(if (selectedPeriod == "Weekly") 22.dp else 45.dp)
                                .fillMaxHeight(heightFraction)
                                .background(Color(0xFFC62828), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    labels.forEach { label ->
                        Text(text = label, fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ---- Muscle group donut ----
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.analytics_muscle_distribution).uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val dist = analyticsData?.muscleGroupDistribution
                    val core = dist?.core ?: 0
                    val upper = dist?.upper ?: 0
                    val lower = dist?.lower ?: 0

                    Box(modifier = Modifier.size(90.dp), contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val radius = size.minDimension / 2
                            val center = Offset(size.width / 2, size.height / 2)

                            val total = (core + upper + lower).toFloat().coerceAtLeast(1f)
                            val slices = listOf(
                                (core / total) to Color(0xFFAB47BC),
                                (upper / total) to Color(0xFF26A69A),
                                (lower / total) to Color(0xFFFFA726)
                            )

                            // -90 starts the first slice at 12 o'clock.
                            var startAngle = -90f
                            slices.forEach { (fraction, color) ->
                                val sweep = fraction * 360f
                                if (sweep > 0f) {
                                    drawArc(
                                        color = color,
                                        startAngle = startAngle,
                                        sweepAngle = sweep,
                                        useCenter = true,
                                        size = Size(radius * 2, radius * 2),
                                        topLeft = Offset(center.x - radius, center.y - radius)
                                    )
                                    startAngle += sweep
                                }
                            }
                            // A white circle in the middle turns the pie into a donut.
                            drawCircle(Color.White, radius = radius * 0.5f, center = center)
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        AnalyticsLegendItem(Color(0xFFAB47BC), stringResource(R.string.analytics_core, core))
                        AnalyticsLegendItem(Color(0xFF26A69A), stringResource(R.string.analytics_upper, upper))
                        AnalyticsLegendItem(Color(0xFFFFA726), stringResource(R.string.analytics_lower, lower))
                    }
                }
            }
        }
    }

    // ---- Goal edit dialog ----
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(text = stringResource(R.string.analytics_edit_dialog)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = inputGoalTitle,
                        onValueChange = { inputGoalTitle = it },
                        label = { Text(stringResource(R.string.analytics_goal_title)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputTargetCalories,
                        onValueChange = { inputTargetCalories = it },
                        label = { Text(stringResource(R.string.analytics_target_calories)) }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    // A value that isn't a number falls back to 2000 rather than sending 0.
                    val targetCals = inputTargetCalories.toIntOrNull() ?: 2000
                    viewModel.updateFullGoal(userId, inputGoalTitle, targetCals)
                    showEditDialog = false
                }) {
                    Text(stringResource(R.string.action_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

/** One row of the donut legend: a coloured dot and its label. */
@Composable
fun AnalyticsLegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, fontSize = 11.sp, color = Color(0xFF1A1C1E), fontWeight = FontWeight.Medium)
    }
}