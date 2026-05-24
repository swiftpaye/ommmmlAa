package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppConfig
import com.example.data.Lead
import com.example.ui.theme.*

@Composable
fun AppContent(viewModel: SaaSViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val configState by viewModel.configState.collectAsState()
    val leadsState by viewModel.leadsState.collectAsState()

    // Enforce RTL Layout Direction
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg)
                .drawBehind {
                    // Modern abstract glowing circles using radial gradients
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x2E6366F1), Color(0x00000000)),
                            center = Offset(size.width * 0.15f, size.height * 0.2f),
                            radius = size.width * 0.8f
                        )
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x2E8B5CF6), Color(0x00000000)),
                            center = Offset(size.width * 0.85f, size.height * 0.75f),
                            radius = size.width * 0.9f
                        )
                    )
                }
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ScreenTransition"
            ) { targetScreen ->
                when (targetScreen) {
                    "Login" -> AuthScreen(viewModel = viewModel, isSignUp = false)
                    "Register" -> AuthScreen(viewModel = viewModel, isSignUp = true)
                    "Dashboard" -> MainDashboard(viewModel = viewModel, config = configState, leads = leadsState)
                }
            }
        }
    }
}

// --- Glassmorphic Container Helper ---
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(GlassBg)
            .border(
                border = BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(24.dp),
        content = content
    )
}

// --- Authentication Screen ---
@Composable
fun AuthScreen(viewModel: SaaSViewModel, isSignUp: Boolean) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val errorText = if (isSignUp) {
        viewModel.registerError.collectAsState().value
    } else {
        viewModel.loginError.collectAsState().value
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
        ) {
            // Header / Brand
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.linearGradient(listOf(ElectricIndigo, GlowingPurple))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "مستخرج العملاء المحترفين AI",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "منصة SaaS الذكية لاستخراج قنوات التواصل B2B والماركتينج المؤتمت",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Text Inputs
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("البريد الإلكتروني للعمل", fontFamily = TajwalFontFamily) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = ElectricIndigo) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = ElectricIndigo,
                    unfocusedBorderColor = CardBorder,
                    focusedLabelColor = ElectricIndigo,
                    unfocusedLabelColor = TextSecondary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("كلمة المرور", fontFamily = TajwalFontFamily) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = ElectricIndigo) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = ElectricIndigo,
                    unfocusedBorderColor = CardBorder,
                    focusedLabelColor = ElectricIndigo,
                    unfocusedLabelColor = TextSecondary
                ),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            if (isSignUp) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("تأكيد كلمة المرور", fontFamily = TajwalFontFamily) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirm", tint = ElectricIndigo) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricIndigo,
                        unfocusedBorderColor = CardBorder,
                        focusedLabelColor = ElectricIndigo,
                        unfocusedLabelColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }

            if (errorText.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = "Error", tint = CoralWarning, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = errorText,
                        color = CoralWarning,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Primary Button
            Button(
                onClick = {
                    if (isSignUp) {
                        viewModel.handleRegister(email, password, confirmPassword)
                    } else {
                        viewModel.handleLogin(email, password)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = if (isSignUp) "إنشاء حساب جديد للشركة" else "تسجيل الدخول الآمن",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Toggle Auth Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isSignUp) "لديك حساب بالفعل؟ " else "ليس لديك حساب مسبق؟ ",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = if (isSignUp) "سجل دخولك" else "سجل حساب تجاري جديد",
                    color = CyanNeon,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clickable {
                            if (isSignUp) {
                                viewModel.navigateTo("Login")
                            } else {
                                viewModel.navigateTo("Register")
                            }
                        }
                        .padding(horizontal = 4.dp)
                )
            }
        }
    }
}

