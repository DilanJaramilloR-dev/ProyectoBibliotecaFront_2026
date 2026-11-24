package edu.itm.proyecto2026.frontend.controllers;

import edu.itm.proyecto2026.frontend.Dtos.CatalogoLibroDto;
import edu.itm.proyecto2026.frontend.Dtos.EjemplarDto;
import edu.itm.proyecto2026.frontend.Dtos.PrestamoDto;
import edu.itm.proyecto2026.frontend.Dtos.UsuarioDto;
import edu.itm.proyecto2026.frontend.Dtos.BibliotecarioDto;
import edu.itm.proyecto2026.frontend.webservicesclients.CatalogoApiClient;
import edu.itm.proyecto2026.frontend.webservicesclients.EjemplarApiClient;
import edu.itm.proyecto2026.frontend.webservicesclients.PrestamoApiClient;
import edu.itm.proyecto2026.frontend.webservicesclients.UsuarioApiClient;
import edu.itm.proyecto2026.frontend.webservicesclients.BibliotecarioApiClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Controller
public class BibliotecaWebController {

    private final CatalogoApiClient catalogoApiClient;
    private final EjemplarApiClient ejemplarApiClient;
    private final PrestamoApiClient prestamoApiClient;
    private final UsuarioApiClient usuarioApiClient;
    private final BibliotecarioApiClient bibliotecarioApiClient;

    public BibliotecaWebController(CatalogoApiClient catalogoApiClient,
                                   EjemplarApiClient ejemplarApiClient,
                                   PrestamoApiClient prestamoApiClient,
                                   UsuarioApiClient usuarioApiClient,
                                   BibliotecarioApiClient bibliotecarioApiClient) {
        this.catalogoApiClient = catalogoApiClient;
        this.ejemplarApiClient = ejemplarApiClient;
        this.prestamoApiClient = prestamoApiClient;
        this.usuarioApiClient = usuarioApiClient;
        this.bibliotecarioApiClient = bibliotecarioApiClient;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("paginaActiva", "inicio");
        model.addAttribute("backendDisponible", true);
        model.addAttribute("librosDestacados", Collections.emptyList());

        try {
            List<CatalogoLibroDto> libros = catalogoApiClient.getCatalogo(null, null, null);
            model.addAttribute("librosDestacados",
                    libros.size() > 6 ? libros.subList(0, 6) : libros);
        } catch (Exception e) {
            model.addAttribute("backendDisponible", false);
        }

        return "inicio";
    }

    @GetMapping("/catalogo")
    public String catalogo(
            @RequestParam(required = false, defaultValue = "") String titulo,
            @RequestParam(required = false, defaultValue = "") String autor,
            @RequestParam(required = false, defaultValue = "") String categoria,
            Model model) {

        model.addAttribute("paginaActiva", "catalogo");
        model.addAttribute("tituloFiltro", titulo);
        model.addAttribute("autorFiltro", autor);
        model.addAttribute("categoriaFiltro", categoria);
        model.addAttribute("libros", Collections.emptyList());
        model.addAttribute("backendDisponible", true);

        try {
            model.addAttribute("libros", catalogoApiClient.getCatalogo(
                    titulo.isBlank() ? null : titulo,
                    autor.isBlank() ? null : autor,
                    categoria.isBlank() ? null : categoria));
        } catch (Exception e) {
            model.addAttribute("backendDisponible", false);
        }

        return "catalogo";
    }

    @GetMapping("/catalogo/{id}")
    public String detalleLibro(@PathVariable Integer id, Model model) {
        model.addAttribute("paginaActiva", "catalogo");
        model.addAttribute("backendDisponible", true);

        try {
            CatalogoLibroDto libro = catalogoApiClient.consultarCatalogoLibro(id);
            model.addAttribute("libro", libro);

            List<EjemplarDto> ejemplares = ejemplarApiClient.getEjemplares().stream()
                    .filter(e -> e.getLibro() != null && e.getLibro().getIdLibro() != null
                            && e.getLibro().getIdLibro().equals(id))
                    .toList();
            model.addAttribute("ejemplares", ejemplares);
        } catch (Exception e) {
            model.addAttribute("backendDisponible", false);
            model.addAttribute("ejemplares", Collections.emptyList());
        }

        return "detalle-libro";
    }

    @GetMapping("/prestamos")
    public String prestamos(Model model) {
        model.addAttribute("paginaActiva", "prestamos");
        model.addAttribute("backendDisponible", true);

        try {
            model.addAttribute("prestamos", prestamoApiClient.getPrestamos());
            model.addAttribute("usuarios", usuarioApiClient.getUsuarios());
            model.addAttribute("bibliotecarios", bibliotecarioApiClient.getBibliotecarios());

            List<EjemplarDto> ejemplaresDisponibles = ejemplarApiClient.getEjemplares().stream()
                    .filter(e -> "Disponible".equalsIgnoreCase(e.getDisponibilidadEjemplar())
                            || "si".equalsIgnoreCase(e.getDisponibilidadEjemplar()))
                    .toList();
            model.addAttribute("ejemplaresDisponibles", ejemplaresDisponibles);
        } catch (Exception e) {
            model.addAttribute("backendDisponible", false);
            model.addAttribute("prestamos", Collections.emptyList());
            model.addAttribute("usuarios", Collections.emptyList());
            model.addAttribute("bibliotecarios", Collections.emptyList());
            model.addAttribute("ejemplaresDisponibles", Collections.emptyList());
        }

        model.addAttribute("fechaHoy", LocalDate.now());
        model.addAttribute("fechaDevolucion", LocalDate.now().plusDays(15));
        return "prestamos";
    }

    @PostMapping("/prestamos/nuevo")
    public String crearPrestamo(
            @RequestParam Integer ejemplarId,
            @RequestParam Integer usuarioId,
            @RequestParam Integer bibliotecarioId,
            @RequestParam LocalDate fechaPrestamo,
            @RequestParam LocalDate fechaDevolucionPrevista,
            RedirectAttributes redirectAttributes) {

        try {
            EjemplarDto ejemplar = new EjemplarDto();
            ejemplar.setIdEjemplar(ejemplarId);

            UsuarioDto usuario = new UsuarioDto();
            usuario.setIdUsuario(usuarioId);

            BibliotecarioDto bibliotecario = new BibliotecarioDto();
            bibliotecario.setIdBibliotecario(bibliotecarioId);

            PrestamoDto prestamo = new PrestamoDto();
            prestamo.setEjemplar(ejemplar);
            prestamo.setUsuario(usuario);
            prestamo.setBibliotecario(bibliotecario);
            prestamo.setFechaPrestamo(fechaPrestamo);
            prestamo.setFechaDevolucionPrevista(fechaDevolucionPrevista);

            prestamoApiClient.insertarPrestamo(prestamo);
            redirectAttributes.addFlashAttribute("mensajeExito", "Préstamo registrado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No fue posible registrar el préstamo. Verifica que el ejemplar esté disponible.");
        }

        return "redirect:/prestamos";
    }
}
