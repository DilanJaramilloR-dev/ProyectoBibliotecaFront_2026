package edu.itm.proyecto2026.frontend.Windows;

import edu.itm.proyecto2026.frontend.Dtos.PersonaDto;
import edu.itm.proyecto2026.frontend.Dtos.UsuarioDto;
import edu.itm.proyecto2026.frontend.webservicesclients.UsuarioApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class UsuariosFrame extends JFrame {

    @Autowired
    private UsuarioApiClient usuarioClient;

    private final JTextField txtId = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtApellido = new JTextField();
    private final JTextField txtCorreo = new JTextField();
    private final JTextField txtTelefono = new JTextField();
    private final JTextField txtDireccion = new JTextField();

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Apellido", "Correo", "Teléfono", "Dirección", "Registro"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    private List<UsuarioDto> usuarios = new ArrayList<>();
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
        setTitle("Usuarios");
        setSize(950, 520);
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
        formulario.add(new JLabel("Apellido:"));
        formulario.add(txtApellido);
        formulario.add(new JLabel("Correo:"));
        formulario.add(txtCorreo);
        formulario.add(new JLabel("Teléfono:"));
        formulario.add(txtTelefono);
        formulario.add(new JLabel("Dirección:"));
        formulario.add(txtDireccion);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0 && fila < usuarios.size()) {
                mostrar(usuarios.get(fila));
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
            usuarios = usuarioClient.getUsuarios();
            modelo.setRowCount(0);
            for (UsuarioDto u : usuarios) {
                modelo.addRow(new Object[]{
                        u.getIdUsuario(),
                        u.getPersona().getNombrePersona(),
                        u.getPersona().getApellidoPersona(),
                        u.getPersona().getCorreoPersona(),
                        u.getPersona().getTelefonoPersona(),
                        u.getDireccionUsuario(),
                        u.getFechaRegistroUsuario()
                });
            }
        } catch (Exception e) {
            error(e);
        }
    }

    private void mostrar(UsuarioDto u) {
        txtId.setText(String.valueOf(u.getIdUsuario()));
        txtNombre.setText(valor(u.getPersona().getNombrePersona()));
        txtApellido.setText(valor(u.getPersona().getApellidoPersona()));
        txtCorreo.setText(valor(u.getPersona().getCorreoPersona()));
        txtTelefono.setText(valor(u.getPersona().getTelefonoPersona()));
        txtDireccion.setText(valor(u.getDireccionUsuario()));
    }

    private UsuarioDto leerFormulario() {
        if (txtNombre.getText().isBlank() || txtApellido.getText().isBlank()
                || txtCorreo.getText().isBlank() || txtTelefono.getText().isBlank()) {
            aviso("Nombre, apellido, correo y teléfono son obligatorios.");
            return null;
        }
        PersonaDto persona = new PersonaDto();
        persona.setNombrePersona(txtNombre.getText().trim());
        persona.setApellidoPersona(txtApellido.getText().trim());
        persona.setCorreoPersona(txtCorreo.getText().trim());
        persona.setTelefonoPersona(txtTelefono.getText().trim());

        UsuarioDto usuario = new UsuarioDto();
        usuario.setPersona(persona);
        usuario.setDireccionUsuario(txtDireccion.getText().trim());
        return usuario;
    }

    private void guardar() {
        UsuarioDto usuario = leerFormulario();
        if (usuario == null) {
            return;
        }
        try {
            usuarioClient.insertarUsuario(usuario);
            info("Usuario guardado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            error(e);
        }
    }

    private void actualizar() {
        if (txtId.getText().isBlank()) {
            aviso("Selecciona un usuario de la tabla.");
            return;
        }
        UsuarioDto usuario = leerFormulario();
        if (usuario == null) {
            return;
        }
        usuario.setIdUsuario(Integer.parseInt(txtId.getText()));
        try {
            usuarioClient.actualizarUsuario(usuario);
            info("Usuario actualizado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            error(e);
        }
    }

    private void eliminar() {
        if (txtId.getText().isBlank()) {
            aviso("Selecciona un usuario de la tabla.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el usuario seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            usuarioClient.eliminarUsuario(Integer.parseInt(txtId.getText()));
            info("Usuario eliminado correctamente.");
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
        txtDireccion.setText("");
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