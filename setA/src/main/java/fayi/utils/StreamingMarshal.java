package fayi.utils;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import jakarta.xml.bind.*;

import java.io.FileOutputStream;
import java.io.IOException;

public class StreamingMarshal<T> {
    private XMLStreamWriter xmlOut;
    private final Marshaller marshaller;
    private final Class<T> type;

    public StreamingMarshal(Class<T> type) throws JAXBException {
        this.type = type;
        JAXBContext context = JAXBContext.newInstance(type);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        this.marshaller = m;
    }

    public void open(String filename) throws XMLStreamException, IOException {
        xmlOut = XMLOutputFactory.newFactory().createXMLStreamWriter(new FileOutputStream(filename));
        xmlOut.writeStartDocument();
        xmlOut.writeStartElement("rootElement");
    }

    public void write(T t) throws JAXBException {
        JAXBElement<T> element = new JAXBElement<T>(QName.valueOf(type.getSimpleName()), type, t);
        marshaller.marshal(element, xmlOut);
    }

    public void close() throws XMLStreamException {
        xmlOut.writeEndDocument();
        xmlOut.close();
    }
}
