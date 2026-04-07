package librarymain;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// This class creates the main window for the library system
public class LibraryGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> userTypeCombo;
    private JTextArea notificationArea;
    private Database database;
    private NotificationSystem notificationSystem;

    public LibraryGUI() {
        database = Database.getInstance();
        notificationSystem = NotificationSystem.getInstance();
        
        setupFrame();
        setupComponents();
        setupNotificationArea();
    }

    // Set up the main window
    private void setupFrame() {
        setTitle("Library Management System");
        setSize(760, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(16, 16));
        getContentPane().setBackground(UiTheme.BACKGROUND);
    }

    // Set up the components in the window
    private void setupComponents() {
        JPanel mainPanel = UiTheme.createPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

        JLabel titleLabel = new JLabel("Library Management System");
        titleLabel.setFont(UiTheme.FONT_TITLE);
        titleLabel.setForeground(UiTheme.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Sign in or create a user account");
        subtitleLabel.setFont(UiTheme.FONT_SUBTITLE);
        subtitleLabel.setForeground(UiTheme.TEXT_MUTED);

        JPanel headingPanel = UiTheme.createPanel(new BorderLayout(0, 4));
        headingPanel.add(titleLabel, BorderLayout.NORTH);
        headingPanel.add(subtitleLabel, BorderLayout.CENTER);

        JPanel authPanel = UiTheme.createPanel(new GridLayout(3, 2, 10, 10));
        authPanel.setBorder(UiTheme.cardBorder());

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setForeground(UiTheme.TEXT_PRIMARY);
        authPanel.add(usernameLabel);
        usernameField = new JTextField();
        UiTheme.styleInput(usernameField);
        authPanel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setForeground(UiTheme.TEXT_PRIMARY);
        authPanel.add(passwordLabel);
        passwordField = new JPasswordField();
        UiTheme.styleInput(passwordField);
        authPanel.add(passwordField);

        JLabel userTypeLabel = new JLabel("User Type");
        userTypeLabel.setForeground(UiTheme.TEXT_PRIMARY);
        authPanel.add(userTypeLabel);
        userTypeCombo = new JComboBox<>(new String[]{"Librarian", "Borrower"});
        userTypeCombo.setFont(UiTheme.FONT_BODY);
        userTypeCombo.setBackground(Color.WHITE);
        authPanel.add(userTypeCombo);

        JPanel buttonPanel = UiTheme.createPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton loginButton = new JButton("Login");
        JButton addUserButton = new JButton("Add User");
        JButton clearButton = new JButton("Clear");
        UiTheme.styleButton(loginButton, true);
        UiTheme.styleButton(addUserButton, false);
        UiTheme.styleButton(clearButton, false);

        loginButton.addActionListener(new LoginButtonListener());
        addUserButton.addActionListener(new AddUserButtonListener());
        clearButton.addActionListener(e -> clearFields());

        buttonPanel.add(loginButton);
        buttonPanel.add(addUserButton);
        buttonPanel.add(clearButton);

        mainPanel.add(headingPanel, BorderLayout.NORTH);
        mainPanel.add(authPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    // Set up the notification area
    private void setupNotificationArea() {
        notificationArea = new JTextArea(5, 40);
        notificationArea.setEditable(false);
        notificationArea.setLineWrap(true);
        notificationArea.setWrapStyleWord(true);
        notificationArea.setFont(UiTheme.FONT_BODY);
        notificationArea.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(notificationArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UiTheme.BORDER),
            "System Messages"
        ));

        JPanel wrapper = UiTheme.createPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        wrapper.add(scrollPane, BorderLayout.CENTER);
        add(wrapper, BorderLayout.SOUTH);

        // Add an observer for system notifications
        notificationSystem.addObserver(message -> 
            SwingUtilities.invokeLater(() -> {
                notificationArea.append(message + "\n");
                notificationArea.setCaretPosition(notificationArea.getDocument().getLength());
            })
        );
    }

    // Clear input fields
    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        userTypeCombo.setSelectedIndex(0);
    }

    // Listener for the login button
    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String userType = (String) userTypeCombo.getSelectedItem();
                
                if (username.isEmpty() || password.isEmpty()) {
                    throw new IllegalArgumentException("Username and password cannot be empty");
                }

                User user = database.authenticateUser(username, password, userType);

                if (user instanceof Librarian && userType.equals("Librarian")) {
                    new LibrarianGUI(database).setVisible(true);
                    notificationArea.append("Librarian logged in: " + username + "\n");
                } else if (user instanceof Borrower && userType.equals("Borrower")) {
                    new BorrowerGUI(database, (Borrower) user).setVisible(true);
                    notificationArea.append("Borrower logged in: " + username + "\n");
                } else {
                    throw new SecurityException("Invalid user type");
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(LibraryGUI.this,
                    ex.getMessage(),
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Listener for the add user button
    private class AddUserButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String userType = (String) userTypeCombo.getSelectedItem();

                User newUser = UserFactory.createUser(userType, username, password);
                if (newUser == null) {
                    throw new IllegalArgumentException("Invalid user type");
                }

                if (!database.addUser(newUser)) {
                    JOptionPane.showMessageDialog(LibraryGUI.this,
                        "User already exists!",
                        "Error Adding User",
                        JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(LibraryGUI.this,
                        "User added successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(LibraryGUI.this,
                    ex.getMessage(),
                    "Error Adding User",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
