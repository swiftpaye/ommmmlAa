package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppConfig
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.data.Lead
import com.example.network.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class SearchState {
    object Idle : SearchState()
    data class Searching(val step: String) : SearchState()
    object Success : SearchState()
    data class Error(val message: String) : SearchState()
}

class SaaSViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = AppRepository(db.appConfigDao(), db.leadDao(), RetrofitClient.moshiInstance)

    // UI State Observables
    val configState: StateFlow<AppConfig> = repository.configFlow
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppConfig()
        )

    val leadsState: StateFlow<List<Lead>> = repository.leadsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Navigator State
    private val _currentScreen = MutableStateFlow("Login") // "Login", "Register", "Dashboard"
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    private val _selectedTab = MutableStateFlow("Setup") // "Setup", "LeadFinder", "MetaIntegration", "AgentMonitor"
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    // Form inputs & Validation triggers
    var loginError = MutableStateFlow("")
    var registerError = MutableStateFlow("")

    // AI Lead Finder Progress States
    private val _searchState = MutableStateFlow<SearchState>(SearchState.Idle)
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    // Outreach progress states map: leadId -> Status String ("Normal", "Drafting", "Replying")
    private val _pitchGenerationMap = MutableStateFlow<Map<Int, String>>(emptyMap())
    val pitchGenerationMap: StateFlow<Map<Int, String>> = _pitchGenerationMap.asStateFlow()

    init {
        // Initialize config row if empty
        viewModelScope.launch {
            val current = db.appConfigDao().getConfig()
            if (current == null) {
                repository.saveConfig(AppConfig())
            } else if (current.isLoggedIn) {
                _currentScreen.value = "Dashboard"
            }
        }
    }

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    fun selectTab(tab: String) {
        _selectedTab.value = tab
    }

    // --- Authentication ---
    fun handleLogin(email: String, password: String) {
        loginError.value = ""
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginError.value = "الرجاء إدخال بريد إلكتروني صحيح وصالح"
            return
        }
        if (password.length < 6) {
            loginError.value = "كلمة المرور يجب أن تكون 6 خانات على الأقل"
            return
        }

        viewModelScope.launch {
            val current = configState.value
            repository.saveConfig(current.copy(userEmail = email, isLoggedIn = true))
            _currentScreen.value = "Dashboard"
        }
    }

    fun handleRegister(email: String, password: String, confirm: String) {
        registerError.value = ""
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            registerError.value = "الرجاء إدخال بريد إلكتروني صالح"
            return
        }
        if (password.length < 6) {
            registerError.value = "كلمة المرور يجب أن تتخطى 6 رموز"
            return
        }
        if (password != confirm) {
            registerError.value = "كلمتا المرور غير متطابقتين"
            return
        }

        viewModelScope.launch {
            val current = configState.value
            repository.saveConfig(current.copy(userEmail = email, isLoggedIn = true))
            _currentScreen.value = "Dashboard"
        }
    }

    fun handleLogout() {
        viewModelScope.launch {
            val current = configState.value
            repository.saveConfig(current.copy(isLoggedIn = false))
            repository.clearLeads()
            _currentScreen.value = "Login"
            _selectedTab.value = "Setup"
        }
    }

    // --- Service Configuration ---
    fun updateServiceSetup(desc: String, niche: String, size: String, count: Int) {
        viewModelScope.launch {
            val current = configState.value
            repository.saveConfig(
                current.copy(
                    serviceDescription = desc,
                    targetIndustry = niche,
                    targetSize = size,
                    targetCount = count
                )
            )
        }
    }

    // --- Integration Credentials ---
    fun updateIntegrationKeys(waKey: String, instaKey: String) {
        viewModelScope.launch {
            val current = configState.value
            repository.saveConfig(
                current.copy(
                    whatsappApiKey = waKey,
                    instagramApiKey = instaKey
                )
            )
        }
    }

    // --- Lead Finder ---
    fun triggerAISearch() {
        val config = configState.value
        if (config.serviceDescription.isBlank()) {
            _searchState.value = SearchState.Error("يرجى ملء مواصفات الخدمة أولاً في لوحة الإعداد")
            return
        }
        if (config.targetIndustry.isBlank()) {
            _searchState.value = SearchState.Error("يرجى إدخال القطاع المستهدف للشركات")
            return
        }

        viewModelScope.launch {
            _searchState.value = SearchState.Searching("جاري الاتصال بمحرك البحث الذكي B2B...")
            delay(1200)
            _searchState.value = SearchState.Searching("تحليل معايير التصفية: ${config.targetIndustry} / الحجم ${config.targetSize}...")
            delay(1000)
            _searchState.value = SearchState.Searching("البحث الفعلي في قواعد البيانات الجغرافية للمستهدفين...")
            delay(1200)
            _searchState.value = SearchState.Searching("استخراج الحسابات النشطة، أرقام الواتساب والإنستغرام المطابقة...")
            delay(1000)

            repository.clearLeads()
            
            val result = repository.searchAndGenerateLeads(
                industry = config.targetIndustry,
                size = config.targetSize,
                count = config.targetCount,
                serviceDescription = config.serviceDescription
            )

            result.fold(
                onSuccess = {
                    _searchState.value = SearchState.Success
                },
                onFailure = { err ->
                    _searchState.value = SearchState.Error(err.message ?: "حدث خطأ غير متوقع أثناء معالجة البحث")
                }
            )
        }
    }

    fun resetSearchState() {
        _searchState.value = SearchState.Idle
    }

    // --- Cold Outreach (AI Agent) ---
    fun generateOutreachPitchAndOutbox(lead: Lead) {
        val config = configState.value
        val leadId = lead.id
        
        // Update generation state map
        _pitchGenerationMap.value = _pitchGenerationMap.value + (leadId to "Drafting")

        viewModelScope.launch {
            // Update UI status inside Room also to reflect progress state
            val inProgressLead = lead.copy(outreachStatus = "تجهيز الرسالة")
            repository.updateLead(inProgressLead)

            val result = repository.generateSalesPitch(lead, config.serviceDescription)
            result.fold(
                onSuccess = { pitch ->
                    _pitchGenerationMap.value = _pitchGenerationMap.value + (leadId to "Idle")
                },
                onFailure = {
                    _pitchGenerationMap.value = _pitchGenerationMap.value + (leadId to "Idle")
                }
            )
        }
    }

    fun runSimulatedSalesConversation(lead: Lead) {
        if (lead.customPitchText.isBlank()) return
        val leadId = lead.id
        _pitchGenerationMap.value = _pitchGenerationMap.value + (leadId to "Replying")

        viewModelScope.launch {
            // Move through progress states representing cold outreach sending:
            val sendingLead = lead.copy(outreachStatus = "جاري الإرسال")
            repository.updateLead(sendingLead)
            delay(1500)
            
            val deliveredLead = sendingLead.copy(outreachStatus = "تم الإرسال / تفاعل")
            repository.updateLead(deliveredLead)
            delay(1200)

            // Dynamic Reply simulation via Gemini REST
            val result = repository.simulateOutreachResponse(deliveredLead, lead.customPitchText)
            
            _pitchGenerationMap.value = _pitchGenerationMap.value + (leadId to "Idle")
        }
    }
}

class SaaSViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SaaSViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SaaSViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
