package edu.itm.proyecto2026.frontend.Windows;

import edu.itm.proyecto2026.frontend.Dtos.CatalogoLibroDto;
import edu.itm.proyecto2026.frontend.webservicesclients.CatalogoApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class CatalogoFrame extends JFrame {

    @Autowired
    private CatalogoApiClient catalogoClient;

    private final JTextField txtTitulo = new JTextField();
    private final JTextField txtAutor = new JTextField();
    private final JTextField txtCategoria = new JTextField();
    private final JTextArea txtDescripcion = new JTextArea(3, 20);

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Título", "ISBN", "Año", "Categoría", "Editorial", "Autores", "Disponibles"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    private List<CatalogoLibroDto> catalogo = new ArrayList<>();
    private JFrame principal;
    private boolean inicializado = false;

    public void abrir(JFrame ventanaPrincipal) {
        principal = ventanaPrincipal;
        if (!inicializado) {
            inicializar();
            inicializado = true;
        }
        principal.setVisible(false);
        buscar();
        setVisible(true);
    }

    private void inicializar() {
        setTitle("Catálogo");
        setSize(1100, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        BibliotecaTheme.estilizarVentana(this);

        JPanel filtros = new JPanel(new GridLayout(1, 6, 8, 8));
        filtros.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        filtros.add(new JLabel("Título:"));
        filtros.add(txtTitulo);
        filtros.add(new JLabel("Autor:"));
        filtros.add(txtAutor);
        filtros.add(new JLabel("Categoría:"));
        filtros.add(txtCategoria);

        txtTitulo.addActionListener(e -> buscar());
        txtAutor.addActionListener(e -> buscar());
        txtCategoria.addActionListener(e -> buscar());

        txtDescripcion.setEditable(false);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        scrollDescripcion.setBorder(BorderFactory.createTitledBorder("Descripción del libro seleccionado"));

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0 && fila < catalogo.size()) {
                String descripcion = catalogo.get(fila).getDescripcionLibro();
                txtDescripcion.setText(descripcion == null ? "" : descripcion);
            }
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botones.add(boton("Buscar", this::buscar));
        botones.add(boton("Limpiar filtros", this::limpiar));
        botones.add(boton("Volver", this::volver));

        JPanel inferior = new JPanel(new BorderLayout());
        inferior.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        inferior.add(scrollDescripcion, BorderLayout.CENTER);
        inferior.add(botones, BorderLayout.SOUTH);

        add(filtros, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(inferior, BorderLayout.SOUTH);
    }

    private JButton boton(String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(e -> accion.run());
        return boton;
    }

    private void buscar() {
        try {
            catalogo = catalogoClient.getCatalogo(
                    txtTitulo.getText().trim(),
                    txtAutor.getText().trim(),
                    txtCategoria.getText().trim());
            modelo.setRowCount(0);
            txtDescripcion.setText("");
            for (CatalogoLibroDto l : catalogo) {
                modelo.addRow(new Object[]{
                        l.getIdLibro(),
                        l.getTituloLibro(),
                        l.getIsbnLibro(),
                        l.getAnioPublicacionLibro(),
                        l.getNombreCategoria(),
                        l.getNombreEditorial(),
                        l.getAutores(),
                        l.getEjemplaresDisponibles()
                });
            }
        } catch (Exception e) {
            error(e);
        }
    }

    private void limpiar() {
        txtTitulo.setText("");
        txtAutor.setText("");
        txtCategoria.setText("");
        buscar();
    }

    private void volver() {
        setVisible(false);
        principal.setVisible(true);
    }

    private void error(Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}