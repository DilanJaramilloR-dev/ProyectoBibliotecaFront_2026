package edu.itm.proyecto2026.frontend.webservicesclients;

import edu.itm.proyecto2026.frontend.Dtos.EjemplarDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "ejemplar-api-client", url = "${api.base-url}/ejemplares")
public interface EjemplarApiClient {

    @GetMapping("/listar")
    List<EjemplarDto> getEjemplares();

    @PostMapping("/nuevo")
    EjemplarDto insertarEjemplar(@RequestBody EjemplarDto ejemplar);

    @PutMapping("/actualizar")
    EjemplarDto actualizarEjemplar(@RequestBody EjemplarDto ejemplar);

    @GetMapping("/consultar/{id}")
    EjemplarDto consultarEjemplar(@PathVariable("id") Integer id);

    @DeleteMapping("/eliminar/{id}")
    void eliminarEjemplar(@PathVariable("id") Integer id);
}