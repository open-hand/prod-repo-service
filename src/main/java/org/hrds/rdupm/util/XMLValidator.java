package org.hrds.rdupm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.File;
import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;


import io.choerodon.core.exception.CommonException;

import static org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants.POM_XML_FORMAT_ERROR;
import static org.hrds.rdupm.nexus.infra.constant.NexusMessageConstants.POM_XML_TAG_MISS;

/**
 * XML校验
 *
 * @author like.zhang@hand-china.com 2020/06/09 10:22
 */
public class XMLValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(XMLValidator.class);

    private static final String POM_XSD_DEFAULT_PATH = "classpath:xsd/pom0.xsd";
    private static final String POM_XSD_DEFAULT_PATH_1 = "classpath:xsd/pom1.xsd";

    public static void validXMLDefault(MultipartFile xml) {
        XMLValidator.validXml(xml, POM_XSD_DEFAULT_PATH, POM_XSD_DEFAULT_PATH_1);
    }

    public static void validXml(MultipartFile xml, String... xsdPaths) {
        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser parser = parserFactory.newSAXParser();

            for (String xsdPath : xsdPaths) {
                SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = factory.newSchema(ResourceUtils.getFile(xsdPath));
                Validator validator = schema.newValidator();
                // 校验pom文件结构
                validator.validate(new StreamSource(xml.getInputStream()));
            }

            SAXHandler saxHandler = new SAXHandler();
            parser.parse(xml.getInputStream(), saxHandler);
            // 校验groupId,artifactId,version是否存在
            saxHandler.checkCount();

        } catch (SAXException e) {
            LOGGER.error("pom文件，校验错误", e);
            throw new CommonException(POM_XML_FORMAT_ERROR);
        } catch (XMLCountException e) {
            LOGGER.error("pom文件，校验错误", e);
            throw new CommonException(POM_XML_TAG_MISS, e.getMessage());
        } catch (Exception e) {
            LOGGER.error("pom文件，校验错误", e);
            throw new CommonException(e);
        }
    }

    public static void validXml(String xmlPath, String... xsdPaths) {
        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser parser = parserFactory.newSAXParser();

            for (String xsdPath : xsdPaths) {
                SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = factory.newSchema(ResourceUtils.getFile(xsdPath));
                Validator validator = schema.newValidator();
                // 校验pom文件结构
                validator.validate(new StreamSource(new File(xmlPath)));
            }

            SAXHandler saxHandler = new SAXHandler();
            parser.parse(xmlPath, saxHandler);
            saxHandler.checkCount();

        } catch (SAXException e) {
            throw new CommonException(POM_XML_FORMAT_ERROR);
        } catch (XMLCountException e) {
            throw new CommonException(POM_XML_TAG_MISS, e.getMessage());
        } catch (Exception e) {
            throw new CommonException(e);
        }
    }
}
