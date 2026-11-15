package edu.itm.proyecto2026.frontend.Windows;

import edu.itm.proyecto2026.frontend.Dtos.EjemplarDto;
import edu.itm.proyecto2026.frontend.Dtos.LibroDto;
import edu.itm.proyecto2026.frontend.webservicesclients.EjemplarApiClient;
import edu.itm.proyecto2026.frontend.webservicesclients.LibroApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
public class EjemplaresFrame extends JFrame {

    @Autowired
    private EjemplarApiClient ejemplarClient;

    @Autowired
    private LibroApiClient libroClient;

    private final JTextField txtId = new JTextField();
    private final JComboBox<LibroDto> comboLibro = new JComboBox<>();
    private final JTextField txtCodigo = new JTextField();
    private final JTextField txtFechaAdquisicion = new JTextField(LocalDate.now().toString());
    private final JTextField txtEstado = new JTextField("bueno");
    private final JTextField txtUbicacion = new JTextField();
    private final JTextField txtDisponibilidad = new JTextField();

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Código", "Libro", "Adquisición", "Estado", "Ubicación", "Disponible"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    private List<EjemplarDto> ejemplares = new ArrayList<>();
    private JFrame principal;
    private boolean inicializado = false;

    public void abrir(JFrame ventanaPrincipal) {
        principal = ventanaPrincipal;
        if (!inicializado) {
            inicializar();
            inicializado = true;
        }
        principal.setVisible(false);
        cargarOpciones();
        cargarTabla();
        setVisible(true);
    }

    private void inicializar() {
        setTitle("Ejemplares");
        setSize(1000, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        txtId.setEditable(false);
        txtDisponibilidad.setEditable(false);

        JPanel formulario = new JPanel(new GridLayout(0, 4, 8, 8));
        formulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        formulario.add(new JLabel("ID:"));
        formulario.add(txtId);
        formulario.add(new JLabel("Libro:"));
        formulario.add(comboLibro);
        formulario.add(new JLabel("Código de inventario:"));
        formulario.add(txtCodigo);
        formulario.add(new JLabel("Adquisición (aaaa-mm-dd):"));
        formulario.add(txtFechaAdquisicion);
        formulario.add(new JLabel("Estado:"));
        formulario.add(txtEstado);
        formulario.add(new JLabel("Ubicación:"));
        formulario.add(txtUbicacion);
        formulario.add(new JLabel("Disponibilidad:"));
        formulario.add(txtDisponibilidad);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0 && fila < ejemplares.size()) {
                mostrar(ejemplares.get(fila));
            }
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botones.add(boton("Guardar nuevo", this::guardar));
        botones.add(boton("Actualizar", this::actualizar));
        botones.add(boton("Eliminar", this::eliminar));
        botones.add(boton("Limpiar", this::limpiar));
        botones.add(boton("Refrescar", this::refrescar));
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

    private void refrescar() {
        cargarOpciones();
        cargarTabla();
    }

    private void cargarOpciones() {
        try {
            comboLibro.removeAllItems();
            for (LibroDto l : libroClient.getLibros()) {
                comboLibro.addItem(l);
            }
            comboLibro.setSelectedIndex(-1);
        } catch (Exception e) {
            error(e);
        }
    }

    private void cargarTabla() {
        try {
            ejemplares = ejemplarClient.getEjemplares();
            modelo.setRowCount(0);
            for (EjemplarDto ej : ejemplares) {
                modelo.addRow(new Object[]{
                        ej.getIdEjemplar(),
                        ej.getCodigoInventario(),
                        ej.getLibro().getTituloLibro(),
                        ej.getFechaAdquisicion(),
                        ej.getEstadoEjemplar(),
                        ej.getUbicacionEjemplar(),
                        ej.getDisponibilidadEjemplar()
                });
            }
        } catch (Exception e) {
            error(e);
        }
    }

    private <T> void seleccionar(JComboBox<T> combo, Integer id, Function<T, Integer> obtenerId) {
        combo.setSelectedIndex(-1);
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (obtenerId.apply(combo.getItemAt(i)).equals(id)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void mostrar(EjemplarDto ej) {
        txtId.setText(String.valueOf(ej.getIdEjemplar()));
        seleccionar(comboLibro, ej.getLibro().getIdLibro(), LibroDto::getIdLibro);
        txtCodigo.setText(valor(ej.getCodigoInventario()));
        txtFechaAdquisicion.setText(ej.getFechaAdquisicion() == null ? "" : ej.getFechaAdquisicion().toString());
        txtEstado.setText(valor(ej.getEstadoEjemplar()));
        txtUbicacion.setText(valor(ej.getUbicacionEjemplar()));
        txtDisponibilidad.setText(valor(ej.getDisponibilidadEjemplar()));
    }

    private EjemplarDto leerFormulario() {
        LibroDto libro = (LibroDto) comboLibro.getSelectedItem();
        if (libro == null || txtCodigo.getText().isBlank()
                || txtFechaAdquisicion.getText().isBlank() || txtEstado.getText().isBlank()) {
            aviso("Libro, código de inventario, fecha de adquisición y estado son obligatorios.");
            return null;
        }
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(txtFechaAdquisicion.getText().trim());
        } catch (DateTimeParseException e) {
            aviso("La fecha debe tener el formato aaaa-mm-dd, por ejemplo 2026-11-15.");
            return null;
        }
        EjemplarDto ejemplar = new EjemplarDto();
        ejemplar.setLibro(libro);
        ejemplar.setCodigoInventario(txtCodigo.getText().trim());
        ejemplar.setFechaAdquisicion(fecha);
        ejemplar.setEstadoEjemplar(txtEstado.getText().trim());
        ejemplar.setUbicacionEjemplar(txtUbicacion.getText().trim());
        return ejemplar;
    }

    private void guardar() {
        EjemplarDto ejemplar = leerFormulario();
        if (ejemplar == null) {
            return;
        }
        try {
            ejemplarClient.insertarEjemplar(ejemplar);
            info("Ejemplar guardado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            error(e);
        }
    }

    private void actualizar() {
        if (txtId.getText().isBlank()) {
            aviso("Selecciona un ejemplar de la tabla.");
            return;
        }
        EjemplarDto ejemplar = leerFormulario();
        if (ejemplar == null) {
            return;
        }
        ejemplar.setIdEjemplar(Integer.parseInt(txtId.getText()));
        try {
            ejemplarClient.actualizarEjemplar(ejemplar);
            info("Ejemplar actualizado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            error(e);
        }
    }

    private void eliminar() {
        if (txtId.getText().isBlank()) {
            aviso("Selecciona un ejemplar de la tabla.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el ejemplar seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            ejemplarClient.eliminarEjemplar(Integer.parseInt(txtId.getText()));
            info("Ejemplar eliminado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            error(e);
        }
    }

    private void limpiar() {
        txtId.setText("");
        comboLibro.setSelectedIndex(-1);
        txtCodigo.setText("");
        txtFechaAdquisicion.setText(LocalDate.now().toString());
        txtEstado.setText("bueno");
        txtUbicacion.setText("");
        txtDisponibilidad.setText("");
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