// --- Dashboard Layout Container ---
@Composable
fun MainDashboard(viewModel: SaaSViewModel, config: AppConfig, leads: List<Lead>) {
    val activeTab by viewModel.selectedTab.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // App Navigation & Header Bar
        DashboardHeader(config = config, onLogout = { viewModel.handleLogout() })

        // Pipeline Quick Statistics Row
        DashboardPipelineStats(leads = leads)

        // Custom Ribbon Tab Header
        ScrollableTabRow(
            selectedTabIndex = when (activeTab) {
                "Setup" -> 0
                "LeadFinder" -> 1
                "MetaIntegration" -> 2
                "AgentMonitor" -> 3
                else -> 0
            },
            containerColor = Color.Transparent,
            contentColor = CyanNeon,
            edgePadding = 16.dp,
            divider = {}
        ) {
            Tab(
                selected = activeTab == "Setup",
                onClick = { viewModel.selectTab("Setup") },
                text = { Text("إعداد العرض والمواصفات", fontWeight = FontWeight.Bold, fontFamily = TajwalFontFamily) },
                icon = { Icon(Icons.Default.Build, contentDescription = "Setup", modifier = Modifier.size(20.dp)) },
                selectedContentColor = CyanNeon,
                unselectedContentColor = TextSecondary
            )
            Tab(
                selected = activeTab == "LeadFinder",
                onClick = { viewModel.selectTab("LeadFinder") },
                text = { Text("محرك التصفية والاستخراج AI", fontWeight = FontWeight.Bold, fontFamily = TajwalFontFamily) },
                icon = { Icon(Icons.Default.Search, contentDescription = "Finder", modifier = Modifier.size(20.dp)) },
                selectedContentColor = CyanNeon,
                unselectedContentColor = TextSecondary
            )
            Tab(
                selected = activeTab == "MetaIntegration",
                onClick = { viewModel.selectTab("MetaIntegration") },
                text = { Text("الربط والتوثيق Meta", fontWeight = FontWeight.Bold, fontFamily = TajwalFontFamily) },
                icon = { Icon(Icons.Default.Settings, contentDescription = "Integration", modifier = Modifier.size(20.dp)) },
                selectedContentColor = CyanNeon,
                unselectedContentColor = TextSecondary
            )
            Tab(
                selected = activeTab == "AgentMonitor",
                onClick = { viewModel.selectTab("AgentMonitor") },
                text = { Text("وكيل المبيعات والتواصل الآلي", fontWeight = FontWeight.Bold, fontFamily = TajwalFontFamily) },
                icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Agent", modifier = Modifier.size(20.dp)) },
                selectedContentColor = CyanNeon,
                unselectedContentColor = TextSecondary
            )
        }

        HorizontalDivider(color = CardBorder, thickness = 1.dp)

        // Central Layout Selector
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    slideInVertically { it } + fadeIn() togetherWith slideOutVertically { -it } + fadeOut()
                },
                label = "TabContentTransition"
            ) { targetTab ->
                when (targetTab) {
                    "Setup" -> SetupTab(viewModel = viewModel, config = config)
                    "LeadFinder" -> LeadFinderTab(viewModel = viewModel, leads = leads)
                    "MetaIntegration" -> MetaIntegrationTab(viewModel = viewModel, config = config)
                    "AgentMonitor" -> AgentMonitorTab(viewModel = viewModel, leads = leads)
                }
            }
        }
    }
}

// --- Header Component ---
@Composable
fun DashboardHeader(config: AppConfig, onLogout: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.linearGradient(listOf(ElectricIndigo, GlowingPurple))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Star, contentDescription = "Brand", tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("رواد المبيعات B2B", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(config.userEmail, style = MaterialTheme.typography.labelSmall, color = CyanNeon)
            }
        }

        IconButton(
            onClick = onLogout,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x11FFFFFF))
                .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = CoralWarning)
        }
    }
}

