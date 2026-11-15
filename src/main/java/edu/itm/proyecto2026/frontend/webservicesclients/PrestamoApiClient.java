package edu.itm.proyecto2026.frontend.webservicesclients;

import edu.itm.proyecto2026.frontend.Dtos.PrestamoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "prestamo-api-client", url = "${api.base-url}/prestamos")
public interface PrestamoApiClient {

    @GetMapping("/listar")
    List<PrestamoDto> getPrestamos();

    @PostMapping("/nuevo")
    PrestamoDto insertarPrestamo(@RequestBody PrestamoDto prestamo);

    @PutMapping("/devolver/{id}")
    PrestamoDto devolverPrestamo(@PathVariable("id") Integer id);

    @PutMapping("/actualizar")
    PrestamoDto actualizarPrestamo(@RequestBody PrestamoDto prestamo);

    @GetMapping("/consultar/{id}")
    PrestamoDto consultarPrestamo(@PathVariable("id") Integer id);

    @DeleteMapping("/eliminar/{id}")
    void eliminarPrestamo(@PathVariable("id") Integer id);
}