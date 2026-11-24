package edu.itm.proyecto2026.frontend.Windows;

import edu.itm.proyecto2026.frontend.Dtos.EditorialDto;
import edu.itm.proyecto2026.frontend.webservicesclients.EditorialApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class EditorialesFrame extends JFrame {

    @Autowired
    private EditorialApiClient editorialClient;

    private final JTextField txtId = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtCorreo = new JTextField();
    private final JTextField txtPais = new JTextField();
    private final JTextField txtTelefono = new JTextField();

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Correo", "País", "Teléfono"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    private List<EditorialDto> editoriales = new ArrayList<>();
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
        setTitle("Editoriales");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        BibliotecaTheme.estilizarVentana(this);

        txtId.setEditable(false);

        JPanel formulario = new JPanel(new GridLayout(0, 4, 8, 8));
        formulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        formulario.add(new JLabel("ID:"));
        formulario.add(txtId);
        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);
        formulario.add(new JLabel("Correo:"));
        formulario.add(txtCorreo);
        formulario.add(new JLabel("País:"));
        formulario.add(txtPais);
        formulario.add(new JLabel("Teléfono:"));
        formulario.add(txtTelefono);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0 && fila < editoriales.size()) {
                mostrar(editoriales.get(fila));
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
            editoriales = editorialClient.getEditoriales();
            modelo.setRowCount(0);
            for (EditorialDto ed : editoriales) {
                modelo.addRow(new Object[]{
                        ed.getIdEditorial(),
                        ed.getNombreEditorial(),
                        ed.getCorreoEditorial(),
                        ed.getPaisEditorial(),
                        ed.getTelefonoEditorial()
                });
            }
        } catch (Exception e) {
            error(e);
        }
    }

    private void mostrar(EditorialDto ed) {
        txtId.setText(String.valueOf(ed.getIdEditorial()));
        txtNombre.setText(valor(ed.getNombreEditorial()));
        txtCorreo.setText(valor(ed.getCorreoEditorial()));
        txtPais.setText(valor(ed.getPaisEditorial()));
        txtTelefono.setText(valor(ed.getTelefonoEditorial()));
    }

    private EditorialDto leerFormulario() {
        if (txtNombre.getText().isBlank() || txtCorreo.getText().isBlank()
                || txtPais.getText().isBlank() || txtTelefono.getText().isBlank()) {
            aviso("Nombre, correo, país y teléfono son obligatorios.");
            return null;
        }
        EditorialDto editorial = new EditorialDto();
        editorial.setNombreEditorial(txtNombre.getText().trim());
        editorial.setCorreoEditorial(txtCorreo.getText().trim());
        editorial.setPaisEditorial(txtPais.getText().trim());
        editorial.setTelefonoEditorial(txtTelefono.getText().trim());
        return editorial;
    }

    private void guardar() {
        EditorialDto editorial = leerFormulario();
        if (editorial == null) {
            return;
        }
        try {
            editorialClient.insertarEditorial(editorial);
            info("Editorial guardada correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            error(e);
        }
    }

    private void actualizar() {
        if (txtId.getText().isBlank()) {
            aviso("Selecciona una editorial de la tabla.");
            return;
        }
        EditorialDto editorial = leerFormulario();
        if (editorial == null) {
            return;
        }
        editorial.setIdEditorial(Integer.parseInt(txtId.getText()));
        try {
            editorialClient.actualizarEditorial(editorial);
            info("Editorial actualizada correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            error(e);
        }
    }

    private void eliminar() {
        if (txtId.getText().isBlank()) {
            aviso("Selecciona una editorial de la tabla.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,"¿Eliminar la editorial seleccionada?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            editorialClient.eliminarEditorial(Integer.parseInt(txtId.getText()));
            info("Editorial eliminada correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            error(e);
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
        txtCorreo.setText("");
        txtPais.setText("");
        txtTelefono.setText("");
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