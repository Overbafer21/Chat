import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TwoWayColorfulChat extends JFrame {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private JTextArea chatAreaLeft;
    private JTextArea chatAreaRight;
    private JTextArea messageAreaLeft;
    private JTextArea messageAreaRight;

    public TwoWayColorfulChat() {
        setTitle("Two-Way Colorful Chat Messenger");
        setSize(980, 640);
        setMinimumSize(new Dimension(900, 580));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new GradientPanel();
        root.setLayout(new BorderLayout(14, 14));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Two-Way Colorful Chat", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(33, 37, 41));
        root.add(title, BorderLayout.NORTH);

        JPanel leftPanel = createChatPanel("User 1", true);
        JPanel rightPanel = createChatPanel("User 2", false);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(470);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setOpaque(false);

        root.add(splitPane, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel createChatPanel(String userName, boolean isLeftUser) {
        Color cardColor = isLeftUser ? new Color(237, 248, 255) : new Color(255, 239, 247);
        Color buttonColor = isLeftUser ? new Color(40, 116, 166) : new Color(212, 70, 145);

        JPanel panel = new RoundedPanel(20, cardColor);
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel label = new JLabel(userName);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(new Color(52, 58, 64));
        panel.add(label, BorderLayout.NORTH);

        JTextArea chatArea = createChatArea();
        JTextArea messageArea = createMessageArea();

        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setBorder(BorderFactory.createLineBorder(new Color(210, 218, 226), 1));
        chatScroll.getViewport().setOpaque(false);
        chatScroll.setOpaque(false);

        JButton sendButton = createSendButton(buttonColor);

        JPanel inputPanel = new JPanel(new BorderLayout(8, 8));
        inputPanel.setOpaque(false);
        JScrollPane inputScroll = new JScrollPane(messageArea);
        inputScroll.setBorder(BorderFactory.createLineBorder(new Color(210, 218, 226), 1));
        inputPanel.add(inputScroll, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        panel.add(chatScroll, BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);

        if (isLeftUser) {
            chatAreaLeft = chatArea;
            messageAreaLeft = messageArea;
            sendButton.addActionListener(e -> sendMessage(messageAreaLeft, chatAreaLeft, chatAreaRight, "User 1"));
            addEnterShortcut(messageAreaLeft, sendButton);
        } else {
            chatAreaRight = chatArea;
            messageAreaRight = messageArea;
            sendButton.addActionListener(e -> sendMessage(messageAreaRight, chatAreaRight, chatAreaLeft, "User 2"));
            addEnterShortcut(messageAreaRight, sendButton);
        }

        return panel;
    }

    private JTextArea createChatArea() {
        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ta.setBackground(new Color(255, 255, 255, 230));
        ta.setMargin(new Insets(10, 10, 10, 10));
        return ta;
    }

    private JTextArea createMessageArea() {
        JTextArea ta = new JTextArea(3, 20);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ta.setMargin(new Insets(8, 8, 8, 8));
        return ta;
    }

    private JButton createSendButton(Color bgColor) {
        JButton btn = new JButton("Send");
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(100, 52));
        return btn;
    }

    private void addEnterShortcut(JTextArea input, JButton button) {
        input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume();
                    button.doClick();
                }
            }
        });
    }

    private void sendMessage(JTextArea inputArea, JTextArea senderChat, JTextArea receiverChat, String user) {
        String message = inputArea.getText().trim();
        if (message.isEmpty()) {
            return;
        }

        String time = LocalTime.now().format(TIME_FORMATTER);
        String formatted = "[" + time + "] " + user + ": " + message + "\n";

        senderChat.append(formatted);
        if (receiverChat != null) {
            receiverChat.append(formatted);
            receiverChat.setCaretPosition(receiverChat.getDocument().getLength());
        }

        inputArea.setText("");
        senderChat.setCaretPosition(senderChat.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TwoWayColorfulChat app = new TwoWayColorfulChat();
            app.setVisible(true);
        });
    }

    private static class RoundedPanel extends JPanel {
        private final int arc;
        private final Color backgroundColor;

        RoundedPanel(int arc, Color backgroundColor) {
            this.arc = arc;
            this.backgroundColor = backgroundColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            GradientPaint paint = new GradientPaint(
                    0,
                    0,
                    new Color(224, 242, 254),
                    getWidth(),
                    getHeight(),
                    new Color(255, 228, 245)
            );
            g2.setPaint(paint);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
