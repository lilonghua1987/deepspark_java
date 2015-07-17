package org.acl.deepspark.nn.conf;

import java.io.Serializable;
import java.util.*;

import org.acl.deepspark.nn.driver.NeuralNet;
import org.acl.deepspark.nn.functions.Activator;


public class NeuralNetConf implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2798664570482111852L;

	private int[] dimIn;
	private int[] dimOut;
	private ArrayList<LayerConf> layers;
	private HashMap<String, Double> params;

	public NeuralNetConf() {
		layers = new ArrayList<>();
		params = new HashMap<>();

		// default value for network configuration
		params.put("learningRate", 0.1);
		params.put("decayLambda", 0.0001);
		params.put("momentum", 0.9);
		params.put("dropOutRate", 0.0);
		params.put("activator", (double) Activator.SIGMOID);
	}

	public NeuralNetConf setLearningRate(double learningRate) {
		params.put("learningRate", learningRate);
		return this;
	}

	public NeuralNetConf setDecayLambda(double decayLambda) {
		params.put("decayLambda", decayLambda);
		return this;
	}

	public NeuralNetConf setMomentum(double momentum) {
		params.put("momentum", momentum);
		return this;
	}

	public NeuralNetConf setDropOutRate(double dropOutRate) {
		params.put("dropOutRate", dropOutRate);
		return this;
	}

	public NeuralNetConf setInputDim(int[] dimIn) {
		this.dimIn = dimIn;
		return this;
	}

	public int[] getDimIn() {
		return dimIn;
	}

	public int[] getDimOutput() {
		return dimOut;
	}

	public ArrayList<LayerConf> getLayerList() {
		return layers;
	}

	public HashMap<String, Double> getParams() {
		return params;
	}

	public NeuralNetConf setOutputDim(int[] dimOut) {
		this.dimOut = dimOut;
		return this;
	}
	
	public NeuralNetConf addLayer(LayerConf l) {
		layers.add(l);
		return this;
	}

	public NeuralNet build() {
		return new NeuralNet(this);
	}

}