# Android IDE Architecture Plan & Draft

## 1. Introduction
This document outlines the architecture and implementation plan for a simple yet advanced Android IDE designed to run on mobile devices. The application will support Java (8, 17, 21) and Kotlin, offering an advanced file manager, a robust code editor, and the capability to build APKs directly from the device.

## 2. Core Modules

### 2.1 File Manager (Gelişmiş Dosya Yöneticisi)
- **Features**:
  - Directory navigation and file operations (Create, Delete, Rename, Move, Copy).
  - Tree-view structure for project navigation.
  - Quick file search and recent files list.
- **Tech Stack**:
  - Kotlin + Coroutines for async I/O.
  - `RecyclerView` for smooth list rendering.

### 2.2 Code Editor (Kod Editörü)
- **Features**:
  - Syntax highlighting for Java, Kotlin, XML, and Gradle files.
  - Auto-completion suggestions and error checking (linters).
  - Multi-tab support.
- **Tech Stack**:
  - Custom implementations or libraries like `Rose`, `CodeView`, or `sora-editor`.

### 2.3 Build System (Derleme Sistemi)
- **Features**:
  - Gradle integration to allow parsing of `build.gradle` and `build.gradle.kts`.
  - Background task processing for builds.
  - Output generation of `.apk` and `.aab` (focus on APK).
- **Tech Stack**:
  - Requires leveraging local compilation toolchains (e.g., ECJ for Java compilation, AAPT2 for resources, d8 for dexing, and apksigner).
  - Advanced background workers for running these build processes without freezing the UI.

### 2.4 User Interface (UI Tasarımcısı)
- **Features**:
  - XML based layout creation.
  - Real-time preview if possible, or drag-and-drop elements.
- **Tech Stack**:
  - Standard Android views combined with custom view-rendering logic for previews.

## 3. Data Storage (Hafıza / Memory)
- Project files will be stored in the app's internal storage (`Context.getFilesDir()`) or external storage (`Context.getExternalFilesDir()`) for user accessibility.
- Preferences and project settings will use `SharedPreferences` or `DataStore`.

## 4. Next Steps
1. Finalize the integration of the file manager UI.
2. Embed the syntax-highlighting text editor.
3. Integrate the Android build toolchain for on-device APK building.
