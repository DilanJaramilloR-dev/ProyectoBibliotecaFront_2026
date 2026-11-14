package edu.itm.proyecto2026.frontend.Windows;

import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

@Component
public class MainFrame extends JFrame {

    public void inicializar() {
        setTitle("Sistema de Biblioteca - El Buen Futuro");
        setSize(600, 420);
        setResizable(false);
        setLocationRelativeTo(null);               // centrada en pantalla
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // cerrar la X detiene el programa
        setLayout(new BorderLayout(10, 10));

        // Título
        JLabel titulo = new JLabel("Menú principal", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        add(titulo, BorderLayout.NORTH);

        // Panel con los botones (4 filas x 3 columnas)
        JPanel panel = new JPanel(new GridLayout(4, 3, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        panel.add(crearBoton("Usuarios", enConstruccion("Usuarios")));
        panel.add(crearBoton("Bibliotecarios", enConstruccion("Bibliotecarios")));
        panel.add(crearBoton("Autores", enConstruccion("Autores")));
        panel.add(crearBoton("Editoriales", enConstruccion("Editoriales")));
        panel.add(crearBoton("Categorías", enConstruccion("Categorías")));
        panel.add(crearBoton("Libros", enConstruccion("Libros")));
        panel.add(crearBoton("Ejemplares", enConstruccion("Ejemplares")));
        panel.add(crearBoton("Préstamos", enConstruccion("Préstamos")));
        panel.add(crearBoton("Sanciones", enConstruccion("Sanciones")));
        panel.add(crearBoton("Catálogo", enConstruccion("Catálogo")));
        panel.add(crearBoton("Reportes", enConstruccion("Reportes")));
        panel.add(crearBoton("Salir", salir()));

        add(panel, BorderLayout.CENTER);
    }

    private JButton crearBoton(String texto, ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 14));
        boton.addActionListener(accion);
        return boton;
    }

    // Temporal: lo iremos reemplazando por la ventana real de cada sección
    private ActionListener enConstruccion(String seccion) {
        return e -> JOptionPane.showMessageDialog(this,
                "La sección " + seccion + " aún está en construcción.");
    }

    private ActionListener salir() {
        return e -> {
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Seguro que deseas salir?", "Salir", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                dispose();
                System.exit(0); // detiene también el servidor del front
            }
        };
    }
}