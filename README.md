<h1 align = "center">💪 𝓜𝔂𝓦𝓮𝓲𝓰𝓱𝓽 𝓕𝓲𝓽𝓷𝓮𝓼𝓼 ✨</h1>
<p align = "center">OOP 101: Object-Oriented Programming | Recommendation and Tracking System
<br/><i>Team: Don't Phuket Up! (Team 5) — IT-2107</i>
<br/><i>Instructor: Agdon, Fatima Marie P.</i>
</p>

<h2>‧₊˚ ┊ About this Project</h2>
<p>
<b>MyWeight Fitness: A Recommendation and Tracking System</b> is a Java-based fitness recommendation and tracking application designed to support <b>Gym Enthusiasts</b>, <b>Athletes</b>, and <b>Rehabilitation Patients</b> within a single platform. It combines automated health baseline calculations, personalized workout recommendations, activity scheduling, daily calorie and hydration tracking, progressive load monitoring, and fitness-level advancement.
</p>

<h2>‧₊˚ ┊ Overview</h2>
<p>
This project models user profiles, fitness categories, workout plans, and progress tracking using core Java Object-Oriented Programming (OOP) principles. Users can manage their physical metrics through CRUD operations, customize available equipment, browse an exercise library, monitor pain levels for recovery, and review workout history and goal progress from a centralized dashboard.
</p>

<h2>‧₊˚ ┊ OOP Concepts Demonstrated</h2>
<ul type = "circle">
	<li><b>Encapsulation.</b> Protecting user profiles, physical metrics, fitness goals, and pain levels using <code>private</code> fields with controlled accessors/mutators and methods.</li>
	<li><b>Inheritance.</b> Creating specialized user subclasses (<code>GymUser</code>, <code>Athlete</code>, and <code>RehabPatient</code>) from a common <code>User</code> parent class.</li>
	<li><b>Polymorphism.</b> Overriding plan generation and display methods (such as <code>generateWorkoutPlan()</code> and <code>showFitnessPlan()</code>) for category-specific behavior.</li>
	<li><b>Abstraction.</b> Hiding workout recommendation logic, calorie and BMI calculations, and progress tracking behind clean classes and GUI controls.</li>
	<li><b>Exception Handling &amp; Collections.</b> Using <code>try-catch</code> blocks to validate numeric inputs and <code>ArrayList</code> collections to store schedules, recommendations, water logs, workout history, and progress records.</li>
</ul>

<table>
	<tr>
		<th>OOP Concept</th>
		<th>How it was demonstrated</th>
	</tr>
	<tr>
		<td><b>Encapsulation</b></td>
		<td>User attributes (such as name, age, height, current weight, target weight, category, fitness level, BMI, and pain levels) are kept encapsulated so updates occur through controlled methods and CRUD operations rather than direct unvalidated access.</td>
	</tr>
	<tr>
		<td><b>Inheritance</b></td>
		<td><code>GymUser</code>, <code>Athlete</code>, and <code>RehabPatient</code> extend the <code>User</code> class to inherit shared profile attributes and behaviors while implementing category-specific training, sport, or recovery logic.</td>
	</tr>
	<tr>
		<td><b>Polymorphism</b></td>
		<td>Methods such as <code>generateWorkoutPlan()</code> and <code>showFitnessPlan()</code> adapt their behavior depending on whether the user is a Gym Enthusiast, Athlete, or Rehabilitation Patient.</td>
	</tr>
	<tr>
		<td><b>Abstraction</b></td>
		<td><code>WorkoutRecommendationEngine</code>, <code>FitnessPlan</code>, <code>ProgressiveLoadTracker</code>, <code>FitnessLevelManager</code>, <code>WorkoutHistory</code>, <code>ExerciseLibrary</code>, and <code>Equipment</code> separate core computation and tracking logic from the user interface.</td>
	</tr>
</table>

