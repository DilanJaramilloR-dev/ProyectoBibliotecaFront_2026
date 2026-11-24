package edu.itm.proyecto2026.frontend;

import edu.itm.proyecto2026.frontend.Windows.MainFrame;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import javax.swing.*;

@SpringBootApplication
@EnableFeignClients
public class FrontendApplication {

	public static void main(String[] args) {
		// Necesario para que Swing pueda abrir ventanas dentro de Spring Boot
		System.setProperty("java.awt.headless", "false");
		SpringApplication.run(FrontendApplication.class, args);
	}

	// Se ejecuta cuando Spring ya terminó de arrancar; abre la ventana principal
	@Bean
	public CommandLineRunner mostrarVentanaPrincipal(MainFrame mainFrame) {
		return args -> SwingUtilities.invokeLater(() -> {
			edu.itm.proyecto2026.frontend.Windows.BibliotecaTheme.aplicarTemaGlobal();
			mainFrame.inicializar();
			mainFrame.setVisible(true);
		});
	}
}
