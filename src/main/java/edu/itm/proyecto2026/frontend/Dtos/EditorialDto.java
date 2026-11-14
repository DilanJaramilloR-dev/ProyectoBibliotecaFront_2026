package edu.itm.proyecto2026.frontend.Dtos;

import lombok.Data;

@Data
public class EditorialDto {
    private Integer idEditorial;
    private String correoEditorial;
    private String nombreEditorial;
    private String paisEditorial;
    private String telefonoEditorial;

    @Override
    public String toString() {
        return nombreEditorial;
    }
}
