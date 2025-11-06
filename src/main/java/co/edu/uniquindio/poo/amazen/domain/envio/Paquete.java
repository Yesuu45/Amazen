package co.edu.uniquindio.poo.amazen.domain.envio;

public class Paquete {
    private String id;
    private double pesoKg;       // kg
    private double volumenCm3;   // cm^3 (largo*ancho*alto)

    public Paquete() { }

    public Paquete(String id, double pesoKg, double volumenCm3) {
        this.id = id;
        this.pesoKg = pesoKg;
        this.volumenCm3 = volumenCm3;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public double getPesoKg() { return pesoKg; }
    public void setPesoKg(double pesoKg) { this.pesoKg = pesoKg; }
    public double getVolumenCm3() { return volumenCm3; }
    public void setVolumenCm3(double volumenCm3) { this.volumenCm3 = volumenCm3; }
}
