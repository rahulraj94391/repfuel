# Gym + Nutrition Android App --- Project Context

## Purpose

This document captures the current product discussion so another AI
model can continue the work without needing the previous conversation.

The user is an Android developer who primarily uses Kotlin and wants to
explore/build a useful gym + eating-habits Android app. The project can
also serve as a strong Android SDE2 portfolio/LLD practice project.

------------------------------------------------------------------------

# 1. Core Product Idea

The initial idea was an Android app for:

-   Gym/workout tracking
-   Food/eating habit tracking
-   Weight and body-progress tracking
-   Daily habits
-   Potential on-device AI for food-photo recognition

The product should not become a generic, bloated fitness app.

The core product philosophy discussed:

> Make the app answer: "What should I do today, and am I actually
> progressing?"

A major goal is to minimize daily friction.

------------------------------------------------------------------------

# 2. Proposed Main Experience

The app should have a strong "Today" screen.

Example:

``` text
TODAY

Workout
Upper Body
4 / 6 exercises

Bench Press
60 kg × 8
60 kg × 8
60 kg × 7

Next → Incline DB Press

Nutrition
1,720 / 2,200 kcal
124 / 150 g protein

Remaining
480 kcal · 26g protein

Habits
✓ Workout
✓ Protein target
○ 8k steps
✓ 3L water
○ Sleep before 12
```

The user should not have to navigate through many screens during a
workout.

------------------------------------------------------------------------

# 3. Workout Features

## Workout plans

Support:

-   Push/Pull/Legs
-   Custom workout routines
-   Weekly schedule
-   Rest days
-   Custom exercises

Example:

``` text
Monday    → Chest + Triceps
Tuesday   → Back + Biceps
Wednesday → Rest
```

## Exercise tracking

Track:

-   Exercise
-   Sets
-   Reps
-   Weight
-   Previous performance
-   Notes

Example:

``` text
Bench Press

Previous:
60 × 8
60 × 8
57.5 × 9

Today:
60 × 8 ✓
60 × 8 ✓
60 × __
```

## Progressive overload

The app can suggest progression.

Example:

> Last session: 60 × 8 × 2 You completed the target reps. Suggested
> today: 62.5 kg.

Important: suggestions should remain suggestions, not automatic
decisions.

## Rest timer

After a set:

``` text
REST

01:42

Next:
Bench Press — Set 3

[ Skip ] [ +30 sec ]
```

Possible defaults:

-   Compound: 2--3 min
-   Isolation: 60--90 sec

Allow customization.

------------------------------------------------------------------------

# 4. Workout History / Progress

Track:

-   Best weight
-   Best reps
-   Estimated 1RM
-   Total volume
-   Weekly volume
-   Personal records
-   Workout consistency

Potential charts:

-   Weight progression
-   Volume progression
-   Estimated 1RM
-   Exercise PRs

------------------------------------------------------------------------

# 5. Food Tracking

Food logging should be fast.

Potential actions:

``` text
+ Add Food

Search food
Scan meal photo
Recent foods
Saved meals
Create meal
```

Saved meals are important.

Example:

``` text
Chicken Rice Bowl

650 kcal
48g protein
```

User should be able to add the entire saved meal with one tap.

------------------------------------------------------------------------

# 6. Indian Food Support

Because the target/context is India, Indian food is potentially
important.

Potential foods:

-   Rice
-   Dal
-   Rajma
-   Chole
-   Sambar
-   Idli
-   Dosa
-   Poha
-   Upma
-   Roti
-   Chapati
-   Paratha
-   Paneer
-   Chicken curry
-   Mutton curry
-   Biryani
-   etc.

Useful serving units:

-   1 bowl
-   1 katori
-   1 roti
-   1 dosa
-   1 piece
-   1 ladle
-   1 plate
-   1 glass

Do not force users to enter grams for everything.

------------------------------------------------------------------------

# 7. Protein-First Nutrition

Protein can be a prominent metric.

Example:

``` text
Protein

124 / 150 g

████████████████░░

26g remaining
```

Potential suggestions:

``` text
200g Greek yogurt → +20g
4 eggs            → +24g
100g chicken      → +27g
```

Calories remain important, but protein can be more actionable for gym
users trying to preserve/build muscle.

------------------------------------------------------------------------

# 8. Weight / Body Tracking

Track:

-   Body weight
-   Waist
-   Chest
-   Arms
-   Thighs
-   Progress photos

Weight should emphasize trends rather than daily fluctuations.

Example:

``` text
Today       78.4 kg
7-day avg   78.8 kg
30-day avg  79.6 kg
```

Potential progress-photo system:

-   Front
-   Side
-   Back
-   Same approximate pose/distance
-   Timeline comparison

------------------------------------------------------------------------

# 9. Habit Tracking

Keep habits related to fitness/nutrition.

Examples:

``` text
✓ Workout
✓ Protein target
○ 8k steps
✓ 3L water
○ Sleep before 12
```

Avoid turning the product into a generic habit tracker.

------------------------------------------------------------------------

# 10. Weekly Review

