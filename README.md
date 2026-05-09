<div align="center">

# Noveo

**A high-fidelity Android shell and Kotlin port of the Noveo Messenger web version.**

[Features](#-features) • [Architecture](#-architecture) • [Themes](#-themes) • [Setup](#-setup)

---

Noveo brings the rich, themeable, and real-time experience of the original web platform to Android. <br/> Built with **Jetpack Compose** as a modern Kotlin implementation of the Noveo ecosystem.

</div>

## ⚡ Core Strengths

*   **Kotlin Port**: A faithful mobile implementation of the Noveo web experience, bringing platform parity to Android.
*   **Real-time Core**: Powered by WebSockets for instant sync. Stay connected with the same live account used on web, with full message and state parity.
*   **Modern Stack**: Built entirely with Jetpack Compose, Kotlin Coroutines, and Material 3 for a fluid, reactive experience.
*   **Modular Design**: Clean separation between UI, Network, and Storage modules for easy scaling.

## 🎨 Personalization

Noveo is built to be seen. It features 12+ handcrafted theme presets ported from the web version to change the entire personality of the app:

| Light | Dark | Special |
| :--- | :--- | :--- |
| Sky Light | Ocean Dark | Sunset Shimmer |
| Sunset Light | OLED Dark | Rainbow Ragebait |
| Snowy Daydream | Plum Dark | Sanoki Meoa |

## 🏗 Architecture

The project is structured into functional modules to maintain high cohesion and low coupling:

*   `app` — The Compose-based UI layer and ViewModels.
*   `core:network` — WebSocket management and API integration.
*   `core:datastore` — Secure session and preference storage.
*   `core:voice` — Voice communication contracts and parity logic.

## 🛠 Setup

1.  **Clone**: `git clone https://github.com/hienob/NoveoKotlin`
2.  **Open**: Use **Android Studio Koala (2024.1.1)** or newer.
3.  **Run**: Select the `app` configuration and deploy to an API 26+ device.

---

<div align="center">
Developed by <b>HienoB</b>
</div>
