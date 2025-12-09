package com.hust.soict.vulntracer.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hust.soict.vulntracer.model.CommonWeaknessEnumeration;
import com.hust.soict.vulntracer.repository.VulnerableRepository;
import com.hust.soict.vulntracer.service.CweImportService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.*;

@Service
public class CweImportServiceImpl implements CweImportService {
    private final VulnerableRepository repo;
    private final ObjectMapper objectMapper;

    public CweImportServiceImpl(VulnerableRepository repo, ObjectMapper objectMapper) {
        this.repo = repo;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public Map<String, Object> importCweXml(MultipartFile file) throws Exception {
        String sourceFileName = file.getOriginalFilename();
        InputStream is = file.getInputStream();

        XMLInputFactory factory = XMLInputFactory.newInstance();
        try { factory.setProperty(XMLInputFactory.SUPPORT_DTD, false); } catch (IllegalArgumentException ignored) {}
        try { factory.setProperty("javax.xml.stream.isSupportingExternalEntities", false); } catch (IllegalArgumentException ignored) {}

        XMLStreamReader reader = factory.createXMLStreamReader(is);

        List<CommonWeaknessEnumeration> buffer = new ArrayList<>();
        final int BATCH = 200;
        int imported = 0;

        boolean inWeakness = false;
        Map<String, String> elementText = new LinkedHashMap<>();
        Map<String, String> weaknessAttrs = new HashMap<>();
        List<Map<String, String>> relatedList = new ArrayList<>();
        String currentTag = null;

        while (reader.hasNext()) {
            int event = reader.next();

            if (event == XMLStreamConstants.START_ELEMENT) {
                String tag = reader.getLocalName();
                if ("Weakness".equalsIgnoreCase(tag)) {
                    inWeakness = true;
                    elementText.clear();
                    weaknessAttrs.clear();
                    relatedList.clear();

                    for (int i = 0; i < reader.getAttributeCount(); i++) {
                        weaknessAttrs.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                    }
                } else if (inWeakness) {
                    currentTag = tag;
                    elementText.putIfAbsent(tag, "");
                    // Parse related weakness
                    if ("Related_Weakness".equalsIgnoreCase(tag)) {
                        Map<String, String> rel = new HashMap<>();
                        for (int i = 0; i < reader.getAttributeCount(); i++) {
                            rel.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                        }
                        relatedList.add(rel);
                    }
                }

            } else if (event == XMLStreamConstants.CHARACTERS && inWeakness && currentTag != null) {
                String text = reader.getText().trim();
                if (!text.isEmpty()) {
                    elementText.put(currentTag, elementText.get(currentTag) + (elementText.get(currentTag).isEmpty() ? "" : " ") + text);
                }

            } else if (event == XMLStreamConstants.END_ELEMENT) {
                String tag = reader.getLocalName();

                if ("Weakness".equalsIgnoreCase(tag)) {
                    String rawId = weaknessAttrs.getOrDefault("ID", weaknessAttrs.getOrDefault("Id", null));
                    if (rawId == null) rawId = elementText.getOrDefault("ID", null);
                    if (rawId == null) {
                        inWeakness = false;
                        continue;
                    }

                    String cweId = toCweId(rawId);
                    Integer cweNum = parseCweNum(rawId);
                    String name = weaknessAttrs.getOrDefault("Name", elementText.getOrDefault("Name", "Unknown"));
                    String desc = elementText.getOrDefault("Description", null);
                    String extended = elementText.getOrDefault("Extended_Description", elementText.getOrDefault("ExtendedDescription", null));
                    String likelihood = elementText.getOrDefault("Likelihood_Of_Exploit", null);
                    String notes = elementText.getOrDefault("Background_Details", null);
                    String example = elementText.getOrDefault("Demonstrative_Example", null);
                    String relatedJson = relatedList.isEmpty() ? null : objectMapper.writeValueAsString(relatedList);

                    CommonWeaknessEnumeration v = new CommonWeaknessEnumeration();
                    v.setCweId(cweId);
                    v.setCweNum(cweNum);
                    v.setCweName(name);
                    v.setShortDescription(desc);
                    v.setExtendedDescription(extended);
                    v.setLikelihood(likelihood);
                    v.setNotes(notes);
                    v.setRelated(relatedJson);
                    v.setExample(example);
                    v.setSourceFile(sourceFileName);

                    buffer.add(v);

                    if (buffer.size() >= BATCH) {
                        repo.saveAll(buffer);
                        repo.flush();
                        imported += buffer.size();
                        buffer.clear();
                    }
                    inWeakness = false;
                }
            }
        }

        if (!buffer.isEmpty()) {
            repo.saveAll(buffer);
            repo.flush();
            imported += buffer.size();
        }

        reader.close();

        Map<String, Object> result = new HashMap<>();
        result.put("imported", imported);
        result.put("source", sourceFileName);
        return result;
    }

    private String toCweId(String raw) {
        if (raw == null) return null;
        String digits = raw.replaceAll("\\D+", "");
        return digits.isEmpty() ? raw : "CWE-" + digits;
    }

    private Integer parseCweNum(String raw) {
        if (raw == null) return null;
        String digits = raw.replaceAll("\\D+", "");
        return digits.isEmpty() ? null : Integer.parseInt(digits);
    }
}
