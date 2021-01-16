// Generated with Weka 3.8.1
//
// This code is public domain and comes with no warranty.
//
// Timestamp: Tue Jun 20 14:19:29 EDT 2017

package com.mcmaster.wiser.idyll.detection.iodetection.classifier;

import weka.classifiers.AbstractClassifier;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;

public class WekaWrapperNight
        extends AbstractClassifier {

    /**
     * Returns only the toString() method.
     *
     * @return a string describing the classifier
     */
    public String globalInfo() {
        return toString();
    }

    /**
     * Returns the capabilities of this classifier.
     *
     * @return the capabilities
     */
    public Capabilities getCapabilities() {
        weka.core.Capabilities result = new weka.core.Capabilities(this);

        result.enable(weka.core.Capabilities.Capability.NOMINAL_ATTRIBUTES);
        result.enable(weka.core.Capabilities.Capability.NUMERIC_ATTRIBUTES);
        result.enable(weka.core.Capabilities.Capability.DATE_ATTRIBUTES);
        result.enable(weka.core.Capabilities.Capability.MISSING_VALUES);
        result.enable(weka.core.Capabilities.Capability.NOMINAL_CLASS);
        result.enable(weka.core.Capabilities.Capability.MISSING_CLASS_VALUES);


        result.setMinimumNumberInstances(0);

        return result;
    }

    /**
     * only checks the data against its capabilities.
     *
     * @param i the training data
     */
    public void buildClassifier(Instances i) throws Exception {
        // can classifier handle the data?
        getCapabilities().testWithFail(i);
    }

    /**
     * Classifies the given instance.
     *
     * @param i the instance to classify
     * @return the classification result
     */
    public double classifyInstance(Instance i) throws Exception {
        Object[] s = new Object[i.numAttributes()];

        for (int j = 0; j < s.length; j++) {
            if (!i.isMissing(j)) {
                if (i.attribute(j).isNominal())
                    s[j] = new String(i.stringValue(j));
                else if (i.attribute(j).isNumeric())
                    s[j] = new Double(i.value(j));
            }
        }

        // set class value to missing
        s[i.classIndex()] = null;

        return WekaClassifierNight.classify(s);
    }

    /**
     * Returns the revision string.
     *
     * @return        the revision
     */
    public String getRevision() {
        return RevisionUtils.extract("1.0");
    }

    /**
     * Returns only the classnames and what classifier it is based on.
     *
     * @return a short description
     */
    public String toString() {
        return "Auto-generated classifier wrapper, based on weka.classifiers.trees.J48 (generated with Weka 3.8.1).\n" + this.getClass().getName() + "/WekaClassifierNight";
    }

    /**
     * Runs the classfier from commandline.
     *
     * @param args the commandline arguments
     */
    public static void main(String args[]) {
        runClassifier(new WekaWrapperNight(), args);
    }
}

class WekaClassifierNight {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifierNight.N16b01f2d12(i);
        return p;
    }
    static double N16b01f2d12(Object[]i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 51.5625) {
            p = WekaClassifierNight.N32d720c113(i);
        } else if (((Double) i[1]).doubleValue() > 51.5625) {
            p = WekaClassifierNight.N4e4324c616(i);
        }
        return p;
    }
    static double N32d720c113(Object[]i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 0;
        } else if (((Double) i[3]).doubleValue() <= 19.150803) {
            p = 0;
        } else if (((Double) i[3]).doubleValue() > 19.150803) {
            p = WekaClassifierNight.N1e9dfa2c14(i);
        }
        return p;
    }
    static double N1e9dfa2c14(Object[]i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 45.02604) {
            p = WekaClassifierNight.N581c019f15(i);
        } else if (((Double) i[1]).doubleValue() > 45.02604) {
            p = 1;
        }
        return p;
    }
    static double N581c019f15(Object[]i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 0;
        } else if (((Double) i[3]).doubleValue() <= 22.356985) {
            p = 0;
        } else if (((Double) i[3]).doubleValue() > 22.356985) {
            p = 1;
        }
        return p;
    }
    static double N4e4324c616(Object[]i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() <= 7.000019) {
            p = WekaClassifierNight.N4519c4e717(i);
        } else if (((Double) i[2]).doubleValue() > 7.000019) {
            p = 1;
        }
        return p;
    }
    static double N4519c4e717(Object[]i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 68.26172) {
            p = WekaClassifierNight.N34241af218(i);
        } else if (((Double) i[1]).doubleValue() > 68.26172) {
            p = 1;
        }
        return p;
    }
    static double N34241af218(Object[]i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 64.79146) {
            p = WekaClassifierNight.Nc0cf51019(i);
        } else if (((Double) i[1]).doubleValue() > 64.79146) {
            p = 0;
        }
        return p;
    }
    static double Nc0cf51019(Object[]i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= 6.760132) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() > 6.760132) {
            p = 0;
        }
        return p;
    }
}
