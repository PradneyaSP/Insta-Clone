# MyInsta - Instagram Clone Project Documentation

## 📋 Project Overview

MyInsta is an Android application that mimics core Instagram functionality, including user authentication, a social media feed with like functionality, and support for offline operations. The project demonstrates modern Android development practices using Kotlin, Room Database, Retrofit, and Coroutines.

### Key Features Implemented

1. **Login Screen** - User authentication with persistent login state
2. **Feed Screen** - Display posts with like functionality and offline support
3. **Reels Tab** - Placeholder for future video reel implementation

---

## 🏗️ Architecture Overview

### Architecture Pattern: MVVM (Model-View-ViewModel)

The project follows a layered architecture pattern with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                      UI Layer (View)                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ LoginActivity│  │ FeedFragment │  │ReelsFragment  │     │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘     │
│         │                  │                  │             │
└─────────┼──────────────────┼──────────────────┼─────────────┘
          │                  │                  │
          │         ┌────────▼────────┐         │
          │         │  FeedAdapter     │         │
          │         │  (RecyclerView)  │         │
          │         └────────┬─────────┘         │
          │                  │                  │
┌─────────┼──────────────────┼──────────────────┼─────────────┐
│         │                  │                  │             │
│  ┌──────▼──────┐   ┌───────▼────────┐        │             │
│  │ ViewModel   │   │  ViewModel      │        │             │
│  │ (Future)    │   │  (Future)       │        │             │
│  └──────┬──────┘   └───────┬─────────┘        │             │
│         │                  │                  │             │
└─────────┼──────────────────┼──────────────────┼─────────────┘
          │                  │                  │
┌─────────┼──────────────────┼──────────────────┼─────────────┐
│         │                  │                  │             │
│  ┌──────▼──────────────────▼──────────────────▼──────┐     │
│  │              Repository Layer                       │     │
│  │  ┌──────────────────────────────────────────────┐  │     │
│  │  │         PostRepository                       │  │     │
│  │  └───────┬──────────────────┬──────────────────┘  │     │
│  └──────────┼──────────────────┼─────────────────────┘     │
│             │                  │                            │
└─────────────┼──────────────────┼────────────────────────────┘
              │                  │
┌─────────────┼──────────────────┼────────────────────────────┐
│             │                  │                            │
│  ┌──────────▼──────────┐  ┌────▼──────────────┐          │
│  │   ApiService        │  │   PostDao          │          │
│  │   (Retrofit)       │  │   (Room)           │          │
│  └──────────┬──────────┘  └────┬──────────────┘          │
│             │                  │                            │
└─────────────┼──────────────────┼────────────────────────────┘
              │                  │
