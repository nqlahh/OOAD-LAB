package drawingstudiopro.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.plaf.basic.BasicButtonUI;

public class ButtonTabComponent extends JPanel {

    private final JTabbedPane pane;

    public ButtonTabComponent(final JTabbedPane pane) {
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        if (pane == null) {
            throw new NullPointerException("TabbedPane cannot be null");
        }
        this.pane = pane;
        setOpaque(false);

        // Create a label that gets its text from the tab pane
        JLabel label = new JLabel() {
            @Override
            public String getText() {
                int i = pane.indexOfTabComponent(ButtonTabComponent.this);
                if (i != -1) {
                    return pane.getTitleAt(i);
                }
                return null;
            }
        };
        add(label);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        // Create the close button
        JButton button = new JButton("x");
        button.setPreferredSize(new Dimension(17, 17));
        button.setToolTipText("Close this tab");
        button.setUI(new BasicButtonUI());
        button.setContentAreaFilled(false);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createEtchedBorder());
        button.setBorderPainted(false);
        button.setRolloverEnabled(true);
        
        button.addActionListener(e -> {
            int index = pane.indexOfTabComponent(ButtonTabComponent.this);
            if (index != -1) {
                String title = pane.getTitleAt(index);
                int choice = JOptionPane.showConfirmDialog(
                    pane,
                    "Do you want to save the canvas '" + title + "' before closing?",
                    "Close Canvas",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );

                if (choice == JOptionPane.YES_OPTION) {
                    // Save logic would go here
                    pane.remove(index);
                } else if (choice == JOptionPane.NO_OPTION) {
                    pane.remove(index);
                }
            }
        });
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Component c = e.getComponent();
                if (c instanceof AbstractButton) {
                    ((AbstractButton) c).setBorderPainted(true);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Component c = e.getComponent();
                if (c instanceof AbstractButton) {
                    ((AbstractButton) c).setBorderPainted(false);
                }
            }
        });
        
        add(button);
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    }
}