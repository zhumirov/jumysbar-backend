package kz.btsd.edmarket.metric.config;

import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Profile({"!local"})
@Slf4j
@Configuration
public class MetricConfiguration {

    public static final int DEFAULT_PORT = 60001;

    private HTTPServer httpServer;

    @PostConstruct
    private void init() {
        DefaultExports.initialize();
        try {
            httpServer = new HTTPServer(DEFAULT_PORT);
            log.info("Prometheus exporter started on port {}", DEFAULT_PORT);;
        } catch (IOException e) {
            log.error("Prometheus exporter failed to start on port {}", DEFAULT_PORT);;
        }
    }

    @PreDestroy
    private void destroy() {
        httpServer.stop();
        log.info("Prometheus exporter stopped");
    }
}
