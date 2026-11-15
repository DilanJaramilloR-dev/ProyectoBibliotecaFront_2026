package edu.itm.proyecto2026.frontend.webservicesclients;

import edu.itm.proyecto2026.frontend.Dtos.BibliotecarioDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "bibliotecario-api-client", url = "${api.base-url}/bibliotecarios")
public interface BibliotecarioApiClient {

    @GetMapping("/listar")
    List<BibliotecarioDto> getBibliotecarios();

    @PostMapping("/nuevo")
    BibliotecarioDto insertarBibliotecario(@RequestBody BibliotecarioDto bibliotecario);

    @PutMapping("/actualizar")
    BibliotecarioDto actualizarBibliotecario(@RequestBody BibliotecarioDto bibliotecario);

    @GetMapping("/consultar/{id}")
    BibliotecarioDto consultarBibliotecario(@PathVariable("id") Integer id);

    @DeleteMapping("/eliminar/{id}")
    void eliminarBibliotecario(@PathVariable("id") Integer id);
}