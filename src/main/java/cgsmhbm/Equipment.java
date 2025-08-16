package cgsmhbm;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Equipment {

    private final StringProperty GLPI = new SimpleStringProperty(this, "GLPI", "");
    private final StringProperty numeroSerie = new SimpleStringProperty(this, "numeroSerie", "");
    private final StringProperty patrimonio = new SimpleStringProperty(this, "patrimonio", "");
    private final StringProperty descricaoMarcaModelo = new SimpleStringProperty(this, "descricaoMarcaModelo", "");
    private final StringProperty defeito = new SimpleStringProperty(this, "defeito", "");
    private final StringProperty batalhao = new SimpleStringProperty(this, "batalhao", "");
    private final StringProperty chamadoEmpresa = new SimpleStringProperty(this, "chamadoEmpresa", "");
    private final ObjectProperty<EquipmentStatus> status = new SimpleObjectProperty<>(this, "status", null);
    private final StringProperty criadoEm = new SimpleStringProperty(this, "criadoEm", "");
    private final StringProperty atualizadoEm = new SimpleStringProperty(this, "atualizadoEm", "");

    public Equipment(String glpi, String numeroSerie, String patrimonio, String descricaoMarcaModelo,
                     String defeito, String batalhao, String chamadoEmpresa, EquipmentStatus status, String criadoEm, String atualizadoEm) {
        setGLPI(glpi);
        setNumeroSerie(numeroSerie);
        setPatrimonio(patrimonio);
        setDescricaoMarcaModelo(descricaoMarcaModelo);
        setDefeito(defeito);
        setBatalhao(batalhao);
        setChamadoEmpresa(chamadoEmpresa);
        setStatus(status);
        setCriadoEm(criadoEm);
        setAtualizadoEm(atualizadoEm);
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
    public EquipmentStatus getStatus() { return status.get(); }
    public void setStatus(EquipmentStatus v) { status.set(v); }
    public ObjectProperty<EquipmentStatus> statusProperty() { return status; }

    // Criado em
    public String getCriadoEm() { return criadoEm.get(); }
    public void setCriadoEm(String v) { criadoEm.set(v); }
    public StringProperty criadoEmProperty() { return criadoEm; }

    // Atualizado em
    public String getAtualizadoEm() { return atualizadoEm.get(); }
    public void setAtualizadoEm(String v) { atualizadoEm.set(v); }
    public StringProperty atualizadoEmProperty() { return atualizadoEm; }
}
