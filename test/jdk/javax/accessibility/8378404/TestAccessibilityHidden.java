import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This unpolished demo app lets us test the new
 * "apple.awt.windowAccessibleHidden". Specifically I'm interested in toggling
 * that property on a disposed window and then re-showing the window.
 */
public class TestAccessibilityHidden extends JFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TestAccessibilityHidden x = new TestAccessibilityHidden();
            x.pack();
            x.setVisible(true);
        });
    }

    public static final String WINDOW_ACCESSIBILITY_HIDDEN = "apple.awt.windowAccessibleHidden";

    class WindowClassPanel extends JPanel {
        Window window;
        final JButton createNewButton, reshowLastButton;
        final JRadioButton radioNull = new JRadioButton("null", true);
        final JRadioButton radioFalse = new JRadioButton("\"false\"");
        final JRadioButton radioTrue = new JRadioButton("\"true\"");
        final Class<? extends Window> windowClass;
        final Map<Class, AtomicInteger> windowMapCtr = new HashMap<>();

        WindowListener windowListener = new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                reshowLastButton.setEnabled(false);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                reshowLastButton.setEnabled(true);
            }
        };

        private WindowClassPanel(Class<? extends Window> windowClass) {
            this.windowClass = windowClass;
            windowMapCtr.put(windowClass, new AtomicInteger(0));
            setBorder(new TitledBorder(windowClass.getSimpleName()));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            createNewButton = new JButton("Create a new " + windowClass.getSimpleName());
            reshowLastButton = new JButton("Reshow last closed " + windowClass.getSimpleName());

            JPanel accessibilityHiddenPanel = new JPanel(new FlowLayout());
            accessibilityHiddenPanel.add(new JLabel("Accessibility Hidden: "));
            accessibilityHiddenPanel.add(radioNull);
            accessibilityHiddenPanel.add(radioFalse);
            accessibilityHiddenPanel.add(radioTrue);

            ButtonGroup group = new ButtonGroup();
            group.add(radioNull);
            group.add(radioFalse);
            group.add(radioTrue);

            add(accessibilityHiddenPanel);
            add(createNewButton);
            add(reshowLastButton);

            reshowLastButton.setEnabled(false);

            createNewButton.addActionListener(e -> doCreateNewWindow());
            reshowLastButton.addActionListener(e -> doReshowLastWindow());

            radioNull.addActionListener(e -> {
                if (window != null)
                    ((RootPaneContainer)window).getRootPane().putClientProperty(WINDOW_ACCESSIBILITY_HIDDEN, null);
            });
            radioFalse.addActionListener(e -> {
                if (window != null)
                    ((RootPaneContainer)window).getRootPane().putClientProperty(WINDOW_ACCESSIBILITY_HIDDEN, "false");
            });
            radioTrue.addActionListener(e -> {
                if (window != null)
                    ((RootPaneContainer)window).getRootPane().putClientProperty(WINDOW_ACCESSIBILITY_HIDDEN, "true");
            });
        }

        private void doCreateNewWindow() {
            if (window != null) {
                window.removeWindowListener(windowListener);
            }

            int ctr = windowMapCtr.get(windowClass).incrementAndGet();
            if (JWindow.class.equals(windowClass)) {
                window = new JWindow(TestAccessibilityHidden.this);
            } else if (JDialog.class.equals(windowClass)) {
                window = new JDialog(TestAccessibilityHidden.this, "Dialog #" + ctr);
            } else {
                window = new JFrame("Frame #" + ctr);
            }
            if (radioFalse.isSelected()) {
                ((RootPaneContainer) window).getRootPane().putClientProperty(WINDOW_ACCESSIBILITY_HIDDEN, "false");
            } else if (radioTrue.isSelected()) {
                ((RootPaneContainer)window).getRootPane().putClientProperty(WINDOW_ACCESSIBILITY_HIDDEN, "true");
            }

            ((RootPaneContainer)window).getContentPane().add(createSampleUI(window));

            window.addWindowListener(windowListener);

            window.pack();
            window.setVisible(true);
        }

        private JPanel createSampleUI(Window window) {
            JPanel returnValue = new JPanel();
            returnValue.setLayout(new BoxLayout(returnValue, BoxLayout.Y_AXIS));
            returnValue.add(new JLabel("JLabel"));
            returnValue.add(new JProgressBar());
            returnValue.add(new JSpinner(new SpinnerNumberModel(0, 0, 100, 10)));

            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> window.dispose());
            returnValue.add(closeButton);

            return returnValue;
        }

        private void doReshowLastWindow() {
            window.setVisible(true);
        }

    }

    public TestAccessibilityHidden() {
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        for (Class<? extends Window> c : new Class[] {JFrame.class, JDialog.class, JWindow.class}) {
            getContentPane().add(new WindowClassPanel(c));
        }
    }
}
