package edu.itm.proyecto2026.frontend.Windows;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class BibliotecaTheme {

    public static final Color CREMA = new Color(245, 240, 226);
    public static final Color CREMA_CLARA = new Color(252, 249, 240);
    public static final Color VERDE_OSCURO = new Color(31, 65, 48);
    public static final Color VERDE = new Color(55, 102, 76);
    public static final Color ORO = new Color(181, 137, 70);
    public static final Color MADERA = new Color(112, 66, 38);
    public static final Color TEXTO = new Color(48, 48, 44);
    public static final Font TIPO_TITULO = new Font("Georgia", Font.BOLD, 24);
    public static final Font TIPO_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);

    private BibliotecaTheme() {
    }

    public static void aplicarTemaGlobal() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        UIManager.put("control", CREMA);
        UIManager.put("info", CREMA_CLARA);
        UIManager.put("nimbusBase", VERDE_OSCURO);
        UIManager.put("nimbusBlueGrey", new Color(205, 195, 170));
        UIManager.put("nimbusLightBackground", CREMA_CLARA);
        UIManager.put("nimbusSelectionBackground", VERDE);
        UIManager.put("text", TEXTO);
        UIManager.put("nimbusInfoBlue", CREMA_CLARA);

        UIManager.put("Panel.background", CREMA);
        UIManager.put("OptionPane.background", CREMA_CLARA);
        UIManager.put("OptionPane.messageForeground", TEXTO);

        UIManager.put("Label.font", TIPO_NORMAL);
        UIManager.put("Label.foreground", TEXTO);
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 14));
        UIManager.put("TextField.font", TIPO_NORMAL);
        UIManager.put("TextArea.font", TIPO_NORMAL);
        UIManager.put("ComboBox.font", TIPO_NORMAL);
        UIManager.put("Table.font", TIPO_NORMAL);
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 14));
        UIManager.put("Table.selectionBackground", VERDE);
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.gridColor", new Color(216, 207, 184));
        UIManager.put("Table.rowHeight", 28);
        UIManager.put("ScrollPane.background", CREMA);
        UIManager.put("TextField.background", Color.WHITE);

        UIManager.put("Button.background", VERDE_OSCURO);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.focus", new Color(ORO.getRed(), ORO.getGreen(), ORO.getBlue(), 80));
    }

    public static void estilizarVentana(JFrame ventana) {
        ventana.getContentPane().setBackground(CREMA);
    }

    public static JPanel crearEncabezado(String titulo, String subtitulo) {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(VERDE_OSCURO);
        encabezado.setBorder(new EmptyBorder(12, 18, 12, 18));

        JLabel labelTitulo = new JLabel(titulo, SwingConstants.LEFT);
        labelTitulo.setFont(TIPO_TITULO);
        labelTitulo.setForeground(Color.WHITE);
        encabezado.add(labelTitulo, BorderLayout.WEST);

        if (subtitulo != null && !subtitulo.isBlank()) {
            JLabel labelSubtitulo = new JLabel(subtitulo, SwingConstants.RIGHT);
            labelSubtitulo.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            labelSubtitulo.setForeground(new Color(224, 213, 184));
            encabezado.add(labelSubtitulo, BorderLayout.EAST);
        }
        return encabezado;
    }

    public static void estilizarTabla(JTable tabla) {
        tabla.setRowHeight(28);
        tabla.setSelectionBackground(VERDE);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setGridColor(new Color(216, 207, 184));
        tabla.setShowVerticalLines(true);

        JTableHeader encabezado = tabla.getTableHeader();
        encabezado.setBackground(MADERA);
        encabezado.setForeground(Color.WHITE);
        encabezado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        encabezado.setPreferredSize(new Dimension(encabezado.getWidth(), 32));
    }

    public static void estilizarBoton(JButton boton) {
        boton.setBackground(VERDE_OSCURO);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(new EmptyBorder(8, 14, 8, 14));
    }

    public static void estilizarCampo(JTextField campo) {
        campo.setBackground(Color.WHITE);
        campo.setForeground(TEXTO);
        campo.setCaretColor(VERDE_OSCURO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 180, 154)),
                new EmptyBorder(5, 8, 5, 8)));
    }
}
