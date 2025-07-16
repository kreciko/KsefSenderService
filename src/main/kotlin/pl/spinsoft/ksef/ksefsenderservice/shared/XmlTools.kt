package pl.spinsoft.ksef.ksefsenderservice.shared

import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.Marshaller
import java.io.File
import kotlin.reflect.KClass

class XmlTools {
    inline fun <reified T : Any> writeObjectToXmlFile(obj: T, fileName: String = "output.xml"): File {
        val jaxbContext = JAXBContext.newInstance(T::class.java)
        val marshaller = jaxbContext.createMarshaller().apply {
            setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        }

        val outputFile = File(fileName)
        marshaller.marshal(obj, outputFile)
        return outputFile
    }

    inline fun <reified T : Any> readXmlIntoObject(targetClass: KClass<T>, file: File): T {
        val jaxbContext = JAXBContext.newInstance(targetClass.java)
        val obj = jaxbContext.createUnmarshaller().unmarshal(file) as T
        return obj
    }

}