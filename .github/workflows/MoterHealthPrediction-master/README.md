
# 💧 Water Pump Health Prediction System

This project monitors the health of a water pump using IoT sensors and provides real-time updates through a Jetpack Compose-based mobile app. It uses ESP8266 to collect sensor data and uploads it to Firebase for storage and alert management.

## 🚀 Features

- Predict pump health using:
  - Ultrasonic sensor (water level)
  - Voltage sensor (power fluctuations)
  - LM35 temperature sensor (motor heat)
- ESP8266 for real-time IoT data transmission
- Firebase Realtime Database for data storage
- Jetpack Compose mobile app with authentication and UI for:
  - Monitoring live readings
  - Managing multiple motors
  - Receiving alerts
  - Editing profiles and settings



### 📱 App Screenshots

| Login                           | Sign Up                          | Home                          | Add Motor                              |
| ------------------------------- | -------------------------------- | ----------------------------- | -------------------------------------- |
| ![Login](/GauravProj/login.png) | ![Sign Up](/GauravProj/sing.png) | ![Home](/GauravProj/home.png) | ![Add Motor](/GauravProj/addmotor.png) |

| Motor List                               | Readings                             | Alerts                           | Profile                             | Settings                             |
| ---------------------------------------- | ------------------------------------ | -------------------------------- | ----------------------------------- | ------------------------------------ |
| ![Motor List](/GauravProj/motorlist.png) | ![Readings](/GauravProj/reading.png) | ![Alerts](/GauravProj/alert.png) | ![Profile](/GauravProj/profile.png) | ![Settings](/GauravProj/setting.png) |

#### ℹ️ About Page

<p align="center">
  <img src="/GauravProj/about.png" width="250"/>
</p>

---

## 🧰 Tech Stack

### Hardware
- 📡 **ESP8266 NodeMCU**
- 🌊 **Ultrasonic Sensor** (Water level)
- 🔌 **Voltage Sensor** (Electrical health)
- 🌡️ **LM35 Temperature Sensor** (Motor temperature)

### Software
- 🔧 **Arduino IDE** – Firmware development
- ☁️ **Firebase Realtime Database** – Cloud data storage and alerts
- 📱 **Jetpack Compose** – Android App development (Kotlin)
- 🔐 Firebase Authentication – User login/signup

## 📦 Project Structure

```



```

## 🛠️ How It Works

1. **Sensors** collect data related to motor health (voltage, temperature, water level).
2. **ESP8266** sends data to **Firebase** every few seconds.
3. The **Jetpack Compose app**:
   - Authenticates the user
   - Displays real-time motor health
   - Sends alerts for abnormal behavior
   - Allows users to add/view/edit motor devices

## 📍 Use Cases

- Remote monitoring of farm water pumps
- Preventive maintenance alerts
- Multi-motor management system for agriculture

=

---

🧠 **Smart farming starts with smart monitoring.**



