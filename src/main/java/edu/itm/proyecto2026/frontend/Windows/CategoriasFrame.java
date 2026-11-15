package edu.itm.proyecto2026.frontend.Windows;

import edu.itm.proyecto2026.frontend.Dtos.CategoriaDto;
import edu.itm.proyecto2026.frontend.webservicesclients.CategoriaApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class CategoriasFrame extends JFrame {

    @Autowired
    private CategoriaApiClient categoriaClient;

    private final JTextField txtId = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtDescripcion = new JTextField();
    private final JTextField txtEstado = new JTextField("activa");

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Descripción", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    private List<CategoriaDto> categorias = new ArrayList<>();
    private JFrame principal;
    private boolean inicializado = false;

    public void abrir(JFrame ventanaPrincipal) {
        principal = ventanaPrincipal;
        if (!inicializado) {
            inicializar();
            inicializado = true;
        }
        principal.setVisible(false);
        cargarTabla();
        setVisible(true);
    }

    private void inicializar() {
        setTitle("Categorías");
        setSize(900, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        txtId.setEditable(false);

        JPanel formulario = new JPanel(new GridLayout(0, 4, 8, 8));
        formulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        formulario.add(new JLabel("ID:"));
        formulario.add(txtId);
        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);
        formulario.add(new JLabel("Descripción:"));
        formulario.add(txtDescripcion);
        formulario.add(new JLabel("Estado:"));
        formulario.add(txtEstado);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0 && fila < categorias.size()) {
                mostrar(categorias.get(fila));
            }
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botones.add(boton("Guardar nuevo", this::guardar));
        botones.add(boton("Actualizar", this::actualizar));
        botones.add(boton("Eliminar", this::eliminar));
        botones.add(boton("Limpiar", this::limpiar));
        botones.add(boton("Refrescar", this::cargarTabla));
        botones.add(boton("Volver", this::volver));

        add(formulario, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }

    private JButton boton(String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(e -> accion.run());
        return boton;
    }

    private void cargarTabla() {
        try {
            categorias = categoriaClient.getCategorias();
            modelo.setRowCount(0);
            for (CategoriaDto c : categorias) {
                modelo.addRow(new Object[]{
                        c.getIdCategoria(),
                        c.getNombreCategoria(),
                        c.getDescripcionCategoria(),
                        c.getEstadoCategoria()
                });
            }
        } catch (Exception e) {
            error(e);
        }
    }

    private void mostrar(CategoriaDto c) {
        txtId.setText(String.valueOf(c.getIdCategoria()));
        txtNombre.setText(valor(c.getNombreCategoria()));
        txtDescripcion.setText(valor(c.getDescripcionCategoria()));
        txtEstado.setText(valor(c.getEstadoCategoria()));
    }

    private CategoriaDto leerFormulario() {
        if (txtNombre.getText().isBlank() || txtDescripcion.getText().isBlank()) {
            aviso("Nombre y descripción son obligatorios.");
            return null;
        }
        CategoriaDto categoria = new CategoriaDto();
        categoria.setNombreCategoria(txtNombre.getText().trim());
        categoria.setDescripcionCategoria(txtDescripcion.getText().trim());
        categoria.setEstadoCategoria(txtEstado.getText().isBlank() ? "activa" : txtEstado.getText().trim());
        return categoria;
    }

    private void guardar() {
        CategoriaDto categoria = leerFormulario();
        if (categoria == null) {
            return;
        }
        try {
            categoriaClient.insertarCategoria(categoria);
            info("Categoría guardada correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            error(e);
        }
    }

    private void actualizar() {
        if (txtId.getText().isBlank()) {
            aviso("Selecciona una categoría de la tabla.");
            return;
        }
        CategoriaDto categoria = leerFormulario();
        if (categoria == null) {
            return;
        }
        categoria.setIdCategoria(Integer.parseInt(txtId.getText()));
        try {
            categoriaClient.actualizarCategoria(categoria);
            info("Categoría actualizada correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            error(e);
        }
    }

    private void eliminar() {
        if (txtId.getText().isBlank()) {
            aviso("Selecciona una categoría de la tabla.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Eliminar la categoría seleccionada?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            categoriaClient.eliminarCategoria(Integer.parseInt(txtId.getText()));
            info("Categoría eliminada correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            error(e);
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtEstado.setText("activa");
        tabla.clearSelection();
    }

    private void volver() {
        setVisible(false);
        principal.setVisible(true);
    }

    private String valor(String texto) {
        return texto == null ? "" : texto;
    }

    private void info(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void aviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private void error(Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}