// --- Quick Statistics Row ---
@Composable
fun DashboardPipelineStats(leads: List<Lead>) {
    val totalCount = leads.size
    val contactedCount = leads.count { it.outreachStatus != "غير متصل" }
    val repliedCount = leads.count { it.outreachStatus == "تم الرد" }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            value = totalCount.toString(),
            label = "العملاء المستخرجين",
            color = ElectricIndigo
        )
        StatCard(
            modifier = Modifier.weight(1f),
            value = contactedCount.toString(),
            label = "تحت الإجراء",
            color = GlowingPurple
        )
        StatCard(
            modifier = Modifier.weight(1f),
            value = repliedCount.toString(),
            label = "تم الرد من العملاء",
            color = MintJade
        )
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, value: String, label: String, color: Color) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}

// --- Tab 1: Service Setup ---
@Composable
fun SetupTab(viewModel: SaaSViewModel, config: AppConfig) {
    var desc by remember { mutableStateOf(config.serviceDescription) }
    var industry by remember { mutableStateOf(config.targetIndustry) }
    var sizeIndex by remember { mutableStateOf(
        when (config.targetSize) {
            "1-9" -> 0
            "10-49" -> 1
            "50-249" -> 2
            "250+" -> 3
            else -> 1
        }
    ) }
    val sizes = listOf("1-9", "10-49", "50-249", "250+")
    var targetCount by remember { mutableStateOf(config.targetCount.toFloat()) }

    var savedSuccessfully by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "مواصفات أعمالك (إعداد عرض القيمة)",
                    style = MaterialTheme.typography.titleMedium,
                    color = CyanNeon,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "سيقوم الذكاء الاصطناعي بدراسة مواصفات خدمتك التفصيلية لصياغة مراسلات صريحة، واقعية وصادقة 100% دون كذب أو تزوير.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("الخدمة التي تقدمها بالتفصيل والأسعار (الحقيقية فقط)", fontFamily = TajwalFontFamily) },
                    placeholder = { Text("مثال: نقدم خدمات تطوير المتاجر الإلكترونية وربط بوابات الدفع بأسعار تبدأ من 5000 ريال، بمدة تنفيذ 30 يوم من تاريخ العقد...", fontFamily = TajwalFontFamily) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricIndigo,
                        unfocusedBorderColor = CardBorder,
                        focusedLabelColor = ElectricIndigo,
                        unfocusedLabelColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    maxLines = 6
                )
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "مواصفات الشركات المستهدفة (محرك التصفية)",
                    style = MaterialTheme.typography.titleMedium,
                    color = CyanNeon,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = industry,
                    onValueChange = { industry = it },
                    label = { Text("نوع القطاع المستهدف ومكانهم (مثال: عيادات التجميل في الرياض)", fontFamily = TajwalFontFamily) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricIndigo,
                        unfocusedBorderColor = CardBorder,
                        focusedLabelColor = ElectricIndigo,
                        unfocusedLabelColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("حجم الشركات (عدد الموظفين)", style = MaterialTheme.typography.labelLarge, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sizes.forEachIndexed { idx, label ->
                        val selected = sizeIndex == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) ElectricIndigo else SurfaceDark)
                                .border(1.dp, if (selected) CyanNeon else CardBorder, RoundedCornerShape(8.dp))
                                .clickable { sizeIndex = idx }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(label, color = if (selected) Color.White else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("عدد الشركات المطلوب البحث عنها: ${targetCount.toInt()}", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                }
                Slider(
                    value = targetCount,
                    onValueChange = { targetCount = it },
                    valueRange = 3f..15f,
                    steps = 12,
                    colors = SliderDefaults.colors(
                        thumbColor = CyanNeon,
                        activeTrackColor = ElectricIndigo,
                        inactiveTrackColor = SurfaceDark
                    )
                )
            }
        }

        item {
            Button(
                onClick = {
                    viewModel.updateServiceSetup(desc, industry, sizes[sizeIndex], targetCount.toInt())
                    savedSuccessfully = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = MintJade),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Save", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("حفظ وتحديث مواصفات العمل", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
            }

            if (savedSuccessfully) {
                Text(
                    text = "تم الحفظ بنجاح! جاهز الآن لاستخراج واستهداف العملاء.",
                    color = MintJade,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
            }
        }
    }
}

