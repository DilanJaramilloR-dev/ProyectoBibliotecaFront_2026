package edu.itm.proyecto2026.frontend.Dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AutorDto {
    private Integer idAutor;
    private String nombreAutor;
    private String apellidoAutor;
    private String nacionalidadAutor;
    private LocalDate fechaNacimiento;

    @Override
    public String toString() {
        return nombreAutor + (apellidoAutor == null || apellidoAutor.isBlank() ? "" : " " + apellidoAutor);
    }
}
