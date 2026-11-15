package edu.itm.proyecto2026.frontend.webservicesclients;

import edu.itm.proyecto2026.frontend.Dtos.EditorialDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "editorial-api-client", url = "${api.base-url}/editoriales")
public interface EditorialApiClient {

    @GetMapping("/listar")
    List<EditorialDto> getEditoriales();

    @PostMapping("/nuevo")
    EditorialDto insertarEditorial(@RequestBody EditorialDto editorial);

    @PutMapping("/actualizar")
    EditorialDto actualizarEditorial(@RequestBody EditorialDto editorial);

    @GetMapping("/consultar/{id}")
    EditorialDto consultarEditorial(@PathVariable("id") Integer id);

    @DeleteMapping("/eliminar/{id}")
    void eliminarEditorial(@PathVariable("id") Integer id);
}