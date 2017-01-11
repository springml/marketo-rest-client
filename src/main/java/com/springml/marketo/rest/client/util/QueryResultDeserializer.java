package com.springml.marketo.rest.client.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import static com.springml.marketo.rest.client.util.MarketoClientConstants.STR_ATTRIBUTES;

/**
 * Created by sam on 11/1/17.
 */
public class QueryResultDeserializer extends JsonDeserializer<List<Map<String, String>>> {
    private static final Logger LOG = Logger.getLogger(QueryResultDeserializer.class.getName());

    @Override
    public List<Map<String, String>> deserialize(JsonParser jsonParser,
                                           DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);


        List<Map<String, String>> result = new ArrayList<>();
        Iterator<JsonNode> fields = jsonNode.elements();
        while (fields.hasNext()) {
            JsonNode resultNode = fields.next();
            result.add(getAsMap(resultNode));
        }

        return result;
    }

    private Map<String, String> getAsMap(JsonNode node) {
        Map<String, String> record = new HashMap<>();

        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            JsonNode value = field.getValue();
            if (value.isArray()) {
                Iterator<JsonNode> elements = value.elements();
                addChildFields(field.getKey(), elements, record);
            } else if (value.isObject() || value.isPojo()) {
                addChildFields(field.getKey(), value, record);
            } else {
                record.put(field.getKey(), value.asText());
            }
        }

        return record;
    }

    private void addChildFields(String parentName, Iterator<JsonNode> elements, Map<String, String> record) {
        while (elements.hasNext()) {
            JsonNode childNode = elements.next();
            if (STR_ATTRIBUTES.equals(parentName)) {
                record.put(STR_ATTRIBUTES + "." + childNode.get("name").textValue(), childNode.get("value").textValue());
            } else if (childNode.isObject() || childNode.isPojo()) {
                addChildFields(parentName, childNode, record);
            }
        }
    }

    private void addChildFields(String parentName, JsonNode childNode, Map<String, String> record) {
        Iterator<Map.Entry<String, JsonNode>> fields = childNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            record.put(parentName + "." + field.getKey(), field.getValue().asText());
        }
    }
}
