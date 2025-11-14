package ar.edu.utn.dds.k3003.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el módulo solicitudes.
 * Emite eventos cuando una solicitud de borrado es aceptada.
 */
@Configuration
public class RabbitConfig {

    // Exchange principal compartido por todos los módulos
    public static final String TOPIC_EXCHANGE_NAME = "hechos-topic-exchange";

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME, true, false);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public org.springframework.boot.ApplicationRunner runner(RabbitAdmin rabbitAdmin) {
        return args -> rabbitAdmin.initialize();
    }
}
