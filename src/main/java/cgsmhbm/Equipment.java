package cgsmhbm;

public class Equipment {
	private String GLPI;
	private String numeroSerie;
	private String patrimonio;
	private String defect;
	private String battalion;
	private String chamadoEmpresa;
	private String status;
	
	public Equipment(String GLPI, String serialNumber, String patrimonio, String defect, String battalion, String chamadoEmpresa, String status) {
		super();
		this.GLPI = GLPI;
		this.numeroSerie = serialNumber;
		this.patrimonio = patrimonio;
		this.defect = defect;
		this.battalion = battalion;
		this.chamadoEmpresa = chamadoEmpresa;
		this.status = status;
	}

	   public String getNumeroSerie() { return numeroSerie; }
	    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }

	    public String getPatrimonio() { return patrimonio; }
	    public void setPatrimonio(String patrimonio) { this.patrimonio = patrimonio; }

	    public String getDefeito() { return defect; }
	    public void setDefeito(String defeito) { this.defect = defeito; }

	    public String getBatalhao() { return battalion; }
	    public void setBatalhao(String batalhao) { this.battalion = batalhao; }

	    public String getStatus() { return status; }
	    public void setStatus(String status) { this.status = status; }

		public String getChamadoEmpresa() { return chamadoEmpresa; }
		public void setChamadoEmpresa(String chamadoEmpresa) { this.chamadoEmpresa = chamadoEmpresa; }

		public String getGLPI() { return GLPI; }
		public void setGLPI(String gLPI) {	GLPI = gLPI; }
	
}