// --- Tab 2: Lead Finder ---
@Composable
fun LeadFinderTab(viewModel: SaaSViewModel, leads: List<Lead>) {
    val searchState by viewModel.searchState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "محرك البحث الفعلي في قواعد البيانات B2B",
                style = MaterialTheme.typography.titleMedium,
                color = CyanNeon,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "يقوم النظام بالبحث والتحقق المباشر من الشركات النشطة واستخراج بياناتها الصحيحة 100%.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            Button(
                onClick = { viewModel.triggerAISearch() },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("بدء البحث واستخراج العملاء بالذكاء الاصطناعي", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Handler
        when (val state = searchState) {
            is SearchState.Searching -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceDark)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = CyanNeon)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = state.step,
                            color = TextPrimary,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            is SearchState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x22EF4444))
                        .border(1.dp, CoralWarning, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = "Error", tint = CoralWarning)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(state.message, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            else -> {}
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Heading Results
        Text(
            text = "العملاء المحتملون الجاهزون للاستهداف (${leads.size})",
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (leads.isEmpty() && searchState !is SearchState.Searching) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceDark)
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                    Icon(Icons.Default.Info, contentDescription = "Empty", tint = TextSecondary, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "لا توجد شركات مستخرجة حالياً. حدد الفلتر واضغط على زر الاستخراج للأعلى للبدء.",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(leads) { lead ->
                    LeadResultCard(lead = lead)
                }
            }
        }
    }
}

@Composable
fun LeadResultCard(lead: Lead) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = lead.companyName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x336366F1))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = lead.size,
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanNeon
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "القطاع: ${lead.industry}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = CardBorder, thickness = 0.5.dp)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Send, contentDescription = "Insta", tint = GlowingPurple, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = lead.instagram, color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = "WA", tint = MintJade, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = lead.whatsapp, color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

// --- Tab 3: Meta Integration & Guide ---
@Composable
fun MetaIntegrationTab(viewModel: SaaSViewModel, config: AppConfig) {
    var waKey by remember { mutableStateOf(config.whatsappApiKey) }
    var instaKey by remember { mutableStateOf(config.instagramApiKey) }
    var showSavedMessage by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "مفاتيح ربط قنوات الاتصال برمجياً",
                    style = MaterialTheme.typography.titleMedium,
                    color = CyanNeon,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = waKey,
                    onValueChange = { waKey = it },
                    label = { Text("رمز وصول الواتساب المؤقت من ميثا (WhatsApp Access Token)", fontFamily = TajwalFontFamily) },
                    placeholder = { Text("يبدأ بـ EAAC...", fontFamily = TajwalFontFamily) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricIndigo,
                        unfocusedBorderColor = CardBorder,
                        focusedLabelColor = ElectricIndigo,
                        unfocusedLabelColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = instaKey,
                    onValueChange = { instaKey = it },
                    label = { Text("رمز وصول إنستقرام للمطورين (Instagram API Token)", fontFamily = TajwalFontFamily) },
                    placeholder = { Text("يبدأ بـ IGAQ...", fontFamily = TajwalFontFamily) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricIndigo,
                        unfocusedBorderColor = CardBorder,
                        focusedLabelColor = ElectricIndigo,
                        unfocusedLabelColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        viewModel.updateIntegrationKeys(waKey, instaKey)
                        showSavedMessage = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("حفظ وتأكيد رموز الربط", fontWeight = FontWeight.Bold, color = Color.White)
                }

                if (showSavedMessage) {
                    Text(
                        text = "تم ربط وتحديث الرموز برمجياً في النظام!",
                        color = MintJade,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    )
                }
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "الدليل الإرشادي: كيف تحصل على مفاتيح الربط من Meta؟",
                    style = MaterialTheme.typography.titleMedium,
                    color = CyanNeon,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                GuideStep(
                    stepNumber = "1",
                    title = "إنشاء حساب مطور على ميثا",
                    desc = "قم بالذهاب إلى موقع Meta for Developers (developers.facebook.com) وسجل دخولك بحساب فيسبوك، ثم قم بالترقية لحساب مطور."
                )

                GuideStep(
                    stepNumber = "2",
                    title = "إنشاء تطبيق جديد (Create App)",
                    desc = "اضغط على زر 'Create App'، ثم اختر نوع التطبيق 'Business' أو 'Other'، وقم بإكمال البيانات الأساسية لتطبيقك."
                )

                GuideStep(
                    stepNumber = "3",
                    title = "إضافة منتج WhatsApp إلى التطبيق",
                    desc = "من لوحة تحكم تطبيقك، ستجد قائمة المنتجات المتوفرة. ابحث عن 'WhatsApp' واضغط على زر إعداد 'Set Up'. سيقوم فيسبوك بإنشاء رقم هاتف تجريبي مؤقت لك لتجربة المراسلة."
                )

                GuideStep(
                    stepNumber = "4",
                    title = "توليد رمز الوصول السريع (API Access Token)",
                    desc = "في لوحة تحكم الواتساب الجانبية، اذهب إلى 'Getting Started'. ستجد هناك رمز وصول مؤقت لـ 24 ساعة (EAAC...)، انسخه وضعه في حقل إدخال النظام بالأعلى."
                )

                GuideStep(
                    stepNumber = "5",
                    title = "الحصول على الرموز الدائمة",
                    desc = "للحصول على توكن لا ينتهي، قم بإنشاء مستخدم نظام (System User) في لوحة تحكم Meta Business Suite وعيّنه كمسؤول لتوليد رمز دائم مدى الحياة."
                )
            }
        }
    }
}

