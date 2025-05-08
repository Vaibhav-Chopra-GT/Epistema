# 📚 Epistema - Wiki Explorer App

Epistema is an advanced Wikipedia reader app developed as a Mobile Computing course project. It offers a wide range of features such as voice-based search, article recommendations, offline reading, article history, image recognition using AI, and text-to-speech capabilities — all wrapped in a smooth Jetpack Compose UI.

## 👥 Team Members

- **Paarth Goyal** (Roll No: 2022343)  
- **Tejus Madan** (Roll No: 2022540)  
- **Utkarsh Dhilliwal** (Roll No: 2022551)  
- **Vaibhav Chopra** (Roll No: 2022552)

---

## ✨ Features

### 🔍 Search
- Text search using Wikipedia's REST API
- 🎙️ **Voice search** with speech-to-text integration via microphone intent

### 📰 Read Articles
- Full article display with proper text and image formatting
- **Text-to-speech** feature to read articles aloud for accessibility

### 🗂️ Article of the Day
- Displays the featured Wikipedia article on the home screen

### 🧠 Image Recognition (Lens Mode)
- Identify objects using a **locally embedded ResNet-50 model (TensorFlow Lite)**
- Snap or upload a photo → app classifies it → relevant Wikipedia article displayed
- Powered by **CameraX** and **TFLite**

### 🌍 Articles by Location
- With location permission, fetches nearby Wikipedia articles
- Uses device GPS (sensors) for geo-aware content discovery

### 📥 Offline Mode
- Download articles to read without internet
- Articles (text + images) stored using **Room database**
- View, manage or delete saved articles from within the app

### 🕘 History
- Automatically logs visited articles with their title and main image
- Revisit past reads easily

### ⚙️ Settings & Accessibility
- Change **language** (English, Hindi, Spanish, French)
- Toggle **dark/light themes**
- Adjust **font sizes** (small, medium, large)

---

## 🛠️ Tech Stack

- **Android Studio** (Kotlin, Jetpack Compose)
- **Retrofit** for HTTP requests to Wikipedia API
- **Room** for local storage
- **CameraX** for camera access
- **TensorFlow Lite (ResNet-50)** for image classification
- **Text-to-Speech** API for accessibility
- **Jetpack Navigation** + Intents for multi-screen routing

---

## ▶️ How to Run

### 📌 Prerequisites

- Android Studio (Electric Eel or newer)
- Android Emulator or physical Android device (API 29+ recommended)
- JDK 11 or higher
- Internet connection for first-time API testing
- Ensure `google-services.json` is added if Firebase is integrated

### 🚀 Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/Vaibhav-Chopra-GT/AP_Project.git
   cd AP_Project
Open in Android Studio

File → Open → Navigate to project directory

Build & Run

Click "Run" ▶️ or Shift+F10

Grant microphone, location, and storage permissions when prompted

Testing on Emulator/Device

Ensure emulator has Play Store support for voice features

For image recognition, testing on a real device is recommended due to camera integration

📂 Project Structure (Highlights)
bash
Copy
Edit
app/
├── activities/         # Each screen is an Activity (Home, Search, ReadArticle, Settings)
├── adapters/           # RecyclerView & list adapters
├── api/                # Retrofit interfaces and models
├── db/                 # Room entities and DAOs for offline storage
├── tflite/             # Image recognition model (resnet50.tflite)
├── ui/                 # Jetpack Compose UI components
├── utils/              # Helper classes, TTS, Location, Theme Utils
└── resources/          # Drawable, layouts, strings, themes