┌─────────────┼──────────────────┼────────────────────────────┐
│             │                  │                            │
│  ┌──────────▼──────────┐  ┌────▼──────────────┐          │
│  │   Remote Data      │  │   Local Database   │          │
│  │   (API Endpoints)   │  │   (Room DB)        │          │
│  └────────────────────┘  └───────────────────┘          │
│                                                           │
└───────────────────────────────────────────────────────────┘
```

### Data Flow

1. **UI Layer** (Fragment/Activity) observes data from Repository
2. **Repository Layer** manages data sources (API + Database)
3. **Data Layer** consists of Remote API (Retrofit) and Local Database (Room)
4. **Model Layer** contains data classes for API responses and database entities

---

## 📱 Feature Implementation Details

### Feature 1: Login Screen

#### Implementation Steps

1. **Created LoginActivity**
   - Used View Binding for UI access
   - Implemented edge-to-edge layout support
   - Added email and password input fields

2. **Authentication Logic**
   - Hardcoded credentials: `user@example.com` / `123456789`
   - Validates credentials on button click
   - Shows Toast message for incorrect credentials

3. **Login State Persistence**
   - Used `SharedPreferences` with key `"UserPrefs"`
   - Stores boolean flag `"isLoggedIn"` on successful login
   - MainActivity checks this flag on startup

4. **Navigation**
   - On successful login, navigates to MainActivity
   - Uses Intent flags: `FLAG_ACTIVITY_CLEAR_TASK` and `FLAG_ACTIVITY_NO_HISTORY`
   - Prevents back navigation to login screen

#### Key Files
- `LoginActivity.kt` - Handles login UI and logic
- `activity_login.xml` - Login screen layout
- `MainActivity.kt` - Checks login state on startup

---

### Feature 2: Feed Screen

#### Implementation Steps

1. **Data Models**
   - Created `Post` data class with Room annotations
   - Fields: `postId`, `userName`, `userImage`, `postImage`, `likeCount`, `likedByUser`
   - Created `FeedResponse` for API response parsing
   - Created `LikeRequest` and `LikeResponse` for like API calls

2. **Database Setup (Room)**
   - Created `AppDatabase` with singleton pattern
   - Created `PostDao` interface with suspend functions:
     - `getAllPosts()` - Fetch all posts
     - `updateAllPosts()` - Insert/update posts
     - `updateLikeStatus()` - Update like status for a post
   - Used KSP (Kotlin Symbol Processing) for code generation
   - Database version: 1

3. **API Integration (Retrofit + Moshi)**
   - Created `RetrofitInstance` singleton
   - Base URL: `https://dfbf9976-22e3-4bb2-ae02-286dfd0d7c42.mock.pstmn.io/`
   - Configured Moshi converter for JSON parsing
   - Created `ApiService` interface with endpoints:
     - `GET /user/feed` - Fetch feed
     - `POST /user/like` - Like a post
     - `DELETE /user/dislike` - Unlike a post

4. **Repository Pattern**
   - Created `PostRepository` to manage data sources
   - `getPosts()` function:
     - Tries to fetch from API first
     - On success: Updates Room database and returns posts
     - On failure: Falls back to Room database (offline mode)
   - `updateLikeStatus()` function:
     - Updates Room database immediately (optimistic update)
     - Calls appropriate API endpoint (like/dislike)
     - Handles API failures gracefully

5. **UI Implementation**
   - Created `FeedFragment` with View Binding
   - Implemented `FeedAdapter` for RecyclerView
   - Used `LinearLayoutManager` for vertical scrolling
   - Added loading state with ProgressBar
   - Added error state with TextView
   - Image loading using Glide library

6. **Like Functionality**
   - Optimistic UI update: Updates UI immediately
   - Creates new Post instance using `copy()` (immutability)
   - Updates local database first
   - Calls API in background coroutine
   - Updates RecyclerView item on success
   - Heart icon changes based on `likedByUser` state

7. **Offline Architecture**
   - Repository automatically falls back to Room database
   - Shows cached data when network is unavailable
   - Like actions are stored locally and synced when network returns
   - Error message displayed when both API and database fail

#### Key Files
- `FeedFragment.kt` - Main feed UI logic
- `FeedAdapter.kt` - RecyclerView adapter for posts
- `PostRepository.kt` - Data management layer
- `PostDao.kt` - Database access interface
- `AppDatabase.kt` - Room database instance
- `ApiService.kt` - Retrofit API interface
- `RetrofitInstance.kt` - Retrofit configuration
- `Post.kt` - Data model

#### Technical Decisions

1. **Coroutines**: Used `lifecycleScope.launch` for async operations
2. **Suspend Functions**: All database and API calls are suspend functions
3. **Immutability**: Post data class uses `copy()` for updates
4. **View Binding**: Used throughout for type-safe view access
5. **Error Handling**: Try-catch blocks for graceful error handling

---

### Feature 3: Reels Tab (Placeholder)

#### Current Status
- Created `ReelsFragment` with basic structure
- Layout file exists but not fully implemented
- Ready for future implementation with ViewPager2 and ExoPlayer

#### Planned Implementation
- Vertical ViewPager2 for swipeable reels
- ExoPlayer for video playback
- Auto-play visible reel, pause others
- Mute/unmute functionality
- Similar offline architecture as Feed

---

## 🛠️ Technical Stack

### Core Technologies
- **Language**: Kotlin
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36 (Android 15)
- **Compile SDK**: 36

