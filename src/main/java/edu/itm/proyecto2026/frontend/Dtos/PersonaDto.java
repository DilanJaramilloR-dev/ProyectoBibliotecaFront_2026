package edu.itm.proyecto2026.frontend.Dtos;

import lombok.Data;

@Data
public class PersonaDto {
    private Integer idPersona;
    private String nombrePersona;
    private String apellidoPersona;
    private String correoPersona;
    private String telefonoPersona;
}