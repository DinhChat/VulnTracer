package com.hust.soict.vulntracer.response;

import lombok.Data;

import java.util.List;

@Data
public class NucleiEvidenceResponse {
    private String type;
    private String command;
    private List<String> resources;
}

