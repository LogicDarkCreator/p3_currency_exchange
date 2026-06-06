package ru.skillbox.currency.exchange.xml;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class CurrencyXml {

    @XmlElement(name = "Name")
    private String name;

    @XmlElement(name = "EngName")
    private String engName;

    @XmlElement(name = "Nominal")
    private Long nominal;

    @XmlElement(name = "ParentCode")
    private String parentCode;

    @XmlAttribute(name = "ID")
    private String id;

    @XmlElement(name = "ISO_Num_Code")
    private String isoNumCode;

    @XmlElement(name = "ISO_Char_Code")
    private String isoCharCode;
}