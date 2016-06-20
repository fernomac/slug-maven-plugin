package ${package};

<#if docs??>
/**${docs}*/
</#if>
public class ${name} {

  <#list fields as field>
    private ${field.javaType} _${field.name};
  </#list>

    /**
     * Default constructor.
     */
    public ${name}() { }

    /**
     * Copy constructor.
     *
     * @param other the other {@code ${name}} to copy
     */
    public ${name}(${name} other) {
      <#list fields as field>
        this._${field.name} = other._${field.name};
      </#list>
    }
  <#list fields as field>

    /**
     * Gets the {@code ${field.name}} field.
     *<#if field.docs??> <p>${field.docs}*</#if>
     * @return the {@code ${field.name}}
     */
    public ${field.javaType} get${field.capitalizedName}() {
        return this._${field.name};
    }

    /**
     * Sets the {@code ${field.name}} field.
     *<#if field.docs??> <p>${field.docs}*</#if>
     * @param value the new value
     * @return this object
     */
    public ${name} set${field.capitalizedName}(${field.javaType} value) {
        this._${field.name} = value;
        return this;
    }
  </#list>

    @Override
    public String toString() {
        return "{"
          <#list fields as field>
            + "${field.name}=" + this._${field.name} + ","
          </#list>
            + "}";
    }

    @Override
    public int hashCode() {
        int hash = 0;
      <#list fields as field>
        hash = (31 * hash) + java.util.Objects.hashCode(this._${field.name});
      </#list>
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ${name})) {
            return false;
        }

        ${name} that = (${name}) o;

      <#list fields as field>
        if (!java.util.Objects.equals(this._${field.name}, that._${field.name})) {
            return false;
        }
      </#list>

        return true;
    }
}