### Libraries & Frameworks

#### UI & Navigation
- **View Binding** - Type-safe view access
- **Navigation Component** - Fragment navigation
- **Material Design Components** - UI components
- **RecyclerView** - List display
- **Glide** - Image loading and caching
- **CircleImageView** - Circular profile images

#### Data & Networking
- **Room Database** - Local data persistence
- **Retrofit** - HTTP client for API calls
- **Moshi** - JSON serialization/deserialization
- **KSP** - Kotlin Symbol Processing for Room

#### Concurrency
- **Kotlin Coroutines** - Asynchronous programming
- **lifecycleScope** - Lifecycle-aware coroutine scope

#### Architecture Components
- **Fragment** - UI components
- **SharedPreferences** - Simple data storage (login state)

---

## 📂 Project Structure

```
app/src/main/java/com/example/myinsta/
├── api/
│   ├── ApiService.kt          # Retrofit API interface
│   └── RetrofitInstance.kt    # Retrofit configuration
├── data/
│   ├── AppDatabase.kt         # Room database instance
│   ├── PostDao.kt             # Database access interface
│   └── PostRepository.kt      # Repository for data management
├── model/
│   ├── Post.kt                # Post data model (Room entity)
│   ├── LikeRequest.kt         # Like API request model
│   └── LikeResponse.kt        # Like API response model
├── ui/
│   ├── feed/
│   │   ├── FeedFragment.kt    # Feed screen fragment
│   │   ├── FeedAdapter.kt     # RecyclerView adapter
│   │   └── FeedViewModel.kt   # ViewModel (placeholder)
│   ├── reels/
│   │   └── ReelsFragment.kt   # Reels screen fragment
│   └── profile/
│       └── ProfileFragment.kt # Profile screen fragment
├── LoginActivity.kt           # Login screen activity
└── MainActivity.kt            # Main activity with navigation
```

---

## 🔄 Data Flow Example: Loading Feed

```
1. User opens FeedFragment
   ↓
2. FeedFragment calls loadFeed()
   ↓
3. Shows ProgressBar (loading state)
   ↓
4. Launches coroutine in lifecycleScope
   ↓
5. Calls repository.getPosts()
   ↓
6. Repository tries API call first
   ├─ Success: Updates Room DB → Returns posts
   └─ Failure: Fetches from Room DB → Returns cached posts
   ↓
7. FeedFragment receives posts
   ↓
8. Updates adapter.posts list
   ↓
9. Calls adapter.notifyItemRangeInserted()
   ↓
10. Hides ProgressBar, shows RecyclerView
```

---

## 🔄 Data Flow Example: Like Action

```
1. User clicks like button
   ↓
2. FeedAdapter creates updatedPost using copy()
   ↓
3. Updates UI immediately (optimistic update)
   - Changes heart icon
   - Updates like count
   ↓
4. Updates posts list: posts[position] = updatedPost
   ↓
5. Calls notifyItemChanged(position)
   ↓
6. Launches coroutine for API call
   ↓
7. Calls repository.updateLikeStatus(updatedPost)
   ↓
8. Repository updates Room database first
   ↓
9. Repository calls API (like/dislike endpoint)
   ├─ Success: Changes persisted
   └─ Failure: Logged, but UI already updated
```

---

## 🎯 Key Implementation Highlights

### 1. Offline-First Architecture
- Repository pattern ensures data availability offline
- Room database acts as cache
- Automatic fallback when network fails
- Local changes synced when network returns

### 2. Optimistic UI Updates
- UI updates immediately for better UX
- Background sync with server
- Graceful error handling

### 3. Modern Android Practices
- Coroutines for async operations
- Suspend functions for database/API calls
- View Binding for type safety
- Lifecycle-aware components

### 4. Code Quality
- Immutable data classes
- Separation of concerns (Repository pattern)
- Error handling at multiple layers
- Clean architecture principles

---

## 🐛 Challenges & Solutions

