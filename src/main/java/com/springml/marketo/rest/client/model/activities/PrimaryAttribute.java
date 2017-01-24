
package com.springml.marketo.rest.client.model.activities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "dataType"
})
public class PrimaryAttribute {

    @JsonProperty("name")
    private String name;
    @JsonProperty("dataType")
    private String dataType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @Override
    public String toString() {
        return "PrimaryAttribute{" +
                "name='" + name + '\'' +
                ", dataType='" + dataType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrimaryAttribute that = (PrimaryAttribute) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return dataType != null ? dataType.equals(that.dataType) : that.dataType == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
        return result;
    }
}
