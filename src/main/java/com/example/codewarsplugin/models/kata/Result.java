package com.example.codewarsplugin.models.kata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {

    public boolean serverError;
    public boolean completed;
    public List<Output> output;
    public String successMode;
    public double passed;
    public double failed;
    public double errors;
    public String error; // Object?
    public Assertions assertions;
    public Specs specs;
    public Unweighted unweighted;
    public Weighted weighted;
    public boolean timedOut;
    public double wallTime;
    public double testTime;

    @Override
    public String toString() {
        return "Result{" +
                "serverError=" + serverError +
                ", completed=" + completed +
                ", output=" + output +
                ", successMode='" + successMode + '\'' +
                ", passed=" + passed +
                ", failed=" + failed +
                ", errors=" + errors +
                ", error='" + error + '\'' +
                ", assertions=" + assertions +
                ", specs=" + specs +
                ", unweighted=" + unweighted +
                ", weighted=" + weighted +
                ", timedOut=" + timedOut +
                ", wallTime=" + wallTime +
                ", testTime=" + testTime +
                '}';
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Output {
        public String t;
        public String m;
        public String v;
        public boolean p;
        public List<Output> items;

        @Override
        public String toString() {
            return "Output{" +
                    "t='" + t + '\'' +
                    ", v='" + v + '\'' +
                    ", p=" + p +
                    ", items=" + items +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Assertions {
        public double passed;
        public double failed;
        public Hidden hidden;

        @Override
        public String toString() {
            return "Assertions{" +
                    "passed=" + passed +
                    ", failed=" + failed +
                    ", hidden=" + hidden +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Specs {
        public double passed;
        public double failed;
        public Hidden hidden;

        @Override
        public String toString() {
            return "Specs{" +
                    "passed=" + passed +
                    ", failed=" + failed +
                    ", hidden=" + hidden +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Unweighted {
        public double passed;
        public double failed;

        @Override
        public String toString() {
            return "Unweighted{" +
                    "passed=" + passed +
                    ", failed=" + failed +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weighted {
        public double passed;
        public double failed;

        @Override
        public String toString() {
            return "Weighted{" +
                    "passed=" + passed +
                    ", failed=" + failed +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Hidden {
        public double passed;
        public double failed;

        @Override
        public String toString() {
            return "Hidden{" +
                    "passed=" + passed +
                    ", failed=" + failed +
                    '}';
        }
    }
}
