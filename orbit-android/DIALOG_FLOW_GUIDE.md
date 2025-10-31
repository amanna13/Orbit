# Pod Dialog Flow - Visual Guide

## State Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         HOMESCREEN                              │
│                                                                 │
│                    [FAB Button Click]                           │
│                            ↓                                    │
└─────────────────────────────────────────────────────────────────┘
                             ↓
┌─────────────────────────────────────────────────────────────────┐
│                   INITIAL DIALOG (State 1)                      │
│  ┌────────────────────────────────────────────────────────┐    │
│  │              Choose an Action                          │    │
│  │                                                        │    │
│  │  ┌──────────────────────────────────────────────┐     │    │
│  │  │  Create Pod                                  │     │    │
│  │  └──────────────────────────────────────────────┘     │    │
│  │                                                        │    │
│  │  ┌──────────────────────────────────────────────┐     │    │
│  │  │  Join Pod                                    │     │    │
│  │  └──────────────────────────────────────────────┘     │    │
│  └────────────────────────────────────────────────────────┘    │
│                                                                 │
│  Animations: Scale In (300ms) / Scale Out (200ms)               │
│  Dismiss: Click outside or back button                          │
└─────────────────────────────────────────────────────────────────┘
       ↓ Create Pod                              ↓ Join Pod
       ↓                                         ↓ → onJoinPod()
       ↓                                         ↓ → Close Dialog
┌─────────────────────────────────────────────────────────────────┐
│                 CREATE POD DIALOG (State 2)                     │
│  ┌────────────────────────────────────────────────────────┐    │
│  │  [←]         Create New Pod                            │    │
│  │                                                        │    │
│  │  Pod Name                                              │    │
│  │  ┌──────────────────────────────────────────────┐     │    │
│  │  │  Enter pod name                              │     │    │
│  │  └──────────────────────────────────────────────┘     │    │
│  │                                                        │    │
│  │  ┌──────────────────────────────────────────────┐     │    │
│  │  │  Create Pod                                  │     │    │
│  │  └──────────────────────────────────────────────┘     │    │
│  └────────────────────────────────────────────────────────┘    │
│                                                                 │
│  Animations: Slide In from Right (400ms) / Slide Out (300ms)   │
│  Dismiss: Back button only (not by clicking outside)           │
└─────────────────────────────────────────────────────────────────┘
       ↓ Back Button              ↓ Create Pod (if name not blank)
       ↓ → Return to State 1      ↓ → onCreatePod(podName)
       ↓                          ↓
┌─────────────────────────────────────────────────────────────────┐
│                  SUCCESS DIALOG (State 3)                       │
│  ┌────────────────────────────────────────────────────────┐    │
│  │                                                    [×]  │    │
│  │                                                        │    │
│  │              [Lottie Animation]                        │    │
│  │           (giving_five.json)                           │    │
│  │                                                        │    │
│  │         Pod Created Successfully!                      │    │
│  │         "My Awesome Pod" is ready to go                │    │
│  │                                                        │    │
│  │  ┌──────────────────────────────────────────────┐     │    │
│  │  │  Invite Others                               │     │    │
│  │  └──────────────────────────────────────────────┘     │    │
│  │                                                        │    │
│  │  ┌──────────────────────────────────────────────┐     │    │
│  │  │  View Pods                                   │     │    │
│  │  └──────────────────────────────────────────────┘     │    │
│  └────────────────────────────────────────────────────────┘    │
│                                                                 │
│  Animations: Scale In from 0.3 (500ms) / Scale Out (300ms)     │
│  Dismiss: Close button or back button (not by clicking out)    │
└─────────────────────────────────────────────────────────────────┘
       ↓                    ↓                        ↓
       ↓ Close             ↓ Invite Others          ↓ View Pods
       ↓ → Dialog Closed   ↓ → onInviteOthers()    ↓ → onViewPods()
       ↓                   ↓ → Dialog Closed        ↓ → Dialog Closed
       ↓                   ↓                        ↓