A weekly review could be a high-value feature.

Example:

``` text
YOUR WEEK

Workouts       4 / 4
Protein        87% days
Steps          61k
Avg calories   2,140
Weight         -0.4 kg
Sleep          6h 48m

What improved
↑ Bench press +2.5 kg
↑ Workout consistency

Needs attention
↓ Protein on rest days
↓ Sleep average
```

Goal:

> Help the user understand whether they are actually progressing.

------------------------------------------------------------------------

# 11. AI Food Photo Recognition

A major discussed feature:

> User uploads/takes a photo of a food plate and AI detects basic food
> items so the user doesn't need to manually search for every food.

Desired UX:

``` text
Take photo
    ↓
On-device AI
    ↓
Detected foods
    ↓
User confirms/corrects
    ↓
User adjusts quantities
    ↓
Nutrition database calculates nutrition
    ↓
Add meal
```

Example:

``` text
AI detected:

✓ Rice
✓ Dal
✓ Chicken
✓ Cucumber
✓ Roti

[ Add all ]
```

Then:

``` text
Lunch

Rice             1 bowl
Dal              1 bowl
Chicken curry    2 pieces
Roti             2
Cucumber         1 serving

[ Add Meal ]
```

## Important product decision

The AI should primarily **identify food items**, not pretend to know
exact nutrition from a photograph.

Prefer:

``` json
{
  "items": [
    {
      "name": "rice",
      "confidence": 0.94
    },
    {
      "name": "dal",
      "confidence": 0.87
    },
    {
      "name": "chicken curry",
      "confidence": 0.78
    }
  ]
}
```

Avoid pretending that a single photo can reliably determine:

``` json
{
  "rice": "183g",
  "calories": "237"
}
```

Portion estimation from photos is inherently uncertain, especially for
mixed dishes and hidden ingredients such as oil.

Therefore:

> AI proposes → user confirms → nutrition database determines logged
> values.

------------------------------------------------------------------------

# 12. On-Device AI

The user specifically asked whether on-device AI can be used.

Relevant Android technologies discussed:

## Gemini Nano / ML Kit GenAI

Potential architecture:

``` text
Camera
   ↓
Bitmap
   ↓
On-device Gemini Nano
   ↓
Food candidates
```

This can provide multimodal image understanding on supported Android
devices.

Important:

-   Device/model support varies.
-   Do not assume every Android phone supports Gemini Nano/AICore.
-   Treat on-device AI as a capability with fallback.
-   The feature should work when the user is actively scanning a meal.
-   Do not make the entire application dependent on AI availability.

## Custom ML / LiteRT / ML Kit

A custom food recognition model could eventually recognize Indian food
categories.

Possible categories:

``` text
Rice
Dal
Roti
Chapati
Paratha
Rajma
Chole
Paneer
Chicken curry
Mutton curry
Sambar
Idli
Dosa
Poha
Upma
...
```

Traditional ML/image labeling alone may not be sufficient for a serious
food-recognition system; a specialized/custom model would be more
appropriate.

------------------------------------------------------------------------

# 13. Recommended AI Architecture

Potential abstraction:

``` text
FoodRecognitionRepository
        │
        ├── On-device AI available?
        │       │
        │       ├── YES → Gemini Nano
        │       │
        │       └── NO → Custom model / fallback
        │
        └── return FoodCandidates
```

Possible Android architecture:

``` text
Compose UI
   ↓
ViewModel
   ↓
Use Cases
   ↓
FoodRecognitionRepository
   ├── GeminiNanoFoodRecognizer
   ├── CustomModelFoodRecognizer
   └── FallbackRecognizer
   ↓
FoodRepository
   ↓
Room
   ↓
Sync Engine
   ↓
Backend
```

The exact architecture is not finalized.

------------------------------------------------------------------------

# 14. Food Normalization

AI output should not directly become the final food item.

Example:

``` text
AI:
"chicken curry"

        ↓

Food database search:

Chicken Curry
Chicken curry with gravy
Chicken breast curry
...

        ↓

User selects the appropriate food

        ↓

User chooses:
2 pieces

        ↓

Nutrition calculated
```

This separates:

1.  Computer vision / AI recognition
2.  Food normalization
3.  Nutrition lookup
4.  User confirmation

This separation is important for accuracy and architecture.

------------------------------------------------------------------------

# 15. Competitor Landscape Discussed

Current competitors identified:

-   HealthifyMe
-   MyFitnessPal
-   Lose It!
-   Cronometer
-   Cal AI
-   Foodvisor
-   SnapCalorie
-   MacroFactor

HealthifyMe is especially important because it is a direct competitor in
India.

HealthifyMe currently offers:

-   AI photo-based food logging ("Snap")
-   AI nutrition coach (Ria)
-   Food/calorie/macro tracking
-   Workout/activity tracking
-   Water/sleep/weight/steps
-   Health integrations
-   Large Indian-food database
-   Indian-cuisine-focused recognition
-   Personalized diet/workout plans
-   Human coaching

Therefore:

> Do NOT build simply "a smaller HealthifyMe."

