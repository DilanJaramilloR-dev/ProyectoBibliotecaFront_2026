package edu.itm.proyecto2026.frontend.Dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SancionDto {
    private Integer idSancion;
    private PrestamoDto prestamo;
    private String tipoSancion;
    private String motivoSancion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estadoSancion;
}