package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.util.AppCurrency
import com.example.ui.util.AppLanguage
import com.example.ui.util.AppStrings
import com.example.ui.util.Formatters
import com.example.ui.viewmodel.CategoryStat
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InteractivePieChart(
    stats: List<CategoryStat>,
    totalAmount: Double,
    title: String,
    selectedCategoryStat: CategoryStat?,
    onCategorySelected: (CategoryStat?) -> Unit,
    currency: AppCurrency = AppCurrency.RUB,
    language: AppLanguage = AppLanguage.RU,
    modifier: Modifier = Modifier
) {
    val animationProgress = remember { Animatable(0f) }
    val strings = AppStrings.get(language)

    LaunchedEffect(stats) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (stats.isEmpty() || totalAmount <= 0.0) {
            // Empty State
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(200.dp)) {
                    drawCircle(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        style = Stroke(width = 24.dp.toPx())
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = strings.noStatsData,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = strings.addOperationsForPeriod,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Interactive Donut Chart
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .testTag("pie_chart_container"),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .size(220.dp)
                        .pointerInput(stats) {
                            detectTapGestures { tapOffset ->
                                val center = Offset(size.width / 2f, size.height / 2f)
                                val dx = tapOffset.x - center.x
                                val dy = tapOffset.y - center.y
                                val distance = sqrt(dx * dx + dy * dy)
                                val outerRadius = size.width / 2f
                                val innerRadius = outerRadius - 40.dp.toPx()
                                val maxRadius = outerRadius * 1.15f

                                if (distance in innerRadius..maxRadius) {
                                    var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                    if (angle < 0) angle += 360f
                                    // Start angle in draw is -90 degrees (top)
                                    val normalizedAngle = (angle + 90f) % 360f

                                    var accumulatedAngle = 0f
                                    var clickedStat: CategoryStat? = null
                                    for (stat in stats) {
                                        val sweep = (stat.percentage / 100f) * 360f
                                        if (normalizedAngle in accumulatedAngle..(accumulatedAngle + sweep)) {
                                            clickedStat = stat
                                            break
                                        }
                                        accumulatedAngle += sweep
                                    }

                                    if (clickedStat != null) {
                                        if (selectedCategoryStat?.categoryId == clickedStat.categoryId) {
                                            onCategorySelected(null)
                                        } else {
                                            onCategorySelected(clickedStat)
                                        }
                                    }
                                } else if (distance < innerRadius) {
                                    // Tapped center: clear selection
                                    onCategorySelected(null)
                                }
                            }
                        }
                ) {
                    val strokeWidth = 36.dp.toPx()
                    val selectedStrokeWidth = 44.dp.toPx()
                    val diameter = size.minDimension - selectedStrokeWidth
                    val topLeft = Offset(
                        (size.width - diameter) / 2f,
                        (size.height - diameter) / 2f
                    )
                    val arcSize = Size(diameter, diameter)

                    var startAngle = -90f

                    stats.forEach { stat ->
                        val sweepAngle = (stat.percentage / 100f) * 360f * animationProgress.value
                        val isSelected = selectedCategoryStat?.categoryId == stat.categoryId
                        val color = Color(stat.color)

                        if (isSelected) {
                            // Draw expanded pop-out slice
                            val midAngleRad = Math.toRadians((startAngle + sweepAngle / 2.0).toDouble())
                            val offsetDist = 6.dp.toPx()
                            val sliceOffset = Offset(
                                x = (cos(midAngleRad) * offsetDist).toFloat(),
                                y = (sin(midAngleRad) * offsetDist).toFloat()
                            )

                            drawArc(
                                color = color,
                                startAngle = startAngle + 1.5f,
                                sweepAngle = (sweepAngle - 3f).coerceAtLeast(0.1f),
                                useCenter = false,
                                topLeft = topLeft + sliceOffset,
                                size = arcSize,
                                style = Stroke(width = selectedStrokeWidth, cap = StrokeCap.Round)
                            )
                        } else {
                            val sliceAlpha = if (selectedCategoryStat != null) 0.45f else 1.0f
                            drawArc(
                                color = color.copy(alpha = sliceAlpha),
                                startAngle = startAngle + 1f,
                                sweepAngle = (sweepAngle - 2f).coerceAtLeast(0.1f),
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }

                        startAngle += sweepAngle
                    }
                }

                // Center Label & Total
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onCategorySelected(null)
                        }
                ) {
                    if (selectedCategoryStat != null) {
                        val translatedCatName = AppStrings.translateCategory(selectedCategoryStat.categoryName, language)
                        Text(
                            text = translatedCatName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = String.format("%.1f%%", selectedCategoryStat.percentage),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(selectedCategoryStat.color)
                        )
                        Text(
                            text = Formatters.formatCurrency(
                                amount = selectedCategoryStat.totalAmount,
                                currency = currency,
                                language = language
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = Formatters.formatCurrency(
                                amount = totalAmount,
                                currency = currency,
                                language = language
                            ),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = strings.tapForDetails,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Badges / Tags Legend
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                stats.forEach { stat ->
                    val isSelected = selectedCategoryStat?.categoryId == stat.categoryId
                    val catName = AppStrings.translateCategory(stat.categoryName, language)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) {
                            Color(stat.color).copy(alpha = 0.2f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        },
                        border = if (isSelected) {
                            BorderStroke(1.5.dp, Color(stat.color))
                        } else null,
                        modifier = Modifier
                            .clickable {
                                if (isSelected) onCategorySelected(null) else onCategorySelected(stat)
                            }
                            .testTag("legend_item_${stat.categoryId}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(
                                        color = Color(stat.color),
                                        shape = CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = catName,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format("%.0f%%", stat.percentage),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(stat.color)
                            )
                        }
                    }
                }
            }
        }
    }
}
