package edu.itm.proyecto2026.frontend.Windows;

import edu.itm.proyecto2026.frontend.Dtos.BibliotecarioDto;
import edu.itm.proyecto2026.frontend.Dtos.EjemplarDto;
import edu.itm.proyecto2026.frontend.Dtos.PrestamoDto;
import edu.itm.proyecto2026.frontend.Dtos.UsuarioDto;
import edu.itm.proyecto2026.frontend.webservicesclients.BibliotecarioApiClient;
import edu.itm.proyecto2026.frontend.webservicesclients.EjemplarApiClient;
import edu.itm.proyecto2026.frontend.webservicesclients.PrestamoApiClient;
import edu.itm.proyecto2026.frontend.webservicesclients.UsuarioApiClient;
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
public class PrestamosFrame extends JFrame {

    @Autowired
    private PrestamoApiClient prestamoClient;

    @Autowired
    private EjemplarApiClient ejemplarClient;

    @Autowired
    private UsuarioApiClient usuarioClient;

    @Autowired
    private BibliotecarioApiClient bibliotecarioClient;

    private final JTextField txtId = new JTextField();
    private final JTextField txtEstado = new JTextField();
    private final JComboBox<EjemplarDto> comboEjemplar = new JComboBox<>();
    private final JComboBox<UsuarioDto> comboUsuario = new JComboBox<>();
    private final JComboBox<BibliotecarioDto> comboBibliotecario = new JComboBox<>();
    private final JTextField txtFechaPrestamo = new JTextField(LocalDate.now().toString());
    private final JTextField txtFechaPrevista = new JTextField(LocalDate.now().plusDays(7).toString());
    private final JTextField txtFechaReal = new JTextField();

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Ejemplar", "Libro", "Usuario", "Bibliotecario", "Estado",
                    "Préstamo", "Prevista", "Devolución real"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    private List<PrestamoDto> prestamos = new ArrayList<>();
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
        setTitle("Préstamos");
        setSize(1150, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        txtId.setEditable(false);
        txtEstado.setEditable(false);
        txtFechaReal.setEditable(false);

        JPanel formulario = new JPanel(new GridLayout(0, 4, 8, 8));
        formulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        formulario.add(new JLabel("ID:"));
        formulario.add(txtId);
        formulario.add(new JLabel("Estado:"));
        formulario.add(txtEstado);
        formulario.add(new JLabel("Ejemplar (disponibles):"));
        formulario.add(comboEjemplar);
        formulario.add(new JLabel("Usuario:"));
        formulario.add(comboUsuario);
        formulario.add(new JLabel("Bibliotecario:"));
        formulario.add(comboBibliotecario);
        formulario.add(new JLabel("Fecha préstamo (aaaa-mm-dd):"));
        formulario.add(txtFechaPrestamo);
        formulario.add(new JLabel("Devolución prevista (aaaa-mm-dd):"));
        formulario.add(txtFechaPrevista);
        formulario.add(new JLabel("Devolución real:"));
        formulario.add(txtFechaReal);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0 && fila < prestamos.size()) {
                mostrar(prestamos.get(fila));
            }
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botones.add(boton("Registrar préstamo", this::registrar));
        botones.add(boton("Devolver", this::devolver));
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
            comboEjemplar.removeAllItems();
            for (EjemplarDto ej : ejemplarClient.getEjemplares()) {
                if ("si".equalsIgnoreCase(ej.getDisponibilidadEjemplar())) {
                    comboEjemplar.addItem(ej);
                }
            }
            comboUsuario.removeAllItems();
            for (UsuarioDto u : usuarioClient.getUsuarios()) {
                comboUsuario.addItem(u);
            }
            comboBibliotecario.removeAllItems();
            for (BibliotecarioDto b : bibliotecarioClient.getBibliotecarios()) {
                comboBibliotecario.addItem(b);
            }
            comboEjemplar.setSelectedIndex(-1);
            comboUsuario.setSelectedIndex(-1);
            comboBibliotecario.setSelectedIndex(-1);
        } catch (Exception e) {
            error(e);
        }
    }

    private void cargarTabla() {
        try {
            prestamos = prestamoClient.getPrestamos();
            modelo.setRowCount(0);
            for (PrestamoDto p : prestamos) {
                modelo.addRow(new Object[]{
                        p.getIdPrestamo(),
                        p.getEjemplar().getCodigoInventario(),
                        p.getEjemplar().getLibro().getTituloLibro(),
                        p.getUsuario().toString(),
                        p.getBibliotecario().toString(),
                        p.getEstadoPrestamo(),
                        p.getFechaPrestamo(),
                        p.getFechaDevolucionPrevista(),
                        p.getFechaDevolucionReal()
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

    private void mostrar(PrestamoDto p) {
        txtId.setText(String.valueOf(p.getIdPrestamo()));
        txtEstado.setText(valor(p.getEstadoPrestamo()));
        seleccionar(comboEjemplar, p.getEjemplar().getIdEjemplar(), EjemplarDto::getIdEjemplar);
        seleccionar(comboUsuario, p.getUsuario().getIdUsuario(), UsuarioDto::getIdUsuario);
        seleccionar(comboBibliotecario, p.getBibliotecario().getIdBibliotecario(),
                BibliotecarioDto::getIdBibliotecario);
        txtFechaPrestamo.setText(p.getFechaPrestamo() == null ? "" : p.getFechaPrestamo().toString());
        txtFechaPrevista.setText(p.getFechaDevolucionPrevista() == null ? "" : p.getFechaDevolucionPrevista().toString());
        txtFechaReal.setText(p.getFechaDevolucionReal() == null ? "" : p.getFechaDevolucionReal().toString());
    }

    private void registrar() {
        EjemplarDto ejemplar = (EjemplarDto) comboEjemplar.getSelectedItem();
        UsuarioDto usuario = (UsuarioDto) comboUsuario.getSelectedItem();
        BibliotecarioDto bibliotecario = (BibliotecarioDto) comboBibliotecario.getSelectedItem();
        if (ejemplar == null || usuario == null || bibliotecario == null) {
            aviso("Selecciona un ejemplar disponible, un usuario y un bibliotecario.");
            return;
        }
        if (txtFechaPrestamo.getText().isBlank() || txtFechaPrevista.getText().isBlank()) {
            aviso("La fecha de préstamo y la de devolución prevista son obligatorias.");
            return;
        }
        LocalDate fechaPrestamo;
        LocalDate fechaPrevista;
        try {
            fechaPrestamo = LocalDate.parse(txtFechaPrestamo.getText().trim());
            fechaPrevista = LocalDate.parse(txtFechaPrevista.getText().trim());
        } catch (DateTimeParseException e) {
            aviso("Las fechas deben tener el formato aaaa-mm-dd, por ejemplo 2026-11-15.");
            return;
        }
        if (fechaPrevista.isBefore(fechaPrestamo)) {
            aviso("La devolución prevista no puede ser anterior a la fecha de préstamo.");
            return;
        }
        PrestamoDto prestamo = new PrestamoDto();
        prestamo.setEjemplar(ejemplar);
        prestamo.setUsuario(usuario);
        prestamo.setBibliotecario(bibliotecario);
        prestamo.setFechaPrestamo(fechaPrestamo);
        prestamo.setFechaDevolucionPrevista(fechaPrevista);
        try {
            prestamoClient.insertarPrestamo(prestamo);
            info("Préstamo registrado correctamente.");
            limpiar();
            cargarOpciones();
            cargarTabla();
        } catch (Exception e) {
            error(e);
        }
    }

    private void devolver() {
        if (txtId.getText().isBlank()) {
            aviso("Selecciona un préstamo de la tabla.");
            return;
        }
        if ("devuelto".equalsIgnoreCase(txtEstado.getText())) {
            aviso("Este préstamo ya fue devuelto.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Registrar la devolución del préstamo seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            prestamoClient.devolverPrestamo(Integer.parseInt(txtId.getText()));
            info("Devolución registrada. Si fue fuera del plazo, el sistema generó una sanción automática.");
            limpiar();
            cargarOpciones();
            cargarTabla();
        } catch (Exception e) {
            error(e);
        }
    }

    private void eliminar() {
        if (txtId.getText().isBlank()) {
            aviso("Selecciona un préstamo de la tabla.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el préstamo seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            prestamoClient.eliminarPrestamo(Integer.parseInt(txtId.getText()));
            info("Préstamo eliminado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            error(e);
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtEstado.setText("");
        comboEjemplar.setSelectedIndex(-1);
        comboUsuario.setSelectedIndex(-1);
        comboBibliotecario.setSelectedIndex(-1);
        txtFechaPrestamo.setText(LocalDate.now().toString());
        txtFechaPrevista.setText(LocalDate.now().plusDays(7).toString());
        txtFechaReal.setText("");
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