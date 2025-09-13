package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.repository.JpaSolicitudRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/admin/metrics")
public class MetricsController {

    private static final Logger log = LoggerFactory.getLogger(MetricsController.class);
    private final AtomicInteger debugGauge = new AtomicInteger(0);
    private final MeterRegistry meterRegistry;
    private final JpaSolicitudRepository solicitudRepository;

    @Autowired
    public MetricsController(MeterRegistry meterRegistry, JpaSolicitudRepository solicitudRepository) {
        this.meterRegistry = meterRegistry;
        this.solicitudRepository = solicitudRepository;
        meterRegistry.gauge("solicitudes.debug.gauge", debugGauge);
        log.info("✅ MetricsController inicializado para métricas de solicitudes");
    }

    @GetMapping("/solicitudes/total")
    public ResponseEntity<Map<String, Object>> getTotalSolicitudes() {
        int total = solicitudRepository.findAll().size();
        return ResponseEntity.ok(Map.of("totalSolicitudes", total));
    }

    @GetMapping("/actividad")
    public ResponseEntity<Map<String, Object>> getActividad() {
        Map<String, Object> actividad = new HashMap<>();
        actividad.put("solicitudes_creadas", getCounterValue("solicitudes", "operation", "crear"));
        actividad.put("solicitudes_actualizadas", getCounterValue("solicitudes", "operation", "actualizar"));
        actividad.put("solicitudes_buscadas", getCounterValue("solicitudes", "operation", "buscar"));
        return ResponseEntity.ok(actividad);
    }

    private double getCounterValue(String name, String... tags) {
        Counter counter = meterRegistry.find(name).tags(tags).counter();
        return counter != null ? counter.count() : 0;
    }

    @GetMapping("/gauge/{value}")
    public ResponseEntity<String> updateDebugGauge(@PathVariable Integer value) {
        debugGauge.set(value);
        log.info("🔧 Valor gauge cambiado a: {}", value);
        return ResponseEntity.ok("updated gauge: " + value);
    }

    @GetMapping("/solicitudes/por-estado")
    public ResponseEntity<Map<String, Object>> getSolicitudesPorEstado() {
        Map<String, Object> estados = new HashMap<>();
        estados.put("activas", getCounterValue("solicitudes", "estado", "activa"));
        estados.put("borradas", getCounterValue("solicitudes", "estado", "borrada"));
        estados.put("pendientes", getCounterValue("solicitudes", "estado", "pendiente"));
        return ResponseEntity.ok(estados);
    }

    @GetMapping("/solicitudes/actividad")
    public ResponseEntity<Map<String, Object>> getActividadSolicitudes() {
        Map<String, Object> actividad = new HashMap<>();
        actividad.put("creadas", getCounterValue("solicitudes", "operation", "crear"));
        actividad.put("actualizadas", getCounterValue("solicitudes", "operation", "actualizar"));
        actividad.put("buscadas", getCounterValue("solicitudes", "operation", "buscar"));
        return ResponseEntity.ok(actividad);
    }

    @GetMapping("/solicitudes/debug-gauge")
    public ResponseEntity<Integer> getDebugGauge() {
        Gauge gauge = meterRegistry.find("solicitudes.debug.gauge").gauge();
        int value = gauge != null ? (int) gauge.value() : 0;
        return ResponseEntity.ok(value);
    }

    @GetMapping("/solicitudes/custom/{metricName}")
    public ResponseEntity<Double> getCustomMetric(@PathVariable String metricName) {
        Gauge gauge = meterRegistry.find(metricName).gauge();
        double value = gauge != null ? gauge.value() : 0;
        return ResponseEntity.ok(value);
    }
}