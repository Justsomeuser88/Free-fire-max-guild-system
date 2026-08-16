# 🔥 FF Guild Master — Free Fire Guild & Esports Management App

[![Build & Release APK](https://github.com/your-username/ff-guild-master/actions/workflows/build-apk.yml/badge.svg)](https://github.com/your-username/ff-guild-master/actions/workflows/build-apk.yml)

A powerful, dark-fire themed Android application built with **Kotlin** and **Jetpack Compose** tailored specifically for Free Fire Guild Leaders, Officers, and Competitive Esports Lineups.

---

## 📲 How to Download APK Directly from GitHub

There are two easy ways to get the `.apk` file directly on your Android phone:

### Method 1: Download from GitHub Releases (Recommended)
1. Go to the **[Releases](https://github.com/your-username/ff-guild-master/releases)** section on the right side of this GitHub repository.
2. Click on the latest release tag (e.g. `v1.0.0`).
3. Under **Assets**, tap **`FF-Guild-Master.apk`** to download it directly.
4. On your Android phone, open the downloaded file and tap **Install** *(if prompted, enable "Install unknown apps" in Android Settings)*.

---

### Method 2: Download Latest Build Artifact from GitHub Actions
1. Navigate to the **[Actions tab](https://github.com/your-username/ff-guild-master/actions)** in this repository.
2. Click on the latest run under **Build & Release APK**.
3. Scroll down to the **Artifacts** section at the bottom.
4. Click on **`FF-Guild-Master-APK`** to download the zip containing the ready-to-install `.apk` file.

---

### Method 3: Trigger a Fresh APK Build (One-Click)
1. Go to **Actions** -> **Build & Release APK**.
2. Click **Run workflow** button.
3. Check *Publish APK to GitHub Releases* if you want it published as a release.
4. Wait 2-3 minutes; your new APK will be available in Releases and Artifacts!

---

## ✨ Features

- 🏆 **Live Guild Overview**: Track Level, Glory, Friday Dog Tag grind progress, and scrim win rates.
- 🐕 **Friday Dog Tag Tracker**: Milestone progress towards 1,800 tags for Room Cards, quick `+8`/`+16` increment buttons, and WhatsApp report exporter.
- ⚔️ **Guild Wars & Scrims**: Clash Squad (CS 4v4) and Battle Royale match scheduler, room password manager, score logging, and official rulebook presets.
- 🎯 **Esports Lineup Builder**: Squad Alpha, Squad Bravo, CS Dominators, combat roles (Rusher, Sniper, IGL, Support, Flanker), active character skills, and weapon combos.
- 👥 **Roster Management**: Full member stats (IGN, UID, Rank, Level, K/D, Headshot %), officer promotion/demotion, and activity status.
- 📋 **Recruitment Hub**: Join requirements card, trial applicants review, one-click roster acceptance, and recruitment poster generator.
- 🛠️ **Free Fire Utilities**: Esports stylized nickname generator (`亗`, `𝄟`, `☬`, `♛`), FFWS tournament points calculator, and guild treasury/giveaway history.

---

## 🛠️ Local Build Instructions

```bash
# Clone the repository
git clone https://github.com/your-username/ff-guild-master.git
cd ff-guild-master

# Build Debug APK
gradle assembleDebug

# The APK will be generated at:
# app/build/outputs/apk/debug/app-debug.apk
```

---

## 🛡️ Requirements
- Android 7.0+ (API level 24+)
- Minimum RAM: 2 GB
