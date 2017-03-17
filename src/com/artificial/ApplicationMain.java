package com.artificial;

import com.artificial.ui.MainUserInterface;

import javax.swing.*;

public class ApplicationMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainUserInterface::new);
    }
}
