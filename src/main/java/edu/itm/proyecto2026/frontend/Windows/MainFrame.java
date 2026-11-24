package edu.itm.proyecto2026.frontend.Windows;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

@Component
public class MainFrame extends JFrame {

    @Autowired
    private UsuariosFrame usuariosFrame;

    @Autowired
    private BibliotecariosFrame bibliotecariosFrame;

    @Autowired
    private AutoresFrame autoresFrame;

    @Autowired
    private EditorialesFrame editorialesFrame;

    @Autowired
    private CategoriasFrame categoriasFrame;

    @Autowired
    private LibrosFrame librosFrame;

    @Autowired
    private EjemplaresFrame ejemplaresFrame;

    @Autowired
    private PrestamosFrame prestamosFrame;

    @Autowired
    private SancionesFrame sancionesFrame;

    @Autowired
    private CatalogoFrame catalogoFrame;

    @Autowired
    private ReportesFrame reportesFrame;

    public void inicializar() {
        setTitle("Sistema de Biblioteca - El Buen Futuro");
        setSize(760, 520);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        BibliotecaTheme.estilizarVentana(this);

        add(BibliotecaTheme.crearEncabezado("El Buen Futuro",
                "Sistema de gestión bibliotecaria"), BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(4, 3, 14, 14));
        panel.setBackground(BibliotecaTheme.CREMA);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

        panel.add(crearBoton("Usuarios", e -> usuariosFrame.abrir(this)));
        panel.add(crearBoton("Bibliotecarios", e -> bibliotecariosFrame.abrir(this)));
        panel.add(crearBoton("Autores", e -> autoresFrame.abrir(this)));
        panel.add(crearBoton("Editoriales", e -> editorialesFrame.abrir(this)));
        panel.add(crearBoton("Categorías", e -> categoriasFrame.abrir(this)));
        panel.add(crearBoton("Libros", e -> librosFrame.abrir(this)));
        panel.add(crearBoton("Ejemplares", e -> ejemplaresFrame.abrir(this)));
        panel.add(crearBoton("Préstamos", e -> prestamosFrame.abrir(this)));
        panel.add(crearBoton("Sanciones", e -> sancionesFrame.abrir(this)));
        panel.add(crearBoton("Catálogo", e -> catalogoFrame.abrir(this)));
        panel.add(crearBoton("Reportes", e -> reportesFrame.abrir(this)));
        panel.add(crearBoton("Salir", salir()));

        add(panel, BorderLayout.CENTER);
    }

    private JButton crearBoton(String texto, ActionListener accion) {
        JButton boton = new JButton(texto);
        BibliotecaTheme.estilizarBoton(boton);
        boton.setPreferredSize(new Dimension(190, 55));
        if (texto.equals("Salir")) {
            boton.setBackground(BibliotecaTheme.MADERA);
        }
        boton.addActionListener(accion);
        return boton;
    }

    private ActionListener salir() {
        return e -> {
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Seguro que deseas salir?", "Salir", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                dispose();
                System.exit(0);
            }
        };
    }
}