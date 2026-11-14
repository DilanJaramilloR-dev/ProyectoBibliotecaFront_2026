package edu.itm.proyecto2026.frontend.Dtos;

import lombok.Data;

@Data
public class BibliotecarioDto {
    private Integer idBibliotecario;
    private PersonaDto persona;
    private String usuarioLoginBibliotecario;
    private String contrasenaBibliotecario;
    private String documentoBibliotecario;
}