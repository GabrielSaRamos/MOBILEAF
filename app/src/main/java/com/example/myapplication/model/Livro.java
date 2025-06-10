package com.example.myapplication.model;

public class Livro {
    private String id;
    private String titulo;
    private String autor;
    private String biblioteca; // novo campo

    public Livro() {} // Obrigatório pro Firebase

    public Livro(String id, String titulo, String autor, String biblioteca) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.biblioteca = biblioteca;
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getBiblioteca() { return biblioteca; }

    public void setId(String id) { this.id = id; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setAutor(String autor) { this.autor = autor; }
    public void setBiblioteca(String biblioteca) { this.biblioteca = biblioteca; }
}
