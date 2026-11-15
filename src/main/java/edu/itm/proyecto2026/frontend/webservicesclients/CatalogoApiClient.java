package edu.itm.proyecto2026.frontend.webservicesclients;

import edu.itm.proyecto2026.frontend.Dtos.CatalogoLibroDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "catalogo-api-client", url = "${api.base-url}/catalogo")
public interface CatalogoApiClient {

    @GetMapping("/listar")
    List<CatalogoLibroDto> getCatalogo(
            @RequestParam(name = "titulo", required = false) String titulo,
            @RequestParam(name = "autor", required = false) String autor,
            @RequestParam(name = "categoria", required = false) String categoria);

    @GetMapping("/consultar/{id}")
    CatalogoLibroDto consultarCatalogoLibro(@PathVariable("id") Integer id);
}