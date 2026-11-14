package edu.itm.proyecto2026.frontend.webservicesclients;

import edu.itm.proyecto2026.frontend.Dtos.AutorDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "autor-api-client", url = "${api.base-url}/autores")
public interface AutorApiClient {

    @GetMapping("/listar")
    List<AutorDto> getAutores();

    @PostMapping("/nuevo")
    AutorDto insertarAutor(@RequestBody AutorDto autor);

    @PutMapping("/actualizar")
    AutorDto actualizarAutor(@RequestBody AutorDto autor);

    @GetMapping("/consultar/{id}")
    AutorDto consultarAutor(@PathVariable("id") Integer id);

    @DeleteMapping("/eliminar/{id}")
    void eliminarAutor(@PathVariable("id") Integer id);
}