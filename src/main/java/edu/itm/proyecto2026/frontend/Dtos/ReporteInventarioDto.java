package edu.itm.proyecto2026.frontend.Dtos;

import lombok.Data;

@Data
public class ReporteInventarioDto {
    private Long idLibro;
    private String tituloLibro;
    private String isbnLibro;
    private String nombreCategoria;
    private Integer totalEjemplares;
    private Integer ejemplaresDisponibles;
    private Integer ejemplaresNoDisponibles;
}