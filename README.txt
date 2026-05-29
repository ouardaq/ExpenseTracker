Expense Tracker — Android Final Project (Base)
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

Project requirements covered
----------------------------
[x] Login                     -> LoginActivity
[x] Record addition           -> AddEditActivity + ExpenseDao.insert()
[x] Record list               -> RecordsFragment (RecyclerView)
[x] Conditional search        -> SearchFragment + ExpenseDao.search()
[x] Modification              -> AddEditActivity (passes EXTRA_ID)
[x] Deletion                  -> RecordsFragment long-press
[x] SQLite database           -> db/DBHelper.java
[x] ListView                  -> SearchFragment
[x] RecyclerView              -> RecordsFragment
[x] AlertDialog               -> register, delete, logout, clear-all, about
[x] ProgressDialog            -> LoginActivity
[x] Menu                      -> res/menu/main_menu.xml
[x] ActionBar                 -> default app bar in MainActivity
[x] ViewPager + Fragment      -> MainActivity + 3 Fragments
[x] Pull-to-refresh           -> RecordsFragment (SwipeRefreshLayout)

Folder layout
-------------
app/src/main/java/com/example/expensetracker/
  db/        SQLite layer (DBHelper, UserDao, ExpenseDao)
  model/     Plain data classes (User, Expense)
  ui/        Activities
    fragments/   Three tab fragments
    adapters/    RecyclerView + ListView adapters
  util/      PasswordUtil, SessionManager, Categories

What to do next (optional polish for higher grades)
---------------------------------------------------
- Add input validation feedback inline (TextInputLayout)
- Add a settings screen (currency symbol, theme color)
- Localize strings (e.g. Chinese translations in values-zh)

Design document
---------------
DesignDocument.docx in the project root covers:
- Project overview and requirements coverage table
- UI design ideas (palette, layout, screen walkthrough)
- Functional design logic (database schema, flows, search query)
- Code technology implementation (project structure, key technologies)
- Testing matrix
- Course learning summary

Note: MPAndroidChart is pulled from JitPack, which is configured in
settings.gradle. The first Gradle sync needs an internet connection
to download it.
