package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSender
import com.example.data.model.GeminiModel
import com.example.data.model.ModelCategory
import com.example.ui.components.CyberThemeToggleButton
import com.example.ui.components.ThemeSelectorDialog
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberTheme
import com.example.ui.theme.SeverityCritical
import com.example.ui.viewmodel.ThreatFeedViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiChatbotScreen(
    viewModel: ThreatFeedViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val theme = CyberTheme.colors
    val themeMode by viewModel.currentThemeMode.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }

    val chatMessages by viewModel.chatMessages.collectAsState()
    val selectedModel by viewModel.selectedGeminiModel.collectAsState()
    val isLoading by viewModel.isChatLoading.collectAsState()
    val isSearchGrounded by viewModel.isSearchGroundingEnabled.collectAsState()

    var inputQuery by remember { mutableStateOf("") }
    var showModelSelectorSheet by remember { mutableStateOf(false) }

    // Uploaded Image State
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageBase64 by remember { mutableStateOf<String?>(null) }
    var selectedImageMimeType by remember { mutableStateOf("image/jpeg") }
    val coroutineScope = rememberCoroutineScope()

    // Android Photo Picker launcher (zero permission, Google Play compliant)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val contentResolver = context.contentResolver
                    val mime = contentResolver.getType(uri) ?: "image/jpeg"
                    selectedImageMimeType = mime

                    // 1. First pass: Measure dimensions
                    val boundsOptions = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    contentResolver.openInputStream(uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream, null, boundsOptions)
                    }

                    // 2. Calculate inSampleSize
                    var sampleSize = 1
                    val maxDim = 1024
                    while (boundsOptions.outWidth / sampleSize > maxDim || boundsOptions.outHeight / sampleSize > maxDim) {
                        sampleSize *= 2
                    }

                    // 3. Second pass: Decode downsampled bitmap
                    val decodeOptions = BitmapFactory.Options().apply {
                        inSampleSize = sampleSize
                    }
                    val bitmap = contentResolver.openInputStream(uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream, null, decodeOptions)
                    }

                    if (bitmap != null) {
                        val outputStream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                        val b64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
                        withContext(Dispatchers.Main) {
                            selectedImageBase64 = b64
                            Toast.makeText(context, "Image attached for Gemini analysis", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Throwable) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to load image safely: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    val listState = rememberLazyListState()

    BackHandler {
        onBack()
    }

    LaunchedEffect(chatMessages.size, isLoading) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    if (showThemeDialog) {
        ThemeSelectorDialog(
            currentMode = themeMode,
            onSelectMode = { viewModel.setThemeMode(it) },
            onDismiss = { showThemeDialog = false }
        )
    }

    if (showModelSelectorSheet) {
        GeminiModelSelectorBottomSheet(
            selectedModel = selectedModel,
            onSelectModel = { model ->
                viewModel.selectGeminiModel(model)
                showModelSelectorSheet = false
                Toast.makeText(context, "Switched model to ${model.displayName}", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showModelSelectorSheet = false }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = theme.bg,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = theme.accentPrimary.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, theme.accentPrimary.copy(alpha = 0.4f)),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Gemini",
                                    tint = theme.accentPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Gemini AI",
                                    color = theme.textPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = theme.accentPrimary.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "CYBER",
                                        color = theme.accentPrimary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            // Interactive Model Switcher Button
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = theme.surfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(0.6.dp, theme.border),
                                modifier = Modifier
                                    .clickable { showModelSelectorSheet = true }
                                    .testTag("chatbot_model_selector_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = selectedModel.displayName,
                                        color = theme.textSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Select Model",
                                        tint = theme.textSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("chatbot_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = theme.textPrimary
                        )
                    }
                },
                actions = {
                    // Reset / Clear Chat
                    IconButton(
                        onClick = {
                            viewModel.clearChat()
                            Toast.makeText(context, "Chat conversation cleared", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("chatbot_clear_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Clear Chat",
                            tint = theme.textSecondary
                        )
                    }

                    // Persistent Theme Toggle
                    CyberThemeToggleButton(
                        currentMode = themeMode,
                        onClick = { showThemeDialog = true }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = theme.surface)
            )
        },
        bottomBar = {
            // Chat Input & Model Indicator Bar
            Surface(
                color = theme.surface,
                tonalElevation = 6.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, theme.border)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    // Active model telemetry - COMPACT
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Model indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { showModelSelectorSheet = true }
                                .padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "Model: ${selectedModel.displayName}",
                                color = theme.accentPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        
                        // Compact Grounding Toggle
                        Text(
                            text = if (isSearchGrounded) "🌐 Grounded" else "Offline",
                            color = if (isSearchGrounded) theme.accentPrimary else theme.textMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { viewModel.toggleSearchGrounding() }
                                .testTag("toggle_search_grounding_button")
                        )
                    }

                    // Selected Image Preview Banner (if user picked an image)
                    AnimatedVisibility(visible = selectedImageUri != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = theme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, theme.accentPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(selectedImageUri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Attached Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Attached for Gemini Analysis",
                                        color = theme.accentPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Multimodal payload ready • Tap Send",
                                        color = theme.textMuted,
                                        fontSize = 10.sp
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        selectedImageUri = null
                                        selectedImageBase64 = null
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove attachment",
                                        tint = theme.textSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Input Field, Photo Upload Button & Send Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Image Picker Button
                        IconButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(theme.surfaceVariant)
                                .border(1.dp, if (selectedImageUri != null) theme.accentPrimary else theme.border, CircleShape)
                                .testTag("chatbot_attach_image_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Attach Image",
                                tint = if (selectedImageUri != null) theme.accentPrimary else theme.textSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        OutlinedTextField(
                            value = inputQuery,
                            onValueChange = { inputQuery = it },
                            placeholder = {
                                Text(
                                    text = if (selectedImageUri != null) "Ask about this image (e.g. Audit topology)..." else "Ask Gemini or attach diagram / architecture image...",
                                    color = theme.textMuted,
                                    fontSize = 12.sp
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chatbot_input_query"),
                            shape = RoundedCornerShape(22.dp),
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = theme.surfaceVariant,
                                unfocusedContainerColor = theme.surfaceVariant,
                                focusedBorderColor = theme.accentPrimary,
                                unfocusedBorderColor = theme.border,
                                focusedTextColor = theme.textPrimary,
                                unfocusedTextColor = theme.textPrimary,
                                cursorColor = theme.accentPrimary
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        val canSend = (inputQuery.isNotBlank() || selectedImageBase64 != null) && !isLoading

                        IconButton(
                            onClick = {
                                if (canSend) {
                                    val queryToSend = inputQuery
                                    val imageB64ToSend = selectedImageBase64
                                    val imageMimeToSend = selectedImageMimeType
                                    val imageUriToSend = selectedImageUri?.toString()

                                    inputQuery = ""
                                    selectedImageUri = null
                                    selectedImageBase64 = null

                                    viewModel.sendChatMessage(
                                        query = queryToSend,
                                        imageUri = imageUriToSend,
                                        imageBase64 = imageB64ToSend,
                                        imageMimeType = imageMimeToSend
                                    )
                                }
                            },
                            enabled = canSend,
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(
                                    if (canSend) Brush.horizontalGradient(
                                        listOf(theme.accentPrimary, theme.accentSecondary)
                                    ) else Brush.horizontalGradient(
                                        listOf(theme.surfaceVariant, theme.surfaceVariant)
                                    )
                                )
                                .testTag("chatbot_send_button")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = theme.accentPrimary,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send Query",
                                    tint = if (canSend) Color.Black else theme.textMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Quick Starter Prompts Chips Row
            QuickStarterPromptsRow(
                onSelectPrompt = { prompt ->
                    inputQuery = prompt
                    viewModel.sendChatMessage(prompt)
                }
            )

            // Message Stream
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 16.dp)
            ) {
                items(chatMessages, key = { it.id }) { message ->
                    ChatMessageBubble(
                        message = message,
                        onCopyText = { text ->
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Gemini AI", text))
                            Toast.makeText(context, "Copied response to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                if (isLoading) {
                    item {
                        ChatbotThinkingIndicator(model = selectedModel)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickStarterPromptsRow(
    onSelectPrompt: (String) -> Unit
) {
    val theme = CyberTheme.colors
    val prompts = listOf(
        "🌐 Search Active Zero-Days in CISA KEV Today",
        "🔍 Latest OpenSSH / XZ Backdoor Exploits",
        "🌐 Live Ransomware Campaign Indicators",
        "🎨 Generate Network Topology Diagram",
        "🛡️ Audit Zero-Trust Architecture",
        "⚡ Generate Sigma Rule for PowerShell",
        "🔍 Analyze CVE-2024-3094 Backdoor",
        "📜 Kubernetes Pod Security Standards",
        "⚔️ Explain MITRE T1059 Command & Scripting",
        "📊 Calculate CVSS 4.0 Vector"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        prompts.forEach { prompt ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = theme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(0.8.dp, theme.border),
                modifier = Modifier
                    .clickable { onSelectPrompt(prompt) }
                    .testTag("prompt_chip_${prompt.take(10)}")
            ) {
                Text(
                    text = prompt,
                    color = theme.textSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    onCopyText: (String) -> Unit
) {
    val context = LocalContext.current
    val theme = CyberTheme.colors
    val isUser = message.sender == ChatSender.USER
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // Gemini Icon Badge
            Surface(
                shape = CircleShape,
                color = theme.accentPrimary.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, theme.accentPrimary.copy(alpha = 0.5f)),
                modifier = Modifier
                    .size(32.dp)
                    .padding(top = 4.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Gemini",
                        tint = theme.accentPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth(if (isUser) 0.85f else 0.90f),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            // Header for assistant message showing which model was used
            if (!isUser) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 3.dp)
                ) {
                    Text(
                        text = message.modelUsed?.displayName ?: "Gemini AI",
                        color = theme.accentPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formattedTime,
                        color = theme.textMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                color = if (isUser) theme.surfaceVariant else theme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isUser) theme.accentPrimary.copy(alpha = 0.4f) else theme.border
                ),
                tonalElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Render user uploaded image if present
                    if (message.imageUri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(message.imageUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Uploaded User Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, theme.border, RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    } else if (isUser && message.imageBase64 != null) {
                        val decodedBytes = remember(message.imageBase64) {
                            try {
                                Base64.decode(message.imageBase64, Base64.DEFAULT)
                            } catch (e: Exception) {
                                null
                            }
                        }
                        val userBitmap = remember(decodedBytes) {
                            try {
                                decodedBytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
                            } catch (e: Throwable) {
                                null
                            }
                        }
                        if (userBitmap != null) {
                            Image(
                                bitmap = userBitmap.asImageBitmap(),
                                contentDescription = "User uploaded image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, theme.border, RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // Render Gemini returned/generated image if present
                    if (!isUser && message.imageBase64 != null) {
                        val decodedBytes = remember(message.imageBase64) {
                            try {
                                Base64.decode(message.imageBase64, Base64.DEFAULT)
                            } catch (e: Exception) {
                                null
                            }
                        }
                        val geminiBitmap = remember(decodedBytes) {
                            try {
                                decodedBytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
                            } catch (e: Throwable) {
                                null
                            }
                        }
                        if (geminiBitmap != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF070B14),
                                border = androidx.compose.foundation.BorderStroke(1.dp, theme.accentPrimary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(6.dp)) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "🎨 Gemini Generated Diagram / Image",
                                            color = theme.accentPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = theme.accentPrimary.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "VISION 1K",
                                                color = theme.accentPrimary,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Image(
                                        bitmap = geminiBitmap.asImageBitmap(),
                                        contentDescription = "Gemini Generated Image",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                    )
                                }
                            }
                        }
                    }

                    FormattedChatText(
                        text = message.text,
                        isUser = isUser
                    )

                    // Grounding Sources & Search Queries section
                    if (!isUser && (message.isSearchGrounded || message.groundingSources.isNotEmpty() || message.searchQueries.isNotEmpty())) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = theme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, theme.accentPrimary.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = "Google Search Grounding",
                                        tint = theme.accentPrimary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "Google Search Live Intelligence",
                                        color = theme.accentPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (message.searchQueries.isNotEmpty()) {
                                    Text(
                                        text = "Queries: " + message.searchQueries.joinToString(", "),
                                        color = theme.textMuted,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                }

                                if (message.groundingSources.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        message.groundingSources.take(5).forEach { source ->
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = theme.surfaceVariant,
                                                border = androidx.compose.foundation.BorderStroke(0.6.dp, theme.border),
                                                modifier = Modifier.clickable {
                                                    try {
                                                        val intent = android.content.Intent(
                                                            android.content.Intent.ACTION_VIEW,
                                                            android.net.Uri.parse(source.url)
                                                        ).apply {
                                                            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                                        }
                                                        context.startActivity(intent)
                                                    } catch (e: Exception) {
                                                        Toast.makeText(context, "Could not open link: ${e.message}", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.OpenInNew,
                                                        contentDescription = null,
                                                        tint = theme.accentSecondary,
                                                        modifier = Modifier.size(11.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = source.title.take(24) + if (source.title.length > 24) "…" else "",
                                                        color = theme.textSecondary,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isUser) {
                            Text(
                                text = formattedTime,
                                color = theme.textMuted,
                                fontSize = 10.sp
                            )
                        } else {
                            Text(
                                text = message.modelUsed?.badge ?: "Verified Intelligence",
                                color = theme.accentSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        IconButton(
                            onClick = { onCopyText(message.text) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy message",
                                tint = theme.textMuted,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = CircleShape,
                color = theme.accentSecondary.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, theme.accentSecondary.copy(alpha = 0.5f)),
                modifier = Modifier
                    .size(32.dp)
                    .padding(top = 4.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User",
                        tint = theme.accentSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FormattedChatText(
    text: String,
    isUser: Boolean
) {
    val theme = CyberTheme.colors
    val context = LocalContext.current

    if (isUser || !text.contains("```")) {
        Text(
            text = text,
            color = if (isUser) theme.textPrimary else theme.textPrimary,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
        return
    }

    // Parse and render formatted markdown sections with code boxes
    val segments = text.split("```")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        segments.forEachIndexed { index, segment ->
            if (index % 2 == 0) {
                // Regular markdown text
                if (segment.trim().isNotEmpty()) {
                    Text(
                        text = segment.trim(),
                        color = theme.textPrimary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            } else {
                // Code block
                val lines = segment.trim().lines()
                val lang = lines.firstOrNull()?.trim() ?: "code"
                val codeContent = if (lines.size > 1) lines.drop(1).joinToString("\n") else segment.trim()

                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF090D16)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, theme.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F172A))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lang.uppercase(),
                                color = theme.accentPrimary,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Code", codeContent))
                                    Toast.makeText(context, "Code copied", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy code",
                                    tint = theme.accentPrimary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                        Text(
                            text = codeContent,
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(10.dp),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatbotThinkingIndicator(model: GeminiModel) {
    val theme = CyberTheme.colors
    val transition = rememberInfiniteTransition(label = "pulse")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val quotaEvent by com.example.data.remote.GeminiModelDispatcher.lastQuotaExceededEvent.collectAsState()
    val showRetryMessage = quotaEvent != null && (System.currentTimeMillis() - (quotaEvent?.timestamp ?: 0) < 10000)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = theme.accentPrimary.copy(alpha = 0.15f),
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = theme.accentPrimary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = theme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, theme.border)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    color = theme.accentPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (showRetryMessage) "Quota limit hit. Retrying with ${model.displayName}..." else "${model.displayName} is reasoning...",
                        color = if (showRetryMessage) SeverityCritical else theme.accentPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.alpha(alpha)
                    )
                    if (showRetryMessage) {
                        Text(
                            text = "Applying exponential backoff to bypass rate limit",
                            color = theme.textMuted,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiModelSelectorBottomSheet(
    selectedModel: GeminiModel,
    onSelectModel: (GeminiModel) -> Unit,
    onDismiss: () -> Unit
) {
    val theme = CyberTheme.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedCategory by remember { mutableStateOf<ModelCategory?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = theme.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = theme.accentPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Select Gemini AI Model",
                            color = theme.textPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Choose from all available generative intelligence models",
                            color = theme.textSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Close",
                        tint = theme.textMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("All Models (${GeminiModel.entries.size})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = theme.accentPrimary.copy(alpha = 0.2f),
                        selectedLabelColor = theme.accentPrimary,
                        containerColor = theme.surfaceVariant,
                        labelColor = theme.textSecondary
                    )
                )

                ModelCategory.entries.forEach { category ->
                    val isSelected = selectedCategory == category
                    val count = GeminiModel.entries.count { it.category == category }
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = { Text("${category.iconEmoji} ${category.categoryName} ($count)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = theme.accentPrimary.copy(alpha = 0.2f),
                            selectedLabelColor = theme.accentPrimary,
                            containerColor = theme.surfaceVariant,
                            labelColor = theme.textSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val filteredModels = remember(selectedCategory) {
                if (selectedCategory == null) GeminiModel.entries
                else GeminiModel.entries.filter { it.category == selectedCategory }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredModels, key = { it.modelId }) { model ->
                    val isSelected = model == selectedModel

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectModel(model) }
                            .testTag("model_item_${model.modelId}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) theme.accentPrimary.copy(alpha = 0.12f) else theme.surfaceVariant
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) theme.accentPrimary else theme.border
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onSelectModel(model) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = theme.accentPrimary,
                                    unselectedColor = theme.textMuted
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = model.displayName,
                                        color = if (isSelected) theme.accentPrimary else theme.textPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = theme.accentSecondary.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = model.speed,
                                            color = theme.accentSecondary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = model.modelId,
                                    color = theme.textMuted,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = model.description,
                                    color = theme.textSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = theme.surface
                                    ) {
                                        Text(
                                            text = "🏷️ ${model.badge}",
                                            color = theme.textMuted,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = theme.surface
                                    ) {
                                        Text(
                                            text = "🎯 ${model.contextInfo}",
                                            color = theme.textMuted,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
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