### Challenge 1: Room Database Implementation
**Problem**: Room annotation processor not generating implementation classes  
**Solution**: 
- Added KSP plugin
- Used `ksp()` instead of `kapt()` for Room compiler
- Configured Room schema export settings

### Challenge 2: Main Thread Database Queries
**Problem**: Initially used `allowMainThreadQueries()` which blocks UI  
**Solution**:
- Removed `allowMainThreadQueries()`
- Made all DAO functions `suspend` functions
- Used coroutines with `lifecycleScope` for database operations

### Challenge 3: Adapter Lifecycle
**Problem**: Creating adapter inside coroutine caused issues  
**Solution**:
- Created adapter outside coroutine with empty list
- Updated adapter data after fetching from repository
- Used `notifyItemRangeInserted()` for efficient updates

### Challenge 4: Like Button State Management
**Problem**: Mutating data class properties directly  
**Solution**:
- Used `copy()` method to create new Post instances
- Maintained immutability of data classes
- Updated list reference instead of mutating objects

---

## 📊 Database Schema

### Posts Table
```sql
CREATE TABLE posts (
    postId TEXT PRIMARY KEY,
    userName TEXT,
    userImage TEXT,
    postImage TEXT,
    likeCount INTEGER,
    likedByUser INTEGER (Boolean)
)
```

---

## 🔌 API Endpoints

### Base URL
```
https://dfbf9976-22e3-4bb2-ae02-286dfd0d7c42.mock.pstmn.io/
```

### Endpoints

1. **GET /user/feed**
   - Returns: `FeedResponse` containing list of posts
   - Used for fetching feed data

2. **POST /user/like**
   - Body: `LikeRequest` (postId, isLiked)
   - Returns: `LikeResponse`
   - Used for liking a post

3. **DELETE /user/dislike**
   - Body: `LikeRequest` (postId, isLiked)
   - Returns: `LikeResponse`
   - Used for unliking a post

---

## 🚀 Future Enhancements

1. **ViewModel Implementation**
   - Move business logic from Fragment to ViewModel
   - Use StateFlow/LiveData for reactive data
   - Better lifecycle management

2. **Reels Feature**
   - Implement ViewPager2 for vertical scrolling
   - Integrate ExoPlayer for video playback
   - Add mute/unmute functionality

3. **Error Handling**
   - Add Snackbar for like action failures
   - Implement retry mechanism
   - Better error messages

4. **Testing**
   - Unit tests for Repository
   - Unit tests for ViewModel (when implemented)
   - UI tests for critical flows

5. **Performance**
   - Implement pagination for feed
   - Add image caching strategies
   - Optimize RecyclerView performance

---

## 📝 Code Quality Improvements Made

1. ✅ Removed `allowMainThreadQueries()` from Room
2. ✅ Made all DAO functions suspend functions
3. ✅ Fixed SQL syntax error in `updateLikeStatus` query
4. ✅ Improved adapter lifecycle management
5. ✅ Used immutable data classes with `copy()`
6. ✅ Added proper error handling
7. ✅ Implemented loading and error states
8. ✅ Optimized RecyclerView updates

---

## 🎓 Learning Outcomes

Through this project, the following concepts were implemented and learned:

1. **Room Database**: Local data persistence with offline support
2. **Retrofit**: RESTful API integration
3. **Coroutines**: Asynchronous programming in Kotlin
4. **Repository Pattern**: Data source abstraction
5. **View Binding**: Type-safe view access
6. **RecyclerView**: Efficient list rendering
7. **SharedPreferences**: Simple key-value storage
8. **Navigation Component**: Fragment-based navigation
9. **Error Handling**: Graceful error management
10. **Offline Architecture**: Caching and sync strategies

---

## 📚 References & Resources

