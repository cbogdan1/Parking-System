package com.parcare.parking_system.exporter;

public class TXTFileExporter implements FileExporter {
    @Override
    public String exportData(Object object) {
        return object.toString();
    }
}
