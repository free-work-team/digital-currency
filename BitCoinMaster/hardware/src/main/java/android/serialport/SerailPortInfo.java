package android.serialport;

public class SerailPortInfo {
	String com;//串口号
	boolean exist;//是否存在
	
	public SerailPortInfo(String com, boolean exist) {
		super();
		this.com = com;
		this.exist = exist;
	}
	public SerailPortInfo() {
		super();
	}
	
	public String getCom() {
		return com;
	}
	public void setCom(String com) {
		this.com = com;
	}
	public boolean isExist() {
		return exist;
	}
	public void setExist(boolean exist) {
		this.exist = exist;
	}
	
	
}
