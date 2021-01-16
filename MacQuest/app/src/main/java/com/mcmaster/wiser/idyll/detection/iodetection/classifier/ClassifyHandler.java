package com.mcmaster.wiser.idyll.detection.iodetection.classifier;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Created by steve on 2017-06-18.
 */

public class ClassifyHandler {

    private static final int NUMBER_OF_ATTRIBUTES_DAY = 6;
    private static final int NUMBER_OF_ATTRIBUTES_NIGHT = 5;

    public boolean isOut(double IncMagCurrent, float lightCurrent, float cellCurrent, float satCurrent,
                         float snrCurrent, boolean isDay) throws Exception {
        Instances dataset = getDataset(isDay);
        dataset.setClassIndex(dataset.numAttributes() - 1);
        Instance inst = getInstance(IncMagCurrent, lightCurrent, cellCurrent, satCurrent, snrCurrent, dataset, isDay);
        double classifyResult = 0;
        if (isDay) {
            WekaWrapperDay wakaWrapperDay = new WekaWrapperDay();
            classifyResult = wakaWrapperDay.classifyInstance(inst);
        } else {
            WekaWrapperNight wekaWrapperNight = new WekaWrapperNight();
            classifyResult = wekaWrapperNight.classifyInstance(inst);
        }
        boolean result = false;
        if (classifyResult == 1) {
            result = true;
        }
        return result;
    }

    private Instance getInstance(double IncMagCurrent, float lightCurrent, float cellCurrent,
                                 float satCurrent, float snrCurrent, Instances dataset, boolean isDay) {
        int numberOfAttributes = isDay ? NUMBER_OF_ATTRIBUTES_DAY : NUMBER_OF_ATTRIBUTES_NIGHT;
        Instance instance = new DenseInstance(numberOfAttributes);
        instance.setDataset(dataset);
        if (isDay) {
            instance.setValue(0, IncMagCurrent);
            instance.setValue(1, lightCurrent);
            instance.setValue(2, cellCurrent);
            instance.setValue(3, satCurrent);
            instance.setValue(4, snrCurrent);
        } else {
            instance.setValue(0, IncMagCurrent);
            instance.setValue(1, cellCurrent);
            instance.setValue(2, satCurrent);
            instance.setValue(3, snrCurrent);
        }
        dataset.add(instance);
        return instance;
    }

    private Instances getDataset(boolean isDay) {
        ArrayList<Attribute> allAttr = new ArrayList<Attribute>();
        String[] featureNames = getFeatureNameArray(isDay);
        for (String featureName : featureNames) {
            allAttr.add(new Attribute(featureName));
        }
        ArrayList<String> labelItems = new ArrayList<String>(2);
        labelItems.add("in");
        labelItems.add("out");
        Attribute mClassAttribute = new Attribute("class", labelItems);
        allAttr.add(mClassAttribute);
        return new Instances("features", allAttr, 10000);
    }

    private String[] getFeatureNameArray(boolean isDay) {
        if (isDay) {
            return new String[]{"IncMagCurrent", "lightCurrent", "cellCurrent", "satCurrent", "snrCurrent"};
        } else {
            return new String[]{"IncMagCurrent", "cellCurrent", "satCurrent", "snrCurrent"};
        }
    }
}
