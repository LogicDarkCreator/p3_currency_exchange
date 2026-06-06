package ru.skillbox.currency.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.currency.exchange.config.CbrConfig;
import ru.skillbox.currency.exchange.dto.CurrencyDto;
import ru.skillbox.currency.exchange.dto.CurrencyShortDto;
import ru.skillbox.currency.exchange.entity.Currency;
import ru.skillbox.currency.exchange.mapper.CurrencyMapper;
import ru.skillbox.currency.exchange.repository.CurrencyRepository;
import ru.skillbox.currency.exchange.xml.CurrencyXml;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyMapper mapper;
    private final CurrencyRepository repository;
    private final CbrParserService cbrParserService;
    private final CbrConfig cbrConfig;

    public CurrencyDto getById(Long id) {
        log.info("CurrencyService method getById executed for id: {}", id);
        Currency currency = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Currency not found with id: " + id));
        return mapper.convertToDto(currency);
    }

    public Double convertValue(Long value, Long numCode) {
        log.info("CurrencyService method convertValue executed for value: {}, numCode: {}", value, numCode);
        Currency currency = repository.findByIsoNumCode(numCode);
        if (currency == null) {
            throw new RuntimeException("Currency not found with isoNumCode: " + numCode);
        }
        return value * currency.getValue();
    }

    public CurrencyDto create(CurrencyDto dto) {
        log.info("CurrencyService method create executed for currency: {}", dto.getName());
        return mapper.convertToDto(repository.save(mapper.convertToEntity(dto)));
    }

    public List<CurrencyShortDto> getAllCurrencies() {
        log.info("CurrencyService method getAllCurrencies executed");
        List<Currency> currencies = repository.findAll();
        return currencies.stream()
                .map(mapper::convertToShortDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateCurrenciesFromCbr() {
        log.info("Starting currency update from CBR");

        try {
            List<CurrencyXml> cbrCurrencies = cbrParserService.parseCurrencies(cbrConfig.getUrl());

            if (cbrCurrencies.isEmpty()) {
                log.warn("No currencies received from CBR");
                return;
            }

            int updatedCount = 0;
            int createdCount = 0;

            for (CurrencyXml cbrCurrency : cbrCurrencies) {
                if (cbrCurrency.getIsoCharCode() == null || cbrCurrency.getIsoCharCode().isEmpty()) {
                    log.debug("Skipping currency without ISO char code: {}", cbrCurrency.getName());
                    continue;
                }

                Optional<Currency> existingCurrency = repository.findByIsoCharCode(cbrCurrency.getIsoCharCode());

                if (existingCurrency.isPresent()) {
                    Currency currency = existingCurrency.get();
                    currency.setName(cbrCurrency.getName());
                    currency.setNominal(cbrCurrency.getNominal());
                    if (cbrCurrency.getIsoNumCode() != null && !cbrCurrency.getIsoNumCode().isEmpty()) {
                        currency.setIsoNumCode(Long.parseLong(cbrCurrency.getIsoNumCode()));
                    }
                    currency.setIsoCharCode(cbrCurrency.getIsoCharCode());
                    repository.save(currency);
                    updatedCount++;
                    log.debug("Updated currency: {}", cbrCurrency.getIsoCharCode());
                } else {
                    Currency newCurrency = new Currency();
                    newCurrency.setName(cbrCurrency.getName());
                    newCurrency.setNominal(cbrCurrency.getNominal());
                    if (cbrCurrency.getIsoNumCode() != null && !cbrCurrency.getIsoNumCode().isEmpty()) {
                        newCurrency.setIsoNumCode(Long.parseLong(cbrCurrency.getIsoNumCode()));
                    }
                    newCurrency.setIsoCharCode(cbrCurrency.getIsoCharCode());
                    newCurrency.setValue(0.0);
                    repository.save(newCurrency);
                    createdCount++;
                    log.debug("Created new currency: {}", cbrCurrency.getIsoCharCode());
                }
            }

            log.info("Currency update completed: {} updated, {} created", updatedCount, createdCount);
        } catch (Exception e) {
            log.error("Failed to update currencies from CBR", e);
            throw new RuntimeException("Failed to update currencies", e);
        }
    }
}