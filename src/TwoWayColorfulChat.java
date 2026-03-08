import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TwoWayColorfulChat extends JFrame {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private JTextPane chatPaneLeft;
    private JTextPane chatPaneRight;
    private JTextArea messageAreaLeft;
    private JTextArea messageAreaRight;

    public TwoWayColorfulChat() {
        setTitle("Two-Way Colorful Chat");
        setSize(1040, 680);
        setMinimumSize(new Dimension(920, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setContentPane(createRootPanel());
    }

    private JPanel createRootPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setBackground(new Color(15, 18, 30));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel header = new JLabel("TWO-WAY CHAT", SwingConstants.CENTER);
        header.setFont(new Font("Monospaced", Font.BOLD, 22));
        header.setForeground(new Color(116, 247, 253));
        header.setBorder(new EmptyBorder(0, 0, 8, 0));
        root.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 12, 0));
        content.setOpaque(false);
        content.add(createUserPanel("USER A", true));
        content.add(createUserPanel("USER B", false));

        root.add(content, BorderLayout.CENTER);
        return root;
    }

    private JPanel createUserPanel(String user, boolean isLeft) {
        Color panelBg = new Color(25, 28, 45);
        Color accent = isLeft ? new Color(79, 195, 247) : new Color(255, 138, 128);
        Color sendBg = isLeft ? new Color(0, 172, 193) : new Color(244, 81, 108);

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(panelBg);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accent, 2),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel top = new JLabel(user + "  ● ONLINE");
        top.setFont(new Font("Monospaced", Font.BOLD, 16));
        top.setForeground(accent);
        panel.add(top, BorderLayout.NORTH);

        JTextPane chatPane = createChatPane(panelBg);
        JScrollPane chatScroll = new JScrollPane(chatPane);
        chatScroll.setBorder(BorderFactory.createLineBorder(new Color(51, 58, 87), 1));
        chatScroll.getViewport().setBackground(new Color(19, 22, 36));

        JTextArea messageArea = createInputArea();
        JButton sendButton = new JButton("SEND ➜");
        sendButton.setForeground(Color.WHITE);
        sendButton.setBackground(sendBg);
        sendButton.setFocusPainted(false);
        sendButton.setFont(new Font("Monospaced", Font.BOLD, 13));
        sendButton.setPreferredSize(new Dimension(110, 54));

        JPanel bottom = new JPanel(new BorderLayout(8, 0));
        bottom.setOpaque(false);
        JScrollPane inputScroll = new JScrollPane(messageArea);
        inputScroll.setBorder(BorderFactory.createLineBorder(new Color(51, 58, 87), 1));

        bottom.add(inputScroll, BorderLayout.CENTER);
        bottom.add(sendButton, BorderLayout.EAST);

        panel.add(chatScroll, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        if (isLeft) {
            chatPaneLeft = chatPane;
            messageAreaLeft = messageArea;
            sendButton.addActionListener(e -> sendMessage(messageAreaLeft, chatPaneLeft, chatPaneRight, "USER A", accent));
            bindEnterToSend(messageAreaLeft, sendButton);
        } else {
            chatPaneRight = chatPane;
            messageAreaRight = messageArea;
            sendButton.addActionListener(e -> sendMessage(messageAreaRight, chatPaneRight, chatPaneLeft, "USER B", accent));
            bindEnterToSend(messageAreaRight, sendButton);
        }

        return panel;
    }

    private JTextPane createChatPane(Color panelBg) {
        JTextPane pane = new JTextPane();
        pane.setEditable(false);
        pane.setBackground(new Color(19, 22, 36));
        pane.setForeground(new Color(226, 232, 240));
        pane.setFont(new Font("Monospaced", Font.PLAIN, 13));
        pane.setMargin(new Insets(10, 10, 10, 10));
        pane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        pane.setCaretColor(panelBg);
        return pane;
    }

    private JTextArea createInputArea() {
        JTextArea area = new JTextArea(3, 20);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setForeground(new Color(230, 236, 245));
        area.setBackground(new Color(30, 34, 53));
        area.setCaretColor(new Color(116, 247, 253));
        area.setMargin(new Insets(8, 8, 8, 8));
        return area;
    }

    private void bindEnterToSend(JTextArea input, JButton button) {
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

    private void sendMessage(JTextArea inputArea, JTextPane senderPane, JTextPane receiverPane, String user, Color accent) {
        String text = inputArea.getText().trim();
        if (text.isEmpty()) {
            return;
        }

        String time = LocalTime.now().format(TIME_FORMATTER);
        String htmlRow = "<div style='margin-bottom:8px;'>"
                + "<span style='color:#94A3B8;'>[" + time + "]</span> "
                + "<span style='color:" + toHex(accent) + ";font-weight:bold;'>" + user + "</span>"
                + "<span style='color:#E2E8F0;'>: " + escapeHtml(text).replace("\n", "<br>") + "</span>"
                + "</div>";

        appendHtml(senderPane, htmlRow);
        if (receiverPane != null) {
            appendHtml(receiverPane, htmlRow);
        }

        inputArea.setText("");
    }

    private void appendHtml(JTextPane pane, String rowHtml) {
        String old = pane.getText();
        String body;

        if (old == null || old.isBlank()) {
            body = rowHtml;
        } else {
            body = old.replaceFirst("(?s)</body>\\s*</html>\\s*$", "") + rowHtml + "</body></html>";
        }

        if (!body.startsWith("<html>")) {
            body = "<html><body style='font-family:Monospaced;background:#131624;color:#E2E8F0;'>" + body + "</body></html>";
        }

        pane.setContentType("text/html");
        pane.setText(body);
        pane.setCaretPosition(pane.getDocument().getLength());
    }

    private String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    private String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TwoWayColorfulChat app = new TwoWayColorfulChat();
            app.setVisible(true);
        });
    }
}
