package edu.itm.proyecto2026.frontend.webservicesclients;

import edu.itm.proyecto2026.frontend.Dtos.SancionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "sancion-api-client", url = "${api.base-url}/sanciones")
public interface SancionApiClient {

    @GetMapping("/listar")
    List<SancionDto> getSanciones();

    @PostMapping("/nuevo")
    SancionDto insertarSancion(@RequestBody SancionDto sancion);

    @PutMapping("/actualizar")
    SancionDto actualizarSancion(@RequestBody SancionDto sancion);

    @GetMapping("/consultar/{id}")
    SancionDto consultarSancion(@PathVariable("id") Integer id);
}