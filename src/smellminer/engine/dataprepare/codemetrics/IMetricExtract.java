package smellminer.engine.dataprepare.codemetrics;

import java.io.File;
import java.io.IOException;
import smellminer.definition.Metrics;

public interface IMetricExtract
{
    public Metrics extractMetrics(File file);   
}
