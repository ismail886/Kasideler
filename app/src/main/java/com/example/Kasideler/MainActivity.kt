package com.example.kasideler

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kasideler.ui.theme.KasidelerTheme

data class Kaside(
    val id: Int,
    val baslik: String,
    val yazar: String,
    val ozet: String,
    val tamMetin: String,
    val renkler: List<Color>,
    val localRawRes: Int? = null
)

class MainActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KasidelerTheme {
                KasideApp()
            }
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun KasideApp() {
        val kasideler = getSampleKasideler()
        var seciliKaside by remember { mutableStateOf<Kaside?>(null) }
        var isPlaying by remember { mutableStateOf(false) }
        val context = LocalContext.current

        fun stopAudio() {
            try {
                mediaPlayer?.stop()
                mediaPlayer?.release()
            } catch (e: Exception) {}
            mediaPlayer = null
            isPlaying = false
        }

        fun playAudio(resId: Int) {
            stopAudio()
            try {
                mediaPlayer = MediaPlayer.create(context, resId)
                mediaPlayer?.let { mp ->
                    mp.start()
                    isPlaying = true
                    mp.setOnCompletionListener { isPlaying = false }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Ses çalınamadı. Dosyayı kontrol edin.", Toast.LENGTH_SHORT).show()
            }
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(seciliKaside?.baslik ?: "Kasideler", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        if (seciliKaside != null) {
                            // GERİ BUTONU - Büyütüldü ve Renklendirildi
                            IconButton(
                                onClick = { stopAudio(); seciliKaside = null },
                                modifier = Modifier.size(64.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Geri",
                                    modifier = Modifier.size(40.dp),
                                    tint = Color(0xFF1A237E) // Lacivert
                                )
                            }
                        }
                    },
                    actions = {
                        if (seciliKaside != null && seciliKaside!!.localRawRes != null) {
                            // OYNAT BUTONU - Büyütüldü ve Renklendirildi
                            IconButton(
                                onClick = {
                                    if (isPlaying) stopAudio() else playAudio(seciliKaside!!.localRawRes!!)
                                },
                                modifier = Modifier.size(64.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                                    contentDescription = "Dinle",
                                    modifier = Modifier.size(48.dp),
                                    tint = if (isPlaying) Color.Red else Color(0xFF2E7D32) // Çalarken Kırmızı, Dururken Yeşil
                                )
                            }
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                if (seciliKaside == null) {
                    KasideListesi(kasideler) { seciliKaside = it }
                } else {
                    KasideDetay(seciliKaside!!)
                    BackHandler { stopAudio(); seciliKaside = null }
                }
            }
        }
    }
}

fun getSampleKasideler() = listOf(
    Kaside(1, "Kaside Ziyafeti", "Meşhur Hocalar", "Seçme hocalarımızdan muazzam bir kaside derlemesi.", "Bu eserde birden fazla hocanın eşsiz yorumuyla klasikleşmiş kasideler yer almaktadır.", listOf(Color(0xFF1A237E), Color(0xFF3949AB)), R.raw.kaside_ziyafeti),
    Kaside(2, "İndiler Gökten Melekler", "İsmail Coşar", "Merhum İsmail Coşar hocamızın o meşhur ve yanık sesiyle.", "İndiler gökten melekler, nur ile doldu felekler...\n\nGönülleri coşturan o unutulmaz eser.", listOf(Color(0xFF004D40), Color(0xFF009688)), R.raw.ismail_cosar_indiler_gokden_melekler),
    Kaside(3, "Elveda (Müziksiz)", "Metin Kara", "Metin Kara'nın yorumuyla, saf sesin huzurunu hissedeceksiniz.", "Müziksiz, sadece sesin derinliği ile huzur veren bir kaside.", listOf(Color(0xFF3E2723), Color(0xFF5D4037)), R.raw.metin_kara_elveda),
    Kaside(4, "Mevlid-i Şerif (Mirac)", "Ahmet Uzunoğlu", "Ahmet Uzunoğlu hocadan Mevlid-i Şerif'in Mirac Bahri bölümü.", "Süleyman Çelebi hazretlerinin ölümsüz eserinin en manevi kısımlarından biri.", listOf(Color(0xFF2E7D32), Color(0xFF4CAF50)), R.raw.ahmet_uzunoglu_mevlid),
    Kaside(5, "Tevhid Bahri", "İsmail Bülbül", "İsmail Bülbül hocanın sesinden Mevlid-i Şerif.", "Allah adın zikr idelim evvelâ...\n\nGönüllere nur saçan o muazzam başlangıç.", listOf(Color(0xFFC62828), Color(0xFFE53935)), R.raw.ismail_bulbul_mevlid),
    Kaside(6, "Bâd-ı Sabâ", "Mevlid Özel", "Mevlid Kandili için hazırlanmış özel Bâd-ı Sabâ kasidesi.", "Bâd-ı sabâya sorsunlar, o yârin izini bulsunlar...\n\nÖzel kandil hediyesi.", listOf(Color(0xFF6A1B9A), Color(0xFF8E24AA)), R.raw.badi_saba),
    Kaside(7, "Kaside-i Bürde", "İmam Buseyri", "Şifa niyetine asırlardır okunan en meşhur kaside.", "Mevlâye salli ve sellim dâimen ebedâ...", listOf(Color(0xFF01579B), Color(0xFF0288D1))),
    Kaside(8, "Su Kasidesi", "Fuzûlî", "Edebiyatımızın Peygamber sevgisiyle yazılmış zirve eseri.", "Saçma ey göz eşkden gönlümdeki odlara su...", listOf(Color(0xFF006064), Color(0xFF0097A7))),
    Kaside(9, "Sakarya Türküsü", "Necip Fazıl", "Gençliğe hitaben yazılmış destansı bir dava şiiri.", "Yol onun, varlık onun, gerisi hep angarya...", listOf(Color(0xFF263238), Color(0xFF455A64))),
    Kaside(10, "Etme", "Mevlana", "Aşkın ve vuslatın diliyle yazılmış derinlikli bir eser.", "Duydum ki bizi bırakmaya azmediyorsun, etme...", listOf(Color(0xFFE65100), Color(0xFFF57C00)))
)

@Composable
fun KasideListesi(kasideler: List<Kaside>, onSelect: (Kaside) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("Kasideler", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(8.dp)) }
        items(kasideler) { kaside -> KasideKarti(kaside, onSelect) }
    }
}

@Composable
fun KasideKarti(kaside: Kaside, onClick: (Kaside) -> Unit) {
    ElevatedCard(modifier = Modifier.fillMaxWidth().wrapContentHeight().clickable { onClick(kaside) }, shape = RoundedCornerShape(24.dp)) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(kaside.renkler)).padding(20.dp)) {
            Column {
                Text(kaside.baslik, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(kaside.yazar, color = Color.White.copy(alpha = 0.9f), fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = kaside.ozet, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, maxLines = 2)
            }
        }
    }
}

@Composable
fun KasideDetay(kaside: Kaside) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(kaside.baslik, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(kaside.yazar, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.height(24.dp))
        Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp)) {
            Text(text = kaside.ozet, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Justify, modifier = Modifier.padding(16.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = kaside.tamMetin, style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 30.sp), textAlign = TextAlign.Center, fontFamily = FontFamily.Serif, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(48.dp))
    }
}
