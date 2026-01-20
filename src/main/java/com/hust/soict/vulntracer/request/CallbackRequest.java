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

    private Map<String, Object> results;

    @Data
    public static class NucleiResultDto {
        private String status;
        private String error;
        private SummaryDto summary;
        @JsonProperty("target_updates")
        private TargetUpdateDto targetUpdates;
        private List<NucleiVulnerabilityDto > vulnerabilities;
    }

    @Data
    public static class NucleiVulnerabilityDto  {
        private String template_id;
        private String name;
        private String severity;
        private String description;
        @JsonProperty("matched_at")
        private String matchedAt;
        @JsonProperty("cwe_ids")
        private List<String> cweIds;
        private List<String> references;

        private NucleiEvidenceDto evidence;
    }

    @Data
    public static class NucleiEvidenceDto  {
        private String type;
        private String command;
        private List<String> resources;
    }

    @Data
    public static class ZapResultDto {
        private String status;
        private SummaryDto summary;
        @JsonProperty("target_updates")
        private TargetUpdateDto targetUpdates;
        private List<ZapVulnerabilityDto> vulnerabilities;
    }

    @Data
    public static class ZapVulnerabilityDto {
        @JsonProperty("plugin_id")
        private String pluginId;
        private String name;
        private String severity;
        private String confidence;
        private String description;
        private String solution;
        @JsonProperty("cwe_id")
        private String cweId;
        @JsonProperty("wasc_id")
        private String wascId;

        private List<ZapEvidenceDto> evidence;
    }

    @Data
    public static class ZapEvidenceDto {
        private String uri;
        private String method;
        private String param;
        private String evidence;
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
}
