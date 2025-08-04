# YouTube SMM Android App - Build Ko'rsatmalari

## Loyiha Tavsifi

Bu Android ilovasi YouTube kanallarini organik rivojlantirish uchun yaratilgan. Ilova 90,000 ta Google hisoblardan foydalanib, YouTube algoritmiga qarshi turish uchun maxsus texnologiyalar ishlatadi.

## APK Build Qilish

### 1. Docker orqali (Tavsiya etiladi)

```bash
# Docker va docker-compose o'rnatilgan bo'lishi kerak
./build.sh
```

### 2. GitHub Actions orqali

1. GitHub ga loyihani push qiling
2. Actions tab ga o'ting
3. "Build APK" workflow ni ishga tushiring
4. APK ni Artifacts dan yuklab oling

### 3. Mahalliy build (Android Studio kerak)

```bash
# Android Studio va Android SDK o'rnatilgan bo'lishi kerak
./gradlew assembleRelease
```

## Loyiha Strukturasi

```
youtube-smm-android/
├── app/
│   └── src/main/
│       ├── java/com/youtubesmm/app/
│       │   ├── data/
│       │   │   ├── database/          # Room Database
│       │   │   └── model/             # Data modellar
│       │   ├── service/               # Background services
│       │   ├── ui/                    # UI components
│       │   │   ├── home/              # Home fragment
│       │   │   ├── order/             # Order fragment
│       │   │   └── ...                # Boshqa fragmentlar
│       │   └── utils/                 # Utility classes
│       └── res/
│           ├── layout/                # Layout files
│           ├── drawable/              # Icons va images
│           ├── values/                # Strings, colors, themes
│           ├── menu/                  # Menu files
│           └── navigation/            # Navigation graph
├── gradle/                            # Gradle wrapper
├── .github/workflows/                 # GitHub Actions
├── Dockerfile                         # Docker build
├── docker-compose.yml                 # Docker compose
├── build.sh                          # Build script
└── README.md                         # Loyiha hujjati
```

## Asosiy Xususiyatlar

### 🎯 Maqsad
- YouTube kanallarini real Google hisoblar bilan organik rivojlantirish
- YouTube algoritmiga qarshi turish
- Avtomatlashtirilgan vazifalar bajarish

### 🔧 Texnik Xususiyatlar
- **Android 8+** qo'llab-quvvatlash
- **Kotlin va Java** da yozilgan
- **Room Database** - mahalliy ma'lumotlar bazasi
- **WebView** - YouTube bilan o'zaro aloqa
- **IP Rotatsiya** - samolyot rejimi orqali
- **User Agent Rotatsiya** - 50+ turli browser
- **Orqa fonda ishlash** - Foreground Service

### 📱 Ilova Navigatsiyalari

1. **Bosh Menu** - YouTube ichida kezish va holatni kuzatish
2. **Buyurtma Berish** - Yangi vazifalar yaratish
3. **Ishchi Menu** - Vazifalarni boshqarish
4. **Monitoring** - Jarayonni kuzatish
5. **Google Hisoblar** - Hisoblarni boshqarish
6. **Sozlamalar** - Ilova sozlamalari

### 🎬 YouTube Vazifalari

- **Ko'rishlar** - Video ko'rishlarini oshirish
- **Layklar** - Video layklarini oshirish
- **Kommentlar** - Video kommentlarini oshirish
- **Obunachilar** - Kanal obunachilarini oshirish
- **Shorts Ko'rishlar** - Shorts ko'rishlarini oshirish
- **Shorts Layklar** - Shorts layklarini oshirish
- **Live Stream Ishtirok** - Live streamda ishtirok etish

### 🔄 Ish Jarayoni

1. **Buyurtma olinadi** - URL va miqdor kiritiladi
2. **Hisoblar ajratiladi** - Buyurtma miqdori bo'yicha
3. **IP rotatsiya** - Samolyot rejimi yoqiladi/o'chiriladi
4. **YouTube ga kirish** - Google hisob bilan
5. **Vazifa bajariladi** - Buyurtma tavsifi bo'yicha
6. **Hisobdan chiqish** - Barcha izlar tozalash
7. **Navbatdagi hisob** - Keyingi hisobga o'tish

### 🛡️ Xavfsizlik

- **IP Rotatsiya** - Har bir hisob uchun alohida IP
- **User Agent Rotatsiya** - Har xil browser simulyatsiya
- **Kesh tozalash** - Barcha izlarni o'chirish
- **Hisob bloklash** - Xatolik yuz berganda
- **Vaqt kechikishlari** - Tabiiy harakatlar

## Foydalanish

### 1. Hisoblarni Yuklash
- `Google Hisoblar` sahifasiga o'ting
- TXT yoki JSON formatda hisoblarni yuklang
- Format: `email:password` yoki `{"email": "...", "password": "..."}`

### 2. Buyurtma Berish
- `Buyurtma Berish` sahifasiga o'ting
- YouTube URL kiriting
- Miqdor va xizmat turini tanlang
- `Buyurtma Berish` tugmasini bosing

### 3. Ishni Boshlash
- `Ishchi Menu` sahifasiga o'ting
- `Ishni Boshlash` tugmasini bosing
- Jarayon avtomatik boshlanadi

### 4. Monitoring
- `Monitoring` sahifasida jarayonni kuzating
- Progress va statistikalarni ko'ring

## Ruxsatlar

Ilova quyidagi ruxsatlarni talab qiladi:

- `INTERNET` - Internetga kirish
- `ACCESS_NETWORK_STATE` - Tarmoq holatini bilish
- `READ_EXTERNAL_STORAGE` - Fayllarni o'qish
- `WRITE_EXTERNAL_STORAGE` - Fayllarni yozish
- `MODIFY_PHONE_STATE` - Samolyot rejimini boshqarish
- `WAKE_LOCK` - Orqa fonda ishlash
- `FOREGROUND_SERVICE` - Foreground service
- `SYSTEM_ALERT_WINDOW` - System overlay

## Xavfsizlik Eslatmalari

⚠️ **Muhim**: Bu ilova faqat ta'lim va test maqsadlarida yaratilgan. Barcha risklar hisobga olingan.

- YouTube Terms of Service ga rioya qiling
- Faqat o'zingizning kontentingiz uchun ishlating
- Qonuniy chegaralarni buzmaslik
- Boshqalar huquqlarini hurmat qiling

## Yordam

Agar savollaringiz bo'lsa:
- Issue oching GitHub da
- Email: support@youtubesmm.com
- Telegram: @youtubesmm_support

---

**Eslatma**: Bu ilova faqat ta'lim maqsadlarida yaratilgan. Foydalanishda o'zingiz javobgarsiz.