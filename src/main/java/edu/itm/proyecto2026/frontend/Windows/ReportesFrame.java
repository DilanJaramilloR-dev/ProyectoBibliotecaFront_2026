package edu.itm.proyecto2026.frontend.Windows;

import edu.itm.proyecto2026.frontend.Dtos.ReporteInventarioDto;
import edu.itm.proyecto2026.frontend.Dtos.ReportePrestamoDto;
import edu.itm.proyecto2026.frontend.webservicesclients.ReporteApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class ReportesFrame extends JFrame {

    @Autowired
    private ReporteApiClient reporteClient;

    private final JLabel lblTitulo = new JLabel("Selecciona un reporte", SwingConstants.CENTER);

    private final DefaultTableModel modelo = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    private JFrame principal;
    private boolean inicializado = false;

    public void abrir(JFrame ventanaPrincipal) {
        principal = ventanaPrincipal;
        if (!inicializado) {
            inicializar();
            inicializado = true;
        }
        principal.setVisible(false);
        setVisible(true);
    }

    private void inicializar() {
        setTitle("Reportes");
        setSize(1100, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botones.add(boton("Préstamos activos", this::cargarActivos));
        botones.add(boton("Préstamos vencidos", this::cargarVencidos));
        botones.add(boton("Inventario", this::cargarInventario));
        botones.add(boton("Volver", this::volver));

        add(lblTitulo, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }

    private JButton boton(String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(e -> accion.run());
        return boton;
    }

    private void cargarActivos() {
        try {
            mostrarPrestamos("Préstamos activos", reporteClient.getPrestamosActivos());
        } catch (Exception e) {
            error(e);
        }
    }

    private void cargarVencidos() {
        try {
            mostrarPrestamos("Préstamos vencidos", reporteClient.getPrestamosVencidos());
        } catch (Exception e) {
            error(e);
        }
    }

    private void mostrarPrestamos(String titulo, List<ReportePrestamoDto> datos) {
        modelo.setColumnIdentifiers(new String[]{"ID", "Código", "Libro", "Usuario", "Bibliotecario",
                "Estado", "Préstamo", "Prevista", "Días de retraso"});
        modelo.setRowCount(0);
        for (ReportePrestamoDto r : datos) {
            modelo.addRow(new Object[]{
                    r.getIdPrestamo(),
                    r.getCodigoInventario(),
                    r.getTituloLibro(),
                    r.getNombreUsuario(),
                    r.getNombreBibliotecario(),
                    r.getEstadoPrestamo(),
                    r.getFechaPrestamo(),
                    r.getFechaDevolucionPrevista(),
                    r.getDiasRetraso()
            });
        }
        lblTitulo.setText(titulo + " (" + datos.size() + " registros)");
    }

    private void cargarInventario() {
        try {
            List<ReporteInventarioDto> datos = reporteClient.getInventario();
            modelo.setColumnIdentifiers(new String[]{"ID", "Título", "ISBN", "Categoría",
                    "Total ejemplares", "Disponibles", "No disponibles"});
            modelo.setRowCount(0);
            for (ReporteInventarioDto r : datos) {
                modelo.addRow(new Object[]{
                        r.getIdLibro(),
                        r.getTituloLibro(),
                        r.getIsbnLibro(),
                        r.getNombreCategoria(),
                        r.getTotalEjemplares(),
                        r.getEjemplaresDisponibles(),
                        r.getEjemplaresNoDisponibles()
                });
            }
            lblTitulo.setText("Inventario (" + datos.size() + " libros)");
        } catch (Exception e) {
            error(e);
        }
    }

    private void volver() {
        setVisible(false);
        principal.setVisible(true);
    }

    private void error(Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}