<h2>‧₊˚ ┊ Key Features</h2>
<ul type = "circle">
	<li><b>Profile Management (CRUD) &amp; BMI Calculator:</b> Add, view, edit, and delete user metrics (name, age, height, weight) and compute BMI.</li>
	<li><b>Multi-Role Categories &amp; Fitness Level Advancement:</b> Supports Gym Enthusiasts, Athletes, and Rehabilitation Patients, with automatic fitness-level promotion as workout completion requirements are met.</li>
	<li><b>Adaptive Exercise Recommendation Engine &amp; Equipment Customization:</b> Generates tailored routines (monthly muscle rotation for beginners; target muscle selection for intermediate/advanced gym users; sport-specific plans for athletes; recovery focus with pain-level monitoring for rehab patients) filtered by available equipment.</li>
	<li><b>Daily Calorie Target, Hydration &amp; Weight Goal Tracking:</b> Calculates daily calorie targets, logs water intake, and tracks progress toward target weight over a chosen timeframe.</li>
	<li><b>Activity Scheduler, Exercise Library, Progressive Load &amp; Workout History:</b> Schedules routines, provides exercise/equipment instructions, recommends gradual load/rep/duration increases, logs completed/missed workouts, supports theme customization, and summarizes status on a dashboard.</li>
</ul>

<h2>‧₊˚ ┊ Project Structure</h2>
<pre><code>MyWeigh-Fitness/
├── docs/
│   └── UMLDiagram.md
└── src/
    └── myweightfitness/
        ├── Main.java
        ├── User.java
        ├── GymUser.java
        ├── Athlete.java
        ├── RehabPatient.java
        ├── WorkoutRecommendationEngine.java
        ├── FitnessPlan.java
        ├── ProgressiveLoadTracker.java
        ├── FitnessLevelManager.java
        ├── WorkoutHistory.java
        ├── ExerciseLibrary.java
        └── Equipment.java
</code></pre>

<h2>‧₊˚ ┊ Requirements</h2>
<ul type = "circle">
	<li>Java Development Kit (JDK)</li>
	<li>A code/text editor or IDE (e.g., Sublime Text, VS Code, IntelliJ IDEA)</li>
	<li>Command-line terminal (e.g., Windows PowerShell, Windows Terminal, Git Bash)</li>
</ul>

<h2>‧₊˚ ┊ How to Run the Program</h2>
<ol type = "1">
	<li>Clone or download the repository (<code>ladyyueee/MyWeigh-Fitness</code>).</li>
	<li>Open the project folder in your terminal.</li>
	<li>Compile the Java source files into an output directory (e.g., <code>javac -d out src/myweightfitness/*.java</code>).</li>
	<li>Run the application from the terminal (e.g., <code>java -cp out myweightfitness.Main</code>).</li>
</ol>

<h2>‧₊˚ ┊ UML Class Diagram</h2>
<p>
See the <code>docs</code> folder for the UML class diagram and its Mermaid source code.
</p>
<img src = "docs/UMLDiagram.png" alt = "MyWeight Fitness UML Class Diagram">

<h2>‧₊˚ ┊ Project Scope &amp; Limitations</h2>
<p>
This project is built for educational purposes as a Java Swing / OOP application and does not include medical diagnosis or treatment, smartwatch/wearable sensor integration, online consultations with doctors or trainers, online payments or subscriptions, food ordering/delivery, social/user-to-user messaging, automated physical exercise verification (users mark completion manually), or clinical/professional fitness replacements.
</p>

<h2>‧₊˚ ┊ Team Members</h2>
<table>
	<tr>
		<th>Member</th>
		<th>SR-Code &amp; GitHub</th>
		<th>Assigned Responsibilities</th>
	</tr>
	<tr>
		<td><b>Almazan, Jasmine Thea P.</b></td>
		<td>25-00314 | <code>Ladyyuee</code></td>
		<td>Analysis, Recommendation &amp; Fitness Logic Developer, Documentation</td>
	</tr>
	<tr>
		<td><b>Arellano, Dimple V.</b></td>
		<td>25-04485 | <code>dimps-v</code></td>
		<td>UI/UX Design, Data Management Developer, Documentation</td>
	</tr>
	<tr>
		<td><b>Magtibay, Allyssa</b></td>
		<td>25-02786 | <code>Ally-Yssa03</code></td>
		<td>UI/UX Design, Database/Data Handling Developer, Testing &amp; Debugging, Data Management, Documentation</td>
	</tr>
</table>
