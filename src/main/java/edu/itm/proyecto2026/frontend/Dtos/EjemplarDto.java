package edu.itm.proyecto2026.frontend.Dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EjemplarDto {
    private Integer idEjemplar;
    private LibroDto libro;
    private String codigoInventario;
    private LocalDate fechaAdquisicion;
    private String estadoEjemplar;
    private String ubicacionEjemplar;
    private String disponibilidadEjemplar;

    @Override
    public String toString() {
        String titulo = libro == null ? "" : libro.getTituloLibro();
        return codigoInventario + " - " + titulo;
    }
}