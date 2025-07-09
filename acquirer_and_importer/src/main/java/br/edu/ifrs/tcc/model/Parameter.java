package br.edu.ifrs.tcc.model;

public class Parameter {
    public Integer id;
    public String name;
    public String description;
    public String type_class;
    public String unit;
    public String equipment;

    public Parameter(Integer id, String name, String description, String type_class, String unit,
            String equipment) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type_class = type_class;
        this.unit = unit;
        this.equipment = equipment;
    }
}