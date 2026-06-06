package ru.skillbox.currency.exchange.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.skillbox.currency.exchange.service.CurrencyService;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyUpdateScheduler {

    private final CurrencyService currencyService;

    @Scheduled(cron = "0 0 * * * *")
    public void updateCurrenciesHourly() {
        log.info("Starting scheduled currency update from CBR");
        long startTime = System.currentTimeMillis();

        try {
            currencyService.updateCurrenciesFromCbr();
            long duration = System.currentTimeMillis() - startTime;
            log.info("Scheduled currency update completed successfully in {} ms", duration);
        } catch (Exception e) {
            log.error("Scheduled currency update failed", e);
        }
    }
}