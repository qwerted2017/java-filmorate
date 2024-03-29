package ru.yandex.practicum.filmorate.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class H2Configuration {

    @Bean
    public void startTCPServer(){
//        try {
//            Server h2Server = Server.createTcpServer().start();
//            if (h2Server.isRunning(true)) {
//                System.out.println(h2Server.getStatus());
//            } else {
//                throw new RuntimeException("Could not start H2 server.");
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("Failed to start H2 server: ", e);
//        }
    }
}