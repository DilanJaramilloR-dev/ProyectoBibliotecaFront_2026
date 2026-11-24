package edu.itm.proyecto2026.frontend.Windows;

import edu.itm.proyecto2026.frontend.Dtos.BibliotecarioDto;
import edu.itm.proyecto2026.frontend.Dtos.PersonaDto;
import edu.itm.proyecto2026.frontend.webservicesclients.BibliotecarioApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class BibliotecariosFrame extends JFrame {

    @Autowired
    private BibliotecarioApiClient bibliotecarioClient;

    private final JTextField txtId = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtApellido = new JTextField();
    private final JTextField txtCorreo = new JTextField();
    private final JTextField txtTelefono = new JTextField();
    private final JTextField txtDocumento = new JTextField();
    private final JTextField txtLogin = new JTextField();
    private final JPasswordField txtContrasena = new JPasswordField();

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Apellido", "Correo", "Teléfono", "Documento", "Usuario login"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    private List<BibliotecarioDto> bibliotecarios = new ArrayList<>();
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
        setTitle("Bibliotecarios");
        setSize(950, 560);
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
        formulario.add(new JLabel("Correo:"));
        formulario.add(txtCorreo);
        formulario.add(new JLabel("Teléfono:"));
        formulario.add(txtTelefono);
        formulario.add(new JLabel("Documento:"));
        formulario.add(txtDocumento);
        formulario.add(new JLabel("Usuario login:"));
        formulario.add(txtLogin);
        formulario.add(new JLabel("Contraseña:"));
        formulario.add(txtContrasena);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0 && fila < bibliotecarios.size()) {
                mostrar(bibliotecarios.get(fila));
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
            bibliotecarios = bibliotecarioClient.getBibliotecarios();
            modelo.setRowCount(0);
            for (BibliotecarioDto b : bibliotecarios) {
                modelo.addRow(new Object[]{
                        b.getIdBibliotecario(),
                        b.getPersona().getNombrePersona(),
                        b.getPersona().getApellidoPersona(),
                        b.getPersona().getCorreoPersona(),
                        b.getPersona().getTelefonoPersona(),
                        b.getDocumentoBibliotecario(),
                        b.getUsuarioLoginBibliotecario()
                });
            }
        } catch (Exception e) {
            error(e);
        }
    }

    private void mostrar(BibliotecarioDto b) {
        txtId.setText(String.valueOf(b.getIdBibliotecario()));
        txtNombre.setText(valor(b.getPersona().getNombrePersona()));
        txtApellido.setText(valor(b.getPersona().getApellidoPersona()));
        txtCorreo.setText(valor(b.getPersona().getCorreoPersona()));
        txtTelefono.setText(valor(b.getPersona().getTelefonoPersona()));
        txtDocumento.setText(valor(b.getDocumentoBibliotecario()));
        txtLogin.setText(valor(b.getUsuarioLoginBibliotecario()));
        txtContrasena.setText("");
    }

    private BibliotecarioDto leerFormulario() {
        if (txtNombre.getText().isBlank() || txtApellido.getText().isBlank()
                || txtCorreo.getText().isBlank() || txtTelefono.getText().isBlank()
                || txtDocumento.getText().isBlank() || txtLogin.getText().isBlank()) {
            aviso("Nombre, apellido, correo, teléfono, documento y usuario login son obligatorios.");
            return null;
        }
        PersonaDto persona = new PersonaDto();
        persona.setNombrePersona(txtNombre.getText().trim());
        persona.setApellidoPersona(txtApellido.getText().trim());
        persona.setCorreoPersona(txtCorreo.getText().trim());
        persona.setTelefonoPersona(txtTelefono.getText().trim());

        BibliotecarioDto bibliotecario = new BibliotecarioDto();
        bibliotecario.setPersona(persona);
        bibliotecario.setDocumentoBibliotecario(txtDocumento.getText().trim());
        bibliotecario.setUsuarioLoginBibliotecario(txtLogin.getText().trim());
        bibliotecario.setContrasenaBibliotecario(new String(txtContrasena.getPassword()));
        return bibliotecario;
    }

    private void guardar() {
        BibliotecarioDto bibliotecario = leerFormulario();
        if (bibliotecario == null) {
            return;
        }
        if (bibliotecario.getContrasenaBibliotecario().isBlank()) {
            aviso("La contraseña es obligatoria para un bibliotecario nuevo.");
            return;
        }
        try {
            bibliotecarioClient.insertarBibliotecario(bibliotecario);
            info("Bibliotecario guardado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            error(e);
        }
    }

    private void actualizar() {
        if (txtId.getText().isBlank()) {
            aviso("Selecciona un bibliotecario de la tabla.");
            return;
        }
        BibliotecarioDto bibliotecario = leerFormulario();
        if (bibliotecario == null) {
            return;
        }
        bibliotecario.setIdBibliotecario(Integer.parseInt(txtId.getText()));
        try {
            bibliotecarioClient.actualizarBibliotecario(bibliotecario);
            info("Bibliotecario actualizado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            error(e);
        }
    }

    private void eliminar() {
        if (txtId.getText().isBlank()) {
            aviso("Selecciona un bibliotecario de la tabla.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this, "¿Eliminar el bibliotecario seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            bibliotecarioClient.eliminarBibliotecario(Integer.parseInt(txtId.getText()));
            info("Bibliotecario eliminado correctamente.");
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
        txtCorreo.setText("");
        txtTelefono.setText("");
        txtDocumento.setText("");
        txtLogin.setText("");
        txtContrasena.setText("");
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