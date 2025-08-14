package cgsmhbm;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Equipment {

    private final StringProperty GLPI = new SimpleStringProperty(this, "GLPI", "");
    private final StringProperty numeroSerie = new SimpleStringProperty(this, "numeroSerie", "");
    private final StringProperty patrimonio = new SimpleStringProperty(this, "patrimonio", "");
    private final StringProperty descricaoMarcaModelo = new SimpleStringProperty(this, "descricaoMarcaModelo", "");
    private final StringProperty defeito = new SimpleStringProperty(this, "defeito", "");
    private final StringProperty batalhao = new SimpleStringProperty(this, "batalhao", "");
    private final StringProperty chamadoEmpresa = new SimpleStringProperty(this, "chamadoEmpresa", "");
    private final StringProperty status = new SimpleStringProperty(this, "status", "");

    public Equipment(String glpi, String numeroSerie, String patrimonio, String descricaoMarcaModelo,
                     String defeito, String batalhao, String chamadoEmpresa, String status) {
        setGLPI(glpi);
        setNumeroSerie(numeroSerie);
        setPatrimonio(patrimonio);
        setDescricaoMarcaModelo(descricaoMarcaModelo);
        setDefeito(defeito);
        setBatalhao(batalhao);
        setChamadoEmpresa(chamadoEmpresa);
        setStatus(status);
    }

    // GLPI
    public String getGLPI() { return GLPI.get(); }
    public void setGLPI(String v) { GLPI.set(v); }
    public StringProperty glpiProperty() { return GLPI; }

    // Número de série
    public String getNumeroSerie() { return numeroSerie.get(); }
    public void setNumeroSerie(String v) { numeroSerie.set(v); }
    public StringProperty numeroSerieProperty() { return numeroSerie; }

    // Patrimônio
    public String getPatrimonio() { return patrimonio.get(); }
    public void setPatrimonio(String v) { patrimonio.set(v); }
    public StringProperty patrimonioProperty() { return patrimonio; }

    // Descrição/Marca/Modelo
    public String getDescricaoMarcaModelo() { return descricaoMarcaModelo.get(); }
    public void setDescricaoMarcaModelo(String v) { descricaoMarcaModelo.set(v); }
    public StringProperty descricaoMarcaModeloProperty() { return descricaoMarcaModelo; }

    // Defeito
    public String getDefeito() { return defeito.get(); }
    public void setDefeito(String v) { defeito.set(v); }
    public StringProperty defeitoProperty() { return defeito; }

    // Batalhão
    public String getBatalhao() { return batalhao.get(); }
    public void setBatalhao(String v) { batalhao.set(v); }
    public StringProperty batalhaoProperty() { return batalhao; }

    // Chamado Empresa
    public String getChamadoEmpresa() { return chamadoEmpresa.get(); }
    public void setChamadoEmpresa(String v) { chamadoEmpresa.set(v); }
    public StringProperty chamadoEmpresaProperty() { return chamadoEmpresa; }

    // Status
    public String getStatus() { return status.get(); }
    public void setStatus(String v) { status.set(v); }
    public StringProperty statusProperty() { return status; }
}
