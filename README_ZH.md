# 🌿 吉卜力探險家 (JiburiAiAgent) - Studio Ghibli Film Guide

[English](README.md) | **繁體中文**

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-2026.02.01-green.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Ktor](https://img.shields.io/badge/Ktor-3.0.1-orange.svg?style=flat&logo=ktor)](https://ktor.io)
[![Room](https://img.shields.io/badge/Room-2.7.0-blue.svg?style=flat&logo=sqlite)](https://developer.android.com/training/data-storage/room)
[![Koin](https://img.shields.io/badge/Koin-4.0.0-purple.svg?style=flat)](https://insert-koin.io)

**吉卜力探險家 (JiburiAiAgent)** 是一款專為吉卜力工作室（Studio Ghibli）影迷打造的現代化 Android 應用程式。我們以宮崎駿、高畑勳等大師的經典動畫為靈感，結合現代化 Android 開發的最佳實踐與頂級的視覺動效美學，建構出流暢、優雅且極具沈浸感的吉卜力電影探索指南。

---

## 🎨 畫面展示 (Screenshots)

透過以下精心雕琢的介面，感受濃郁的吉卜力奇幻氛圍：

<p align="center">
  <img src="screenshots/MovieList.png" width="30%" alt="電影探索首頁" />
  <img src="screenshots/MovieSearch.png" width="30%" alt="電影搜尋" />
  <img src="screenshots/MovieWatchList.png" width="30%" alt="收藏清單" />
</p>
<p align="center">
  <img src="screenshots/MovieDetail.png" width="30%" alt="電影詳細資料" />
  <img src="screenshots/MovieDetailAddWatchList.png" width="30%" alt="加入收藏動態" />
</p>

---

## ✨ 核心特色 (Core Features)

- **🍂 沈浸式吉卜力美學與動效**
  - **煤炭球背景動畫 (Soot Sprites Background)**：全螢幕浮動的煤炭球與灰塵精靈動效，為介面注入靈動生命力。
  - **玻璃擬態底部導航 (Glassmorphism Bottom Navigation)**：現代化模糊半透明磨砂玻璃質感導航列，完美適應各種背景色彩變化。
  - **精選英雄電影 (Featured Hero Section)**：頂部專屬的經典《龍貓》英雄主視覺，配以高質感漸層。
  - **即將上映預告 (Coming Soon Card)**：專門為《蒼鷺與少年》設計的精緻期待卡片。

- **🔍 獨立強大的搜尋系統 (Search Feature)**
  - 獨立的 `SearchScreen` 核心，完全符合單一職責原則。
  - **導演篩選晶片 (Director Filter Chips)**：支援宮崎駿、高畑勳等多位傳奇導演的單選與多選快速篩選。
  - **防抖搜尋 (Debounced Search)**：自動對使用者輸入的關鍵字進行 debounce 處理，提供絲滑無延遲的即時搜尋體驗。

- **💾 單一真實來源資料流 (Room + Ktor SSOT)**
  - 當網路連線可用時，透過 **Ktor** 自動從 Studio Ghibli API 同步電影資訊。
  - 結合 **Room Database** 作為本機緩存與單一真實來源（Single Source of Truth, SSOT）。
  - 當無網路連線時，應用程式依然能流暢地離線運行。

- **🎬 細緻入微的電影詳情 (Detailed Movie Profile)**
  - 精確呈現上映年份、執行片長、爛番茄評分（Rotten Tomatoes Score）等核心數據。
  - **經典導演引言卡片 (Director Quote Card)**：呈現導演深具啟發性的金句。
  - **即時收藏列 (Watchlist Toggle)**：支援一鍵加入/移除個人收藏清單，具備流暢的 Snackbar 回饋。

---

## 🛠 關鍵技術棧 (Tech Stack)

吉卜力探險家採用了目前 Android 社群最推薦的現代化技術棧：

- **Jetpack Compose (2026.02.01)**：完全採用聲明式 UI（Declarative UI）建構，高度模組化且易於維護。
- **Ktor HTTP Client (3.0.1)**：高性能、跨平台的非同步網路連線庫，全面替代 Retrofit2 實現更現代的 API 請求與安全 Safe API Calls。
- **Room Database (2.7.0)**：本機 SQLite 物件對映庫，提供強健的離線緩存支援與 Flow 反應式數據同步。
- **Koin (4.0.0)**：輕量、強大且對 Kotlin 友善的依賴注入（Dependency Injection）框架，採用最先進的 `viewModelOf` 自動建構注入。
- **Kotlinx Coroutines & Serialization**：處理高併發非同步資料流，並以極速完成 JSON 序列化與反序列化。
- **Coil (2.6.0)**：基於協程的現代化 Android 圖片載入庫，支援優雅的漸現與緩存機制。
- **Type-Safe Compose Navigation**：使用 Kotlin 序列化（Serializable）安全定義路由物件，免除字串路徑的拼寫錯誤風險。

---

## 📐 架構設計 (Architecture)

本專案採用 **MVI (Model-View-Intent)** 架構，並嚴格遵循 **Package-by-Feature (按功能分包)** 結構，確保高度的擴充性、測試性與可讀性。

```
com.lihan.jiburiaiagent/
│
├── core/                         # 跨功能核心基礎模組
│   ├── data/                     # 全域資料庫定義、Ktor 客戶端設定
│   ├── di/                       # 全域依賴注入 (CoreModule)
│   ├── domain/                   # 基礎資料實體與操作定義
│   └── presentation/             # 全域主題 (JiburiTheme) 與基礎 UI 元件
│
├── explore/                      # 探索首頁功能 (Browse, Featured, Watchlist)
│   ├── data/                     # 網路與本機 Repository 實作
│   ├── di/                       # 模組 DI (ExploreModule)
│   ├── domain/                   # 電影領域層接口與模型
│   └── presentation/             # MVI 畫面 (MovieListState, Action, Event, ViewModel, Screen)
│
├── search/                       # 搜尋功能 (Director filter, Debounce input)
│   ├── di/                       # 模組 DI (SearchModule)
│   └── presentation/             # 獨立搜尋 MVI 元件 (SearchViewModel, SearchScreen)
│
└── detail/                       # 電影詳情功能 (Details, Watchlist toggler)
    ├── di/                       # 模組 DI (DetailModule)
    └── presentation/             # 詳情頁面 MVI 元件 (MovieDetailViewModel, MovieDetailScreen)
```

### MVI 資料流運作機制
專案中每個功能模組都精準切分為三個關鍵 MVI 元素：
1. **`State` (狀態)**：由 ViewModel 唯一暴露的唯讀狀態，UI 透過 `collectAsStateWithLifecycle()` 進行高效訂閱。
2. **`Action` (意圖/操作)**：使用者在畫面上觸發的具體意圖（例如：`OnFilterDirector`、`OnSearchQueryChange`）。
3. **`Event` (單次事件)**：不可重複的單次通知（例如：`ShowSnackbar`、`Navigate`），確保 UI 的副作用處理乾淨利落。

---

## 🚀 快速開始 (Getting Started)

### 系統要求
- Android Studio Ladybug (2024.2.1) 或更高版本
- JDK 17
- Android SDK 24+ (Android 7.0+)

### 安裝與運行
1. 複製此專案到您的本機環境：
   ```bash
   git clone https://github.com/chenlihan/JiburiAiAgent.git
   ```
2. 在 Android Studio 中開啟專案。
3. 等待 Gradle 同步完成。
4. 選擇您的 Android 模擬器或實體裝置，點擊 **Run** 即可享受唯美的吉卜力之旅！

---

## 📝 專案優化與維護歷程 (Development Journey)

- **移除冗餘領域層 (UseCase Layer Removal)**：簡化架構，將 MovieRepository 直接注入到 ViewModels，有效減少樣板代碼並提升執行效率。
- **獨立 SearchScreen 重構**：將原先嵌入在首頁的 `SearchTabLayout` 全面重構成為一個完全獨立、可重用的 `SearchScreen` 特色模組，配有專屬的 `SearchViewModel` 與 debounced reactive 搜尋管線。
- **全面升級至 Ktor HTTP 客戶端**：告別傳統 Retrofit2，採用 Ktor 的 CIO 引擎配置，並打造無縫的安全 Safe API Call 操作符以處理異常，大幅增加網路層的彈性。

---

*“在混亂的日常裡，讓吉卜力的魔法帶給你一絲溫暖。”* ✨
