package hexlet.code;

import net.datafaker.Faker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
//import io.sentry.Sentry;

@SpringBootApplication
@EnableJpaAuditing
public class AppApplication {

    public static void main(final String[] args) {

//        try {
//            throw new Exception("This is a test.");
//        } catch (Exception e) {
//            Sentry.captureException(e);
//        }

        SpringApplication.run(AppApplication.class, args);
    }

    @Bean
    public Faker getFaker() {

        return new Faker();
    }
}
