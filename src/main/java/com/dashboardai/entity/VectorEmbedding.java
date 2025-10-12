package com.dashboardai.entity;

import jakarta.persistence.*;

import java.math.BigInteger;

/**
 * Entidad para la tabla de embeddings vectorizados (documents_vectors)
 */
@Entity
@Table(name = "documents_vectors")
public class VectorEmbedding {
    
    @Id
    @Column(name = "id", nullable = false)
    private BigInteger id;
    
    @Column(name = "text", columnDefinition = "TEXT")
    private String text;
    
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;
    
    // Constructors
    public VectorEmbedding() {
    }
    
    // Getters and Setters
    public BigInteger getId() {
        return id;
    }
    
    public void setId(BigInteger id) {
        this.id = id;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
