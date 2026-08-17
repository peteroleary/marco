package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TripActivityEntity
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.OceanBlue
import com.example.ui.theme.SunsetCoral
import com.example.ui.theme.VenetianGold

/**
 * Expanded detail and edit view for itinerary items opened directly from in-chat cards.
 * Allows viewing all metadata and editing fields before returning seamlessly to chat.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItinerarySnippetDetailSheet(
    activity: TripActivityEntity,
    onDismiss: () -> Unit,
    onSave: (TripActivityEntity) -> Unit,
    onDelete: (Long) -> Unit = {},
    onCallVendor: (vendorName: String, question: String) -> Unit = { _, _ -> }
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedTitle by remember { mutableStateOf(activity.title) }
    var editedTimeSlot by remember { mutableStateOf(activity.timeSlot) }
    var editedLocation by remember { mutableStateOf(activity.location) }
    var editedNotes by remember { mutableStateOf(activity.notes) }
    var editedCost by remember { mutableStateOf(activity.cost.toString()) }
    var editedConfirmation by remember { mutableStateOf(activity.confirmationCode) }
    var editedAccessibility by remember { mutableStateOf(activity.accessibilityBadge) }
    var isCompleted by remember { mutableStateOf(activity.isCompleted) }
    var showSavedSnackbar by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        },
        modifier = Modifier.testTag("itinerary_snippet_detail_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Category Icon + Title + Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(OceanBlue, Color(0xFF0F172A)))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        val icon = when (activity.category.uppercase()) {
                            "FLIGHT" -> Icons.Default.FlightTakeoff
                            "TIMESHARE", "HOTEL", "VILLA" -> Icons.Default.Hotel
                            "ATTRACTION" -> Icons.Default.Attractions
                            "DINING", "RESTAURANT" -> Icons.Default.Restaurant
                            "ACTIVITY", "TOUR" -> Icons.Default.Hiking
                            else -> Icons.Default.EventNote
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = VenetianGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = OceanBlue.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "DAY ${activity.dayNumber} • ${activity.category}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.8.sp,
                                    color = OceanBlue
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Itinerary Segment Details",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("close_snippet_detail_sheet")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Content: View mode or Edit Mode
            if (!isEditing) {
                // View Mode
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = editedTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Time & Location
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = OceanBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = editedTimeSlot,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            )
                            Text("•", color = Color.Gray)
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = SunsetCoral,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = editedLocation,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        if (editedConfirmation.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                tonalElevation = 1.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ConfirmationNumber,
                                        contentDescription = null,
                                        tint = VenetianGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Confirmation: $editedConfirmation",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            letterSpacing = 0.5.sp
                                        )
                                    )
                                }
                            }
                        }

                        if (editedNotes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = editedNotes,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        if (editedAccessibility.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EmeraldGreen.copy(alpha = 0.12f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Accessible,
                                        contentDescription = null,
                                        tint = EmeraldGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = editedAccessibility,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = EmeraldGreen,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Cost & Status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (activity.cost > 0) "$${activity.cost.toInt()} ${activity.currency}" else "Included in Pass",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = OceanBlue
                                )
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                FilterChip(
                                    selected = isCompleted,
                                    onClick = {
                                        isCompleted = !isCompleted
                                        onSave(activity.copy(isCompleted = isCompleted))
                                    },
                                    label = {
                                        Text(
                                            text = if (isCompleted) "Completed" else "Mark Done",
                                            fontSize = 11.sp
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = if (isCompleted) EmeraldGreen else Color.Gray
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // Edit Form
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = editedTitle,
                            onValueChange = { editedTitle = it },
                            label = { Text("Activity Title", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = editedTimeSlot,
                                onValueChange = { editedTimeSlot = it },
                                label = { Text("Time Slot", fontSize = 12.sp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = editedCost,
                                onValueChange = { editedCost = it },
                                label = { Text("Cost ($)", fontSize = 12.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        OutlinedTextField(
                            value = editedLocation,
                            onValueChange = { editedLocation = it },
                            label = { Text("Location", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = editedConfirmation,
                            onValueChange = { editedConfirmation = it },
                            label = { Text("Confirmation Code / Voucher #", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = editedAccessibility,
                            onValueChange = { editedAccessibility = it },
                            label = { Text("Accessibility / Dietary Badges", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = editedNotes,
                            onValueChange = { editedNotes = it },
                            label = { Text("Expedition Notes & Hints", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!isEditing) {
                    OutlinedButton(
                        onClick = { isEditing = true },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("edit_snippet_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit Details", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    if (activity.vendorPhone.isNotBlank()) {
                        Button(
                            onClick = {
                                onCallVendor(
                                    activity.vendorName.ifBlank { activity.title },
                                    "Inquiry regarding reservation ${activity.confirmationCode.ifBlank { "for " + activity.title }}"
                                )
                                onDismiss()
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OceanBlue),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("call_vendor_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Call Desk", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            isEditing = false
                            // Revert
                            editedTitle = activity.title
                            editedTimeSlot = activity.timeSlot
                            editedLocation = activity.location
                            editedNotes = activity.notes
                            editedCost = activity.cost.toString()
                            editedConfirmation = activity.confirmationCode
                            editedAccessibility = activity.accessibilityBadge
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val parsedCost = editedCost.toDoubleOrNull() ?: activity.cost
                            val updated = activity.copy(
                                title = editedTitle,
                                timeSlot = editedTimeSlot,
                                location = editedLocation,
                                notes = editedNotes,
                                cost = parsedCost,
                                confirmationCode = editedConfirmation,
                                accessibilityBadge = editedAccessibility,
                                isCompleted = isCompleted
                            )
                            onSave(updated)
                            isEditing = false
                            showSavedSnackbar = true
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OceanBlue),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_snippet_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save & Update", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            AnimatedVisibility(visible = showSavedSnackbar) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = EmeraldGreen.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Itinerary changes saved and synced to expedition database!",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = EmeraldGreen,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Done & Return to Chat button
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("return_to_chat_button")
            ) {
                Text(
                    "← Return to Expedition Chat",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
