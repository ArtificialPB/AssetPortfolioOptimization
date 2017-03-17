package com.artificial.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class NamedTextField extends JTextField implements FocusListener {
    private final Color defaultColor = getForeground();
    private final Color textColor = new Color(168, 168, 168);
    protected String name;

    public NamedTextField() {
        setHorizontalAlignment(JTextField.CENTER);
        addFocusListener(this);
    }

    public String getSlotName() {
        return name;
    }

    public void setSlotName(String name) {
        this.name = name;
        setText(name);
        focusLost(new FocusEvent(this, 1));
    }

    public void clear() {
        setText(name);
        setForeground(textColor);
    }

    /**
     * Invoked when a component gains the keyboard focus.
     */
    @Override
    public void focusGained(FocusEvent e) {
        if (getText().equals("null")) {
            setText("");
        }
    }

    /**
     * Invoked when a component loses the keyboard focus.
     */
    @Override
    public void focusLost(FocusEvent e) {
        if (getText().trim().isEmpty()) {
            setText(name);
        }
    }

    @Override
    public void setText(String t) {
        if (t != null && t.equals(name)) {
            setForeground(textColor);
        } else {
            setForeground(defaultColor);
        }
        super.setText(t);
    }

    @Override
    public String getText() {
        if (super.getText().equals(name)) return "null";
        return super.getText();
    }
}
