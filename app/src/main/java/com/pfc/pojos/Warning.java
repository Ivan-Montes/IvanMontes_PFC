package com.pfc.pojos;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

/*
Pojo for WarnActivity with builder design patter inside
 */
public class Warning {

    private final boolean activo;
    private final Timestamp fechaInicio;
    private final Timestamp fechaFin;
    private final String lugar;
    private final long nivel;
    private final long tipo;
    private final String idDoc;

    public boolean isActivo() {
        return activo;
    }

    public Timestamp getFechaInicio() {
        return fechaInicio;
    }

    public Timestamp getFechaFin() {
        return fechaFin;
    }

    public String getLugar() {
        return lugar;
    }

    public long getNivel() {
        return nivel;
    }

    public long getTipo() {
        return tipo;
    }

    public String getIdDoc() {
        return idDoc;
    }

    @NonNull
    @Override
    public String toString() {
        return "Warning{" +
                "activo=" + activo +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", lugar='" + lugar + '\'' +
                ", nivel=" + nivel +
                ", tipo=" + tipo +
                ", idDoc='" + getIdDoc() + '\'' +
                '}';
    }

    private Warning(Warning.WarningBuilder builder){
        this.activo = builder.activo;
        this.fechaInicio = builder.fechaInicio;
        this.fechaFin = builder.fechaFin;
        this.lugar = builder.lugar;
        this.nivel = builder.nivel;
        this.tipo = builder.tipo;
        this.idDoc = builder.idDoc;
    }

    /*
    Builder design pattern
     */
    public static class WarningBuilder{

        private boolean activo;
        private Timestamp fechaInicio;
        private Timestamp fechaFin;
        private String lugar;
        private long nivel;
        private long tipo;
        private String idDoc;

        public WarningBuilder(){}

        public WarningBuilder setActivo(boolean activo) {
            this.activo = activo;
            return this;
        }

        public WarningBuilder setFechaInicio(Timestamp fechaInicio) {
            this.fechaInicio = fechaInicio;
            return this;
        }

        public WarningBuilder setFechaFin(Timestamp fechaFin) {
            this.fechaFin = fechaFin;
            return this;
        }

        public WarningBuilder setLugar(String lugar) {
            this.lugar = lugar;
            return this;
        }

        public WarningBuilder setNivel(long nivel) {
            this.nivel = nivel;
            return this;
        }

        public WarningBuilder setTipo(long tipo) {
            this.tipo = tipo;
            return this;
        }

        public WarningBuilder setIdDoc(String idDoc) {
            this.idDoc = idDoc;
            return this;
        }

        public Warning build(){
            return new Warning(this);
        }

    }//End WarningBuilder class


}//End Warning class
