package com.hust.soict.vulntracer.response;

import lombok.Data;

@Data
public class CWEResponse {
    private String cweId;
    private String name;
    private String description;
}
