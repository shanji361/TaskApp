package com.example.taskcalendarapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.taskcalendarapp.ui.theme.TaskCalendarAppTheme
import java.text.SimpleDateFormat
import java.util.*

import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.compose.animation.core.tween


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskCalendarAppTheme {
                MainScreen()
            }
        }
    }
}


// added animated transition 
enum class TransitionType {
    SLIDE_LEFT,
    SLIDE_RIGHT,
    FADE,
    SLIDE_UP,
    NONE
}

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Notes : Screen("notes?transition={transition}", "Notes", Icons.Default.Home) {
        fun createRoute(transition: TransitionType = TransitionType.SLIDE_LEFT) =
            "notes?transition=${transition.name}"
    }
    object Tasks : Screen("tasks?transition={transition}", "Tasks", Icons.Default.CheckCircle) {
        fun createRoute(transition: TransitionType = TransitionType.FADE) =
            "tasks?transition=${transition.name}"
    }
    object Calendar : Screen("calendar?transition={transition}", "Calendar", Icons.Default.DateRange) {
        fun createRoute(transition: TransitionType = TransitionType.SLIDE_RIGHT) =
            "calendar?transition=${transition.name}"
    }

    val baseRoute: String get() = route.substringBefore("?")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val notesViewModel: NotesViewModel = viewModel()
    val tasksViewModel: TasksViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val screens = listOf(Screen.Notes, Screen.Tasks, Screen.Calendar)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentDestination?.route?.substringBefore("?")) {
                            "notes" -> "Notes"
                            "tasks" -> "Tasks"
                            "calendar" -> "Calendar"
                            else -> "App"
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.route?.startsWith(screen.baseRoute) == true,
                        onClick = {
                            val route = when (screen) {
                                is Screen.Notes -> Screen.Notes.createRoute(TransitionType.SLIDE_LEFT)
                                is Screen.Tasks -> Screen.Tasks.createRoute(TransitionType.FADE)
                                is Screen.Calendar -> Screen.Calendar.createRoute(TransitionType.SLIDE_RIGHT)
                            }

                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Notes.createRoute(TransitionType.FADE),
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                route = Screen.Notes.route,
                arguments = listOf(
                    navArgument("transition") {
                        type = NavType.StringType
                        defaultValue = TransitionType.SLIDE_LEFT.name
                    }
                ),
                enterTransition = {
                    // Read the transition from the entering screen (targetState)
                    val transitionType = TransitionType.valueOf(
                        targetState.arguments?.getString("transition") ?: TransitionType.SLIDE_LEFT.name
                    )

                    when (transitionType) {
                        TransitionType.SLIDE_LEFT -> slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(3000)
                        )

                        TransitionType.SLIDE_RIGHT -> slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(3000)
                        )

                        TransitionType.SLIDE_UP -> slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(3000)
                        )
                        TransitionType.FADE -> fadeIn(animationSpec = tween(3000))
                        TransitionType.NONE -> EnterTransition.None
                    }
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(3000))
                }
            ) {
                NotesScreen(viewModel = notesViewModel)
            }

            composable(
                route = Screen.Tasks.route,
                arguments = listOf(
                    navArgument("transition") {
                        type = NavType.StringType
                        defaultValue = TransitionType.FADE.name
                    }
                ),
                enterTransition = {
                    val transitionType = TransitionType.valueOf(
                        targetState.arguments?.getString("transition") ?: TransitionType.FADE.name
                    )

                    when (transitionType) {
                        TransitionType.SLIDE_LEFT -> slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(3000)
                        )

                        TransitionType.SLIDE_RIGHT -> slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(3000)
                        )

                        TransitionType.SLIDE_UP -> slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(3000)
                        )
                        TransitionType.FADE -> fadeIn(animationSpec = tween(3000))
                        TransitionType.NONE -> EnterTransition.None
                    }
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(3000))
                }
            ) {
                TasksScreen(viewModel = tasksViewModel)
            }

            composable(
                route = Screen.Calendar.route,
                arguments = listOf(
                    navArgument("transition") {
                        type = NavType.StringType
                        defaultValue = TransitionType.SLIDE_RIGHT.name
                    }
                ),
                enterTransition = {
                    val transitionType = TransitionType.valueOf(
                        targetState.arguments?.getString("transition") ?: TransitionType.SLIDE_RIGHT.name
                    )

                    when (transitionType) {
                        TransitionType.SLIDE_LEFT -> slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(3000)
                        )
                        TransitionType.SLIDE_RIGHT -> slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(3000)
                        )

                        TransitionType.SLIDE_UP -> slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(3000)
                        )
                        TransitionType.FADE -> fadeIn(animationSpec = tween(3000))
                        TransitionType.NONE -> EnterTransition.None
                    }
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(300))
                }
            ) {
                CalendarScreen()
            }
        }
    }
}



