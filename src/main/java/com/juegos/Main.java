package com.juegos;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new MainFrame().setVisible(true);
        });
    }
}

/*
 * USO DE INTELIGENCIA ARTIFICIAL:
 * - La estructura base del proyecto, partes de la interfaz gráfica Swing luego 
 *   ciertas funcionalidads JSON fueron generadas y posteriormente simplificadas con
 *   asistencia de IA.
 * - Revisé manualmente todo para ajustarme al enunciado 
 */