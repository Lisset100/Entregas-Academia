package com.cine.AppGestionCine.funciones;

import com.cine.AppGestionCine.funciones.events.FuncionCanceladaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FuncionService {

    private final FuncionRepository funcionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Funcion crearFuncion(String pelicula, LocalDateTime fechaHora, String sala, Integer totalAsientos, Double precio) {
        log.info("Creando nueva función: {} en sala {}", pelicula, sala);

        Funcion funcion = new Funcion(pelicula, fechaHora, sala, totalAsientos, precio);
        Funcion funcionGuardada = funcionRepository.save(funcion);

        log.info("Función creada con ID: {}", funcionGuardada.getId());
        return funcionGuardada;
    }


    public void cancelarFuncion(Long funcionId) {
        log.info("Cancelando función con ID: {}", funcionId);

        Optional<Funcion> funcionOpt = funcionRepository.findById(funcionId);
        if (funcionOpt.isEmpty()) {
            throw new RuntimeException("Función no encontrada con ID: " + funcionId);
        }

        Funcion funcion = funcionOpt.get();
        if (funcion.getEstado() == EstadoFuncion.CANCELADA) {
            throw new RuntimeException("La función ya está cancelada");
        }


        funcion.setEstado(EstadoFuncion.CANCELADA);
        funcion.setAsientosDisponibles(0);
        funcionRepository.save(funcion);

        FuncionCanceladaEvent evento = new FuncionCanceladaEvent(
                funcion.getId(),
                funcion.getPelicula(),
                funcion.getSala()
        );

        log.info("Publicando evento FuncionCanceladaEvent para función ID: {}", funcionId);
        eventPublisher.publishEvent(evento);
    }


    public void actualizarAsientosDisponibles(Long funcionId, int cambio) {
        log.info("Actualizando asientos disponibles para función {}: {}", funcionId, cambio);

        Optional<Funcion> funcionOpt = funcionRepository.findById(funcionId);
        if (funcionOpt.isPresent()) {
            Funcion funcion = funcionOpt.get();
            int nuevosDisponibles = funcion.getAsientosDisponibles() + cambio;

            // Validar que no sea negativo
            if (nuevosDisponibles < 0) {
                log.warn("Intento de asientos disponibles negativos para función {}", funcionId);
                return;
            }

            funcion.setAsientosDisponibles(nuevosDisponibles);
            funcionRepository.save(funcion);

            log.info("Asientos disponibles actualizados para función {}: {}", funcionId, nuevosDisponibles);
        }
    }


    @Transactional(readOnly = true)
    public List<Funcion> getFuncionesEnCartelera() {
        return funcionRepository.findByEstado(EstadoFuncion.EN_CARTELERA);
    }


    @Transactional(readOnly = true)
    public Optional<Funcion> getFuncionById(Long id) {
        return funcionRepository.findById(id);
    }


    @Transactional(readOnly = true)
    public List<Funcion> buscarPorPelicula(String pelicula) {
        return funcionRepository.findByPeliculaContainingIgnoreCase(pelicula);
    }


    @Transactional(readOnly = true)
    public List<Funcion> getFuncionesConAsientosDisponibles() {
        return funcionRepository.findFuncionesConAsientosDisponibles();
    }
}