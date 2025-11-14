package ar.edu.utn.dds.k3003.service;

import ar.edu.utn.dds.k3003.config.RabbitConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para emitir eventos de borrado de hechos.
 * Cuando una solicitud de borrado es aceptada, notifica al agregador para actualizar el índice.
 */
@Service
public class EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public EventPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Emite un evento cuando una solicitud de borrado es aceptada.
     * El agregador escuchará este evento y marcará el hecho como borrado en el índice.
     */
    public void emitirHechoBorrado(String hechoId, String solicitudId) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "HECHO_BORRADO");
            event.put("hechoId", hechoId);
            event.put("solicitudId", solicitudId);
            event.put("timestamp", System.currentTimeMillis());

            String json = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(
                    RabbitConfig.TOPIC_EXCHANGE_NAME,
                    "hecho.borrado",
                    json
            );

            log.info("✅ Evento HECHO_BORRADO emitido para hecho: {}, solicitud: {}", hechoId, solicitudId);
        } catch (JsonProcessingException e) {
            log.error("❌ Error al emitir evento HECHO_BORRADO para hecho: {}", hechoId, e);
        }
    }
}
