package org.hrds.rdupm.util;


import java.util.List;

/**
 * description
 *
 * @author like.zhang@hand-china.com 2020/06/09 11:43
 */
public class XMLCountException extends Exception {

    String message;

    XMLCountException() {

    }

    XMLCountException(List<String> fieldNames) {
        StringBuilder sb = new StringBuilder();
        for (String fieldName : fieldNames) {
            sb.append(String.format("<%s>", fieldName));
        }

        this.message = sb.toString();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
