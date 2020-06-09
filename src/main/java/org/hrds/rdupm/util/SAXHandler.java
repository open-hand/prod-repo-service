package org.hrds.rdupm.util;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * description
 *
 * @author like.zhang@hand-china.com 2020/06/09 14:22
 */
@Getter
@Setter
public class SAXHandler extends DefaultHandler {

    private Integer groupIdCount;
    private Integer artifactIdCount;
    private Integer versionCount;

    private Stack<String> stack;

    SAXHandler() {
        this.groupIdCount = 0;
        this.artifactIdCount = 0;
        this.versionCount = 0;
        this.stack = new Stack<>();
    }

    private static final String GROUP_ID_TAG = "groupId";
    private static final String ARTIFACT_ID_TAG = "artifactId";
    private static final String VERSION_TAG = "version";
    private static final String PROJECT_TAG = "project";

    @Override
    public void startElement(String uri, String localName,
                             String qName, Attributes attributes) throws SAXException {
        String parent = this.stack.empty() ? null : this.stack.peek();
        if (StringUtils.equals(parent, PROJECT_TAG)) {
            switch (qName) {
                case GROUP_ID_TAG:
                    groupIdCount ++;
                    break;
                case ARTIFACT_ID_TAG:
                    artifactIdCount ++;
                    break;
                case VERSION_TAG:
                    versionCount ++;
                    break;
                default:
            }
        }
        this.stack.push(qName);
    }

    @Override
    public void endElement (String uri, String localName, String qName) throws SAXException{
        String poppedElement = this.stack.empty() ? null : this.stack.pop();
        if (!StringUtils.equals(qName, poppedElement)) {
            throw new SAXException();
        }
    }

    public void checkCount() throws XMLCountException {
        List<String> fieldNames = new ArrayList<>();
        if (this.groupIdCount < 1) {
            fieldNames.add(GROUP_ID_TAG);
        }
        if (this.artifactIdCount < 1) {
            fieldNames.add(ARTIFACT_ID_TAG);
        }
        if (this.versionCount < 1) {
            fieldNames.add(VERSION_TAG);
        }

        if (CollectionUtils.isNotEmpty(fieldNames)) {
            throw new XMLCountException(fieldNames);
        }
    }
}
