package edu.itm.proyecto2026.frontend.Windows;

import edu.itm.proyecto2026.frontend.Dtos.AutorDto;
import edu.itm.proyecto2026.frontend.Dtos.CategoriaDto;
import edu.itm.proyecto2026.frontend.Dtos.EditorialDto;
import edu.itm.proyecto2026.frontend.Dtos.LibroDto;
import edu.itm.proyecto2026.frontend.webservicesclients.AutorApiClient;
import edu.itm.proyecto2026.frontend.webservicesclients.CategoriaApiClient;
import edu.itm.proyecto2026.frontend.webservicesclients.EditorialApiClient;
import edu.itm.proyecto2026.frontend.webservicesclients.LibroApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LibrosFrame extends JFrame {

    @Autowired
    private LibroApiClient libroClient;

    @Autowired
    private CategoriaApiClient categoriaClient;

    @Autowired
    private EditorialApiClient editorialClient;

    @Autowired
    private AutorApiClient autorClient;

    private final JTextField txtId = new JTextField();
    private final JTextField txtTitulo = new JTextField();
    private final JTextField txtIsbn = new JTextField();
    private final JTextField txtAnio = new JTextField();
    private final JTextField txtDescripcion = new JTextField();
    private final JComboBox<CategoriaDto> comboCategoria = new JComboBox<>();
    private final JComboBox<EditorialDto> comboEditorial = new JComboBox<>();
    private final DefaultListModel<AutorDto> modeloAutores = new DefaultListModel<>();
    private final JList<AutorDto> listaAutores = new JList<>(modeloAutores);

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Título", "ISBN", "Año", "Categoría", "Editorial", "Autores"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    private List<LibroDto> libros = new ArrayList<>();
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
        setTitle("Libros");
        setSize(1050, 660);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        BibliotecaTheme.estilizarVentana(this);

        txtId.setEditable(false);

        JPanel formulario = new JPanel(new GridLayout(0, 4, 8, 8));
        formulario.add(new JLabel("ID:"));
        formulario.add(txtId);
        formulario.add(new JLabel("Título:"));
        formulario.add(txtTitulo);
        formulario.add(new JLabel("ISBN:"));
        formulario.add(txtIsbn);
        formulario.add(new JLabel("Año de publicación:"));
        formulario.add(txtAnio);
        formulario.add(new JLabel("Categoría:"));
        formulario.add(comboCategoria);
        formulario.add(new JLabel("Editorial:"));
        formulario.add(comboEditorial);
        formulario.add(new JLabel("Descripción:"));
        formulario.add(txtDescripcion);

        listaAutores.setVisibleRowCount(4);
        listaAutores.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollAutores = new JScrollPane(listaAutores);
        scrollAutores.setBorder(BorderFactory.createTitledBorder("Autores (Ctrl + clic para seleccionar varios)"));

        JPanel superior = new JPanel(new BorderLayout(8, 8));
        superior.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        superior.add(formulario, BorderLayout.NORTH);
        superior.add(scrollAutores, BorderLayout.CENTER);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0 && fila < libros.size()) {
                mostrar(libros.get(fila));
            }
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botones.add(boton("Guardar nuevo", this::guardar));
        botones.add(boton("Actualizar", this::actualizar));
        botones.add(boton("Eliminar", this::eliminar));
        botones.add(boton("Limpiar", this::limpiar));
        botones.add(boton("Refrescar", this::refrescar));
        botones.add(boton("Volver", this::volver));

        add(superior, BorderLayout.NORTH);
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
            comboCategoria.removeAllItems();
            for (CategoriaDto c : categoriaClient.getCategorias()) {
                comboCategoria.addItem(c);
            }
            comboEditorial.removeAllItems();
            for (EditorialDto ed : editorialClient.getEditoriales()) {
                comboEditorial.addItem(ed);
            }
            modeloAutores.clear();
            for (AutorDto a : autorClient.getAutores()) {
                modeloAutores.addElement(a);
            }
            comboCategoria.setSelectedIndex(-1);
            comboEditorial.setSelectedIndex(-1);
        } catch (Exception e) {
            error(e);
        }
    }

    private void cargarTabla() {
        try {
            libros = libroClient.getLibros();
            modelo.setRowCount(0);
            for (LibroDto l : libros) {
                modelo.addRow(new Object[]{
                        l.getIdLibro(),
                        l.getTituloLibro(),
                        l.getIsbnLibro(),
                        l.getAnioPublicacionLibro(),
                        l.getCategoria().getNombreCategoria(),
                        l.getEditorial().getNombreEditorial(),
                        nombresAutores(l)
                });
            }
        } catch (Exception e) {
            error(e);
        }
    }

    private String nombresAutores(LibroDto libro) {
        if (libro.getAutores() == null) {
            return "";
        }
        return libro.getAutores().stream().map(AutorDto::toString).collect(Collectors.joining(", "));
    }

    private void mostrar(LibroDto l) {
        txtId.setText(String.valueOf(l.getIdLibro()));
        txtTitulo.setText(valor(l.getTituloLibro()));
        txtIsbn.setText(valor(l.getIsbnLibro()));
        txtAnio.setText(l.getAnioPublicacionLibro() == null ? "" : String.valueOf(l.getAnioPublicacionLibro()));
        txtDescripcion.setText(valor(l.getDescripcionLibro()));
        seleccionarCategoria(l.getCategoria().getIdCategoria());
        seleccionarEditorial(l.getEditorial().getIdEditorial());
        seleccionarAutores(l.getAutores());
    }

    private void seleccionarCategoria(Integer id) {
        comboCategoria.setSelectedIndex(-1);
        for (int i = 0; i < comboCategoria.getItemCount(); i++) {
            if (comboCategoria.getItemAt(i).getIdCategoria().equals(id)) {
                comboCategoria.setSelectedIndex(i);
                return;
            }
        }
    }

    private void seleccionarEditorial(Integer id) {
        comboEditorial.setSelectedIndex(-1);
        for (int i = 0; i < comboEditorial.getItemCount(); i++) {
            if (comboEditorial.getItemAt(i).getIdEditorial().equals(id)) {
                comboEditorial.setSelectedIndex(i);
                return;
            }
        }
    }

    private void seleccionarAutores(List<AutorDto> autoresLibro) {
        listaAutores.clearSelection();
        if (autoresLibro == null) {
            return;
        }
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < modeloAutores.size(); i++) {
            for (AutorDto a : autoresLibro) {
                if (modeloAutores.get(i).getIdAutor().equals(a.getIdAutor())) {
                    indices.add(i);
                }
            }
        }
        listaAutores.setSelectedIndices(indices.stream().mapToInt(Integer::intValue).toArray());
    }

    private LibroDto leerFormulario() {
        if (txtTitulo.getText().isBlank() || txtIsbn.getText().isBlank() || txtAnio.getText().isBlank()) {
            aviso("Título, ISBN y año de publicación son obligatorios.");
            return null;
        }
        int anio;
        try {
            anio = Integer.parseInt(txtAnio.getText().trim());
        } catch (NumberFormatException e) {
            aviso("El año de publicación debe ser un número.");
            return null;
        }
        CategoriaDto categoria = (CategoriaDto) comboCategoria.getSelectedItem();
        EditorialDto editorial = (EditorialDto) comboEditorial.getSelectedItem();
        List<AutorDto> autoresSeleccionados = listaAutores.getSelectedValuesList();
        if (categoria == null || editorial == null || autoresSeleccionados.isEmpty()) {
            aviso("Selecciona una categoría, una editorial y al menos un autor. "
                    + "Si las listas están vacías, primero registra esos datos en sus secciones.");
            return null;
        }
        LibroDto libro = new LibroDto();
        libro.setTituloLibro(txtTitulo.getText().trim());
        libro.setIsbnLibro(txtIsbn.getText().trim());
        libro.setAnioPublicacionLibro(anio);
        libro.setDescripcionLibro(txtDescripcion.getText().trim());
        libro.setCategoria(categoria);
        libro.setEditorial(editorial);
        libro.setAutores(new ArrayList<>(autoresSeleccionados));
        return libro;
    }

    private void guardar() {
        LibroDto libro = leerFormulario();
        if (libro == null) {
            return;
        }
        try {
            libroClient.insertarLibro(libro);
            info("Libro guardado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            error(e);
        }
    }

    private void actualizar() {
        if (txtId.getText().isBlank()) {
            aviso("Selecciona un libro de la tabla.");
            return;
        }
        LibroDto libro = leerFormulario();
        if (libro == null) {
            return;
        }
        libro.setIdLibro(Integer.parseInt(txtId.getText()));
        try {
            libroClient.actualizarLibro(libro);
            info("Libro actualizado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            error(e);
        }
    }

    private void eliminar() {
        if (txtId.getText().isBlank()) {
            aviso("Selecciona un libro de la tabla.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el libro seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            libroClient.eliminarLibro(Integer.parseInt(txtId.getText()));
            info("Libro eliminado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            error(e);
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtTitulo.setText("");
        txtIsbn.setText("");
        txtAnio.setText("");
        txtDescripcion.setText("");
        comboCategoria.setSelectedIndex(-1);
        comboEditorial.setSelectedIndex(-1);
        listaAutores.clearSelection();
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