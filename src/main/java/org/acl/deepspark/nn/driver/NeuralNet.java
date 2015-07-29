package org.acl.deepspark.nn.driver;

import org.acl.deepspark.data.Sample;
import org.acl.deepspark.data.Weight;
import org.acl.deepspark.nn.conf.LayerConf;
import org.acl.deepspark.nn.conf.NeuralNetConf;
import org.acl.deepspark.nn.layers.ConvolutionLayer;
import org.acl.deepspark.nn.layers.FullyConnectedLayer;
import org.acl.deepspark.nn.layers.Layer;
import org.acl.deepspark.nn.layers.PoolingLayer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jaehong on 2015-07-16.
 */
public class NeuralNet {
    private Layer[]     layers;
    private Weight[]    weights;
    private Weight[]    weightUpdates;

    double learningRate;
    double decayLambda;
    double momentum;
    double dropOutRate;

    public NeuralNet(final NeuralNetConf conf) {
        learningRate = conf.getLearningRate();
        decayLambda = conf.getDecayLambda();
        momentum = conf.getMomentum();
        dropOutRate = conf.getDropOutRate();
        initNetwork(conf);
    }

    public void initNetwork(final NeuralNetConf conf) {
        int size = conf.getLayerList().size();
        layers = new Layer[size];
        weights = new Weight[size];
        weightUpdates = new Weight[size];
        buildNetwork(conf.getLayerList(), conf.getDimIn());
    }

    private void buildNetwork(ArrayList<LayerConf> arr, int[] dimIn) {
        for (int i = 0 ; i< arr.size(); i++) {
            LayerConf layerConf = arr.get(i);
            switch (layerConf.getType()) {
                case CONVOLUTION:
                    layers[i] = new ConvolutionLayer(dimIn, layerConf);
                    break;
                case POOLING:
                    layers[i] = new PoolingLayer(dimIn, layerConf);
                    break;
                case FULLYCONN:
                    layers[i] = new FullyConnectedLayer(dimIn, layerConf);
                    break;
            }
            weights[i] = layers[i].createWeight(layerConf, dimIn);
            dimIn = layers[i].calculateOutputDimension(layerConf, dimIn);
            if (weights[i] != null)
                weightUpdates[i] = new Weight(weights[i].getWeightShape(), weights[i].getBiasShape());
        }
    }

    public void setWeights(Weight[] weights) {
        this.weights = weights;
    }

    public Weight[] getWeights() {
        return weights;
    }

    public int getNumLayers() {
        return layers.length;
    }

    public Weight[] train(Sample in) throws Exception {
        Weight[] gradient = new Weight[layers.length];
        INDArray[] output = new INDArray[layers.length];
        INDArray[] activated = new INDArray[layers.length + 1];
        activated[0] = in.data;

        for (int i = 0; i < layers.length; i++) {
            output[i] = layers[i].generateOutput(weights[i], activated[i]);
            activated[i+1] = layers[i].activate(output[i]);
        }

        INDArray delta = activated[layers.length].sub(in.label);
        
        System.out.println(Nd4j.sum(delta.mul(delta)));
        
        for (int i = layers.length-1; i >= 0; i--) {
            delta = layers[i].deriveDelta(activated[i+1], delta);
            	
            gradient[i] = layers[i].gradient(activated[i], delta);
            
            if (i > 0)
                delta = layers[i].calculateBackprop(weights[i], delta);
        }
        return gradient;
    }

    public INDArray predict(Sample in) {
        INDArray activatedOut = in.data;
        for (int i = 0; i < layers.length; i++) {
            INDArray output = layers[i].generateOutput(weights[i], activatedOut);
            activatedOut = layers[i].activate(output);
        }
        return activatedOut;
    }

    public void updateWeight(Weight[] deltaWeight) {
        if (weights.length != deltaWeight.length)
            System.out.println("Weight update dimension mismatch");
    //        throw new Exception("Weight dimension mismatch");
        for (int i = 0 ; i < weights.length; i++) {
            if (weights[i] != null) {
                weightUpdates[i].w.muli(momentum);
                weightUpdates[i].w.subi(weights[i].w.mul(learningRate * decayLambda));
                weightUpdates[i].w.subi(deltaWeight[i].w.mul(learningRate));
                
                weightUpdates[i].b.muli(momentum);
                weightUpdates[i].b.subi(deltaWeight[i].b.mul(learningRate));      
                
                weights[i].w.addi(weightUpdates[i].w);
                weights[i].b.addi(weightUpdates[i].b);
                
            }
        }
    }
}
