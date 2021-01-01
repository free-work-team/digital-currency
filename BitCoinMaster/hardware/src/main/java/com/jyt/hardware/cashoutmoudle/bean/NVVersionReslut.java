package com.jyt.hardware.cashoutmoudle.bean;

public class NVVersionReslut extends CommonBaseReslut {

	public NVVersionReslut(boolean flat) {
		super(flat);
		// TODO Auto-generated constructor stub
	}

	/**
	 * NV模块DSP1软件版本号
	 */
	private String acSoftwareDSP1;
	/**
	 * FPGA1软件版本号
	 */
	private String acFPGA1;
	/**
	 * DSP1算法版本号
	 */
	private String acArithmeticDSP1;
	/**
	 * 算法版本号(8240N用)
	 */
	private String acArithmetic;

	public String getacSoftwareDSP1() {
		return acSoftwareDSP1;
	}

	public void setacSoftwareDSP1(String SoftwareDSP1) {
		acSoftwareDSP1 = SoftwareDSP1;
	}
	public String getacFPGA1() {
		return acFPGA1;
	}

	public void setacFPGA1(String FPGA1) {
		acFPGA1 = FPGA1;
	}
	public String getacArithmeticDSP1() {
		return acArithmeticDSP1;
	}

	public void setacArithmeticDSP1(String ArithmeticDSP1) {
		acArithmeticDSP1 = ArithmeticDSP1;
	}
	public String getacArithmetic() {
		return acArithmetic;
	}

	public void setacArithmetic(String Arithmetic) {
		acArithmetic = Arithmetic;
	}
	
}
