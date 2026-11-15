package edu.itm.proyecto2026.frontend.webservicesclients;

import edu.itm.proyecto2026.frontend.Dtos.CategoriaDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "categoria-api-client", url = "${api.base-url}/categorias")
public interface CategoriaApiClient {

    @GetMapping("/listar")
    List<CategoriaDto> getCategorias();

    @PostMapping("/nuevo")
    CategoriaDto insertarCategoria(@RequestBody CategoriaDto categoria);

    @PutMapping("/actualizar")
    CategoriaDto actualizarCategoria(@RequestBody CategoriaDto categoria);

    @GetMapping("/consultar/{id}")
    CategoriaDto consultarCategoria(@PathVariable("id") Integer id);

    @DeleteMapping("/eliminar/{id}")
    void eliminarCategoria(@PathVariable("id") Integer id);
}