package myweightfitness;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class MainApp extends JFrame {
    // Theme colors
    private static final Color PRIMARY = new Color(30, 130, 120);
    private static final Color LIGHT_BG = new Color(230, 245, 240);
    private static final Color CARD_BG = new Color(215, 240, 230);
    private static final Color TEXT_DARK = new Color(30, 50, 60);

    private User currentUser;
    private RecommendationEngine engine;
    private WorkoutHistory history;
    private FitnessLevelManager levelManager;
    private ExerciseLibrary library;

    private JPanel contentPanel;
    private CardLayout cardLayout;

    public MainApp() {
        library = new ExerciseLibrary();
        engine = new RecommendationEngine(library);
        history = new WorkoutHistory();
        levelManager = new FitnessLevelManager();
        currentUser = null;

        setTitle("MyWeight Fitness");
        setSize(420, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        add(contentPanel);

        // Show welcome screen
        showWelcomeScreen();
    }

    // ================= SCREEN 1: WELCOME =================
    private void showWelcomeScreen() {
        JPanel panel = createBasePanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("MYWEIGHT FITNESS", SwingConstants.CENTER);
        logo.setFont(new Font("SansSerif", Font.BOLD, 22));
        logo.setForeground(PRIMARY);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(new EmptyBorder(60, 0, 10, 0));

        JLabel tagline = new JLabel("Your Adaptive Fitness Journey", SwingConstants.CENTER);
        tagline.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tagline.setForeground(Color.GRAY);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        tagline.setBorder(new EmptyBorder(0, 0, 40, 0));

        JButton startBtn = createPrimaryButton("Get Started");
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startBtn.addActionListener(e -> showProfileForm());

        panel.add(logo);
        panel.add(tagline);
        panel.add(startBtn);

        contentPanel.add(panel, "WELCOME");
        cardLayout.show(contentPanel, "WELCOME");
    }

    // ================= SCREEN 2: PROFILE FORM =================
    private void showProfileForm() {
        JPanel panel = createBasePanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Tell us about yourself");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField nameField = addLabeledField(panel, "Name", "");
        JTextField ageField = addLabeledField(panel, "Age", "");
        JTextField heightField = addLabeledField(panel, "Height (cm)", "");
        JTextField weightField = addLabeledField(panel, "Weight (kg)", "");
        JTextField targetWeightField = addLabeledField(panel, "Target Weight (kg)", "");

        JButton calcBtn = createPrimaryButton("Calculate BMI");
        calcBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        calcBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JLabel bmiResult = new JLabel("Your BMI: --");
        bmiResult.setFont(new Font("SansSerif", Font.BOLD, 16));
        bmiResult.setForeground(PRIMARY);
        bmiResult.setBorder(new EmptyBorder(15, 20, 15, 20));
        bmiResult.setAlignmentX(Component.LEFT_ALIGNMENT);

        calcBtn.addActionListener(e -> {
            try {
                double h = Double.parseDouble(heightField.getText());
                double w = Double.parseDouble(weightField.getText());
                User temp = new User();
                temp.setHeight(h);
                temp.setCurrentWeight(w);
                double bmi = temp.calculateBMI();
                bmiResult.setText(String.format("Your BMI: %.1f (%s)", bmi, temp.getBMIStatus()));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton nextBtn = createPrimaryButton("Next");
        nextBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        nextBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        nextBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                double h = Double.parseDouble(heightField.getText().trim());
                double w = Double.parseDouble(weightField.getText().trim());
                double tw = Double.parseDouble(targetWeightField.getText().trim());

                if (name.isEmpty()) throw new IllegalArgumentException("Name required");

                // Store temporarily
                currentUser = new User();
                currentUser.setName(name);
                currentUser.setAge(age);
                currentUser.setHeight(h);
                currentUser.setCurrentWeight(w);
                currentUser.setTargetWeight(tw);

                showCategoryScreen();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please fill all fields correctly:\n" + ex.getMessage(),
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(title);
        panel.add(calcBtn);
        panel.add(bmiResult);
        panel.add(Box.createVerticalStrut(10));
        panel.add(nextBtn);

        contentPanel.add(panel, "PROFILE");
        cardLayout.show(contentPanel, "PROFILE");
    }

    // ================= SCREEN 3: CATEGORY SELECTION =================
    private void showCategoryScreen() {
        JPanel panel = createBasePanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("I am a...");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(TEXT_DARK);
        title.setBorder(new EmptyBorder(20, 20, 20, 20));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JRadioButton gymBtn = createRadioOption("🏋️ Gym Enthusiast");
        JRadioButton athleteBtn = createRadioOption("🏃 Athlete");
        JRadioButton rehabBtn = createRadioOption("💚 Rehabilitation Patient");

        ButtonGroup group = new ButtonGroup();
        group.add(gymBtn);
        group.add(athleteBtn);
        group.add(rehabBtn);
        gymBtn.setSelected(true);

        JButton nextBtn = createPrimaryButton("Confirm Category");
        nextBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        nextBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        nextBtn.addActionListener(e -> {
            User finalUser;
            if (athleteBtn.isSelected()) {
                finalUser = new Athlete();
            } else if (rehabBtn.isSelected()) {
                finalUser = new RehabPatient();
            } else {
                finalUser = new GymUser();
            }
            // Copy fields
            finalUser.setName(currentUser.getName());
            finalUser.setAge(currentUser.getAge());
            finalUser.setHeight(currentUser.getHeight());
            finalUser.setCurrentWeight(currentUser.getCurrentWeight());
            finalUser.setTargetWeight(currentUser.getTargetWeight());
            finalUser.setFitnessLevel("Beginner");

            currentUser = finalUser;
            showFitnessLevelScreen();
        });

        panel.add(title);
        panel.add(gymBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(athleteBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(rehabBtn);
        panel.add(Box.createVerticalStrut(30));
        panel.add(nextBtn);

        contentPanel.add(panel, "CATEGORY");
        cardLayout.show(contentPanel, "CATEGORY");
    }

    // ================= SCREEN 4: FITNESS LEVEL =================
    private void showFitnessLevelScreen() {
        JPanel panel = createBasePanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Select Your Fitness Level");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(TEXT_DARK);
        title.setBorder(new EmptyBorder(20, 20, 10, 20));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Choose the level that matches your fitness condition.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(Color.GRAY);
        subtitle.setBorder(new EmptyBorder(0, 20, 20, 20));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JRadioButton beginner = createLevelOption("Beginner", "New to exercise, less than 6 months");
        JRadioButton intermediate = createLevelOption("Intermediate", "Moderately active, 6 months - 1 year");
        JRadioButton advanced = createLevelOption("Advanced", "Very active, 1+ year of consistent training");

        ButtonGroup group = new ButtonGroup();
        group.add(beginner);
        group.add(intermediate);
        group.add(advanced);
        beginner.setSelected(true);

        JButton nextBtn = createPrimaryButton("Next");
        nextBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        nextBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        nextBtn.addActionListener(e -> {
            if (intermediate.isSelected()) currentUser.setFitnessLevel("Intermediate");
            else if (advanced.isSelected()) currentUser.setFitnessLevel("Advanced");
            else currentUser.setFitnessLevel("Beginner");

            // Ask for sport if athlete
            if (currentUser instanceof Athlete) {
                String sport = JOptionPane.showInputDialog(this, "Enter your sport:");
                if (sport != null && !sport.isEmpty()) {
                    ((Athlete) currentUser).setSport(sport);
                }
            }

            // Ask for recovery focus if rehab
            if (currentUser instanceof RehabPatient) {
                String focus = JOptionPane.showInputDialog(this, "Enter recovery focus area (e.g., Knee, Shoulder):");
                if (focus != null && !focus.isEmpty()) {
                    ((RehabPatient) currentUser).setRecoveryFocus(focus);
                }
            }

            showDashboard();
        });

        panel.add(title);
        panel.add(subtitle);
        panel.add(beginner);
        panel.add(Box.createVerticalStrut(10));
        panel.add(intermediate);
        panel.add(Box.createVerticalStrut(10));
        panel.add(advanced);
        panel.add(Box.createVerticalStrut(30));
        panel.add(nextBtn);

        contentPanel.add(panel, "LEVEL");
        cardLayout.show(contentPanel, "LEVEL");
    }

    // ================= SCREEN 5: DASHBOARD =================
    private void showDashboard() {
        JPanel panel = createBasePanel();
        panel.setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel welcome = new JLabel("Welcome, " + currentUser.getName() + "!");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 18));
        welcome.setForeground(Color.WHITE);

        JLabel categoryLabel = new JLabel(currentUser.getCategory());
        categoryLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        categoryLabel.setForeground(new Color(200, 240, 235));

        JPanel headerText = new JPanel(new GridLayout(2, 1));
        headerText.setOpaque(false);
        headerText.add(welcome);
        headerText.add(categoryLabel);
        header.add(headerText, BorderLayout.WEST);

        // Body - scrollable
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(LIGHT_BG);
        body.setBorder(new EmptyBorder(15, 15, 15, 15));

        // BMI Card
        body.add(createInfoCard("📊 BMI Status",
                String.format("%.1f - %s", currentUser.calculateBMI(), currentUser.getBMIStatus())));

        // Calorie target
        double calories = engine.calculateCalorieTarget(currentUser);
        body.add(createInfoCard("🔥 Daily Calorie Target", (int) calories + " kcal"));

        // Water intake
        body.add(createInfoCard("💧 Water Intake Goal",
                engine.calculateWaterGoal(currentUser) + " L / day"));

        // Weight goal
        String weightGoal = String.format("%.1f kg → %.1f kg",
                currentUser.getCurrentWeight(), currentUser.getTargetWeight());
        body.add(createInfoCard("🎯 Weight Goal", weightGoal));

        // Fitness plan
        JTextArea planArea = new JTextArea(engine.generateMonthlySchedule(currentUser, calories));
        planArea.setEditable(false);
        planArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        planArea.setBackground(CARD_BG);
        planArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel planPanel = new JPanel(new BorderLayout());
        planPanel.setBackground(CARD_BG);
        planPanel.setBorder(new CompoundBorder(
                new EmptyBorder(5, 0, 10, 0),
                new LineBorder(PRIMARY, 1, true)
        ));
        planPanel.add(planArea, BorderLayout.CENTER);
        body.add(planPanel);

        // Action buttons
        JButton completeBtn = createPrimaryButton("✓ Mark Today's Workout Complete");
        completeBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        completeBtn.addActionListener(e -> {
            history.recordWorkoutHistory("Workout " + (history.viewHistory().size() + 1),
                    WorkoutRecord.COMPLETED);
            double rate = history.getCompletionRate();
            String advancement = levelManager.checkFitnessLevel(currentUser, history);
            JOptionPane.showMessageDialog(this,
                    "Workout recorded!\nCompletion: " +
                            String.format("%.0f", rate) + "%\n\n" + advancement,
                    "Progress Updated", JOptionPane.INFORMATION_MESSAGE);
            // Refresh dashboard
            contentPanel.removeAll();
            showDashboard();
        });

        JButton libraryBtn = createSecondaryButton("📚 Exercise Library");
        libraryBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        libraryBtn.addActionListener(e -> showExerciseLibrary());

        JButton historyBtn = createSecondaryButton("📋 Workout History");
        historyBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        historyBtn.addActionListener(e -> showHistory());

        body.add(completeBtn);
        body.add(Box.createVerticalStrut(8));
        body.add(libraryBtn);
        body.add(Box.createVerticalStrut(8));
        body.add(historyBtn);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(header, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        contentPanel.add(panel, "DASHBOARD");
        cardLayout.show(contentPanel, "DASHBOARD");
    }

    // ================= EXERCISE LIBRARY =================
    private void showExerciseLibrary() {
        JPanel panel = createBasePanel();
        panel.setLayout(new BorderLayout());

        JLabel title = new JLabel("📚 Exercise Library");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(PRIMARY);
        title.setBorder(new EmptyBorder(15, 20, 15, 20));
        panel.add(title, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(LIGHT_BG);

        for (Exercise ex : library.viewExercise()) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(CARD_BG);
            card.setBorder(new CompoundBorder(
                    new EmptyBorder(5, 10, 5, 10),
                    new LineBorder(PRIMARY, 1, true)
            ));
            JLabel name = new JLabel("<html><b>" + ex.getExerciseName() + "</b> — <i>" + ex.getTargetMuscle() + "</i><br>" +
                    "<small>" + ex.getInstructions() + "</small><br>" +
                    "<small>Equipment: " + ex.getEquipment() + "</small></html>");
            name.setForeground(TEXT_DARK);
            card.add(name, BorderLayout.CENTER);
            list.add(card);
            list.add(Box.createVerticalStrut(8));
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);

        JButton back = createPrimaryButton("← Back to Dashboard");
        back.addActionListener(e -> {
            contentPanel.removeAll();
            showDashboard();
        });
        JPanel bottom = new JPanel();
        bottom.setBackground(LIGHT_BG);
        bottom.add(back);
        panel.add(bottom, BorderLayout.SOUTH);

        contentPanel.add(panel, "LIBRARY");
        cardLayout.show(contentPanel, "LIBRARY");
    }

    // ================= WORKOUT HISTORY =================
    private void showHistory() {
        JPanel panel = createBasePanel();
        panel.setLayout(new BorderLayout());

        JLabel title = new JLabel("📋 Workout History");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(PRIMARY);
        title.setBorder(new EmptyBorder(15, 20, 15, 20));
        panel.add(title, BorderLayout.NORTH);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setBackground(LIGHT_BG);

        if (history.viewHistory().isEmpty()) {
            area.setText("\n\nNo workouts recorded yet.\nStart by marking a workout complete!");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Completion: ").append(history.completionStatus()).append("\n");
            sb.append("─────────────────────────────────\n\n");
            for (String h : history.viewHistory()) {
                sb.append(h).append("\n");
            }
            area.setText(sb.toString());
        }

        panel.add(new JScrollPane(area), BorderLayout.CENTER);

        JButton back = createPrimaryButton("← Back to Dashboard");
        back.addActionListener(e -> {
            contentPanel.removeAll();
            showDashboard();
        });
        JPanel bottom = new JPanel();
        bottom.setBackground(LIGHT_BG);
        bottom.add(back);
        panel.add(bottom, BorderLayout.SOUTH);

        contentPanel.add(panel, "HISTORY");
        cardLayout.show(contentPanel, "HISTORY");
    }

    // ================= HELPER METHODS =================
    private JPanel createBasePanel() {
        JPanel p = new JPanel();
        p.setBackground(LIGHT_BG);
        return p;
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(CARD_BG);
        btn.setForeground(PRIMARY);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(PRIMARY, 1, true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JRadioButton createRadioOption(String text) {
        JRadioButton rb = new JRadioButton(text);
        rb.setFont(new Font("SansSerif", Font.BOLD, 14));
        rb.setBackground(CARD_BG);
        rb.setForeground(TEXT_DARK);
        rb.setBorder(new CompoundBorder(
                new LineBorder(PRIMARY, 1, true),
                new EmptyBorder(12, 15, 12, 15)
        ));
        rb.setAlignmentX(Component.LEFT_ALIGNMENT);
        rb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        return rb;
    }

    private JRadioButton createLevelOption(String title, String desc) {
        JRadioButton rb = new JRadioButton("<html><b>" + title + "</b><br><small>" + desc + "</small></html>");
        rb.setFont(new Font("SansSerif", Font.PLAIN, 13));
        rb.setBackground(CARD_BG);
        rb.setForeground(TEXT_DARK);
        rb.setBorder(new CompoundBorder(
                new LineBorder(PRIMARY, 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));
        rb.setAlignmentX(Component.LEFT_ALIGNMENT);
        rb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        return rb;
    }

    private JTextField addLabeledField(JPanel parent, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(TEXT_DARK);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(5, 20, 3, 20));

        JTextField field = new JTextField(value);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setBorder(new CompoundBorder(
                new EmptyBorder(0, 20, 10, 20),
                new CompoundBorder(new LineBorder(PRIMARY, 1, true),
                        new EmptyBorder(8, 8, 8, 8))
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        parent.add(lbl);
        parent.add(field);
        return field;
    }

    private JPanel createInfoCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
                new EmptyBorder(5, 0, 5, 0),
                new CompoundBorder(new LineBorder(PRIMARY, 1, true),
                        new EmptyBorder(12, 15, 12, 15))
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleLbl.setForeground(TEXT_DARK);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        valueLbl.setForeground(PRIMARY);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.SOUTH);
        return card;
    }

    // ================= MAIN =================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) { /* ignore */ }
            new MainApp().setVisible(true);
        });
    }
}