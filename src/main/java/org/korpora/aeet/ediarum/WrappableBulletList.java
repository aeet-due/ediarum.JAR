package org.korpora.aeet.ediarum;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * a JList that uses list bullets and can be resized
 * <p>
 * extended from <a href="https://stackoverflow.com/questions/7306295/swing-jlist-with-multiline-text-and-dynamic-height?rq=1/}">StackOverflow #7306295</a>
 *
 * @param <E>
 */
public class WrappableBulletList<E> extends JList<E> {

    /**
     * @inherited <p>
     */
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    public WrappableBulletList(ListModel model) {
        super(model);
        WrappableBulletList<E> list = this;
        this.setCellRenderer(new WrappableCellRenderer());

        ComponentListener componentListener = new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                // force cache invalidation by temporarily setting fixed height
                list.setFixedCellHeight(10);
                list.setFixedCellHeight(-1);
            }

        };

        this.addComponentListener(componentListener);
    }

    private class WrappableCellRenderer implements ListCellRenderer {

        private JPanel panel;
        private JTextArea textArea;

        public WrappableCellRenderer() {
            panel = new JPanel();
            panel.setLayout(new BorderLayout());

            // label
            JPanel labelPanel = new JPanel(new BorderLayout());
            JLabel label = new JLabel("• ");
            labelPanel.add(label, BorderLayout.NORTH);
            panel.add(labelPanel, BorderLayout.WEST);

            // text
            textArea = new JTextArea();
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            panel.add(textArea, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(final JList list, final Object value, final int index,
                                                      final boolean isSelected, final boolean hasFocus) {

            textArea.setText((String) value);
            int width = list.getWidth();
            // this is just to lure the text area's internal sizing mechanism into action
            if (width > 0) textArea.setSize(width, Short.MAX_VALUE);
            if (isSelected) {
                textArea.setForeground(list.getSelectionForeground());
                textArea.setBackground(list.getSelectionBackground());
            } else {
                textArea.setForeground(list.getForeground());
                textArea.setBackground(list.getBackground());
            }
            return panel;

        }
    }
}
