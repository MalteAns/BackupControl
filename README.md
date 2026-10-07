# Backup Control

Multiplatform application and server to supervise a backup server

----

## Setup (with docker compose)

### Suggested file structure:
```text
backupControl
├── backupControl-server.jar    ⬅ ShadowJar generated from this repository
├── backupControl.db            ⬅ TBD: Should be auto-generated, if not exist (not working yet)
├── commands.json               ⬅ See below for template
├── docker-compose.yml          ⬅ See below for template
├── runPipeCommands.log         ⬅ Just an (initially) empty file
├── runPipeCommands.sh          ⬅ See below for template
└── commandPipe                 ⬅ Create with `mkfifo commandPipe`
```

### File templates:
#### commands.json
```json
{
  "pipePath": "/app/commandPipe",
  "commands": [
    {"name": "Hello World", "command": "touch \"Hello World!\""},
    {"name": "Start WireGuard", "command": "wg-quick up home"},
    {"name": "Stop WireGuard", "command": "wg-quick down home"}
  ]
}
```
#### docker-compose.yml
```yaml
services:
  server:
    image: eclipse-temurin:21-jdk-jammy
    container_name: backupControl-server
    restart: unless-stopped
    working_dir: /app
    volumes:
      - ./backupControl-server.jar:/app/server.jar:ro
      - ./backupControl.db:/app/backupControl.db
      - ./commands.json:/app/commands.json:ro
      - ./commandPipe:/app/commandPipe
      - /YOUR/LOGS/PATH:/app/logs
    environment:
      - API_TOKEN=YOUR_SECURE_API_TOKEN
    command: ["java", "-jar", "server.jar"]
    networks:
      - app-network
    
  cloudflared:
    container_name: cloudflared-tunnel
    image: cloudflare/cloudflared
    restart: unless-stopped
    command: tunnel run
    environment:
      - TUNNEL_TOKEN=YOUR_CLOUDFLARED_TUNNEL_TOKEN
    networks:
      - app-network

networks:
  app-network:
    driver: bridge
```
#### runPipeCommands.sh
* Dependency: `jq`. If not installed, install it with `sudo apt install jq` (or something similar adjusted to your OS)
* Auto-run by adding it as cronjob (`@reboot /YOUR/PATH/TO/runPipeCommands.sh >> /YOUR/PATH/TO/runPipeCommands.log 2>&1`)
  * With user permissions (e.g. `wg-quick` won't work): `crontab -e` 
  * With root permissions: `sudo crontab -e`
```shell
#!/bin/bash

set -u

CONFIG_FILE="/YOUR/PATH/TO/commands.json" # Replace
PIPE_PATH="/YOUR/PATH/TO/commandPipe"     # Replace

if ! command -v jq >/dev/null 2>&1; then
    echo "Error: jq is required but not installed." >&2
    exit 1
fi

while true; do
    raw_cmd="$(cat "$PIPE_PATH")"

    if [[ -z "$raw_cmd" ]]; then
        continue
    fi

    if [[ ! -f "$CONFIG_FILE" ]]; then
        echo "Warning: Configuration file not found: $CONFIG_FILE. Discarding command: $raw_cmd" >&2
        continue
    fi

    if jq -e --arg cmd "$raw_cmd" '.commands[]? | select(.command == $cmd)' "$CONFIG_FILE" >/dev/null 2>&1; then
        eval "$raw_cmd"
        echo "Info: Command executed: $raw_cmd"
    else
        echo "Warning: Command not permitted: $raw_cmd" >&2
    fi
done
```

### 

---

# Auto-generated KMP Project stuff

This is a Kotlin Multiplatform project targeting Android, iOS, Desktop (JVM), Server.

* [/app/iosApp](./app/iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose
  Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/app/shared](./app/shared/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
    - [commonMain](./app/shared/src/commonMain/kotlin) is for code that’s common for all targets.
    - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
      For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
      the [iosMain](./app/shared/src/iosMain/kotlin) folder would be the right place for such calls.
      Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./app/shared/src/jvmMain/kotlin)
      folder is the appropriate location.

* [/core](./core/src) is for the code that will be shared between all targets in the project.
  The most important subfolder is [commonMain](./core/src/commonMain/kotlin). If preferred, you
  can add code to the platform-specific folders here too.

* [/server](./server/src/main/kotlin) is for the Ktor server application.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and
options:

- Android app: `./gradlew :app:androidApp:assembleDebug`
- Desktop app:
    - Hot reload: `./gradlew :app:desktopApp:hotRun --auto`
    - Standard run: `./gradlew :app:desktopApp:run`
- Server: `./gradlew :server:run`
- iOS app: open the [/app/iosApp](./app/iosApp) directory in Xcode and run it from there.

### Running tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

- Android tests: `./gradlew :app:shared:testAndroidHostTest`
- Desktop tests: `./gradlew :app:shared:jvmTest`
- Server tests: `./gradlew :server:test`
- iOS tests: `./gradlew :app:shared:iosSimulatorArm64Test`

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…