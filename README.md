# Expense Management Android App
Managing shared expenses with friends has never been easier. This app helps you keep track of who paid for what, split costs fairly, and avoid awkward money conversations.
Create balance groups, invite your friends, and add any expenses you want to share - the app will automatically divide the costs equally among all members. No more manual calculations, no more confusion, no more “who owes whom.”
You’ll never have to wonder who owes what ever again!

# Application architecture
This application was created for educational purposes. It contains the following packages:
* ```app/``` - Main application activity and navigation definitions,
* ```domain/``` - Application logic written in Kotlin,
* ```data/``` - Persistence configuration and repository implementations,
* ```sync/``` - Module containing the P2P synchronization implementation,
* ```core/``` - Common components such as security and utilities,
* ```feature/``` - MVVM architecture implementation for each application screen.

# Troubleshooting
## Virtual Device not starting from Android Studio
Run the following commands (works on Windows):
* ```taskkill /F /IM emulator.exe```
* ```taskkill /F /IM adb.exe```
* ```taskkill /F /IM qemu-system-x86_64.exe```
* ```emulator -avd "Small_Phone" -no-snapshot -gpu swiftshader_indirect``` - "Small_Phone" is device name
After last command device will start in separate window
