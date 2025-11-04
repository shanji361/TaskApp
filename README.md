# Tasks App  

## Features

- **Three Screens**
  - **Notes** — text entry screen with state stored in a ViewModel so notes persist across recomposition and navigation.
  - **Tasks** — checkbox list with each item maintaining checked/unchecked state.
  - **Calendar** — static placeholder screen.

- **Navigation**
  - Uses **sealed route objects** to define destinations safely.
  - Screen switches use:
    - `popUpTo(route)` — prevents stacking multiple root screens
    - `launchSingleTop = true` — avoids duplicate destinations on reselection
    - `restoreState = true` — restores screen state when returning to a tab

- **Backstack Behavior**
  - When switching tabs, previous state is restored instead of recreated.
  - Reselecting a tab does **not** create a duplicate destination.
  - Pressing back:
    - Pops through the backstack normally,

---

## How to Run


1. Clone this repository:
   ```
   git clone https://github.com/shanji361/TaskApp.git
   ```
2. Open the project in Android Studio.

3. Run the app on an emulator or a physical Android device.
   
--- 

## Reference 

-AI helped with the calendar math used to calculate the grid cell count, where to put restoreState and launchSingleTop, nav bar icon suggestions, library imports, and in using the correct syntax for popUpTo(navController.graph.findStartDestination().id). AI also helped with the logic for generating a new note ID
for addTask and addNote functions in ViewModel files. 
-AI Misunderstandings: using rememberSaveable { mutableStateOf(rememberNavController()) } in place of rememberNavController(). 
