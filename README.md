# Game Client - Multiplayer Sorting Game

Ứng dụng client JavaFX cho game sắp xếp đa người chơi. Người chơi tham gia các trận đấu sắp xếp số hoặc chữ theo thứ tự tăng dần hoặc giảm dần trong thời gian giới hạn.

## 📋 Mục lục

- [Tổng quan](#tổng-quan)
- [Tính năng](#tính-năng)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Cấu trúc dự án](#cấu-trúc-dự-án)
- [Yêu cầu hệ thống](#yêu-cầu-hệ-thống)
- [Cài đặt và chạy](#cài-đặt-và-chạy)
- [Hướng dẫn sử dụng](#hướng-dẫn-sử-dụng)
- [Kiến trúc](#kiến-trúc)
- [Cấu hình](#cấu-hình)

## 🎮 Tổng quan

Multiplayer Sorting Game là một ứng dụng client JavaFX cho phép người chơi:
- Đăng ký và đăng nhập tài khoản
- Xem danh sách người chơi online
- Tạo hoặc tham gia phòng chơi
- Chơi game sắp xếp đối kháng với người chơi khác
- Xem lịch sử trận đấu và bảng xếp hạng
- Quản lý âm thanh trong game

## ✨ Tính năng

### Xác thực người dùng
- Đăng ký tài khoản mới
- Đăng nhập với username/password
- Quản lý thông tin người dùng (nickname, thống kê)

### Quản lý phòng chơi
- Tạo phòng chơi với mã phòng
- Tham gia phòng bằng mã phòng
- Xem danh sách người chơi trong phòng
- Rời khỏi phòng

### Gameplay
- **Game sắp xếp**: Sắp xếp số hoặc chữ theo thứ tự tăng dần/giảm dần
- **Nhiều vòng chơi**: Mỗi trận đấu có nhiều vòng
- **Giới hạn thời gian**: Mỗi câu hỏi có thời gian giới hạn
- **Điểm số**: Tính điểm dựa trên độ chính xác và thời gian hoàn thành
- **Mời chơi tiếp**: Mời đối thủ chơi vòng tiếp theo

### Thống kê và lịch sử
- Xem bảng xếp hạng người chơi
- Xem lịch sử trận đấu
- Thống kê cá nhân (tổng số trận, số lần thắng, tổng điểm)

### Giao diện người dùng
- Giao diện JavaFX hiện đại
- Responsive design
- Hiệu ứng animation
- Quản lý âm thanh nền

## 🛠 Công nghệ sử dụng

- **Java 17**: Ngôn ngữ lập trình
- **JavaFX 17.0.6**: Framework GUI
  - `javafx-controls`: Controls và components
  - `javafx-fxml`: FXML cho UI
  - `javafx-media`: Phát âm thanh
- **Maven**: Quản lý dependencies
- **JSON**: Giao tiếp với server (thư viện `org.json`)
- **Socket**: Kết nối TCP với game server

## 📁 Cấu trúc dự án

```
game_client_v3/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── ltm/
│       │           └── game_client_v3/
│       │               ├── MainApp.java              # Entry point
│       │               ├── controller/                # Controllers
│       │               │   ├── ClientManager.java     # Quản lý client chính
│       │               │   ├── SocketManager.java    # Quản lý kết nối socket
│       │               │   ├── MessageHandler.java   # Xử lý message từ server
│       │               │   ├── ViewManager.java      # Quản lý các view
│       │               │   ├── UserManager.java      # Quản lý người dùng
│       │               │   └── SoundManager.java     # Quản lý âm thanh
│       │               ├── models/                   # Data models
│       │               │   ├── User.java             # Model người dùng
│       │               │   ├── Room.java             # Model phòng chơi
│       │               │   ├── Question.java         # Model câu hỏi
│       │               │   ├── GameData.java         # Dữ liệu game
│       │               │   ├── MatchSummary.java    # Tóm tắt trận đấu
│       │               │   └── MatchHistory.java    # Lịch sử trận đấu
│       │               └── views/                    # View controllers
│       │                   ├── AuthController.java    # Màn hình đăng nhập/đăng ký
│       │                   ├── WelcomeController.java # Màn hình chào mừng
│       │                   ├── HomeController.java   # Màn hình chính
│       │                   ├── GameSortingController.java # Màn hình game
│       │                   ├── MatchResultController.java # Kết quả trận đấu
│       │                   └── ScoreboardController.java # Bảng xếp hạng
│       └── resources/
│           ├── views/                                # FXML files
│           │   ├── AuthView.fxml
│           │   ├── WelcomeView.fxml
│           │   ├── HomeView.fxml
│           │   ├── GameSortingView.fxml
│           │   ├── MatchResultView.fxml
│           │   └── ScoreboardView.fxml
│           ├── images/                              # Hình ảnh
│           ├── sounds/                              # Âm thanh
│           └── fonts/                              # Font chữ
├── pom.xml                                          # Maven configuration
└── README.md
```

## 💻 Yêu cầu hệ thống

- **Java**: JDK 17 hoặc cao hơn
- **Maven**: 3.6+ (hoặc sử dụng Maven Wrapper có sẵn)
- **Hệ điều hành**: Windows, macOS, hoặc Linux
- **Kết nối mạng**: Kết nối với game server

## 🚀 Cài đặt và chạy

### 1. Clone repository

```bash
git clone <repository-url>
cd game_client_v3
```

### 2. Cấu hình server

Mở file `ClientManager.java` và cập nhật địa chỉ server:

```java
// Trong ClientManager.java, dòng 23
socketManager = new SocketManager("localhost", 8989, this::onMessageReceived);
// Hoặc
socketManager = new SocketManager("172.30.34.82", 8989, this::onMessageReceived);
```

### 3. Build project

Sử dụng Maven Wrapper:

**Windows:**
```bash
mvnw.cmd clean compile
```

**Linux/macOS:**
```bash
./mvnw clean compile
```

Hoặc nếu đã cài Maven:
```bash
mvn clean compile
```

### 4. Chạy ứng dụng

**Sử dụng Maven Wrapper:**

**Windows:**
```bash
mvnw.cmd javafx:run
```

**Linux/macOS:**
```bash
./mvnw javafx:run
```

**Hoặc sử dụng Maven:**
```bash
mvn javafx:run
```

**Hoặc chạy trực tiếp:**
```bash
java --module-path <path-to-javafx> --add-modules javafx.controls,javafx.fxml -cp target/classes com.ltm.game_client_v3.MainApp
```

## 📖 Hướng dẫn sử dụng

### Đăng ký và đăng nhập

1. Khởi động ứng dụng
2. Nhập thông tin đăng ký (username, password, nickname) hoặc đăng nhập
3. Sau khi đăng nhập thành công, bạn sẽ được chuyển đến màn hình Welcome

### Chơi game

1. **Từ màn hình Home:**
   - Xem danh sách người chơi online
   - Mời người chơi khác chơi game
   - Tạo phòng hoặc tham gia phòng bằng mã phòng
   - Xem bảng xếp hạng
   - Xem lịch sử trận đấu

2. **Trong game:**
   - Đọc hướng dẫn và đợi đếm ngược
   - Kéo/thả các số/chữ từ hàng trên xuống hàng dưới để sắp xếp
   - Click vào box để di chuyển giữa hai hàng
   - Nhấn "Reset" để làm lại
   - Nhấn "Send" khi hoàn thành
   - Xem kết quả sau mỗi vòng
   - Mời đối thủ chơi vòng tiếp theo hoặc thoát game

### Quản lý âm thanh

- Âm thanh nền được phát tự động
- Có thể tắt/bật âm thanh từ màn hình Home

## 🏗 Kiến trúc

### Pattern thiết kế

- **Singleton**: `ClientManager` sử dụng pattern Singleton
- **MVC**: Tách biệt Model, View, Controller
- **Observer**: SocketManager sử dụng callback để xử lý message

### Luồng hoạt động

1. **Khởi động:**
   - `MainApp` khởi tạo JavaFX Application
   - `ClientManager` được khởi tạo (Singleton)
   - `SocketManager` kết nối với server
   - `ViewManager` hiển thị màn hình đăng nhập

2. **Xử lý message:**
   - `SocketManager` nhận message từ server
   - `MessageHandler` xử lý và phân loại message
   - Cập nhật UI thông qua các Controller

3. **Gameplay:**
   - Người chơi thực hiện hành động (mời, trả lời, v.v.)
   - Gửi message JSON đến server
   - Nhận response và cập nhật UI

### Các thành phần chính

#### ClientManager
- Quản lý toàn bộ client
- Điều phối giữa các manager khác
- Entry point cho các thao tác

#### SocketManager
- Quản lý kết nối TCP với server
- Gửi/nhận message dạng JSON
- Chạy trong thread riêng

#### MessageHandler
- Xử lý tất cả message từ server
- Phân loại theo action type
- Cập nhật UI và data models

#### ViewManager
- Quản lý các màn hình (Scene)
- Chuyển đổi giữa các view
- Khởi tạo và quản lý controllers

## ⚙️ Cấu hình

### Thay đổi địa chỉ server

Sửa trong `ClientManager.java`:

```java
socketManager = new SocketManager("your-server-ip", 8989, this::onMessageReceived);
```

### Thay đổi cổng

Sửa port trong `ClientManager.java`:

```java
socketManager = new SocketManager("localhost", YOUR_PORT, this::onMessageReceived);
```

### Cấu hình Maven

File `pom.xml` chứa:
- JavaFX dependencies (version 17.0.6)
- JSON library (version 20231013)
- JavaFX Maven plugin để chạy ứng dụng

## 📝 Protocol Communication

Ứng dụng giao tiếp với server qua JSON messages:

### Message gửi đi (Client → Server)

- `LOGIN`: Đăng nhập
- `REGISTER`: Đăng ký
- `GET_ONLINE_USERS`: Lấy danh sách người chơi online
- `INVITE_USER_TO_GAME`: Mời người chơi
- `INVITE_USER_TO_GAME_RESPONSE`: Phản hồi lời mời
- `CREATE_ROOM`: Tạo phòng
- `JOIN_ROOM`: Tham gia phòng
- `LEAVE_ROOM`: Rời phòng
- `SUBMIT_USER_ANSWER`: Gửi câu trả lời
- `QUIT_GAME`: Thoát game
- `INVITE_USER_TO_NEXT_GAME`: Mời chơi vòng tiếp theo
- `GET_RANKING`: Lấy bảng xếp hạng
- `GET_MATCH_HISTORY`: Lấy lịch sử trận đấu

### Message nhận về (Server → Client)

- `LOGIN_RESPONSE`: Kết quả đăng nhập
- `REGISTER_RESPONSE`: Kết quả đăng ký
- `GET_ONLINE_USERS_RESPONSE`: Danh sách người chơi online
- `INVITE_USER_TO_GAME_REQUEST`: Nhận lời mời
- `INVITE_USER_TO_GAME_RESULT`: Kết quả lời mời
- `START_GAME`: Bắt đầu game
- `GAME_RESULT`: Kết quả vòng chơi
- `GAME_FINAL_RESULT`: Kết quả trận đấu cuối cùng
- `CONTINUE_NEXT_GAME`: Tiếp tục vòng tiếp theo
- `ROOM_UPDATED`: Cập nhật thông tin phòng
- `GET_RANKING_RESPONSE`: Bảng xếp hạng
- `GET_MATCH_HISTORY_RESPONSE`: Lịch sử trận đấu

## 🐛 Troubleshooting

### Lỗi kết nối server

- Kiểm tra địa chỉ IP và port trong `ClientManager.java`
- Đảm bảo server đang chạy
- Kiểm tra firewall và network

### Lỗi JavaFX

- Đảm bảo đã cài đặt JavaFX SDK
- Kiểm tra module path trong `module-info.java`
- Kiểm tra dependencies trong `pom.xml`

### Lỗi build
- Xóa thư mục `target` và build lại: `mvn clean compile`
- Kiểm tra Java version: `java -version` (phải là 17+)
- Kiểm tra Maven version: `mvn -version`