@Composable
fun NotesScreen(viewModel: NotesViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (viewModel.notes.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("No notes yet", style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Text("Tap + to create your first note", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.notes, key = { it.id }) { note ->
                    NoteItem(note = note, onDelete = { viewModel.deleteNote(note.id) })
                }
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Note")
        }
    }

    if (showDialog) {
        AddNoteDialog(
            onDismiss = { showDialog = false },
            onConfirm = { title, content ->
                viewModel.addNote(title, content)
                showDialog = false
            }
        )
    }
}

@Composable
fun NoteItem(note: Note, onDelete: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = note.title, style = MaterialTheme.typography.titleMedium,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = note.content, style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2, overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = dateFormat.format(Date(note.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Note",
                    tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AddNoteDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Note") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it },
                    label = { Text("Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = content, onValueChange = { content = it },
                    label = { Text("Content") }, modifier = Modifier.fillMaxWidth(),
                    minLines = 3, maxLines = 5)
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (title.isNotBlank() && content.isNotBlank()) onConfirm(title, content) },
                enabled = title.isNotBlank() && content.isNotBlank()
            ) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun TasksScreen(viewModel: TasksViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    val completedCount = viewModel.tasks.count { it.isCompleted }
    val totalCount = viewModel.tasks.size

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (totalCount > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Progress: $completedCount / $totalCount tasks completed",
                            style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { if (totalCount > 0) completedCount.toFloat() / totalCount else 0f },
                            modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            if (viewModel.tasks.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No tasks yet", style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Text("Tap + to create your first task", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.tasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onToggle = { viewModel.toggleTask(task.id) },
                            onDelete = { viewModel.deleteTask(task.id) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Task")
        }
    }

    if (showDialog) {
        AddTaskDialog(
            onDismiss = { showDialog = false },
            onConfirm = { title -> viewModel.addTask(title); showDialog = false }
        )
    }
}

@Composable
fun TaskItem(task: Task, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = task.isCompleted, onCheckedChange = { onToggle() })
            Text(text = task.title, style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                color = if (task.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                else MaterialTheme.colorScheme.onSurface)
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Task",
                    tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var taskTitle by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Task") },
        text = {
            OutlinedTextField(value = taskTitle, onValueChange = { taskTitle = it },
                label = { Text("Task Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        },
        confirmButton = {
            TextButton(onClick = { if (taskTitle.isNotBlank()) onConfirm(taskTitle) },
                enabled = taskTitle.isNotBlank()) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun CalendarScreen() {
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    val today = remember { Calendar.getInstance() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentMonth.time),
                    style = MaterialTheme.typography.titleLarge)

            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                Text(text = day, modifier = Modifier.weight(1f), textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        CalendarGrid(currentMonth = currentMonth, today = today)
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Calendar View", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "This is a static calendar placeholder. Navigate through months using the arrows above. The current date is highlighted.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
fun CalendarGrid(currentMonth: Calendar, today: Calendar) {
    val cal = currentMonth.clone() as Calendar
    cal.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val totalCells = ((firstDayOfWeek + daysInMonth) / 7.0).let {
        if (it > it.toInt()) (it.toInt() + 1) * 7 else it.toInt() * 7
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(totalCells) { index ->
            val dayNumber = index - firstDayOfWeek + 1
            if (dayNumber in 1..daysInMonth) {
                val isToday = today.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) &&
                        today.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH) &&
                        today.get(Calendar.DAY_OF_MONTH) == dayNumber
                DayCell(day = dayNumber, isToday = isToday)
            } else {
                Box(modifier = Modifier.aspectRatio(1f))
            }
        }
    }
}

@Composable
fun DayCell(day: Int, isToday: Boolean) {
    Box(
        modifier = Modifier.aspectRatio(1f)
            .then(if (isToday) Modifier.background(MaterialTheme.colorScheme.primary, CircleShape)
            else Modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), CircleShape))
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = day.toString(), style = MaterialTheme.typography.bodyMedium,
            color = if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
    }
}
