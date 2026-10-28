# NB Android Voice Assistant — Starter Scaffold

This is a starter scaffold for integrating a Lovable React/Vite web app with Android native actions using Capacitor. It is not a finished, store-ready application and has not been compiled against your private Lovable project.

## Before you start
1. Export/sync your Lovable project to GitHub and clone/download the source code.
2. Back up the original project.
3. Check the actual Capacitor version, Android package ID, Gradle versions, and `MainActivity` before copying files.
4. Copy the Kotlin files into the matching package directory, adapting the package name if needed.
5. Merge the manifest entries rather than replacing your existing manifest.
6. Follow the commands below from your web project root after installing a compatible Node.js and Android Studio version.

## Typical Capacitor commands (run in the web project root)
```bash
npm install
npm install @capacitor/core @capacitor/cli @capacitor/android
npx cap init
npm run build
npx cap add android
npx cap sync android
npx cap open android
```

If Android has already been added, do not run `npx cap add android` again. Set `webDir` to the real build output folder in `capacitor.config.ts` (often `dist` for Vite, but verify it).

## Important limitations
- `openApp` can launch an installed app only if Android permits the launch and the package ID is correct.
- `openDialer` opens the dialer; it does not place a call automatically.
- `speak` uses the device's installed text-to-speech voices. The app cannot guarantee a male/female voice for every language/device.
- This scaffold does not include unrestricted background listening, boot-time microphone activation, iOS integrations, or universal app-screen automation.
- Android background microphone use, notification access, and AccessibilityService require separate design, user consent, policy review, and testing.
- Do not claim Google Play approval until the app has been tested and reviewed against current policies.

## Suggested implementation order
1. Verify the existing Lovable project builds unchanged.
2. Add Capacitor and test the original UI on a real Android phone.
3. Add/register `NativeActionsPlugin` and test dialer/app launching.
4. Connect your existing voice intent pipeline to the TypeScript bridge.
5. Add settings, history, permissions explanations, and platform-specific features one at a time.
