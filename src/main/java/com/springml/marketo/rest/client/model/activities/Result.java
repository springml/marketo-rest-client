
package com.springml.marketo.rest.client.model.activities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name",
    "description",
    "primaryAttribute",
    "attributes"
})
public class Result {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("primaryAttribute")
    private PrimaryAttribute primaryAttribute;
    @JsonProperty("attributes")
    private List<Attribute> attributes = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PrimaryAttribute getPrimaryAttribute() {
        return primaryAttribute;
    }

    public void setPrimaryAttribute(PrimaryAttribute primaryAttribute) {
        this.primaryAttribute = primaryAttribute;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result result = (Result) o;

        if (id != null ? !id.equals(result.id) : result.id != null) return false;
        if (name != null ? !name.equals(result.name) : result.name != null) return false;
        if (description != null ? !description.equals(result.description) : result.description != null) return false;
        if (primaryAttribute != null ? !primaryAttribute.equals(result.primaryAttribute) : result.primaryAttribute != null)
            return false;
        return attributes != null ? attributes.equals(result.attributes) : result.attributes == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (primaryAttribute != null ? primaryAttribute.hashCode() : 0);
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Result{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", primaryAttribute=" + primaryAttribute +
                ", attributes=" + attributes +
                '}';
    }
}
