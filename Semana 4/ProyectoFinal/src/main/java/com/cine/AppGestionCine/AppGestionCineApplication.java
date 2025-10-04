
package com.cine.AppGestionCine;

import com.cine.AppGestionCine.asientos.AsientoService;
import com.cine.AppGestionCine.funciones.FuncionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.modulith.Modulithic;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import java.time.LocalDateTime;

@SpringBootApplication
@Modulithic
@EnableTransactionManagement
@Slf4j
public class AppGestionCineApplication {

    public static void main(String[] args) {
        log.info("🎬 Iniciando App Gestión de Cine con Spring Modulith...");
        SpringApplication.run(AppGestionCineApplication.class, args);
        log.info("🎭 ¡Aplicación iniciada exitosamente!");
    }

    /**
     * Datos de prueba para inicializar la aplicación
     */
    @Bean
    CommandLineRunner initData(FuncionService funcionService, AsientoService asientoService) {
        return args -> {
            log.info("Inicializando datos de prueba");

            try {
                // Crear algunas funciones de ejemplo
                var funcion1 = funcionService.crearFuncion(
                        "Avengers: Endgame",
                        LocalDateTime.now().plusDays(1).withHour(18).withMinute(30),
                        "Sala 1",
                        50,
                        150.0
                );

                var funcion2 = funcionService.crearFuncion(
                        "Spider-Man: No Way Home",
                        LocalDateTime.now().plusDays(2).withHour(20).withMinute(0),
                        "Sala 2",
                        80,
                        180.0
                );

                var funcion3 = funcionService.crearFuncion(
                        "The Batman",
                        LocalDateTime.now().plusDays(3).withHour(16).withMinute(15),
                        "Sala 1",
                        50,
                        160.0
                );

                // Generar asientos para cada función
                asientoService.generarAsientosPorFuncion(funcion1.getId(), funcion1.getTotalAsientos());
                asientoService.generarAsientosPorFuncion(funcion2.getId(), funcion2.getTotalAsientos());
                asientoService.generarAsientosPorFuncion(funcion3.getId(), funcion3.getTotalAsientos());

                log.info("✅ Datos de prueba inicializados:");
                log.info("   - {} con {} asientos en {}", funcion1.getPelicula(), funcion1.getTotalAsientos(), funcion1.getSala());
                log.info("   - {} con {} asientos en {}", funcion2.getPelicula(), funcion2.getTotalAsientos(), funcion2.getSala());
                log.info("   - {} con {} asientos en {}", funcion3.getPelicula(), funcion3.getTotalAsientos(), funcion3.getSala());

                // Hacer algunas reservas de ejemplo
                asientoService.reservarAsiento(funcion1.getId(), "A1", "juan@example.com");
                asientoService.reservarAsiento(funcion1.getId(), "A2", "maria@example.com");
                asientoService.reservarAsiento(funcion2.getId(), "B5", "carlos@example.com");

                log.info("🎫 Reservas de ejemplo creadas");

            } catch (Exception e) {
                log.error("❌ Error al inicializar datos: {}", e.getMessage(), e);
            }
        };
    }
}
