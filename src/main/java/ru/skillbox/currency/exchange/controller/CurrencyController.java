package ru.skillbox.currency.exchange.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.currency.exchange.dto.CurrenciesResponseDto;
import ru.skillbox.currency.exchange.dto.CurrencyDto;
import ru.skillbox.currency.exchange.dto.CurrencyShortDto;
import ru.skillbox.currency.exchange.service.CurrencyService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/currency")
public class CurrencyController {

    private final CurrencyService service;

    @GetMapping(value = "/{id}")
    ResponseEntity<CurrencyDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping(value = "/convert")
    ResponseEntity<Double> convertValue(@RequestParam("value") Long value, @RequestParam("numCode") Long numCode) {
        return ResponseEntity.ok(service.convertValue(value, numCode));
    }

    @PostMapping("/create")
    ResponseEntity<CurrencyDto> create(@RequestBody CurrencyDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    ResponseEntity<CurrenciesResponseDto> getAllCurrencies() {
        List<CurrencyShortDto> currencies = service.getAllCurrencies();
        CurrenciesResponseDto response = new CurrenciesResponseDto(currencies);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-from-cbr")
    ResponseEntity<String> updateFromCbr() {
        service.updateCurrenciesFromCbr();
        return ResponseEntity.ok("Currencies updated successfully from CBR");
    }
}