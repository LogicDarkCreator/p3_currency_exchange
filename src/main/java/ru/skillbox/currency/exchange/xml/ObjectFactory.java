package ru.skillbox.currency.exchange.xml;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

    public ValCurs createValCurs() {
        return new ValCurs();
    }

    public CurrencyXml createCurrencyXml() {
        return new CurrencyXml();
    }
}