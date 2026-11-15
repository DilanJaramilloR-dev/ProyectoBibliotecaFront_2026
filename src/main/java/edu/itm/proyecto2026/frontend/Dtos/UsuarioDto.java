package edu.itm.proyecto2026.frontend.Dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UsuarioDto {
    private Integer idUsuario;
    private PersonaDto persona;
    private String direccionUsuario;
    private LocalDate fechaRegistroUsuario;

    @Override
    public String toString() {
        if (persona == null) {
            return "Usuario " + idUsuario;
        }
        return persona.getNombrePersona() + " " + persona.getApellidoPersona();
    }
}