┌─────────────────────────────────────────────────────────────────┐
│                      HOMESCREEN                                 │
│                   (Dialog Closed)                               │
└─────────────────────────────────────────────────────────────────┘
```

## Component Breakdown

### PodDialogFlow (Main Controller)
```kotlin
- State Management: Uses remember { mutableStateOf(DialogState.INITIAL) }
- Pod Name Storage: Uses remember { mutableStateOf("") }
- Auto-resets state when dialog is dismissed
- Handles all state transitions
```

### InitialPodDialog
```
Colors:
  - Background: Charcoal (#252525)
  - Title: White
  - Create Button: CustomRed background, White text
  - Join Button: White background, Charcoal text
  
Layout:
  - Padding: 24dp
  - Button height: 56dp
  - Spacing between buttons: 12dp
  - Border radius: 12dp for buttons, 24dp for dialog
```

### CreatePodDialog
```
Colors:
  - Background: Charcoal (#252525)
  - Back button bg: White 10% opacity
  - Title: White
  - Input border (focused): CherryRed (#D20A2E)
  - Input border (unfocused): White 30% opacity
  - Input text: White
  - Button: CustomRed (disabled: 50% opacity)
  
Layout:
  - Back button: 40dp circle, top-left
  - Title: Centered in header
  - Input field: Full width
  - Submit button: 56dp height, full width
  - Spacing: 32dp between sections
```

### SuccessDialog
```
Colors:
  - Background: Charcoal (#252525)
  - Close button bg: White 10% opacity
  - Title: White, Bold
  - Subtitle: White 70% opacity
  - Invite button: CustomRed background
  - View Pods button: White background, Charcoal text
  
Layout:
  - Close button: 36dp, top-right
  - Animation: 180dp size
  - Buttons: 56dp height, full width
  - Spacing: 12dp between buttons
```

## Callback Integration Points

```kotlin
// In HomeScreen.kt
PodDialogFlow(
    showDialog = showDialog,
    onDismiss = { 
        // Called when dialog needs to close
        showDialog = false 
    },
    onCreatePod = { podName ->
        // Called when "Create Pod" is submitted
        // TODO: Call your API here
        // viewModel.createPod(podName)
    },
    onJoinPod = {
        // Called when "Join Pod" is clicked
        // TODO: Navigate to join pod screen
        // navController.navigate("join_pod")
    },
    onInviteOthers = {
        // Called from success dialog
        // TODO: Open share sheet or invite screen
        // shareManager.sharePodInvite(podId)
    },
    onViewPods = {
        // Called from success dialog
        // TODO: Navigate to pods list
        // navController.navigate("pods_list")
    }
)
```

## Animation Specifications

### Scale Animations
```kotlin
// Initial Dialog Enter
scaleIn(
    animationSpec = tween(300),
    initialScale = 0.8f
) + fadeIn(animationSpec = tween(300))

// Success Dialog Enter (more dramatic)
scaleIn(
    animationSpec = tween(500),
    initialScale = 0.3f  // Starts smaller for impact
) + fadeIn(animationSpec = tween(500))
```

### Slide Animations
```kotlin
// Create Pod Dialog Enter
slideInHorizontally(
    animationSpec = tween(400),
    initialOffsetX = { it }  // Starts from right edge
) + fadeIn(animationSpec = tween(400))
```

## Best Practices Implemented

✅ **Material Design 3**: Uses Material3 components throughout
✅ **Responsive**: Adapts to different screen sizes
✅ **Accessibility**: Proper content descriptions for icons
✅ **Performance**: Efficient state management with remember
✅ **User Experience**: Disabled states, proper feedback
✅ **Clean Code**: Modular, well-commented, easy to understand
✅ **Type Safety**: Enum for dialog states prevents errors
✅ **Consistency**: Uniform styling and spacing

## Testing the Implementation

1. **Launch app** → HomeScreen loads
2. **Click FAB** → Initial dialog appears with scale animation
3. **Click "Create Pod"** → Slides to create form
4. **Try submitting empty** → Button is disabled
5. **Enter pod name** → Button becomes enabled
6. **Click back** → Returns to initial dialog
7. **Enter pod name again** → Submit
8. **Success dialog appears** → Lottie animation plays
9. **Click "Invite Others"** → Callback fires, dialog closes
10. **Test all dismiss scenarios** → Everything works smoothly

