package org.acl.deepspark.data;

import java.io.Serializable;

import org.jblas.DoubleMatrix;
import org.nd4j.linalg.api.ndarray.INDArray;

public class Sample implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 258491956070013844L;
	
	public INDArray data;
	public INDArray label;

	public INDArray getFeature() {
		return data;
	}

	public INDArray getLabel() {
		return label;
	}
}
