package edu.itm.proyecto2026.frontend.webservicesclients;

import edu.itm.proyecto2026.frontend.Dtos.LibroDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "libro-api-client", url = "${api.base-url}/libros")
public interface LibroApiClient {

    @GetMapping("/listar")
    List<LibroDto> getLibros();

    @PostMapping("/nuevo")
    LibroDto insertarLibro(@RequestBody LibroDto libro);

    @PutMapping("/actualizar")
    LibroDto actualizarLibro(@RequestBody LibroDto libro);

    @GetMapping("/consultar/{id}")
    LibroDto consultarLibro(@PathVariable("id") Integer id);

    @DeleteMapping("/eliminar/{id}")
    void eliminarLibro(@PathVariable("id") Integer id);
}
