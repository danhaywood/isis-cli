    // region > ${propertyName} (property)
    private ${dataType} ${propertyName};
    public ${dataType} get${propertyNameUpper}() {
        return this.${propertyName};
    }
    public void set${propertyNameUpper}(final ${dataType} ${propertyName}) {
        this.${propertyName} = ${propertyName};
    }
    public boolean hide${propertyNameUpper}() {
        return false;
    }
    // endregion