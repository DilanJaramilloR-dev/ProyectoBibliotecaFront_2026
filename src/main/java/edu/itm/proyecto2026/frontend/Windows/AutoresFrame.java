package edu.itm.proyecto2026.frontend.Windows;

import edu.itm.proyecto2026.frontend.Dtos.AutorDto;
import edu.itm.proyecto2026.frontend.webservicesclients.AutorApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class AutoresFrame extends JFrame {

    @Autowired
    private AutorApiClient autorClient;

    private final JTextField txtId = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtApellido = new JTextField();
    private final JTextField txtNacionalidad = new JTextField();
    private final JTextField txtFechaNacimiento = new JTextField();

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Apellido", "Nacionalidad", "Fecha nacimiento"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    private List<AutorDto> autores = new ArrayList<>();
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
        setTitle("Autores");
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
        formulario.add(new JLabel("Apellido:"));
        formulario.add(txtApellido);
        formulario.add(new JLabel("Nacionalidad:"));
        formulario.add(txtNacionalidad);
        formulario.add(new JLabel("Nacimiento (aaaa-mm-dd):"));
        formulario.add(txtFechaNacimiento);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0 && fila < autores.size()) {
                mostrar(autores.get(fila));
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
            autores = autorClient.getAutores();
            modelo.setRowCount(0);
            for (AutorDto a : autores) {
                modelo.addRow(new Object[]{
                        a.getIdAutor(),
                        a.getNombreAutor(),
                        a.getApellidoAutor(),
                        a.getNacionalidadAutor(),
                        a.getFechaNacimiento()
                });
            }
        } catch (Exception e) {
            error(e);
        }
    }

    private void mostrar(AutorDto a) {
        txtId.setText(String.valueOf(a.getIdAutor()));
        txtNombre.setText(valor(a.getNombreAutor()));
        txtApellido.setText(valor(a.getApellidoAutor()));
        txtNacionalidad.setText(valor(a.getNacionalidadAutor()));
        txtFechaNacimiento.setText(a.getFechaNacimiento() == null ? "" : a.getFechaNacimiento().toString());
    }

    private AutorDto leerFormulario() {
        if (txtNombre.getText().isBlank() || txtFechaNacimiento.getText().isBlank()) {
            aviso("Nombre y fecha de nacimiento son obligatorios.");
            return null;
        }
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(txtFechaNacimiento.getText().trim());
        } catch (DateTimeParseException e) {
            aviso("La fecha debe tener el formato aaaa-mm-dd, por ejemplo 1927-03-06.");
            return null;
        }
        AutorDto autor = new AutorDto();
        autor.setNombreAutor(txtNombre.getText().trim());
        autor.setApellidoAutor(txtApellido.getText().trim());
        autor.setNacionalidadAutor(txtNacionalidad.getText().trim());
        autor.setFechaNacimiento(fecha);
        return autor;
    }

    private void guardar() {
        AutorDto autor = leerFormulario();
        if (autor == null) {
            return;
        }
        try {
            autorClient.insertarAutor(autor);
            info("Autor guardado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            error(e);
        }
    }

    private void actualizar() {
        if (txtId.getText().isBlank()) {
            aviso("Selecciona un autor de la tabla.");
            return;
        }
        AutorDto autor = leerFormulario();
        if (autor == null) {
            return;
        }
        autor.setIdAutor(Integer.parseInt(txtId.getText()));
        try {
            autorClient.actualizarAutor(autor);
            info("Autor actualizado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            error(e);
        }
    }

    private void eliminar() {
        if (txtId.getText().isBlank()) {
            aviso("Selecciona un autor de la tabla.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el autor seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            autorClient.eliminarAutor(Integer.parseInt(txtId.getText()));
            info("Autor eliminado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            error(e);
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtNacionalidad.setText("");
        txtFechaNacimiento.setText("");
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
