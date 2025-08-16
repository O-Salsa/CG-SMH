package cgsmhbm;

public enum EquipmentStatus {
    AGUARDANDO_ABERTURA("Aguardando abertura"),
    GARANTIA_ACIONADA("Garantia acionada"),
    QAP_AGUARDANDO_DEVOLUCAO("QAP - Aguardando devolução"),
    FECHADO("Fechado");

    private final String descricao;

    EquipmentStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}


