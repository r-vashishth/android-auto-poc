# AQI Monitor

An Android application that displays Air Quality Index (AQI) data on both mobile devices and Android Auto.

## Features

- Circular AQI display that changes color based on air quality level
- Mobile application for phones and tablets
- Android Auto support for in-car display
- Bluetooth connectivity for external sensor data (to be implemented)
- Data storage and history (to be implemented)
- User account system (to be implemented)

## Project Structure

- `app/` - Mobile application module
- `automotive/` - Android Auto specific module
- `shared/` - Shared code between modules

## Building the Project

1. Open the project in Android Studio
2. Sync Gradle files
3. Build the project
4. Run on your device or emulator

## Development Notes

This project supports both standard Android devices and Android Automotive. The same codebase is used for both platforms with platform-specific UI adjustments.

For testing Android Auto features, you'll need to use the Desktop Head Unit (DHU) simulator included with Android Studio.


Android SDK location

`/Users/rahul/Library/Android/sdk/extras/google/auto`

how to run the DHU

`./desktop-head-unit --usb`

Restart DHU with proper forwarding:
```
/Users/rahul/Library/Android/sdk/platform-tools/adb forward tcp:5277 tcp:5277
/Users/rahul/Library/Android/sdk/extras/google/auto/desktop-head-unit
```