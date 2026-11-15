package edu.itm.proyecto2026.frontend.webservicesclients;

import edu.itm.proyecto2026.frontend.Dtos.ReporteInventarioDto;
import edu.itm.proyecto2026.frontend.Dtos.ReportePrestamoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "reporte-api-client", url = "${api.base-url}/reportes")
public interface ReporteApiClient {

    @GetMapping("/prestamos-activos")
    List<ReportePrestamoDto> getPrestamosActivos();

    @GetMapping("/prestamos-vencidos")
    List<ReportePrestamoDto> getPrestamosVencidos();

    @GetMapping("/inventario")
    List<ReporteInventarioDto> getInventario();
}