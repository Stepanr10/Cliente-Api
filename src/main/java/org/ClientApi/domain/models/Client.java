package org.ClientApi.domain.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    public Long id;

    @NotBlank
    @Column(name = "primer_nombre")
    public String primerNombre;

    @Column(name = "segundo_nombre")
    public String segundoNombre;

    @NotBlank
    @Column(name = "primer_apellido")
    public String primerApellido;

    @Column(name = "segundo_apellido")
    public String segundoApellido;

    @NotBlank
    @Email
    @Column(unique = true)
    public String correo;

    @NotBlank
    public String direccion;

    @NotBlank
    public String telefono;

    @NotBlank
    @Column(name = "pais_codigo")
    public String pais;

    @Column(name = "gentilicio")
    public String gentilicio;

}