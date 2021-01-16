// Generated with Weka 3.8.1
//
// This code is public domain and comes with no warranty.
//
// Timestamp: Tue Jun 20 14:01:35 EDT 2017

package com.mcmaster.wiser.idyll.detection.iodetection.classifier;

import weka.classifiers.AbstractClassifier;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;

public class WekaWrapperDay
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

        return WekaClassifierDay.classify(s);
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
        return "Auto-generated classifier wrapper, based on weka.classifiers.trees.J48 (generated with Weka 3.8.1).\n" + this.getClass().getName() + "/WekaClassifierDay";
    }

    /**
     * Runs the classfier from commandline.
     *
     * @param args the commandline arguments
     */
    public static void main(String args[]) {
        runClassifier(new WekaWrapperDay(), args);
    }
}

class WekaClassifierDay {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifierDay.N26a25cc66(i);
        return p;
    }
    static double N26a25cc66(Object[]i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() <= 51.916832) {
            p = WekaClassifierDay.N13bcfc8d7(i);
        } else if (((Double) i[2]).doubleValue() > 51.916832) {
            p = WekaClassifierDay.N44cca0d59(i);
        }
        return p;
    }
    static double N13bcfc8d7(Object[]i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 0;
        } else if (((Double) i[4]).doubleValue() <= 26.55828) {
            p = 0;
        } else if (((Double) i[4]).doubleValue() > 26.55828) {
            p = WekaClassifierDay.N5b97b97f8(i);
        }
        return p;
    }
    static double N5b97b97f8(Object[]i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 22.782505) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() > 22.782505) {
            p = 1;
        }
        return p;
    }
    static double N44cca0d59(Object[]i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 7.186006157359039) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() > 7.186006157359039) {
            p = WekaClassifierDay.N703c90e610(i);
        }
        return p;
    }
    static double N703c90e610(Object[]i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 0;
        } else if (((Double) i[3]).doubleValue() <= 0.1875) {
            p = WekaClassifierDay.N41052f811(i);
        } else if (((Double) i[3]).doubleValue() > 0.1875) {
            p = 1;
        }
        return p;
    }
    static double N41052f811(Object[]i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 21864.379) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() > 21864.379) {
            p = 1;
        }
        return p;
    }
}
