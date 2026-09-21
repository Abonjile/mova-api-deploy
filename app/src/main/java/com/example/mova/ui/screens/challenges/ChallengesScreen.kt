package com.example.mova.ui.screens.challenges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Composable function representing the Challenges screen.
 * Allows users to view and manage active fitness programs, track progress, and view target muscle groups.
njd*/
@Composable
fun ChallengesScreen() {
    // Outer vertical scroll container for the entire screen content
    Column(
        modifier = Modifier // Modifiers for layout styling
            .fillMaxSize() // Fill the entire available screen space
            .background(Color(0xFFF8F9FA)) // Light background color for the screen
            .verticalScroll(rememberScrollState()) // Enable scrolling for overflow content
            .padding(16.dp) // Apply padding around all screen contents
    ) {
        // Row container for the top app bar elements (Title and Brand Logo)
        Row(
            modifier = Modifier.fillMaxWidth(), // Span the full width of the screen
            horizontalArrangement = Arrangement.SpaceBetween, // Space title and logo apart
            verticalAlignment = Alignment.CenterVertically // Center items vertically in the row
        ) {
            // Text composable displaying the screen title "Challenges"
            Text(
                text = "Challenges", // String value for the header
                fontSize = 24.sp, // Large font size for prominent heading
                fontWeight = FontWeight.Bold, // Bold text weight
                color = Color(0xFF1A237E) // Dark primary text color
            )
            // Row container for the MOVA brand indicator dots and logo text
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Black circle dot for branding design
                Box(modifier = Modifier.size(8.dp).background(Color.Black, CircleShape))
                // Horizontal spacing between the dots
                Spacer(modifier = Modifier.width(4.dp))
                // Red circle dot for branding design
                Box(modifier = Modifier.size(8.dp).background(Color.Red, CircleShape))
                // Horizontal spacing before the brand name text
                Spacer(modifier = Modifier.width(6.dp))
                // Brand name text "MOVA"
                Text(text = "MOVA", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A237E))
            }
        }

        // Vertical spacing below the top app bar
        Spacer(modifier = Modifier.height(20.dp))

        // Section header text for Target Muscle Groups
        Text(
            text = "Target Muscle Groups", // Header text
            fontWeight = FontWeight.Bold, // Bold font weight
            fontSize = 15.sp, // Medium font size
            color = Color(0xFF1A237E) // Dark text color
        )
        // Vertical spacing below the header
        Spacer(modifier = Modifier.height(10.dp))

        // Card container displaying target muscle group summary (e.g., Legs)
        Card(
            modifier = Modifier.width(140.dp), // Fixed width for a compact card look
            colors = CardDefaults.cardColors(containerColor = Color.White), // White background for the card
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp) // Subtle shadow elevation
        ) {
            // Inner column container for card contents
            Column(
                modifier = Modifier.padding(12.dp) // Internal padding inside the card
            ) {
                // Background container for the muscle icon
                Box(
                    modifier = Modifier
                        .size(36.dp) // Fixed square size for icon container
                        .background(Color(0xFFFFF3E0), RoundedCornerShape(8.dp)), // Light orange rounded background
                    contentAlignment = Alignment.Center // Center the icon inside the box
                ) {
                    // Emoji representing leg workouts
                    Text(text = "🏃", fontSize = 18.sp)
                }
                // Vertical spacing below the icon
                Spacer(modifier = Modifier.height(8.dp))
                // Text showing the muscle group name
                Text(text = "Legs", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1A1C1E))
                // Text showing workout count for the muscle group
                Text(text = "10 Workouts", fontSize = 11.sp, color = Color.Gray)
            }
        }

        // Vertical spacing between sections
        Spacer(modifier = Modifier.height(24.dp))
        // Section header for Active Programs djfb
        Text(
            text = "Active Programs", // Header text
            fontWeight = FontWeight.Bold, // Bold font weight
            fontSize = 16.sp, // Medium-large font size
            color = Color(0xFF1A237E) // Dark text color
        )
        // Vertical spacing below section header
        Spacer(modifier = Modifier.height(12.dp))

        // Reusable card composable call for the first active program (7-Day Jumpstart)
        ActiveProgramCard(
            title = "7-Day Jumpstart", // Program title
            subtitle = "Build consistency", // Program description subtitle
            progressText = "6/7 days", // Current progress text in days
            progressValue = 6f / 7f, // Numerical progress ratio for progress bar
            percentageString = "85%" // Completion percentage string
        )

        // Vertical spacing between cards
        Spacer(modifier = Modifier.height(12.dp))

        // Reusable card composable call for the second active program (14-Day Shred)
        ActiveProgramCard(
            title = "14-Day Shred", // Program title
            subtitle = "High-intensity fat burn", // Program description subtitle
            progressText = "6/14 days", // Current progress text in days
            progressValue = 6f / 14f, // Numerical progress ratio for progress bar
            percentageString = "42%" // Completion percentage string
        )

        // Vertical spacing between cards
        Spacer(modifier = Modifier.height(12.dp))

        // Reusable card composable call for the third active program (30-Day Endurance)
        ActiveProgramCard(
            title = "30-Day Endurance", // Program title
            subtitle = "Ultimate body shaping", // Program description subtitle
            progressText = "0/30 days", // Current progress text in days
            progressValue = 0f / 30f, // Numerical progress ratio for progress bar
            percentageString = "0%" // Completion percentage string
        )
    }
}

