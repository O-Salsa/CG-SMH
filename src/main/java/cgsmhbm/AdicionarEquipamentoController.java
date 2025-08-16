package cgsmhbm;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdicionarEquipamentoController {
	
    @FXML private TextField txtGLPI;
    @FXML private TextField txtPatrimonio;
    @FXML private TextField txtNumeroSerie;
    @FXML private TextField txtDescricaoMarcaModelo;
    @FXML private TextField txtDefeito;
    @FXML private TextField txtBatalhao;
    @FXML private TextField txtchamadoEmpresa;
    @FXML private ComboBox<EquipmentStatus> cboxStatus;
    
    private Equipment equipamentoSalvo = null;
    private Equipment equipamentoOriginal = null;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    public Equipment getEquipamentoSalvo() {
        return equipamentoSalvo;
    }
    
    @FXML
    private void initialize() {
        cboxStatus.getItems().addAll(EquipmentStatus.values());
    }
    
    @FXML
    private void onAplicar(ActionEvent event) {
    	String GLPI = txtGLPI.getText().trim();
        String numeroSerie = txtNumeroSerie.getText().trim();
        String patrimonio = txtPatrimonio.getText().trim();
        String descricaoMarcaModelo = txtDescricaoMarcaModelo.getText().trim();
        String defeito = txtDefeito.getText().trim();
        String batalhao = txtBatalhao.getText().trim();
        String chamadoEmpresa = txtchamadoEmpresa.getText().trim();
        EquipmentStatus status = cboxStatus.getValue();

        if (numeroSerie.isEmpty() || patrimonio.isEmpty() || defeito.isEmpty() || batalhao.isEmpty() || status == null) {
            javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alerta.setTitle("Campos obrigatórios");
            alerta.setHeaderText(null);
            alerta.setContentText("Preencha todos os campos para adicionar o equipamento!");
            alerta.showAndWait();
            return; 
        }

        String agora = LocalDateTime.now().format(FORMATTER);
        String criadoEm = (equipamentoOriginal != null) ? equipamentoOriginal.getCriadoEm() : agora;
        String atualizadoEm = agora;

        equipamentoSalvo = new Equipment(GLPI, numeroSerie, patrimonio, descricaoMarcaModelo, defeito, batalhao, chamadoEmpresa, status, criadoEm, atualizadoEm);
    }

    private void fecharJanela() {
    	Stage stage = (Stage) txtPatrimonio.getScene().getWindow();
    	stage.close();
    }

    @FXML
    private void onCancelar(ActionEvent event) {
    	Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
    	stage.close();
    }

    @FXML
    public void preencherCampos(Equipment equipamento) {
        if (equipamento == null) return;
        equipamentoOriginal = equipamento;
        txtGLPI.setText(equipamento.getGLPI());
        txtNumeroSerie.setText(equipamento.getNumeroSerie());
        txtPatrimonio.setText(equipamento.getPatrimonio());
        txtDescricaoMarcaModelo.setText(equipamento.getDescricaoMarcaModelo());
        txtDefeito.setText(equipamento.getDefeito());
        txtBatalhao.setText(equipamento.getBatalhao());
        txtchamadoEmpresa.setText(equipamento.getChamadoEmpresa());
        cboxStatus.setValue(equipamento.getStatus());
    }
}
