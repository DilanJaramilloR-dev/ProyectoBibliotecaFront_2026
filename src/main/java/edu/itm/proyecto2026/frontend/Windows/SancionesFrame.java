package edu.itm.proyecto2026.frontend.Windows;

import edu.itm.proyecto2026.frontend.Dtos.PrestamoDto;
import edu.itm.proyecto2026.frontend.Dtos.SancionDto;
import edu.itm.proyecto2026.frontend.webservicesclients.PrestamoApiClient;
import edu.itm.proyecto2026.frontend.webservicesclients.SancionApiClient;
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
public class SancionesFrame extends JFrame {

    @Autowired
    private SancionApiClient sancionClient;

    @Autowired
    private PrestamoApiClient prestamoClient;

    private final JTextField txtId = new JTextField();
    private final JComboBox<PrestamoDto> comboPrestamo = new JComboBox<>();
    private final JTextField txtTipo = new JTextField("multa");
    private final JTextField txtMotivo = new JTextField();
    private final JTextField txtFechaInicio = new JTextField(LocalDate.now().toString());
    private final JTextField txtFechaFin = new JTextField();
    private final JTextField txtEstado = new JTextField("activa");

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Préstamo", "Tipo", "Motivo", "Inicio", "Fin", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    private List<SancionDto> sanciones = new ArrayList<>();
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
        setTitle("Sanciones");
        setSize(1000, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        txtId.setEditable(false);

        JPanel formulario = new JPanel(new GridLayout(0, 4, 8, 8));
        formulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        formulario.add(new JLabel("ID:"));
        formulario.add(txtId);
        formulario.add(new JLabel("Préstamo:"));
        formulario.add(comboPrestamo);
        formulario.add(new JLabel("Tipo:"));
        formulario.add(txtTipo);
        formulario.add(new JLabel("Motivo:"));
        formulario.add(txtMotivo);
        formulario.add(new JLabel("Inicio (aaaa-mm-dd):"));
        formulario.add(txtFechaInicio);
        formulario.add(new JLabel("Fin (aaaa-mm-dd, opcional):"));
        formulario.add(txtFechaFin);
        formulario.add(new JLabel("Estado:"));
        formulario.add(txtEstado);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0 && fila < sanciones.size()) {
                mostrar(sanciones.get(fila));
            }
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botones.add(boton("Guardar nueva", this::guardar));
        botones.add(boton("Actualizar", this::actualizar));
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
            comboPrestamo.removeAllItems();
            for (PrestamoDto p : prestamoClient.getPrestamos()) {
                comboPrestamo.addItem(p);
            }
            comboPrestamo.setSelectedIndex(-1);
        } catch (Exception e) {
            error(e);
        }
    }

    private void cargarTabla() {
        try {
            sanciones = sancionClient.getSanciones();
            modelo.setRowCount(0);
            for (SancionDto s : sanciones) {
                modelo.addRow(new Object[]{
                        s.getIdSancion(),
                        "#" + s.getPrestamo().getIdPrestamo(),
                        s.getTipoSancion(),
                        s.getMotivoSancion(),
                        s.getFechaInicio(),
                        s.getFechaFin(),
                        s.getEstadoSancion()
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

    private void mostrar(SancionDto s) {
        txtId.setText(String.valueOf(s.getIdSancion()));
        seleccionar(comboPrestamo, s.getPrestamo().getIdPrestamo(), PrestamoDto::getIdPrestamo);
        comboPrestamo.setEnabled(false);
        txtTipo.setText(valor(s.getTipoSancion()));
        txtMotivo.setText(valor(s.getMotivoSancion()));
        txtFechaInicio.setText(s.getFechaInicio() == null ? "" : s.getFechaInicio().toString());
        txtFechaFin.setText(s.getFechaFin() == null ? "" : s.getFechaFin().toString());
        txtEstado.setText(valor(s.getEstadoSancion()));
    }

    private boolean fechaValida(String texto) {
        if (texto.isBlank()) {
            return true;
        }
        try {
            LocalDate.parse(texto.trim());
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private LocalDate fecha(String texto) {
        return texto.isBlank() ? null : LocalDate.parse(texto.trim());
    }

    private SancionDto leerFormulario(boolean nueva) {
        PrestamoDto prestamo = (PrestamoDto) comboPrestamo.getSelectedItem();
        if (nueva && prestamo == null) {
            aviso("Selecciona el préstamo al que pertenece la sanción.");
            return null;
        }
        if (txtTipo.getText().isBlank()) {
            aviso("El tipo de sanción es obligatorio.");
            return null;
        }
        if (!fechaValida(txtFechaInicio.getText()) || !fechaValida(txtFechaFin.getText())) {
            aviso("Las fechas deben tener el formato aaaa-mm-dd, por ejemplo 2026-11-15.");
            return null;
        }
        SancionDto sancion = new SancionDto();
        sancion.setPrestamo(prestamo);
        sancion.setTipoSancion(txtTipo.getText().trim());
        sancion.setMotivoSancion(txtMotivo.getText().trim());
        sancion.setFechaInicio(fecha(txtFechaInicio.getText()));
        sancion.setFechaFin(fecha(txtFechaFin.getText()));
        sancion.setEstadoSancion(txtEstado.getText().isBlank() ? "activa" : txtEstado.getText().trim());
        return sancion;
    }

    private void guardar() {
        SancionDto sancion = leerFormulario(true);
        if (sancion == null) {
            return;
        }
        try {
            sancionClient.insertarSancion(sancion);
            info("Sanción guardada correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            error(e);
        }
    }

    private void actualizar() {
        if (txtId.getText().isBlank()) {
            aviso("Selecciona una sanción de la tabla.");
            return;
        }
        SancionDto sancion = leerFormulario(false);
        if (sancion == null) {
            return;
        }
        sancion.setIdSancion(Integer.parseInt(txtId.getText()));
        try {
            sancionClient.actualizarSancion(sancion);
            info("Sanción actualizada correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            error(e);
        }
    }

    private void limpiar() {
        txtId.setText("");
        comboPrestamo.setSelectedIndex(-1);
        comboPrestamo.setEnabled(true);
        txtTipo.setText("multa");
        txtMotivo.setText("");
        txtFechaInicio.setText(LocalDate.now().toString());
        txtFechaFin.setText("");
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