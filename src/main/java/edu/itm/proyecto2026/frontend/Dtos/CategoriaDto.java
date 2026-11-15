package edu.itm.proyecto2026.frontend.Dtos;

import lombok.Data;

@Data
public class CategoriaDto {
    private Integer idCategoria;
    private String descripcionCategoria;
    private String nombreCategoria;
    private String estadoCategoria;

    @Override
    public String toString() {
        return nombreCategoria;
    }
}