The original concept of "gym + food photo AI" is already close to
HealthifyMe's product.

------------------------------------------------------------------------

# 16. Potential Differentiation

The strongest discussed direction is:

> Gym-first + extremely fast food logging.

The food AI is not necessarily the product itself.

The value proposition is:

> Take a photo → AI detects foods → user adjusts quantity → Add meal.

The AI exists to reduce logging friction.

Potential Today screen:

``` text
TODAY

🏋️ Push Day
Bench 60 × 8

🍗 124 / 150g protein
🔥 1,720 / 2,200 kcal

📷 Scan Meal
```

The app should connect workout and nutrition progress rather than
treating them as two unrelated trackers.

------------------------------------------------------------------------

# 17. Possible Android Integrations

Potential future features:

## Health Connect

Read/write relevant data such as:

-   Steps
-   Calories
-   Sleep
-   Weight
-   Exercise sessions

## Notifications

Examples:

> Workout scheduled in 30 min.

> You're 25g short of your protein target.

> Weekly progress is ready.

## Home-screen widget

Example:

``` text
TODAY

🏋️ Push
🍗 124 / 150g protein
🔥 1,720 / 2,200 kcal
🚶 6,240 steps
```

------------------------------------------------------------------------

# 18. Suggested MVP

Do not build everything initially.

Recommended MVP:

## Home

``` text
Today
 ├── Workout
 ├── Calories
 ├── Protein
 ├── Steps
 └── Habits
```

## Gym

``` text
Workout Plans
     ↓
Exercises
     ↓
Sets / Reps / Weight
     ↓
Rest Timer
```

## Food

``` text
Search food
Recent food
Saved meals
Calories
Protein
```

## AI Food Scan

``` text
Take photo
     ↓
Detect food items
     ↓
Confirm/correct
     ↓
Adjust quantity
     ↓
Add meal
```

## Progress

``` text
Weight
Workout PRs
Volume
Progress photos
```

## Weekly Review

``` text
Workout consistency
Protein adherence
Weight trend
Strength progression
```

------------------------------------------------------------------------

# 19. Android SDE2 / Portfolio Value

This project can be useful beyond being a consumer app because it gives
opportunities to demonstrate:

-   Jetpack Compose
-   ViewModel
-   StateFlow
-   Room
-   Flow
-   Repository pattern
-   Use cases
-   Offline-first architecture
-   Background synchronization
-   Conflict resolution
-   Health Connect
-   Notifications
-   WorkManager where appropriate
-   Foreground Service where appropriate
-   Camera integration
-   On-device AI
-   Model capability/fallback handling
-   Image processing
-   Caching
-   Data modeling
-   Modularization
-   Testing
-   Dependency injection
-   Error handling

A potential architecture:

``` text
                 Compose UI
                     │
                 ViewModel
                     │
                  UseCase
                     │
                Repository
              ┌──────┴──────┐
              ↓             ↓
            Room          Network
              │             │
              └──────┬──────┘
                     ↓
                Sync Engine

AI subsystem:

Camera
  ↓
Image preprocessing
  ↓
FoodRecognizer
  ├── Gemini Nano
  ├── Custom on-device model
  └── Fallback
  ↓
Food candidates
  ↓
Food normalization
  ↓
Nutrition database
```

------------------------------------------------------------------------

# 20. Open Questions / Next Decisions

These have NOT been finalized and should be discussed before
implementation:

1.  Target audience:
    -   Indian gym users?
    -   General Android fitness users?
    -   Personal use / portfolio project / production app?
2.  Primary goal:
    -   Fat loss
    -   Muscle gain
    -   Strength
    -   General fitness
3.  Backend:
    -   Firebase?
    -   Custom backend?
    -   Backend only for sync/account?
    -   Fully local-first?
4.  Food database:
    -   Open food database?
    -   Custom Indian food database?
    -   API?
    -   User-created foods?
5.  AI:
    -   Gemini Nano first?
    -   Custom LiteRT model?
    -   Hybrid?
    -   Cloud fallback?
6.  Authentication/account:
    -   Required?
    -   Optional?
    -   Anonymous/local-first?
7.  Monetization:
    -   Free
    -   Subscription
    -   AI feature limits
    -   No monetization / portfolio project
8.  Scope:
    -   Build MVP first
    -   Design full architecture first
    -   Use project primarily for Android SDE2/LLD preparation

------------------------------------------------------------------------

# 21. Recommended Next Step

Before writing code, define the MVP requirements and core data model.

Suggested first entities:

``` text
User
WorkoutPlan
WorkoutSession
Exercise
ExerciseSet
Food
Meal
MealItem
DailyNutrition
BodyMeasurement
Habit
ProgressPhoto
```

Then define relationships and decide which data is local-only vs
synchronized.

After that, design:

1.  Room schema
2.  Repository interfaces
3.  Use cases
4.  ViewModel state
5.  Compose screens
6.  Food AI abstraction
7.  Sync strategy

The project should stay small enough to build incrementally rather than
attempting the complete feature set at once.