@Composable
fun GuideStep(stepNumber: String, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(listOf(ElectricIndigo, GlowingPurple))),
            contentAlignment = Alignment.Center
        ) {
            Text(stepNumber, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(2.dp))
            Text(desc, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}

// --- Tab 4: AI Sales Agent Monitor ---
@Composable
fun AgentMonitorTab(viewModel: SaaSViewModel, leads: List<Lead>) {
    var selectedLeadForDialog by remember { mutableStateOf<Lead?>(null) }
    val pitchMap by viewModel.pitchGenerationMap.collectAsState()

    if (leads.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceDark)
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                Icon(Icons.Default.Info, contentDescription = "Empty", tint = TextSecondary, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "يرجى البحث أولاً واستخراج بعض العملاء لاستخدام وكيل المراسلة بمواصفات خدمتك الحقيقية.",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
        return
    }

    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        // Leads List Panel
        Column(modifier = Modifier.weight(1.1f)) {
            Text(
                "طابور مراسلة العملاء",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(leads) { lead ->
                    val statusInMap = pitchMap[lead.id] ?: "Idle"
                    OutreachControlCard(
                        lead = lead,
                        draftingState = statusInMap,
                        onGeneratePitch = { viewModel.generateOutreachPitchAndOutbox(lead) },
                        onSendOutreach = { viewModel.runSimulatedSalesConversation(lead) },
                        onViewConversation = { selectedLeadForDialog = lead }
                    )
                }
            }
        }

        // Live Chat Transcript Panel
        Column(modifier = Modifier.weight(0.9f)) {
            Text(
                "سجل محادثات الوكيل والمبيعات",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val activeLead = selectedLeadForDialog ?: leads.firstOrNull()
            
            if (activeLead != null) {
                // Fetch the newest live copy of the lead from database state
                val refreshedLead = leads.find { it.id == activeLead.id } ?: activeLead
                ChatTranscriptCard(lead = refreshedLead)
            }
        }
    }
}

@Composable
fun OutreachControlCard(
    lead: Lead,
    draftingState: String,
    onGeneratePitch: () -> Unit,
    onSendOutreach: () -> Unit,
    onViewConversation: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .clickable { onViewConversation() }
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(lead.companyName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                
                // Outreach status badge
                val badgeColor = when (lead.outreachStatus) {
                    "غير متصل" -> TextSecondary
                    "تجهيز الرسالة" -> GlowingPurple
                    "جاري الإرسال" -> ElectricIndigo
                    "تم الإرسال / تفاعل" -> CyanNeon
                    "تم الرد" -> MintJade
                    else -> TextSecondary
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(lead.outreachStatus, fontSize = 11.sp, color = badgeColor, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(lead.industry, fontSize = 12.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (lead.customPitchText.isBlank()) {
                    Button(
                        onClick = onGeneratePitch,
                        enabled = draftingState != "Drafting",
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        if (draftingState == "Drafting") {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                        } else {
                            Text("صياغة العرض الفعلي", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                } else {
                    Button(
                        onClick = onSendOutreach,
                        enabled = draftingState != "Replying",
                        colors = ButtonDefaults.buttonColors(containerColor = MintJade),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        if (draftingState == "Replying") {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                        } else {
                            Text("إرسال آلي ومتابعة", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                OutlinedButton(
                    onClick = onViewConversation,
                    border = BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("عرض الحوار", fontSize = 11.sp, color = TextPrimary)
                }
            }
        }
    }
}

@Composable
fun ChatTranscriptCard(lead: Lead) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceDark)
            .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0x286366F1)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Face", tint = ElectricIndigo, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(lead.companyName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    Text("مسار العمل النشط لبرمجة المبيعات", style = MaterialTheme.typography.labelSmall, color = CyanNeon)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Body Chat logs
            if (lead.customPitchText.isBlank()) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "اضغط على زر (صياغة العرض الفعلي) لتوليد وكتابة رسالة صادقة ومخصصة تناسب أعمال العميل.",
                        color = TextMuted,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (lead.chatHistory.isBlank()) {
                        item {
                            // Render pitch draft
                            ChatBubble(
                                isAgentSender = true,
                                name = "وكيل مبيعات رواد الأعمال (أنت)",
                                text = lead.customPitchText
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "العرض مسجل كمسودة وبانتظار الإرسال الفعلي للتفاعل.",
                                    color = GlowingPurple,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        // Render full threaded conversation by parsing the saved transcripts
                        val dialogues = lead.chatHistory.split("\n\n")
                        items(dialogues) { block ->
                            if (block.startsWith("👤") || block.startsWith("وكيل")) {
                                ChatBubble(
                                    isAgentSender = true,
                                    name = "وكيل المبيعات (أنت)",
                                    text = block.substringAfter(":\n")
                                )
                            } else {
                                ChatBubble(
                                    isAgentSender = false,
                                    name = lead.companyName,
                                    text = block.substringAfter(":\n")
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
fun ChatBubble(isAgentSender: Boolean, name: String, text: String) {
    val bubbleColor = if (isAgentSender) Color(0x1E6366F1) else Color(0x1F10B981)
    val borderStroke = if (isAgentSender) ElectricIndigo.copy(alpha = 0.3f) else MintJade.copy(alpha = 0.3f)
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isAgentSender) Alignment.Start else Alignment.End
    ) {
        Text(name, fontSize = 11.sp, color = if (isAgentSender) ElectricIndigo else MintJade, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(
                    topStart = 12.dp,
                    topEnd = 12.dp,
                    bottomStart = if (isAgentSender) 0.dp else 12.dp,
                    bottomEnd = if (isAgentSender) 12.dp else 0.dp
                ))
                .background(bubbleColor)
                .border(
                    width = 1.dp,
                    color = borderStroke,
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = if (isAgentSender) 0.dp else 12.dp,
                        bottomEnd = if (isAgentSender) 12.dp else 0.dp
                    )
                )
                .padding(12.dp)
        ) {
            Text(text.trim(), fontSize = 13.sp, color = TextPrimary, lineHeight = 18.sp)
        }
    }
}