/**
 * Reusable composable function for displaying an individual active fitness challenge program card.
 */
@Composable
fun ActiveProgramCard(
    title: String, // Title parameter for the program
    subtitle: String, // Subtitle/description parameter for the program
    progressText: String, // Text showing day-based progress (e.g., 6/7 days)
    progressValue: Float, // Float ratio between 0.0 and 1.0 for the progress bar
    percentageString: String // String representation of overall percentage completed
) {
    // Card container for the active program item
    Card(
        modifier = Modifier.fillMaxWidth(), // Fill maximum available width
        colors = CardDefaults.cardColors(containerColor = Color.White), // White background color
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp) // Subtle elevation shadow
    ) {
        // Inner column layout for card content elements
        Column(
            modifier = Modifier
                .fillMaxWidth() // Span full width of card
                .padding(16.dp) // Internal padding for content spacing
        ) {
            // Top row inside card holding program title and day progress text
            Row(
                modifier = Modifier.fillMaxWidth(), // Span full width
                horizontalArrangement = Arrangement.SpaceBetween, // Space title and progress apart
                verticalAlignment = Alignment.CenterVertically // Center align vertically
            ) {
                // Text showing the main program title
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1A1C1E)
                )
                // Text showing current progress days (e.g., 6/7 days)
                Text(
                    text = progressText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFFD9820B)
                )
            }
            // Vertical spacing below title row
            Spacer(modifier = Modifier.height(2.dp))
            // Text showing program subtitle/description
            Text(text = subtitle, fontSize = 11.sp, color = Color.Gray)
            // Vertical spacing before progress bar
            Spacer(modifier = Modifier.height(12.dp))
            // Linear progress indicator representing challenge completion ratio
            LinearProgressIndicator(
                progress = progressValue, // Lambda providing progress ratio
                modifier = Modifier
                    .fillMaxWidth() // Fill full card width
                    .height(6.dp) // Height thickness of progress bar
                    .clip(RoundedCornerShape(3.dp)), // Rounded corner clipping for smooth look
                color = Color(0xFFD9820B), // Amber color for filled progress portion
                trackColor = Color(0xFFF1F3F5) // Light grey background track color
            )
            // Vertical spacing below progress bar
            Spacer(modifier = Modifier.height(8.dp))
            // Bottom row showing overall progress label and percentage completed
            Row(
                modifier = Modifier.fillMaxWidth(), // Span full width
                horizontalArrangement = Arrangement.SpaceBetween // Space items to opposite ends
            ) {
                // Label text for overall progress
                Text(text = "Overall progress", fontSize = 11.sp, color = Color.Gray)
                // Text showing exact completion percentage
                Text(
                    text = percentageString,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E)
                )
            }
        }
    }
}