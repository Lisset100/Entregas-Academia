package com.cine.AppGestionCine.batch;


import com.cine.AppGestionCine.asientos.Asiento;
import com.cine.AppGestionCine.asientos.AsientoRepository;
import com.cine.AppGestionCine.asientos.EstadoAsiento;
import com.cine.AppGestionCine.funciones.Funcion;
import com.cine.AppGestionCine.funciones.FuncionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Configuration
@RequiredArgsConstructor
@Slf4j
public class LimpiezaReservasJobConfig {

    private final FuncionRepository funcionRepository;
    private final AsientoRepository asientoRepository;

    // ════════════════════════════════════════════════════════
    // 1. JOB (Trabajo Completo)
    // ════════════════════════════════════════════════════════

    /**
     * Anotaciones de repaso
     * Definición del Job principal
     *
     * Un Job puede tener múltiples Steps, pero este solo tiene uno
     */
    @Bean
    public Job limpiezaReservasJob(
            JobRepository jobRepository,
            Step limpiarReservasStep) {

        log.info("Configurando Job: limpiezaReservasJob");

        return new JobBuilder("limpiezaReservasJob", jobRepository)
                .start(limpiarReservasStep)  // Primer step
                // .next(otroStep)           // Ejemplo de como podría agregar más steps aquí
                .build();
    }

    // ════════════════════════════════════════════════════════
    // 2. STEP (Paso del Job)
    // ════════════════════════════════════════════════════════

    /**
     * Step: Limpiar reservas de funciones vencidas
     *
     * Un Step tiene 3 componentes:
     * - Reader: Lee datos
     * - Processor: Procesa cada dato
     * - Writer: Guarda resultados
     */
    @Bean
    public Step limpiarReservasStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<Funcion> funcionVencidaReader,
            ItemProcessor<Funcion, Funcion> reservaProcessor,
            ItemWriter<Funcion> reservaWriter) {

        log.info("Configurando Step: limpiarReservasStep");

        return new StepBuilder("limpiarReservasStep", jobRepository)
                .<Funcion, Funcion>chunk(10, transactionManager)
                //   ↑        ↑       ↑
                // Tipo     Tipo    Procesa de 10 en 10
                // Input   Output   (commit cada 10)
                .reader(funcionVencidaReader)
                .processor(reservaProcessor)
                .writer(reservaWriter)
                .build();
    }

    // ════════════════════════════════════════════════════════
    // 3. READER (Lee datos de BD)
    // ════════════════════════════════════════════════════════

    /**
     * Reader: Lee TODAS las funciones de la BD
     *
     * Nota: El Processor filtrará cuáles procesar
     */
    @Bean
    public RepositoryItemReader<Funcion> funcionVencidaReader() {
        log.info(" Configurando Reader: funcionVencidaReader");

        RepositoryItemReader<Funcion> reader = new RepositoryItemReader<>();

        // Configuración del reader
        reader.setRepository(funcionRepository);
        reader.setMethodName("findAll");  // Metodo del repository a usar
        reader.setSort(Map.of("id", Sort.Direction.ASC));  // Ordenar por ID
        reader.setPageSize(10);  // Lee de 10 en 10

        return reader;
    }

    // ════════════════════════════════════════════════════════
    // 4. PROCESSOR (Procesa cada item)
    // ════════════════════════════════════════════════════════

    /**
     * Processor: Filtra y valida cada función
     *
     * Retorna:
     * - Funcion: Si debe procesarse
     * - null: Si debe omitirse
     */
    @Bean
    public ItemProcessor<Funcion, Funcion> reservaProcessor() {
        return funcion -> {
            log.debug(" Procesando función ID: {} - {}",
                    funcion.getId(), funcion.getPelicula());

            // ════════════════════════════════════════════════
            // FILTRO 1: ¿La función ya pasó?
            // ════════════════════════════════════════════════
            LocalDateTime ahora = LocalDateTime.now();

            if (funcion.getFechaHora().isAfter(ahora)) {
                // Función todavía no ha pasado
                log.debug("⏭️  Función {} aún no pasó ({}), omitiendo",
                        funcion.getId(), funcion.getFechaHora());
                return null;  // null = no procesar
            }

            // ════════════════════════════════════════════════
            // FILTRO 2: ¿Tiene asientos reservados?
            // ════════════════════════════════════════════════
            List<Asiento> asientosReservados = asientoRepository
                    .findByFuncionIdAndEstado(funcion.getId(), EstadoAsiento.RESERVADO);

            if (asientosReservados.isEmpty()) {
                // No hay asientos para limpiar
                log.debug(" Función {} sin asientos reservados, omitiendo",
                        funcion.getId());
                return null;
            }

            // ════════════════════════════════════════════════
            // CONTINUAR: Esta función debe limpiarse
            // ════════════════════════════════════════════════
            log.info("Función {} lista para limpieza: {} asientos reservados",
                    funcion.getId(), asientosReservados.size());

            return funcion;  // Pasar al Writer
        };
    }

    // ════════════════════════════════════════════════════════
    // 5. WRITER (Guarda/actualiza datos)
    // ════════════════════════════════════════════════════════

    /**
     * Writer: Cancela asientos de funciones vencidas
     *
     * Recibe lista de funciones (chunk)
     * y procesa todas juntas
     */
    @Bean
    public ItemWriter<Funcion> reservaWriter() {
        return funciones -> {

            // Procesar cada función del chunk
            for (Funcion funcion : funciones) {
                log.info("🧹 Limpiando reservas de función: {} - {}",
                        funcion.getId(), funcion.getPelicula());

                // Buscar todos los asientos reservados
                List<Asiento> asientosReservados = asientoRepository
                        .findByFuncionIdAndEstado(funcion.getId(), EstadoAsiento.RESERVADO);

                int contador = 0;

                // Cancelar cada asiento
                for (Asiento asiento : asientosReservados) {
                    log.debug("   ❌ Cancelando asiento: {} (Cliente: {})",
                            asiento.getNumeroAsiento(),
                            asiento.getClienteEmail());

                    // Cambiar estado a CANCELADO
                    asiento.setEstado(EstadoAsiento.CANCELADO);
                    asiento.setClienteId(null);
                    asiento.setClienteEmail(null);

                    // Guardar cambios
                    asientoRepository.save(asiento);
                    contador++;
                }

                log.info("Limpieza completada: {} asientos cancelados para función {}",
                        contador, funcion.getId());
            }
        };
    }
}
