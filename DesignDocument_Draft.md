# Expense Tracker - Design Document

**Student Name:** [Your Name]
**Student ID:** [Your ID]
**Class:** [Your Class]
**Date:** 2026-06-XX

---

## 1. UI Design Ideas

### 1.1 Design Philosophy

The application adopts a **Glassmorphism** design style — a modern UI trend featuring semi-transparent frosted glass cards layered on dark gradient backgrounds. This creates visual depth and a premium, modern aesthetic while maintaining readability and usability.

Core design principles:

- **Dark theme foundation:** Deep navy gradient background (#0D1B2A → #1B2838 → #234E70) across all screens provides a consistent, immersive experience
- **Glass effect cards:** Semi-transparent white containers (20% opacity) with subtle white border strokes and rounded corners (16dp) simulate frosted glass panels
- **Color accent system:** Cyan (#00BCD4) as primary color and coral (#FF7043) as accent create high contrast and visual energy against the dark background
- **Vibrant category colors:** Each of the 11 expense categories has a unique bright color optimized for dark backgrounds, paired with an emoji icon for instant recognition
- **Consistent components:** All screens use custom-styled TextInputLayout (glass background, cyan focused border), MaterialButton, and MaterialAlertDialogBuilder with dark theming

### 1.2 Screen Design

**Login Screen**
- Full-screen layout without ActionBar (NoActionBar theme) for an immersive experience
- Dark navy gradient background fills the entire screen
- App icon tinted in cyan and app title in white establish brand identity at the top
- Frosted glass card contains the login form with:
  - Custom-styled outlined text fields (translucent background, cyan border on focus, bright hint text)
  - Password field with visibility toggle (eye icon) in secondary color
  - Inline validation errors displayed directly on the TextInputLayout
- Full-width cyan MaterialButton (56dp height, 12dp corner radius) for login
- Text-style button navigates to a separate registration page

**Register Screen**
- Matches the login screen glassmorphism style for visual consistency
- Same dark gradient background and frosted glass card layout
- Three outlined input fields: username, password, confirm password
- Both password fields have visibility toggle icons
- Inline validation: username uniqueness, password length, password match
- "Already have an account? Sign in" text button returns to login
- On successful registration, credentials are passed back via ActivityResultLauncher to auto-fill login fields

**Main Screen (3 Tabs)**
- Dark toolbar (#151F2E) with white title showing "Expense Tracker - [username]"
- TabLayout with dark background, cyan selected tab text, cyan indicator (3dp height)
- Dark popup overlay for overflow menu (Currency, Clear All, About, Logout)
- ViewPager2 provides swipeable navigation between Records, Search, and Stats tabs

**Records Tab**
- Dark gradient background with SwipeRefreshLayout (cyan/coral refresh spinner on dark surface)
- RecyclerView displays expense cards using frosted glass style:
  - Dark semi-transparent card background (#CC1E2D3D) with white border stroke and 16dp corners
  - Left-side color indicator bar (4dp wide, category-specific vibrant color)
  - Category emoji (18sp) + colored badge pill (category color background, white text)
  - Date in secondary text color aligned to the right
  - Note text below in hint color
  - Amount in coral accent color (18sp bold)
- ExtendedFloatingActionButton with coral background, "Add expense" label, white icon + text
- Empty state: large money bag emoji (56sp) + secondary text instruction

**Search Tab**
- Frosted glass card contains the search form with:
  - Custom glass-styled TextInputLayout for keyword search (translucent bg, cyan border)
  - AutoCompleteTextView with ExposedDropdownMenu for category filter (dark dropdown items)
  - Date range pickers using glass-styled rounded boxes (translucent background, white border)
  - Two buttons: filled cyan "Search" + outlined "Reset" with glass border
- Result count in secondary text color
- ListView displays matching results using the same glass expense card layout

**Stats Tab**
- Glass stats header with cyan-tinted gradient background showing:
  - "Total spent" label in secondary color with letter spacing
  - Large amount in white (42sp bold)
  - Record count in secondary color
- PieChart wrapped in a frosted glass container:
  - Transparent hole (no white center) for dark theme compatibility
  - Legend text in secondary color
  - Category-colored slices with white percentage labels
- Category breakdown list: glass card rows with colored dot + emoji + category name + amount

**Add/Edit Expense Screen**
- Dark gradient background with frosted glass form card containing:
  - Amount field with dynamic currency prefix ($ or ¥) set from SessionManager
  - Category selector using Material ExposedDropdownMenu with dark dropdown items
  - Date field (non-editable, click opens dark-themed DatePickerDialog)
  - Multiline note input
  - All fields use custom glass TextInputLayout style (translucent bg, 12dp corners, cyan focus)
- Full-width cyan MaterialButton (56dp, 12dp corners) for save
- Keyboard auto-hides on save tap and date picker open

**All Dialogs (AlertDialog, DatePicker, Currency Picker)**
- Dark themed using MaterialAlertDialogBuilder with custom theme overlay
- Dark surface background (#1B2838), light text (#F0F0F0), cyan action buttons
- DatePickerDialog uses dark theme with cyan accent for selected date
- ProgressDialog (login) uses device default dark theme

### 1.3 Category Visual System

Each of the 11 expense categories has a unique vibrant color (optimized for dark backgrounds) and emoji:

| Category | Emoji | Color |
|---|---|---|
| Housing | House | Vibrant Purple (#7C4DFF) |
| Utilities | Light Bulb | Warm Orange (#FFB74D) |
| Transportation | Car | Sky Blue (#4FC3F7) |
| Groceries | Shopping Cart | Soft Green (#81C784) |
| Dining Out | Fork & Knife | Salmon Red (#FF8A80) |
| Healthcare | Hospital | Bright Cyan (#4DD0E1) |
| Entertainment | Clapper Board | Light Purple (#CE93D8) |
| Personal Care | Haircut | Pink (#F48FB1) |
| Shopping | Shopping Bags | Peach (#FFAB91) |
| Savings | Money Bag | Teal (#80CBC4) |
| Travel | Airplane | Warm Brown (#BCAAA4) |

### 1.4 Glassmorphism Implementation Details

The glass effect is achieved through Android XML shape drawables without any third-party blur libraries:

- **Glass card** (`bg_glass_card.xml`): Rectangle shape with `#33FFFFFF` (20% white) solid fill, 16dp rounded corners, and `#44FFFFFF` (27% white) 1dp border stroke
- **Glass card solid** (`bg_glass_card_solid.xml`): Darker variant `#CC1E2D3D` for expense cards and stat rows, maintaining readability for smaller text
- **Glass input** (`bg_glass_input.xml`): Subtle `#22FFFFFF` (13% white) fill for date picker boxes and input backgrounds
- **Glass stats header** (`bg_glass_stats_header.xml`): Gradient from `#4400BCD4` to `#4400838F` (translucent cyan) with 16dp corners and cyan border
- **Custom TextInputLayout styles**: Glass background color, 12dp corner radius on all four corners, cyan box stroke on focus, bright hint text color (#AABBCC)

---

## 2. Functional Design Logic

### 2.1 Application Flow

```
App Launch
    |
    v
SessionManager checks SharedPreferences
    |
    +-- User logged in --> MainActivity (3 tabs)
    |
    +-- Not logged in --> LoginActivity
                            |
                            +-- Login --> validate --> ProgressDialog --> MainActivity
                            |
                            +-- Register --> RegisterActivity --> back to Login (auto-fill)
```

### 2.2 Core Functions

**User Authentication**
- Registration: validate username uniqueness, password length (min 4 chars), password match confirmation. Passwords are hashed with SHA-256 before storage.
- Login: verify credentials against database, show ProgressDialog during authentication, save session to SharedPreferences on success.
- Session persistence: SessionManager stores user ID, username, and currency preference in SharedPreferences. App auto-navigates to main screen if session exists.
- Logout: clear SharedPreferences, navigate back to login screen.

**Expense CRUD Operations**
- Create: user fills amount, category (dropdown), date (DatePicker), optional note. Validated before insert. Keyboard auto-hides on save.
- Read: RecordsFragment loads all expenses for current user, displayed in RecyclerView sorted by date.
- Update: tap an expense card to open AddEditActivity in edit mode. Existing data pre-filled.
- Delete: long-press an expense card shows MaterialAlertDialog confirmation, then deletes from database.
- Clear All: menu option with MaterialAlertDialog confirmation deletes all records for current user.

**Conditional Search**
- Three filter criteria work together:
  - Keyword: searches in the note field (partial match)
  - Category: Material dropdown filter with "All" option
  - Date range: From and To dates via dark-themed DatePickerDialog
- Results displayed in ListView with result count
- Reset button clears all filters
- Keyboard auto-hides on search

**Currency Selection**
- Users can switch between USD ($) and CNY (¥) via dark-themed menu dialog
- Preference stored in SharedPreferences
- All amount displays update immediately across all tabs
- Currency prefix shown dynamically on the Add/Edit form amount field

### 2.3 Data Flow Diagram

```
UI Layer (Activities/Fragments)
        |
        v
DAO Layer (UserDao / ExpenseDao)
        |
        v
DBHelper (SQLiteOpenHelper singleton)
        |
        v
SQLite Database (expense_tracker.db)
```

---

## 3. Code Technology Implementation

### 3.1 Project Architecture

The project uses a layered architecture with clear separation of concerns:

```
com.example.expensetracker/
    db/
        DBHelper.java          -- SQLite database creation and migration
        UserDao.java           -- User authentication queries
        ExpenseDao.java        -- Expense CRUD and search queries
    model/
        User.java              -- User data model
        Expense.java           -- Expense data model
    ui/
        LoginActivity.java     -- Login screen (NoActionBar theme)
        RegisterActivity.java  -- Registration screen (NoActionBar theme)
        MainActivity.java      -- Main tabbed container (DarkActionBar theme)
        AddEditActivity.java   -- Add/Edit expense form
        adapters/
            ExpenseAdapter.java      -- RecyclerView adapter with category color tinting
            ExpenseListAdapter.java  -- ListView adapter with category color tinting
        fragments/
            RecordsFragment.java     -- Expense list tab with SwipeRefreshLayout
            SearchFragment.java      -- Search/filter tab with AutoCompleteTextView
            StatsFragment.java       -- Statistics tab with PieChart
    util/
        Categories.java        -- Category definitions (names, emojis, colors)
        PasswordUtil.java      -- SHA-256 password hashing
        SessionManager.java    -- SharedPreferences session, currency, and settings
```

### 3.2 Key Technologies Used

| Technology | Purpose |
|---|---|
| SQLite (SQLiteOpenHelper) | Local database for users and expenses |
| SharedPreferences | Session management and currency preference |
| ViewPager2 + TabLayout | Swipeable tabbed interface |
| FragmentStateAdapter | Fragment management for ViewPager2 |
| RecyclerView | Efficient scrollable expense list (Records tab) |
| ListView | Search results display (Search tab) |
| SwipeRefreshLayout | Pull-to-refresh on Records tab (dark-themed spinner) |
| MPAndroidChart (PieChart) | Visual spending breakdown with transparent hole for dark theme |
| TextInputLayout (OutlinedBox) | Custom glass-styled form fields with translucent background |
| ExposedDropdownMenu | Material-styled category dropdown with dark item layout |
| MaterialAlertDialogBuilder | Dark-themed confirmation dialogs |
| MaterialButton | Styled buttons (filled cyan, outlined glass, text) |
| ExtendedFloatingActionButton | Labeled action button for adding expenses |
| ProgressDialog | Loading indicator during login (dark theme) |
| DatePickerDialog | Date selection with dark theme overlay |
| SHA-256 (MessageDigest) | Password hashing for security |
| ActivityResultLauncher | Modern activity result handling (login/register flow) |
| GradientDrawable | Programmatic color tinting for category indicators and badges |
| XML Shape Drawables | Glassmorphism effects (gradients, translucent fills, border strokes) |
| Custom Theme Styles | Dark ActionBar, dark popup overlay, dark dialog themes |

### 3.3 Database Design

**Users Table**

| Column | Type | Constraints |
|---|---|---|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT |
| username | TEXT | UNIQUE NOT NULL |
| password | TEXT | NOT NULL (SHA-256 hash) |
| created_at | INTEGER | NOT NULL |

**Expenses Table**

| Column | Type | Constraints |
|---|---|---|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT |
| user_id | INTEGER | NOT NULL, FOREIGN KEY -> users(id) ON DELETE CASCADE |
| amount | REAL | NOT NULL |
| category | TEXT | NOT NULL |
| date | TEXT | NOT NULL (yyyy-MM-dd format) |
| note | TEXT | (optional) |
| created_at | INTEGER | NOT NULL |

**Indexes:** idx_exp_user (user_id), idx_exp_date (date)

**Database Migration:** Version 1 to 2 includes category name migration (e.g., "Food" -> "Groceries", "Transport" -> "Transportation") to support updated category system without data loss. Uses UPDATE SQL statements in onUpgrade instead of dropping and recreating tables.

### 3.4 Security Measures

- Passwords hashed with SHA-256 before database storage
- All SQL queries use parameterized statements to prevent SQL injection
- Foreign key constraints with CASCADE DELETE ensure data integrity
- Session validation on every activity launch

### 3.5 Third-Party Libraries

| Library | Version | Purpose |
|---|---|---|
| androidx.appcompat | 1.6.1 | Backward-compatible Activity and ActionBar |
| com.google.android.material | 1.10.0 | Material Design components (TextInputLayout, MaterialButton, MaterialAlertDialogBuilder, TabLayout, ExtendedFAB) |
| androidx.recyclerview | 1.3.2 | RecyclerView widget |
| androidx.viewpager2 | 1.0.0 | ViewPager2 for tab swiping |
| androidx.fragment | 1.6.2 | Fragment support |
| androidx.swiperefreshlayout | 1.1.0 | Pull-to-refresh |
| androidx.cardview | 1.0.0 | CardView widget |
| androidx.constraintlayout | 2.1.4 | Flexible layouts |
| MPAndroidChart | 3.1.0 | Pie chart visualization |

### 3.6 Glassmorphism Theme Architecture

The dark glassmorphism theme is implemented through a layered styling system:

1. **Color palette** (`colors.xml`): Dark backgrounds (#0D1B2A, #1B2838), translucent glass colors (#33FFFFFF, #22FFFFFF), bright text colors (#F0F0F0, #AABBCC), vibrant category colors optimized for dark backgrounds
2. **Shape drawables** (7 glass drawables): Translucent fills with white border strokes and rounded corners simulate frosted glass without third-party blur libraries
3. **Theme styles** (`themes.xml`): Custom ActionBar style with dark background and white title, dark popup overlay for menus, dark dialog themes for DatePicker and AlertDialog, custom TextInputLayout styles with glass background and cyan accents
4. **Layout XML**: All layouts use `@drawable/bg_gradient_main` as root background, glass card drawables as content containers, and explicit light text colors (`@color/white`, `@color/text_primary`, `@color/text_secondary`)
5. **Java runtime tinting**: ExpenseAdapter and ExpenseListAdapter use `GradientDrawable.mutate().setColor()` to dynamically tint category indicators and badges per item

---

## 4. Course Learning Summary

### 4.1 Android Fundamentals

Through this project, I gained practical experience with core Android development concepts:

- **Activity Lifecycle:** Understanding how onCreate, onResume, and onDestroy work, and how to properly manage state across configuration changes and activity transitions.
- **Fragment Lifecycle:** Learning how Fragments operate within Activities, including communication between Fragments and their host Activity through interfaces (Refreshable interface pattern).
- **Intent System:** Using explicit Intents for activity navigation, passing data between activities with extras, and using ActivityResultLauncher for the modern activity result pattern.

### 4.2 Data Persistence

- **SQLite Database:** Learned to design relational database schemas, write CRUD operations with parameterized queries, implement database versioning and migration strategies, and use the singleton pattern for DBHelper to prevent connection leaks.
- **SharedPreferences:** Used for lightweight key-value storage for session management and user preferences (currency selection).

### 4.3 UI Development

- **Glassmorphism Design:** Implemented a modern glassmorphism UI style using only Android XML shape drawables — translucent fills, gradient backgrounds, and border strokes create the frosted glass effect without any third-party blur libraries. Learned to balance visual style with readability by choosing appropriate opacity levels for different UI elements.
- **Dark Theme Implementation:** Discovered that implementing a dark theme on Android requires attention to many system components: ActionBar, popup menus, DatePickerDialog, AlertDialog, ProgressDialog, SwipeRefreshLayout spinner, and dropdown menus all need explicit dark styling. A single `android:textColorPrimary` change can break system dialogs. Learned to use theme overlays (`actionBarPopupTheme`, `materialAlertDialogTheme`, `android:datePickerDialogTheme`) to target specific components.
- **Material Design Components:** Applied Material Components library extensively, including TextInputLayout with custom styles, MaterialButton, MaterialAlertDialogBuilder, ExposedDropdownMenu, and ExtendedFloatingActionButton. Created reusable custom styles in themes.xml for consistent glass-styled inputs across all screens.
- **Adapter Pattern:** Implemented both RecyclerView.Adapter and BaseAdapter for different list display needs, understanding view recycling and the ViewHolder pattern for performance.
- **Custom Drawables:** Created XML shape drawables for gradients, rounded borders, translucent fills, and color indicators. Learned to tint them programmatically using `GradientDrawable.mutate().setColor()` to prevent shared-state color bleed across recycled views.

### 4.4 Challenges and Solutions

- **Spinner to AutoCompleteTextView Migration:** Replacing Spinner with Material ExposedDropdownMenu required understanding that AutoCompleteTextView needs `inputType="none"` to prevent free-text input, and `setText(value, false)` to set values without triggering the filter.
- **View Recycling Color Bugs:** When tinting GradientDrawable backgrounds in RecyclerView, recycled views would show wrong colors. Solved by calling `.mutate()` on the drawable before setting color, which creates an independent copy.
- **Database Category Migration:** When updating categories, existing records needed remapping. Implemented a proper onUpgrade migration with UPDATE SQL statements instead of dropping and recreating tables.
- **Dark Theme Color Conflicts:** Setting `android:textColorPrimary` to white for the ActionBar title caused system dialogs (DatePicker, AlertDialog) to show white text on white backgrounds. Solved by adding explicit dark theme overlays for each dialog type and using MaterialAlertDialogBuilder instead of plain AlertDialog.Builder.
- **Dropdown Menu Visibility:** Default `android.R.layout.simple_dropdown_item_1line` showed white text on white background in the dark theme. Created a custom `item_dropdown.xml` layout with explicit dark background and light text colors.
- **Keyboard Management:** Soft keyboard would remain visible after tapping Save or date fields. Added `InputMethodManager.hideSoftInputFromWindow()` calls before save actions and date picker launches in both AddEditActivity and SearchFragment.

### 4.5 Key Takeaways

This project reinforced the importance of separating concerns through a layered architecture (UI -> DAO -> Database), which makes the code easier to maintain and modify. The Material Design component library greatly simplifies creating a professional-looking interface, but requires careful theme configuration when deviating from standard light/dark themes. Working with SQLite taught me the fundamentals of relational database design, and implementing search with multiple filters demonstrated how to build parameterized queries dynamically. The glassmorphism implementation taught me that modern UI trends can be achieved on Android using only native XML drawables and theme overlays, without relying on third-party visual effect libraries. Overall, this project provided a comprehensive hands-on experience covering the full Android development workflow from database design to advanced UI theming.
