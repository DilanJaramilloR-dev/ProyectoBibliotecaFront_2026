package edu.itm.proyecto2026.frontend.Dtos;

import lombok.Data;

import java.util.List;

@Data
public class LibroDto {
    private Integer idLibro;
    private CategoriaDto categoria;
    private EditorialDto editorial;
    private String tituloLibro;
    private String descripcionLibro;
    private String isbnLibro;
    private Integer anioPublicacionLibro;
    private List<AutorDto> autores;

    @Override
    public String toString() {
        return tituloLibro;
    }
}