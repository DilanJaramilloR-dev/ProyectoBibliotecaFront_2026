package edu.itm.proyecto2026.frontend.servlets;

import edu.itm.proyecto2026.frontend.Dtos.CatalogoLibroDto;
import edu.itm.proyecto2026.frontend.webservicesclients.CatalogoApiClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CatalogoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final transient CatalogoApiClient catalogoClient;
    private final String urlBackend;

    public CatalogoServlet(CatalogoApiClient catalogoClient, String urlBackend) {
        this.catalogoClient = catalogoClient;
        this.urlBackend = urlBackend;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");
        res.setCharacterEncoding("UTF-8");
        PrintWriter pw = res.getWriter();

        String titulo = req.getParameter("titulo") == null ? "" : req.getParameter("titulo").trim();

        pw.println("<html><head><meta charset=\"UTF-8\"><title>Catálogo - El Buen Futuro</title></head>");
        pw.println("<body style=\"font-family: sans-serif; margin: 30px;\">");
        pw.println("<h2>Sistema de Biblioteca - El Buen Futuro</h2>");
        pw.println("<p>Frontend activo en el puerto " + req.getLocalPort() + ". Backend: " + HtmlUtils.htmlEscape(urlBackend) + "</p>");
        pw.println("<form method=\"get\">");
        pw.println("Buscar por título: <input type=\"text\" name=\"titulo\" value=\"" + HtmlUtils.htmlEscape(titulo) + "\"> <input type=\"submit\" value=\"Buscar\">");
        pw.println("</form><br>");

        try {
            List<CatalogoLibroDto> libros = catalogoClient.getCatalogo(titulo, "", "");
            pw.println("<p>Libros encontrados: " + libros.size() + "</p>");
            pw.println("<table border=\"1\" cellpadding=\"6\" cellspacing=\"0\">");
            pw.println("<tr><th>Título</th><th>Autores</th><th>Categoría</th>" + "<th>Editorial</th><th>Año</th><th>Disponibles</th></tr>");
            for (CatalogoLibroDto l : libros) {
                pw.println("<tr>");
                pw.println("<td>" + HtmlUtils.htmlEscape(String.valueOf(l.getTituloLibro())) + "</td>");
                pw.println("<td>" + HtmlUtils.htmlEscape(String.valueOf(l.getAutores())) + "</td>");
                pw.println("<td>" + HtmlUtils.htmlEscape(String.valueOf(l.getNombreCategoria())) + "</td>");
                pw.println("<td>" + HtmlUtils.htmlEscape(String.valueOf(l.getNombreEditorial())) + "</td>");
                pw.println("<td>" + l.getAnioPublicacionLibro() + "</td>");
                pw.println("<td>" + l.getEjemplaresDisponibles() + "</td>");
                pw.println("</tr>");
            }
            pw.println("</table>");
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            pw.println("<p style=\"color: red;\">No se pudo consultar el backend: " + HtmlUtils.htmlEscape(String.valueOf(e.getMessage())) + "</p>");
        }
        pw.println("</body></html>");
        pw.close();
    }
}