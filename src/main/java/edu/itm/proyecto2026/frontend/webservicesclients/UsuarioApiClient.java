package edu.itm.proyecto2026.frontend.webservicesclients;

import edu.itm.proyecto2026.frontend.Dtos.UsuarioDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "usuario-api-client", url = "${api.base-url}/usuarios")
public interface UsuarioApiClient {

    @GetMapping("/listar")
    List<UsuarioDto> getUsuarios();

    @PostMapping("/nuevo")
    UsuarioDto insertarUsuario(@RequestBody UsuarioDto usuario);

    @PutMapping("/actualizar")
    UsuarioDto actualizarUsuario(@RequestBody UsuarioDto usuario);

    @GetMapping("/consultar/{id}")
    UsuarioDto consultarUsuario(@PathVariable("id") Integer id);

    @DeleteMapping("/eliminar/{id}")
    void eliminarUsuario(@PathVariable("id") Integer id);
}