# Pod Dialogs - Quick Reference

## 🚀 Complete Flow Overview

### Create Pod Flow
```
FAB → Initial Dialog → Create Pod → Enter Name → Success → Invite/View
```

### Join Pod Flow  
```
FAB → Initial Dialog → Join Pod → Scan QR → Success → Let's Dive In
```

---

## 📋 QR Code Format

**Required Format:**
```
flowpods://join?code=ABCD1234
```

**Examples:**
- ✅ `flowpods://join?code=POD123`
- ✅ `flowpods://join?code=TEAM2024`
- ❌ `https://example.com/join` (wrong protocol)
- ❌ `flowpods://join` (missing code)

---

## 🎨 Dialog States

| State | Description | Animation |
|-------|-------------|-----------|
| `INITIAL` | Choose Create/Join | Slide from left |
| `CREATE_POD` | Enter pod name | Slide from right |
| `SUCCESS` | Pod created | Scale up (dramatic) |
| `JOIN_POD` | QR scanner | Slide from right |
| `JOIN_SUCCESS` | Joined successfully | Scale up (dramatic) |

---

## 🔧 Backend Integration

### Create Pod
```kotlin
onCreatePod = { podName ->
    viewModel.createPod(podName)
    // POST /api/pods/create
    // Body: { "name": "podName" }
}
```

### Join Pod
```kotlin
onJoinPod = { qrCode ->
    viewModel.joinPod(qrCode)
    // POST /api/pods/join
    // Body: { "code": "qrCode" }
}
```

### Invite Others
```kotlin
onInviteOthers = {
    shareManager.sharePodInvite(podId)
    // Generate QR code: flowpods://join?code=XXXXX
    // Share via system share sheet
}
```

### View Pods
```kotlin
onViewPods = {
    navController.navigate("pods_list")
}
```

---

## 🎯 Key Features

### Security ✅
- QR format validation
- Protocol verification
- Empty code rejection

### UX ✅
- Smooth animations (400-500ms)
- Clear error messages
- Fun, engaging copy
- Consistent design

### Modularity ✅
- Reusable components
- State management
- Easy to extend
- Clean callbacks

---

## 📱 Testing

### Manual Test Cases:

1. **Create Pod Flow:**
   - [ ] Click FAB
   - [ ] Click "Create Pod"
   - [ ] Enter pod name
   - [ ] Click "Create Pod" button
   - [ ] See success animation
   - [ ] Test both action buttons

2. **Join Pod Flow:**
   - [ ] Click FAB
   - [ ] Click "Join Pod"
   - [ ] Click "Open Scanner"
   - [ ] Scan valid QR code
   - [ ] See success animation
   - [ ] Click "Let's Dive In"

3. **Error Cases:**
   - [ ] Try empty pod name
   - [ ] Scan invalid QR code
   - [ ] Cancel scanner
   - [ ] Test back buttons
   - [ ] Test close buttons

---

## 🎨 Colors Used

| Element | Color |
|---------|-------|
| Background | Charcoal `#252525` |
| Primary Button | CustomRed `#D71921` |
| Secondary Button | White `#FFFFFF` |
| Error Text | CustomRed `#D71921` |
| Border (focused) | CherryRed `#D20A2E` |
| Border (unfocused) | White 30% |
| Icon Background | White 10% |

---

## 🔤 Typography

All text uses **Poppins** font:
- **Bold** (700): Success headings
- **SemiBold** (600): Dialog titles, buttons
- **Medium** (500): Labels
- **Regular** (400): Body text, descriptions

---

## 📦 Dependencies

```kotlin
// ML Kit Barcode Scanning
implementation("com.google.android.gms:play-services-code-scanner:16.1.0")

// Lottie (already added)
implementation("com.airbnb.android:lottie-compose:6.7.0")
```

---

## 💡 Pro Tips

1. **QR Code Generation** (for backend):
   ```
   Format: flowpods://join?code={uniqueCode}
   Use UUID or base64 encoded pod ID
   ```

2. **Error Handling**:
   - Show errors in the same dialog
   - Use CustomRed for error text
   - Provide retry option

3. **Loading States**:
   - Add CircularProgressIndicator while verifying
   - Disable buttons during network calls
   - Show "Verifying..." text

4. **Success Messages**:
   - Keep them brief and celebratory
   - Use emojis sparingly
   - Provide clear next actions

---

## 🚀 Next Steps

1. **Sync Gradle** to download ML Kit
2. **Test Scanner** on a real device (emulator may not work)
3. **Implement Backend** API calls
4. **Add Loading States** during network operations
5. **Generate QR Codes** for created pods
6. **Add Error Handling** for failed operations

---

**Status**: ✅ Complete & Ready for Integration

