package edu.itm.proyecto2026.frontend.Dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PrestamoDto {
    private Integer idPrestamo;
    private EjemplarDto ejemplar;
    private UsuarioDto usuario;
    private BibliotecarioDto bibliotecario;
    private String estadoPrestamo;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionPrevista;
    private LocalDate fechaDevolucionReal;

    @Override
    public String toString() {
        String nombre = usuario == null ? "" : usuario.toString();
        String codigo = ejemplar == null ? "" : ejemplar.getCodigoInventario();
        return "#" + idPrestamo + " - " + nombre + " - " + codigo;
    }
}