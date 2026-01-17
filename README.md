# 粤语评书 - Cantonese Storyteller

A cross-platform application for creating personalized Cantonese audiobooks using AI voice synthesis. Record your voice to train a custom voice model, then generate audiobooks in your own voice.

## 🎯 Features

- **Voice Recording**: Record 30-second Cantonese voice samples
- **AI Voice Training**: Train custom voice models using TTS services
- **Chapter Management**: Upload and manage book chapters
- **Voice Synthesis**: Generate audiobooks in your personalized voice
- **Real-time Progress**: WebSocket-based progress updates
- **Offline Support**: Local caching with SQLite
- **Cross-platform**: Windows, macOS, Linux support via Tauri

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Frontend (Tauri + React)                  │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  Rust Backend (Audio Processing + Local Cache)        │ │
│  │  - Audio recording/playback                           │ │
│  │  - File system management                             │ │
│  │  - SQLite local caching                               │ │
│  │  - Tauri IPC commands                                 │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ HTTP/WebSocket
                              ▼
┌─────────────────────────────────────────────────────────────┐
│              Java Spring Boot Backend                       │
│  - User/voice model management                            │
│  - Synthesis task scheduling                              │
│  - PostgreSQL data storage                                │
│  - WebSocket progress updates                             │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ HTTP API
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    TTS Service Layer                         │
│  - Voice model training                                   │
│  - Speech synthesis                                       │
│  - Audio post-processing                                  │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 Quick Start

### Prerequisites

- **Node.js** (v18 or higher)
- **Rust** (latest stable)
- **Java** (JDK 17 or higher)
- **PostgreSQL** (v12 or higher)

### 1. Clone and Setup

```bash
git clone <repository-url>
cd cantonese-storyteller
```

### 2. Frontend Setup

```bash
# Install dependencies
npm install

# Run development server
npm run tauri:dev
```

### 3. Java Backend Setup

```bash
cd backend-java

# Build the project
./gradlew build

# Run the Spring Boot application
./gradlew bootRun
```

### 4. Database Setup

```bash
# Create PostgreSQL database
createdb storyteller

# Update application.yml with your database credentials
# The application will automatically create tables on startup
```

## 📁 Project Structure

```
cantonese-storyteller/
├── src/                          # React Frontend
│   ├── App.tsx                   # Main application
│   ├── pages/                    # Page components
│   └── services/                 # API service layer
├── src-tauri/                    # Rust Backend
│   ├── src/
│   │   ├── commands/             # Tauri command handlers
│   │   ├── services/             # Business logic
│   │   └── db/                   # Database layer
│   └── migrations/               # SQLite migrations
├── backend-java/                 # Java Spring Boot
│   ├── src/main/java/
│   │   ├── controller/           # REST API controllers
│   │   ├── service/              # Business logic
│   │   ├── entity/               # JPA entities
│   │   └── repository/           # Data repositories
│   └── src/main/resources/       # Configuration files
└── README.md
```

## 🔧 Development

### Frontend Development

```bash
# Run with hot reload
npm run tauri:dev

# Build for production
npm run tauri:build
```

### Backend Development

```bash
# Run with Spring Boot DevTools (auto-restart)
cd backend-java
./gradlew bootRun

# Run tests
./gradlew test
```

### Database Migrations

```bash
# For SQLite (Tauri local storage)
cd src-tauri
sqlx migrate run

# For PostgreSQL (Java backend)
# Migrations run automatically on startup
```

## 🎤 Usage Guide

### 1. Record Your Voice

1. Launch the application
2. Click "开始录制" (Start Recording)
3. Read the provided Cantonese text for 30 seconds
4. Click "停止录制" (Stop Recording)
5. Click "训练声音模型" (Train Voice Model)

### 2. Generate Audiobooks

1. Upload or select chapter text
2. Choose your trained voice model
3. Submit synthesis request
4. Monitor progress in real-time
5. Download completed audio file

## 🔌 API Endpoints

### Voice Management
- `POST /api/voice/train` - Train voice model
- `GET /api/voice/models/{userId}` - Get user voice models

### Synthesis
- `POST /api/synthesis/request` - Create synthesis task
- `GET /api/synthesis/task/{taskId}` - Get task status

### WebSocket
- `ws://localhost:8080/api/ws/progress` - Real-time progress updates

## 🧪 Testing

### Frontend Tests
```bash
npm test
```

### Backend Tests
```bash
cd backend-java
./gradlew test
```

## 🚀 Deployment

### Desktop Application
```bash
# Build for current platform
npm run tauri:build

# Build for specific platforms
npm run tauri:build -- --target universal-apple-darwin
npm run tauri:build -- --target x86_64-pc-windows-msvc
npm run tauri:build -- --target x86_64-unknown-linux-gnu
```

### Java Backend
```bash
# Create executable JAR
cd backend-java
./gradlew bootJar

# Run with Java
java -jar build/libs/storyteller-0.1.0.jar
```

## 🔧 Configuration

### Tauri Configuration
Edit `src-tauri/tauri.conf.json` for:
- Window settings
- Security permissions
- Bundle configuration
- Plugin settings

### Spring Boot Configuration
Edit `backend-java/src/main/resources/application.yml` for:
- Database connection
- Server port
- File upload limits
- Logging levels

## 🛠️ Technology Stack

### Frontend
- **React 18** - UI framework
- **TypeScript** - Type safety
- **Tauri 2.0** - Cross-platform app framework
- **Vite** - Build tool

### Rust Backend
- **Tauri Commands** - IPC communication
- **Tokio** - Async runtime
- **SQLx** - Database toolkit
- **Rodio** - Audio processing
- **Hound** - WAV file handling

### Java Backend
- **Spring Boot 3** - Application framework
- **Spring WebSocket** - Real-time communication
- **Spring Data JPA** - Database access
- **PostgreSQL** - Primary database

## 📋 Roadmap

### MVP (Current)
- ✅ Voice recording (30 seconds)
- ✅ Basic voice training simulation
- ✅ Single chapter synthesis
- ✅ Real-time progress updates

### Beta Features
- [ ] Full book synthesis
- [ ] Offline mode with SQLite caching
- [ ] Audio player with controls
- [ ] Chapter management UI
- [ ] Production TTS service integration

### Production Features
- [ ] Multi-language support
- [ ] Advanced audio processing
- [ ] Community sharing
- [ ] Mobile app support
- [ ] Cloud deployment

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For issues and questions:
- Create an issue in the GitHub repository
- Check the documentation
- Review the troubleshooting guide

## 🙏 Acknowledgments

- Tauri team for the excellent cross-platform framework
- Spring Boot community for the robust backend ecosystem
- Contributors and testers

---

**粤语评书 - Bringing your stories to life with your voice!**