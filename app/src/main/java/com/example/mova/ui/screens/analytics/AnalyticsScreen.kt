/**
 * COMPOSABLE: AnalyticsScreen
 * Shows the goal, calorie progress, trend and volume charts, and the muscle group donut.
 * Everything is drawn from the server's AnalyticsDto. The period switch reloads the data.
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Calendar
import java.util.Locale

/**
 * X-axis labels for the trend charts.
 *
 * Weekly: single-letter weekday names for the last seven days, ending today.
 * Monthly: four weekly buckets, oldest first.
 *
 * Uses java.util.Calendar rather than java.time, because java.time needs API 26
 * and this app's minSdk is 24.
 */
private fun trendLabels(period: String): List<String> =
    if (period == "Weekly") {
        val locale = Locale.getDefault()
        (6 downTo 0).map { daysAgo ->
            val day = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -daysAgo)
            }
            // SHORT gives "Mon", "Tue" and so on. Take the first letter for the chart axis.
            val short = day.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, locale) ?: ""
            short.take(1)
        }
    } else {
        listOf("Wk 1", "Wk 2", "Wk 3", "Wk 4")
    }

@Composable
fun AnalyticsScreen(
    userId: String,
    viewModel: AnalyticsViewModel = viewModel()
) {
    // Local UI state. Only the segmented control and the dialog use these.
    var selectedPeriod by remember { mutableStateOf("Weekly") }
    var showEditDialog by remember { mutableStateOf(false) }

    val analyticsData by viewModel.analyticsData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Edit-dialog fields. They are reset from the saved goal each time the dialog opens.
    var inputGoalTitle by remember { mutableStateOf("") }
    var inputTargetCalories by remember { mutableStateOf("") }

    // Reloads whenever the user or the period changes.
    LaunchedEffect(userId, selectedPeriod) {
        if (userId.isNotBlank()) {
            viewModel.loadAnalytics(userId, selectedPeriod)
        }
    }

    // Shared by the trend charts so their labels always match.
    val labels = trendLabels(selectedPeriod)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // ---- Top app bar: title and logo ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Analytics",
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

        // ---- Period switcher: Weekly / Monthly ----
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
                        text = period,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color(0xFF1A1C1E) else Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Shows a failed load or failed save. Nothing appears when there is no error.
        errorMessage?.let { message ->
            Text(text = message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(text = "Goal Setting", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1A237E))
        Spacer(modifier = Modifier.height(8.dp))

        // ---- Goal card: title, progress bar and the edit button ----
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
                        Text(text = "$selectedPeriod Active Goal", fontSize = 11.sp, color = Color.Gray)
                        Text(
                            text = analyticsData?.goalTitle ?: "No goal set",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    TextButton(onClick = {
                        // Pre-fill the dialog with the saved goal, falling back to 2000 kcal.
                        inputGoalTitle = analyticsData?.goalTitle ?: ""
                        inputTargetCalories = analyticsData?.targetCalories?.toString() ?: "2000"
                        showEditDialog = true
                    }) {
                        Text(text = "EDIT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD9820B))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                val burned = analyticsData?.burnedCalories ?: 0
                val target = analyticsData?.targetCalories ?: 2000
                // Clamped to 0..1 so the bar never overflows or goes negative.
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
                    Text(text = "Current Status", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = "$burned / $target cal",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Performance Trends", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1A237E))
        Spacer(modifier = Modifier.height(10.dp))

        // ---- Calorie trend line chart ----
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "$selectedPeriod Calories Trend".uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))

                Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                    val points: List<Float> = analyticsData?.calorieTrend ?: emptyList()
                    if (points.isNotEmpty()) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // The largest point sets the top of the chart. coerceAtLeast(1f)
                            // avoids dividing by zero when every value is 0.
                            val maxVal = (points.maxOrNull() ?: 1f).coerceAtLeast(1f)
                            // Spacing between points. coerceAtLeast(1) avoids dividing by zero
                            // when there is only one point.
                            val widthStep = size.width / (points.size - 1).coerceAtLeast(1)
                            val path = Path()

                            points.forEachIndexed { index, fl ->
                                val x = index * widthStep
                                // Canvas y grows downward, so flip it: high values sit near the top.
                                val y = size.height * (1f - (fl / maxVal).coerceIn(0f, 1f))
                                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            }
                            drawPath(path, color = Color(0xFF26A69A), style = Stroke(width = 3.dp.toPx()))
                        }
                    } else {
                        Text(text = "No trend data available", fontSize = 11.sp, color = Color.Gray)
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

        // ---- Training volume bar chart ----
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Training Volume (Mins)".uppercase(),
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
                    if (volumes.isNotEmpty()) {
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
                    text = "MUSCLE GROUP DISTRIBUTION",
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
                    Box(modifier = Modifier.size(90.dp), contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val radius = size.minDimension / 2
                            val center = Offset(size.width / 2, size.height / 2)

                            val dist = analyticsData?.muscleGroupDistribution
                            val core = dist?.core?.toFloat() ?: 0f
                            val upper = dist?.upper?.toFloat() ?: 0f
                            val lower = dist?.lower?.toFloat() ?: 0f
                            // coerceAtLeast(1f) keeps the division safe when there is no data yet.
                            val total = (core + upper + lower).coerceAtLeast(1f)

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
                            // A white circle over the middle turns the pie into a donut.
                            drawCircle(Color.White, radius = radius * 0.5f, center = center)
                        }
                    }

                    val dist = analyticsData?.muscleGroupDistribution
                    val coreVal = dist?.core ?: 0
                    val upperVal = dist?.upper ?: 0
                    val lowerVal = dist?.lower ?: 0

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        AnalyticsLegendItem(Color(0xFFAB47BC), "Core & Abs ($coreVal%)")
                        AnalyticsLegendItem(Color(0xFF26A69A), "Upper Body ($upperVal%)")
                        AnalyticsLegendItem(Color(0xFFFFA726), "Lower Body ($lowerVal%)")
                    }
                }
            }
        }
    }

    // ---- Goal edit dialog ----
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(text = "Edit Active Goal") },
            text = {
                Column {
                    OutlinedTextField(
                        value = inputGoalTitle,
                        onValueChange = { inputGoalTitle = it },
                        label = { Text("Goal Title") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputTargetCalories,
                        onValueChange = { inputTargetCalories = it },
                        label = { Text("Target Calories") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    // Falls back to 2000 if the field isn't a number, so a typo doesn't
                    // silently send 0.
                    val targetCals = inputTargetCalories.toIntOrNull() ?: 2000
                    viewModel.updateFullGoal(userId, inputGoalTitle, targetCals)
                    showEditDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * One row in the donut legend: a colored dot followed by the label.
 */
@Composable
fun AnalyticsLegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, fontSize = 11.sp, color = Color(0xFF1A1C1E), fontWeight = FontWeight.Medium)
    }
}