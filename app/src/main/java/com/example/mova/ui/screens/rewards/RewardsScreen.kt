package com.example.mova.ui.screens.rewards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mova.R

@Composable
fun RewardsScreen(
    userId: String,
    viewModel: RewardsViewModel = viewModel()
) {
    val rewardData by viewModel.rewardData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(userId) {
        viewModel.fetchRewards(userId)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp)
    ) {
        item {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.rewards_title), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Black))
                    Spacer(modifier = Modifier.width(2.dp))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Red))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(R.string.app_name), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A237E))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Streak Banner Card
        item {
            val streak = rewardData?.streakDays ?: 0
            val consistency = rewardData?.consistencyLevel ?: 0
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE53935)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = stringResource(R.string.day_streak_format, streak), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = stringResource(R.string.consistency_level_format, consistency), fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Active Challenge Rewards Section
        item {
            Text(text = stringResource(R.string.active_challenge_rewards), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A237E))
            Spacer(modifier = Modifier.height(10.dp))
        }

        val claimables = rewardData?.claimableRewards ?: emptyList()
        if (claimables.isEmpty()) {
            item {
                Text(text = stringResource(R.string.no_claimable_rewards), fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
            }
        } else {
            items(claimables) { reward ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_red_medal),
                                contentDescription = reward.name,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = reward.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text(text = reward.description, fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                        Button(
                            onClick = { viewModel.claimReward(userId, reward.id) },
                            enabled = !reward.isClaimed,
                            colors = ButtonDefaults.buttonColors(containerColor = if (reward.isClaimed) Color.Gray else Color(0xFF00B0FF)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = if (reward.isClaimed) stringResource(R.string.claimed) else stringResource(R.string.claim),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        // Milestone Badges Section
        item {
            Text(text = stringResource(R.string.milestone_badges), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A237E))
            Spacer(modifier = Modifier.height(10.dp))
        }

        val badges = rewardData?.milestoneBadges ?: emptyList()
        if (badges.isEmpty()) {
            item {
                Text(text = stringResource(R.string.no_milestone_badges), fontSize = 12.sp, color = Color.Gray)
            }
        } else {
            items(badges) { badge ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_golden_trophy),
                            contentDescription = badge.name,
                            modifier = Modifier.size(36.dp).alpha(if (badge.isUnlocked) 1f else 0.4f)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = badge.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (badge.isUnlocked) MaterialTheme.colorScheme.onSurface else Color.Gray
                            )
                            Text(text = badge.description, fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }

        errorMessage?.let { error ->
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = error, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }
        }
    }
}