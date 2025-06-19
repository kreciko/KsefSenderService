package pl.spinsoft.ksef.ksefsenderservice.shared

import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.Marshaller
import java.io.File

class XmlWriter {
    inline fun <reified T : Any> writeXmlToFile(obj: T, fileName: String = "output.xml"): File {
        val jaxbContext = JAXBContext.newInstance(T::class.java)
        val marshaller = jaxbContext.createMarshaller().apply {
            setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        }

        val outputFile = File(fileName)
        marshaller.marshal(obj, outputFile)
        return outputFile
    }
}