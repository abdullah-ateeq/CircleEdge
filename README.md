# 🔵 CircleEdge

**CircleEdge** is a modern, high-precision Android geometry calculator application designed to perform instant circle calculations with step-by-step mathematical breakdowns, visual diagrams, flexible unit conversions, and calculation history tracking.

Built entirely with **Kotlin**, **Jetpack Compose**, and **Material Design 3**, CircleEdge provides an intuitive, accessible experience for students, engineers, machinists, woodworkers, designers, and DIY enthusiasts.

---

## ✨ Features

- **⚡ Instant 4-Way Conversions**:
  Input any one property—**Radius**, **Diameter**, **Circumference**, or **Area**—and CircleEdge instantly computes the remaining three properties in real-time.

- **📐 Visual Circle Diagram**:
  Dynamic vector representation of the circle highlighting the selected input parameter and calculated metrics.

- **🧮 Mathematical Formula Breakdown**:
  Step-by-step execution breakdown showing exact formulas used ($\pi$, $r$, $d$, $C$, $A$), fostering learning and verification.

- **📏 Multi-Unit Support & Smart Conversions**:
  - Linear units: **Millimeters ($mm$)**, **Centimeters ($cm$)**, **Meters ($m$)**, **Inches ($in$)**, **Feet ($ft$)**.
  - Area units: **$mm^2$**, **$cm^2$**, **$m^2$**, **$sq\ in$**, **$sq\ ft$**.
  - Changing measurement units automatically converts input values seamlessly while maintaining exact physical dimensions.

- **🎯 Quick Presets**:
  One-tap presets for common circle radii ($1\ cm$, $5\ cm$, $10\ cm$, $25\ cm$, $50\ cm$, $100\ cm$).

- **💾 Project History & Favorites**:
  - Save calculations with custom project titles.
  - Search history by project name, input type, or unit.
  - Pin important calculations to **Favorites**.
  - Restore previous calculations directly back into the live calculator screen.
  - Duplicate or delete saved entries with ease.

- **📋 Share & Copy**:
  Copy full calculation summaries to the clipboard or share them via the Android system share sheet.

- **🎨 Theme Customization**:
  - Light Mode, Dark Mode, and System Default theme support.
  - Material 3 **Dynamic Color (Monet)** support on Android 12+.
  - Customizable default unit, input type, decimal precision (up to 10 decimal places), and haptic feedback toggles.

- **🔒 100% Offline & Private**:
  No network permissions required. All data remains securely on your device.

---

## 🏗️ Architecture & Tech Stack

CircleEdge follows **Modern Android Development (MAD)** practices and Clean Architecture principles:

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3 components & Material Icons Extended
- **Architecture Pattern**: MVVM (Model-View-ViewModel) + Clean Domain Layer
- **State Management**: Kotlin `StateFlow`, `SharedFlow`, and Coroutines
- **Navigation**: [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- **Local Persistence**: [Jetpack DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore) with `kotlinx.serialization`
- **Math Engine**: Arbitrary-precision `BigDecimal` arithmetic engine for exact geometric computations and zero float-point rounding errors.
- **Dependency Management**: Gradle Version Catalogs (`gradle/libs.versions.toml`)

---

## 📁 Project Structure

```text
com.example.circleedge/
├── data/
│   ├── local/          # DataStore preferences & serializer
│   └── model/          # App settings, units, circle input/result models
├── domain/
│   ├── format/         # Formatter for numbers and localized units
│   └── math/           # BigDecimal Math engine, unit converter & calculator
├── ui/
│   ├── components/     # Circle diagram, formula breakdown, result cards, selectors
│   ├── navigation/     # NavHost & screen definitions
│   ├── screens/
│   │   ├── about/      # About & app info screen
│   │   ├── calculator/ # Core calculator screen with real-time updates
│   │   ├── history/    # Searchable & filterable history list
│   │   ├── settings/   # User preferences & customization
│   │   └── splash/     # Animated splash screen
│   └── theme/          # Material 3 Color palette, typography & theme
└── MainActivity.kt     # App entry point with edge-to-edge layout
```

---

## 🛠️ Getting Started

### Prerequisites

- **Android Studio**: Ladybug (2024.2.1) or newer
- **JDK**: Version 11 or higher
- **Android SDK**: Target SDK 37, Minimum SDK 24 (Android 7.0+)

### Building and Running

1. **Clone the repository**:
   ```bash
   git clone https://github.com/abdullah-ateeq/CircleEdge.git
   cd CircleEdge
   ```

2. **Open in Android Studio**:
   Open Android Studio and choose **Open an Existing Project**, selecting the `CircleEdge` directory.

3. **Build the project**:
   Run the following command in the terminal or click **Build > Make Project** in Android Studio:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run unit tests**:
   ```bash
   ./gradlew test
   ```

---

## 📜 License

This project is open source and available under the [MIT License](LICENSE).
