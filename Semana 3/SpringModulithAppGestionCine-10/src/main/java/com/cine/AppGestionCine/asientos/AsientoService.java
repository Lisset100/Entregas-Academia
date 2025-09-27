package com.cine.AppGestionCine.asientos;


import com.cine.AppGestionCine.asientos.events.AsientoReservadoEvent;
import com.cine.AppGestionCine.asientos.events.AsientoCanceladoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AsientoService {

    private final AsientoRepository asientoRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void generarAsientosPorFuncion(Long funcionId, Integer totalAsientos) {
        log.info("Generando {} asientos para función {}", totalAsientos, funcionId);

        // Generar asientos con formato A1, A2, B1, B2, etc.
        int asientosGenerados = 0;
        char fila = 'A';
        int columna = 1;
        int asientosPorFila = 10;

        while (asientosGenerados < totalAsientos) {
            String numeroAsiento = fila + String.valueOf(columna);
            Asiento asiento = new Asiento(funcionId, numeroAsiento, (int)(fila - 'A' + 1), columna);
            asientoRepository.save(asiento);

            asientosGenerados++;
            columna++;

            if (columna > asientosPorFila) {
                fila++;
                columna = 1;
            }
        }

        log.info("Asientos generados exitosamente para función {}", funcionId);
    }

    public Asiento reservarAsiento(Long funcionId, String numeroAsiento, String clienteEmail) {
        log.info("Reservando asiento {} para función {} por cliente {}", numeroAsiento, funcionId, clienteEmail);

        Optional<Asiento> asientoOpt = asientoRepository.findByFuncionIdAndNumeroAsiento(funcionId, numeroAsiento);

        if (asientoOpt.isEmpty()) {
            throw new RuntimeException("Asiento no encontrado: " + numeroAsiento);
        }

        Asiento asiento = asientoOpt.get();

        if (asiento.getEstado() != EstadoAsiento.LIBRE) {
            throw new RuntimeException("El asiento " + numeroAsiento + " no está disponible");
        }

        asiento.setEstado(EstadoAsiento.RESERVADO);
        asiento.setClienteEmail(clienteEmail);
        Asiento asientoReservado = asientoRepository.save(asiento);

        AsientoReservadoEvent evento = new AsientoReservadoEvent(
                asiento.getId(),
                funcionId,
                numeroAsiento,
                clienteEmail
        );

        log.info("Publicando evento AsientoReservadoEvent para asiento {}", numeroAsiento);
        eventPublisher.publishEvent(evento);

        return asientoReservado;
    }

    public void cancelarReserva(Long asientoId) {
        log.info("Cancelando reserva de asiento {}", asientoId);

        Optional<Asiento> asientoOpt = asientoRepository.findById(asientoId);

        if (asientoOpt.isEmpty()) {
            throw new RuntimeException("Asiento no encontrado");
        }

        Asiento asiento = asientoOpt.get();

        if (asiento.getEstado() != EstadoAsiento.RESERVADO) {
            throw new RuntimeException("El asiento no está reservado");
        }

        String clienteEmail = asiento.getClienteEmail();
        String numeroAsiento = asiento.getNumeroAsiento();
        Long funcionId = asiento.getFuncionId();

        asiento.setEstado(EstadoAsiento.LIBRE);
        asiento.setClienteEmail(null);
        asientoRepository.save(asiento);

        AsientoCanceladoEvent evento = new AsientoCanceladoEvent(
                asientoId,
                funcionId,
                numeroAsiento,
                clienteEmail
        );

        log.info("Publicando evento AsientoCanceladoEvent para asiento {}", numeroAsiento);
        eventPublisher.publishEvent(evento);
    }

    public void cancelarAsientosPorFuncion(Long funcionId) {
        log.info("Cancelando todos los asientos de función {}", funcionId);

        List<Asiento> asientos = asientoRepository.findByFuncionId(funcionId);

        for (Asiento asiento : asientos) {
            if (asiento.getEstado() != EstadoAsiento.CANCELADO) {
                asiento.setEstado(EstadoAsiento.CANCELADO);
                asientoRepository.save(asiento);
            }
        }

        log.info("Todos los asientos de función {} han sido cancelados", funcionId);
    }

    @Transactional(readOnly = true)
    public List<Asiento> getAsientosPorFuncion(Long funcionId) {
        return asientoRepository.findByFuncionId(funcionId);
    }


    @Transactional(readOnly = true)
    public List<Asiento> getAsientosDisponibles(Long funcionId) {
        return asientoRepository.findByFuncionIdAndEstado(funcionId, EstadoAsiento.LIBRE);
    }
}