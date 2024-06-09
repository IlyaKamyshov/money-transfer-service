package ru.netology.moneytransferservice.logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class TransferSimpleLogger implements TransferLogger {

    @Value("${info.log.path}")
    private String infoLogPath;
    @Value("${error.log.path}")
    private String errorLogPath;

    @Override
    public void logInfo(String msg) {
        log(msg, infoLogPath);
    }

    @Override
    public void logError(String msg) {
        log(msg, errorLogPath);
    }

    private void log(String msg, String logPath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(logPath, true))) {
            writer.append("[")
                    .append(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
                    .append(" ")
                    .append(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.nnn")))
                    .append("]\t")
                    .append(msg)
                    .append("\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
