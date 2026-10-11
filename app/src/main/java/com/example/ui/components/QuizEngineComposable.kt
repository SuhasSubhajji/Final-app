package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.learning.Sc200PracticeQuestion
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberNavyDark
import com.example.ui.theme.CyberNavySurface
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTheme
import com.example.ui.theme.SeverityCritical
import kotlinx.coroutines.delay

@Composable
fun QuizEngineComposable(
    title: String,
    questions: List<Sc200PracticeQuestion>,
    onQuizCompleted: (score: Int, total: Int) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val theme = CyberTheme.colors

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val userAnswers = remember { mutableStateMapOf<Int, Int>() }
    var isQuizSubmitted by remember { mutableStateOf(false) }
    var isTimedMode by remember { mutableStateOf(false) }
    var timeRemainingSeconds by remember { mutableIntStateOf(300) } // 5 min timer

    // Timer coroutine
    LaunchedEffect(isTimedMode, isQuizSubmitted, timeRemainingSeconds) {
        if (isTimedMode && !isQuizSubmitted && timeRemainingSeconds > 0) {
            delay(1000L)
            timeRemainingSeconds -= 1
            if (timeRemainingSeconds == 0) {
                isQuizSubmitted = true
            }
        }
    }

    val currentQuestion = questions.getOrNull(currentQuestionIndex)

    Surface(
        color = theme.surface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with progress and timer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        color = CyberCyan,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                        color = theme.textMuted,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Timed Mode",
                        color = theme.textSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Switch(
                        checked = isTimedMode,
                        onCheckedChange = { isTimedMode = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberCyan,
                            checkedTrackColor = CyberCyan.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.testTag("switch_timed_mode")
                    )
                }
            }

            if (isTimedMode) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Timer",
                        tint = if (timeRemainingSeconds < 60) SeverityCritical else CyberEmerald,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = String.format("%02d:%02d remaining", timeRemainingSeconds / 60, timeRemainingSeconds % 60),
                        color = if (timeRemainingSeconds < 60) SeverityCritical else CyberEmerald,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { ((currentQuestionIndex + 1).toFloat() / questions.size.coerceAtLeast(1)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = CyberCyan,
                trackColor = theme.bg
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isQuizSubmitted) {
                // Quiz Results Summary
                val correctCount = questions.indices.count { idx ->
                    userAnswers[idx] == questions[idx].correctOptionIndex
                }
                val scorePercent = ((correctCount.toFloat() / questions.size.coerceAtLeast(1)) * 100).toInt()

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "🏆 Quiz Completed!",
                        color = CyberEmerald,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Score: $scorePercent% ($correctCount / ${questions.size} correct)",
                        color = theme.textPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Surface(
                        color = if (scorePercent >= 70) CyberEmerald.copy(alpha = 0.15f) else SeverityCritical.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (scorePercent >= 70) "PASSED • Great mastery of Defender XDR and Sentinel queries!" else "REVIEW REQUIRED • Revisit weak topics in SC-200 / SC-500 study modules.",
                            color = if (scorePercent >= 70) CyberEmerald else SeverityCritical,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                userAnswers.clear()
                                currentQuestionIndex = 0
                                isQuizSubmitted = false
                                timeRemainingSeconds = 300
                            },
                            modifier = Modifier.testTag("btn_retake_quiz")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retake",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Retake Quiz")
                        }
                    }
                }
            } else if (currentQuestion != null) {
                // Active Question View
                Text(
                    text = currentQuestion.question,
                    color = theme.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Answer Options
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currentQuestion.options.forEachIndexed { optIdx, optionText ->
                        val isSelected = userAnswers[currentQuestionIndex] == optIdx

                        Surface(
                            color = if (isSelected) CyberCyan.copy(alpha = 0.18f) else theme.bg,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) CyberCyan else CyberBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    userAnswers[currentQuestionIndex] = optIdx
                                }
                                .testTag("option_${currentQuestionIndex}_$optIdx")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = if (isSelected) CyberCyan else theme.surface,
                                    shape = CircleShape,
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = ('A' + optIdx).toString(),
                                            color = if (isSelected) Color.Black else theme.textSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = optionText,
                                    color = if (isSelected) CyberCyan else theme.textPrimary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Navigation & Submit Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            if (currentQuestionIndex > 0) currentQuestionIndex--
                        },
                        enabled = currentQuestionIndex > 0,
                        modifier = Modifier.testTag("btn_quiz_prev")
                    ) {
                        Text("Previous")
                    }

                    if (currentQuestionIndex < questions.size - 1) {
                        Button(
                            onClick = { currentQuestionIndex++ },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black),
                            modifier = Modifier.testTag("btn_quiz_next")
                        ) {
                            Text("Next Question")
                        }
                    } else {
                        Button(
                            onClick = {
                                isQuizSubmitted = true
                                val correctCount = questions.indices.count { idx ->
                                    userAnswers[idx] == questions[idx].correctOptionIndex
                                }
                                onQuizCompleted(correctCount, questions.size)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald, contentColor = Color.Black),
                            modifier = Modifier.testTag("btn_quiz_submit")
                        ) {
                            Text("Submit Quiz")
                        }
                    }
                }
            }
        }
    }
}
