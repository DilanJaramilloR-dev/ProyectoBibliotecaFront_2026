package edu.itm.proyecto2026.frontend.Dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReportePrestamoDto {
    private Long idPrestamo;
    private String codigoInventario;
    private String tituloLibro;
    private String nombreUsuario;
    private String nombreBibliotecario;
    private String estadoPrestamo;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionPrevista;
    private Integer diasRetraso;
}