package com.artificial.ui;

import com.artificial.MarketIndex;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomPresetCombo extends JComboBox<ItemWrapper> {
    private static final String ADD_CUSTOM_STRING = "Add custom...";

    public CustomPresetCombo() {
        super.addItem(new ItemWrapper(ADD_CUSTOM_STRING));
        this.addActionListener(new AddCustomItemListener());
    }

    @Override
    public void addItem(final ItemWrapper item) {
        final MutableComboBoxModel<ItemWrapper> model = ((MutableComboBoxModel<ItemWrapper>) dataModel);
        model.insertElementAt(item, Math.max(0, model.getSize() - 1));
    }

    private class AddCustomItemListener implements ActionListener {
        private int lastIndex = 0;

        @Override
        public void actionPerformed(final ActionEvent e) {
            final ItemWrapper selected = (ItemWrapper) getSelectedItem();
            if (selected.toString().equals(ADD_CUSTOM_STRING)) {
                final String symbol = JOptionPane.showInputDialog(CustomPresetCombo.this, "Please input Asset Symbol");
                if (symbol == null) {
                    setSelectedIndex(lastIndex);
                    return;
                }
                final MarketIndex index = new MarketIndex(symbol);
                if (!index.isLoaded()) {
                    JOptionPane.showMessageDialog(CustomPresetCombo.this, "Failed to get information for " + symbol + "!", "Loading error", JOptionPane.ERROR_MESSAGE);
                    setSelectedIndex(lastIndex);
                    return;
                }
                addItem(new ItemWrapper(index));
                final MutableComboBoxModel<ItemWrapper> model = ((MutableComboBoxModel<ItemWrapper>) dataModel);
                setSelectedIndex(model.getSize() - 2);
            } else {
                lastIndex = getSelectedIndex();
            }
        }
    }

}
