Expense Tracker — Android App
================================================

How to run
----------
1. Open Android Studio (Hedgehog 2023.1.1 or later).
2. File -> Open -> choose this ExpenseTracker folder.
3. Wait for Gradle sync to finish (it will download dependencies on first run).
4. Run on an emulator or device with Android 7.0+ (API 24+).

What is included in this base
-----------------------------
- Login + Register (with SHA-256 password hashing)
- SQLite database (users + expenses tables, foreign key cascade delete)
- Three-tab main screen via ViewPager2 + TabLayout:
    * Records   — RecyclerView, pull-to-refresh, FAB to add, long-press to delete
    * Search    — ListView results, filter by keyword/category/date range
    * Stats     — total spent, pie chart by category, full breakdown
- Add / Edit screen with DatePicker and category Spinner
- ActionBar overflow menu (Clear all / About / Logout) with AlertDialogs
- ProgressDialog during login
- Session persistence via SharedPreferences

Folder layout
-------------
app/src/main/java/com/example/expensetracker/
  db/        SQLite layer (DBHelper, UserDao, ExpenseDao)
  model/     Plain data classes (User, Expense)
  ui/        Activities
    fragments/   Three tab fragments
    adapters/    RecyclerView + ListView adapters
  util/      PasswordUtil, SessionManager, Categories


