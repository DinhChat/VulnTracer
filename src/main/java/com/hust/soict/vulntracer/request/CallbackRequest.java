package com.hust.soict.vulntracer.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CallbackRequest {
    private Long scanId;
    private String status;
    private String completedAt;

    private Map<String, NucleiResultDto> results;

    @Data
    public static class NucleiResultDto {
        private String status;
        private String error;
        private SummaryDto summary;
        @JsonProperty("target_updates")
        private TargetUpdateDto targetUpdates;
        private List<VulnerabilityDto> vulnerabilities;
    }

    @Data
    public static class SummaryDto {
        private Integer total;
        private Integer critical;
        private Integer high;
        private Integer medium;
        private Integer low;
        private Integer info;
    }

    @Data
    public static class TargetUpdateDto {
        private String ip;
        private String host;
        private String scheme;
        private Object port;

        public Integer getPort() {
            if (port instanceof Integer) return (Integer) port;
            if (port instanceof String) return Integer.valueOf((String) port);
            return null;
        }
    }

    @Data
    public static class VulnerabilityDto {
        private String template_id;
        private String name;
        private String severity;
        private String description;
        @JsonProperty("matched_at")
        private String matchedAt;
        @JsonProperty("cwe_ids")
        private List<String> cweIds;
        private List<String> references;

        private EvidenceDto evidence;
    }

    @Data
    public static class EvidenceDto {
        private String type;
        private String command;
        private List<String> resources;
    }
}
