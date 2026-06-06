package ru.skillbox.currency.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.skillbox.currency.exchange.xml.CurrencyXml;
import ru.skillbox.currency.exchange.xml.ValCurs;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CbrParserService {

    private final RestTemplate restTemplate;

    public List<CurrencyXml> parseCurrencies(String url) {
        try {
            log.info("Fetching currencies from CBR: {}", url);
            String xml = restTemplate.getForObject(url, String.class);

            if (xml == null || xml.isEmpty()) {
                log.warn("Received empty response from CBR");
                return Collections.emptyList();
            }

            JAXBContext context = JAXBContext.newInstance(ValCurs.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            ValCurs valCurs = (ValCurs) unmarshaller.unmarshal(new StringReader(xml));

            List<CurrencyXml> currencies = valCurs.getItems();
            log.info("Successfully parsed {} currencies from CBR", currencies != null ? currencies.size() : 0);

            return currencies != null ? currencies : Collections.emptyList();
        } catch (JAXBException e) {
            log.error("Error parsing XML from CBR", e);
            throw new RuntimeException("Failed to parse currency data from CBR", e);
        } catch (Exception e) {
            log.error("Error fetching currencies from CBR", e);
            throw new RuntimeException("Failed to fetch currency data from CBR", e);
        }
    }
}