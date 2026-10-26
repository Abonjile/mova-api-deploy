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

/**
 * Composable function representing the Analytics screen.
 * Features goal tracking with a Weekly/Monthly period toggle, calorie trend line charts,
 * daily/monthly training volume bar charts, and muscle group distribution donut charts.
 */
@Composable
fun AnalyticsScreen() {
    // State variable to manage whether the user is viewing Weekly or Monthly analytics data
    var selectedPeriod by remember { mutableStateOf("Weekly") }

    // Main vertical scroll column container for the Analytics screen contents
    Column(
        modifier = Modifier
            .fillMaxSize() // Fill entire screen size
            .background(Color(0xFFF8F9FA)) // Light grey screen background
            .verticalScroll(rememberScrollState()) // Enable scrolling for overflow content
            .padding(16.dp) // Padding around screen edges
    ) {
        // Row container for the top application bar (Title and Brand Logo)
        Row(
            modifier = Modifier.fillMaxWidth(), // Span full width
            horizontalArrangement = Arrangement.SpaceBetween, // Space title and logo apart
            verticalAlignment = Alignment.CenterVertically // Center items vertically
        ) {
            // Text displaying screen title "Analytics"
            Text(
                text = "Analytics", // Title string
                fontSize = 24.sp, // Large font size
                fontWeight = FontWeight.Bold, // Bold text weight
                color = Color(color = 0xFF1A237E) //Dark Blue color
            )
            // Row container for MOVA brand indicators
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Black brand dot
                Box(modifier = Modifier.size(8.dp).background(Color.Black, CircleShape))
                // Horizontal spacing between dots
                Spacer(modifier = Modifier.width(4.dp))
                // Red brand dot
                Box(modifier = Modifier.size(8.dp).background(Color.Red, CircleShape))
                // Horizontal spacing before brand name
                Spacer(modifier = Modifier.width(6.dp))
                // Brand name text "MOVA"
                Text(text = "MOVA", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A237E)) //Dark Blue colour
            }
        }

        // Vertical spacing below top app bar
        Spacer(modifier = Modifier.height(16.dp))

        // Segmented period switcher row for toggling between Weekly and Monthly views
        Row(
            modifier = Modifier
                .fillMaxWidth() // Span full width
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp)) // Light grey rounded container background
                .padding(4.dp), // Inner padding around tab items
            horizontalArrangement = Arrangement.SpaceBetween // Distribute space evenly
        ) {
            // Loop through period options ("Weekly" and "Monthly")
            listOf("Weekly", "Monthly").forEach { period ->
                // Check if current option is selected
                val isSelected = selectedPeriod == period
                // Clickable Box container for each period tab
                Box(
                    modifier = Modifier
                        .weight(1f) // Distribute available width equally between tabs
                        .clip(RoundedCornerShape(8.dp)) // Rounded corners for tab item
                        .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent) // White background if selected, transparent otherwise
                        .clickable { selectedPeriod = period } // Update selected period state on click
                        .padding(vertical = 8.dp), // Vertical padding inside tab
                    contentAlignment = Alignment.Center // Center text inside tab box
                ) {
                    // Tab label text
                    Text(
                        text = period, // Period name ("Weekly" or "Monthly")
                        fontSize = 13.sp, // Font size for tab label
                        fontWeight = FontWeight.Bold, // Bold text weight
                        color = if (isSelected) Color(0xFF1A1C1E) else Color.Gray // Dark color if selected, grey otherwise
                    )
                }
            }
        }

        // Vertical spacing below period switcher
        Spacer(modifier = Modifier.height(16.dp))
        // Section header indicating goal setting status for the selected period
        Text(text = "Goal Setting", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1A237E))
        // Vertical spacing below header
        Spacer(modifier = Modifier.height(8.dp))

        // Card container for Goal Setting metrics, dynamically updated based on active period selection
        Card(
            modifier = Modifier.fillMaxWidth(), // Span full width
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // White background color
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp) // Subtle shadow elevation
        ) {
            // Inner column layout for goal details
            Column(modifier = Modifier.padding(16.dp)) {
                // Row for goal title and edit icon
                Row(
                    modifier = Modifier.fillMaxWidth(), // Span full width
                    horizontalArrangement = Arrangement.SpaceBetween, // Space elements to opposite ends
                    verticalAlignment = Alignment.CenterVertically // Center vertically
                ) {
                    // Left row grouping target emoji and text descriptions
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Target emoji icon
                        Text(text = "🎯", fontSize = 20.sp, color = Color(0xFFD9820B))
                        // Horizontal spacing
                        Spacer(modifier = Modifier.width(8.dp))
                        // Column holding subtitle label and dynamic goal description
                        Column {
                            // Subtitle label showing active period
                            Text(text = "$selectedPeriod Active Goal", fontSize = 11.sp, color = Color.Gray)
                            // Dynamic goal target text based on whether Weekly or Monthly is selected
                            Text(
                                text = if (selectedPeriod == "Weekly") "Burn 2,000 cal this week" else "Burn 8,500 cal this month",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                    // Edit icon symbol
                    Text(text = "✏️", fontSize = 14.sp, color = Color.Gray)
                }
                // Vertical spacing below header row
                Spacer(modifier = Modifier.height(14.dp))
                // Compute dynamic progress ratio based on selected period
                val progress = if (selectedPeriod == "Weekly") 1420f / 2000f else 6100f / 8500f
                // Linear progress indicator showing calorie burn completion progress
                LinearProgressIndicator(
                    progress = progress, // supplies calculated progress ratio
                    modifier = Modifier
                        .fillMaxWidth() // Span full width
                        .height(6.dp) // Height thickness of progress bar
                        .clip(RoundedCornerShape(3.dp)), // Rounded corners
                    color = Color(0xFFD9820B), // Orange-amber progress fill color
                    trackColor = Color(0xFFF1F3F5) // Light grey background track color
                )
                // Vertical spacing below progress bar
                Spacer(modifier = Modifier.height(8.dp))
                // Bottom status row showing current status label and numeric values
                Row(
                    modifier = Modifier.fillMaxWidth(), // Span full width
                    horizontalArrangement = Arrangement.SpaceBetween // Space items apart
                ) {
                    // Status label text
                    Text(text = "Current Status", fontSize = 11.sp, color = Color.Gray)
                    // Dynamic status text reflecting current burned calories vs target for the selected period
                    Text(
                        text = if (selectedPeriod == "Weekly") "1,420 / 2,000 cal" else "6,100 / 8,500 cal",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E)
                    )
                }
            }
        }

        // Vertical spacing below goal setting card
        Spacer(modifier = Modifier.height(20.dp))
        // Section header for Performance Trends
        Text(text = "Performance Trends", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1A237E))
        // Vertical spacing below header
        Spacer(modifier = Modifier.height(10.dp))

        // Card container for Calories Trend Line Chart
        Card(
            modifier = Modifier.fillMaxWidth(), // Span full width
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // White background color
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp) // Subtle elevation shadow
        ) {
            // Inner column container for chart elements
            Column(modifier = Modifier.padding(16.dp)) {
                // Dynamic section title for calories trend
                Text(text = "$selectedPeriod Calories Trend".uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                // Vertical spacing below title
                Spacer(modifier = Modifier.height(12.dp))

                // Custom Canvas composable drawing line graph representing calorie burn trends
                Canvas(modifier = Modifier
                    .fillMaxWidth() // Span full width
                    .height(80.dp)) { // Fixed chart height
                    // Select data points based on active period (weekly vs monthly)
                    val points = if (selectedPeriod == "Weekly") listOf(0.2f, 0.4f, 0.3f, 0.6f, 0.4f, 0.8f, 0.7f) else listOf(0.3f, 0.5f, 0.6f, 0.8f)
                    val widthStep = size.width / (points.size - 1).coerceAtLeast(1) // Calculate horizontal step distance between points
                    val path = Path() // Initialize drawing path object

                    // Iterate through data points to construct line path
                    points.forEachIndexed { index, fl ->
                        val x = index * widthStep // Calculate X coordinate
                        val y = size.height * (1f - fl) // Calculate inverted Y coordinate for canvas rendering
                        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y) // Move to start or draw line segment to next point
                    }
                    // Draw the constructed path on canvas with teal stroke color
                    drawPath(path, color = Color(0xFF26A69A), style = Stroke(width = 3.dp.toPx()))
                }

                // Vertical spacing below line chart canvas
                Spacer(modifier = Modifier.height(8.dp))
                // Row displaying X-axis labels (days of the week or weeks of the month)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    // Select labels based on selected period
                    val labels = if (selectedPeriod == "Weekly") listOf("M", "T", "W", "T", "F", "S", "S") else listOf("Wk 1", "Wk 2", "Wk 3", "Wk 4")
                    // Render each label text
                    labels.forEach { label ->
                        Text(text = label, fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }

        // Vertical spacing between trend cards
        Spacer(modifier = Modifier.height(12.dp))

        // Card container for Training Volume Bar Chart
        Card(
            modifier = Modifier.fillMaxWidth(), // Span full width
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // White background color
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp) // Subtle shadow elevation
        ) {
            // Inner column container for bar chart elements
            Column(modifier = Modifier.padding(16.dp)) {
                // Section title for training volume
                Text(text = "Training Volume (Mins)".uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                // Vertical spacing below title
                Spacer(modifier = Modifier.height(12.dp))

                // Row container aligning vertical bar graph columns at the bottom
                Row(
                    modifier = Modifier
                        .fillMaxWidth() // Span full width
                        .height(70.dp), // Fixed chart height
                    horizontalArrangement = Arrangement.SpaceBetween, // Space bars evenly across width
                    verticalAlignment = Alignment.Bottom // Align bars to bottom baseline
                ) {
                    // Select bar height fractions based on selected period
                    val heights = if (selectedPeriod == "Weekly") listOf(0.4f, 0.7f, 0.3f, 0.9f, 0.6f, 0.8f, 0.5f) else listOf(0.6f, 0.8f, 0.7f, 0.9f)
                    // Iterate through heights to render each bar
                    heights.forEach { h ->
                        Box(
                            modifier = Modifier
                                .width(if (selectedPeriod == "Weekly") 22.dp else 45.dp) // Adjust bar width based on count
                                .fillMaxHeight(h) // Set proportional height fraction
                                .background(Color(0xFFC62828), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)) // Red bar with rounded top corners
                        )
                    }
                }
                // Vertical spacing below bar chart
                Spacer(modifier = Modifier.height(8.dp))
                // Row displaying X-axis labels under the bars
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    // Select labels based on period
                    val labels = if (selectedPeriod == "Weekly") listOf("M", "T", "W", "T", "F", "S", "S") else listOf("Wk 1", "Wk 2", "Wk 3", "Wk 4")
                    // Render each label text
                    labels.forEach { label ->
                        Text(text = label, fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }

        // Vertical spacing between cards
        Spacer(modifier = Modifier.height(12.dp))

        // Card container for Muscle Group Distribution Donut Chart
        Card(
            modifier = Modifier.fillMaxWidth(), // Span full width
            colors = CardDefaults.cardColors(containerColor = Color.White), // White background color
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp) // Subtle shadow elevation
        ) {
            // Inner column container for chart content
            Column(modifier = Modifier.padding(16.dp)) {
                // Section title for muscle distribution
                Text(text = "MUSCLE GROUP DISTRIBUTION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                // Vertical spacing below title
                Spacer(modifier = Modifier.height(12.dp))

                // Row layout holding donut chart canvas on left and legend items on right
                Row(
                    modifier = Modifier.fillMaxWidth(), // Span full width
                    verticalAlignment = Alignment.CenterVertically, // Center vertically
                    horizontalArrangement = Arrangement.SpaceBetween // Space chart and legend apart
                ) {
                    // Box containing custom Canvas for donut chart rendering
                    Box(modifier = Modifier.size(90.dp), contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val radius = size.minDimension / 2 // Calculate chart radius
                            val center = Offset(size.width / 2, size.height / 2) // Calculate center offset coordinate
                            // Define slices with percentage fractions and corresponding colors
                            val slices = listOf(0.50f to Color(0xFFAB47BC), 0.28f to Color(0xFF26A69A), 0.22f to Color(0xFFFFA726))
                            var startAngle = -90f // Start drawing from top (12 o'clock position)

                            // Iterate through slices to draw arc segments
                            slices.forEach { (fraction, color) ->
                                val sweep = fraction * 360f // Calculate sweep angle for the slice
                                drawArc(color, startAngle, sweep, true, size = Size(radius * 2, radius * 2), topLeft = Offset(center.x - radius, center.y - radius))
                                startAngle += sweep // Increment start angle for next slice
                            }
                            // Draw inner white circle to transform pie chart into a donut chart
                            drawCircle(Color.White, radius = radius * 0.5f, center = center)
                        }
                    }

                    // Column containing legend items mapping colors to muscle groups
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LegendItem(Color(0xFFAB47BC), "Core & Abs (50%)") // Legend item for Core
                        LegendItem(Color(0xFF26A69A), "Upper Body (28%)") // Legend item for Upper Body
                        LegendItem(Color(0xFFFFA726), "Lower Body (22%)") // Legend item for Lower Body
                    }
                }
            }
        }
    }
}

/**
 * Reusable helper composable function for rendering individual legend color dots and accompanying labels.
 */
@Composable
fun LegendItem(color: Color, text: String) {
    // Row container aligning color dot and text horizontally
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Circular color indicator dot
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        // Horizontal spacing between dot and text
        Spacer(modifier = Modifier.width(6.dp))
        // Legend text description
        Text(text = text, fontSize = 11.sp, color = Color(0xFF1A1C1E), fontWeight = FontWeight.Medium)
    }
}