- [Android Room Persistence Library](https://developer.android.com/training/data-storage/room)
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Android Architecture Components](https://developer.android.com/topic/architecture)
- [Moshi Documentation](https://github.com/square/moshi)

---

## 📱 Feed Fragment Implementation Details

### Overview

The Feed Fragment is the core feature of the application, implementing a complete MVVM architecture pattern with proper lifecycle management, state persistence, and reactive data updates.

### Architecture Components

#### 1. FeedViewModel

The ViewModel serves as the single source of truth for feed data, managing UI-related data in a lifecycle-aware manner.

**Key Features:**
- **Lifecycle Survival**: Survives configuration changes (screen rotation)
- **State Management**: Manages feed data, loading state, and error state
- **Repository Integration**: Communicates with PostRepository for data operations

**LiveData Exposed:**
- `feed: LiveData<List<Post>>` - The list of posts to display
- `isLoading: LiveData<Boolean>` - Loading state indicator
- `error: LiveData<String?>` - Error message state

**Key Functions:**
```kotlin
fun loadFeed() // Fetches posts from repository
fun toggleLikeStatus(updatedPost: Post) // Updates a single post's like status
```

**Implementation Highlights:**
- Uses `viewModelScope` for coroutine management
- Automatically cancels coroutines when ViewModel is cleared
- Updates LiveData on background thread, observers receive on main thread
- Null-safe implementation with early returns

#### 2. FeedViewModelFactory

Custom ViewModelProvider.Factory to inject dependencies (PostRepository) into ViewModel.

**Purpose:**
- Enables dependency injection for ViewModel
- Required because ViewModel has constructor parameters
- Ensures single instance of ViewModel per Fragment scope

#### 3. FeedFragment

The UI layer that observes ViewModel and updates the UI accordingly.

**Lifecycle Management:**
- Uses `viewLifecycleOwner` for LiveData observation
- Properly handles view binding lifecycle
- Observers automatically cleaned up when view is destroyed

**Key Responsibilities:**
- Initialize ViewModel with repository dependencies
- Set up RecyclerView and adapter
- Observe ViewModel's LiveData for UI updates
- Manage loading, error, and success states

**Observer Pattern:**
```kotlin
viewModel.feed.observe(viewLifecycleOwner) { feed ->
    // Update adapter when feed data changes
}

viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
    // Show/hide loading indicator
}

viewModel.error.observe(viewLifecycleOwner) { error ->
    // Show/hide error message
}
```

#### 4. FeedAdapter

RecyclerView adapter that displays posts and handles user interactions.

**Key Features:**
- Receives ViewModel reference for updating like status
- Uses Glide for image loading
- Implements optimistic UI updates
- Handles like button clicks

**Like Functionality Flow:**
1. User clicks like button
2. Create updated Post instance using `copy()`
3. Update repository (database + API) in background
4. Update ViewModel immediately (optimistic update)
5. Observer receives update and refreshes adapter

### Data Flow Architecture

```
User Action (Like Button Click)
    ↓
FeedAdapter creates updatedPost
    ↓
Repository.updateLikeStatus() [Background]
    ├─ Updates Room Database
    └─ Calls API (like/dislike endpoint)
    ↓
ViewModel.toggleLikeStatus(updatedPost)
    ├─ Updates _feed.value with new post
    └─ Triggers LiveData observer
    ↓
FeedFragment Observer receives update
    ├─ Updates adapter.posts list
    └─ Calls notifyDataSetChanged()
    ↓
RecyclerView refreshes UI
```

### State Management

#### Loading State Flow

```
Initial Load:
1. Fragment created → Check if ViewModel has data
2. If empty → Call viewModel.loadFeed()
3. isLoading = true → Show ProgressBar
4. Repository fetches data (API or Database)
5. isLoading = false → Hide ProgressBar
6. feed updated → Show RecyclerView
```

#### Like Action State Flow

```
Like Button Clicked:
1. Create updatedPost (optimistic update)
2. Update ViewModel immediately (UI updates instantly)
3. Background: Update database and API
4. ViewModel LiveData triggers observer
5. Adapter refreshes with new data
6. State persists across rotations
```

### Configuration Change Handling

**Problem Solved:**
- Previously, data was reloaded on every screen rotation
- Like changes were lost after rotation

**Solution:**
- ViewModel survives configuration changes
- Check if data exists before reloading: `if (viewModel.feed.value.isNullOrEmpty())`
- ViewModel maintains latest state including like changes
- Observer automatically receives data when Fragment recreates

### Key Implementation Details

#### 1. Null Safety

**FeedViewModel.toggleLikeStatus():**
```kotlin
val currentFeed = _feed.value ?: return  // Early return if null
```

**FeedFragment Observer:**
```kotlin
feed?.let {  // Null-safe handling
    // Update adapter
}
```

#### 2. Single Source of Truth

- ViewModel is the only place that updates feed data
- Adapter doesn't directly modify its list
- All updates flow through: ViewModel → Observer → Adapter

#### 3. Optimistic Updates

- UI updates immediately when like is clicked
- Background sync happens asynchronously
- Better user experience (no waiting for API response)

#### 4. Error Handling

- Repository handles network errors gracefully
- Falls back to database on network failure
- ViewModel exposes error state for UI display
- User sees appropriate error messages

### UI State Management

#### Three States:

1. **Loading State**
   - ProgressBar visible
   - RecyclerView hidden
   - Error TextView hidden

2. **Success State**
   - ProgressBar hidden
   - RecyclerView visible (if data exists)
   - Error TextView hidden

3. **Error State**
   - ProgressBar hidden
   - RecyclerView hidden
   - Error TextView visible

### Benefits of MVVM Implementation

1. **Separation of Concerns**
   - Fragment handles UI only
   - ViewModel handles business logic
   - Repository handles data operations

2. **Testability**
   - ViewModel can be unit tested independently
   - No Android framework dependencies in ViewModel
   - Easy to mock Repository for testing

3. **Lifecycle Awareness**
   - ViewModel survives configuration changes
   - Automatic cleanup of observers
   - No memory leaks

4. **Reactive Updates**
   - UI automatically updates when data changes
   - No manual refresh needed
   - Consistent state across UI

### Code Quality Improvements

#### Issues Fixed:

1. ✅ **Removed `allowMainThreadQueries()`** - All database operations use coroutines
2. ✅ **Made DAO functions suspend** - Proper async handling
3. ✅ **Fixed adapter lifecycle** - Created outside coroutine
4. ✅ **Added error handling** - Loading and error states
5. ✅ **Implemented ViewModel** - Proper state management
6. ✅ **Fixed rotation issues** - Data persists across rotations
7. ✅ **Fixed like persistence** - Changes survive rotation
8. ✅ **Null safety** - Proper null checks throughout
9. ✅ **Single source of truth** - ViewModel manages all data

### Performance Optimizations

1. **Efficient Notifications**
   - Uses `notifyDataSetChanged()` for simplicity
   - Could be optimized with DiffUtil for better performance

2. **Image Loading**
   - Glide handles caching automatically
   - Images loaded asynchronously
   - Memory-efficient image handling

3. **Coroutine Management**
   - Uses `viewModelScope` for automatic cancellation
   - Uses `lifecycleScope` for Fragment-scoped operations
   - Proper cleanup on lifecycle events

### Future Enhancements

1. **DiffUtil Implementation**
   - Replace `notifyDataSetChanged()` with DiffUtil
   - Animate item changes
   - Better performance for large lists

2. **Pagination**
   - Load posts in batches
   - Infinite scroll functionality
   - Better memory management

3. **Pull-to-Refresh**
   - SwipeRefreshLayout integration
   - Manual refresh capability
   - Visual feedback

4. **StateFlow Migration**
   - Consider migrating from LiveData to StateFlow
   - More Kotlin-friendly
   - Better coroutine integration

---

## ✨ Conclusion

This project demonstrates a solid understanding of modern Android development practices, including:
- Clean architecture principles
- Offline-first data management
- Modern Kotlin features (coroutines, data classes)
- Android Jetpack components
- Best practices for UI/UX

The codebase is well-structured, maintainable, and ready for future enhancements.

---

**Project Status**: ✅ Feed Feature Complete | ✅ ViewModel Implementation Complete | 🔄 Reels Feature Pending

**Last Updated**: Current implementation reflects the latest codebase state with all recent improvements and fixes applied.
