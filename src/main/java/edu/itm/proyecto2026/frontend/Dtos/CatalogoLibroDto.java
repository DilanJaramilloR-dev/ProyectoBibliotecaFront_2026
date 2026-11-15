package edu.itm.proyecto2026.frontend.Dtos;

import lombok.Data;

@Data
public class CatalogoLibroDto {
    private Long idLibro;
    private String tituloLibro;
    private String isbnLibro;
    private String descripcionLibro;
    private Integer anioPublicacionLibro;
    private String nombreCategoria;
    private String nombreEditorial;
    private String autores;
    private Integer ejemplaresDisponibles;
}