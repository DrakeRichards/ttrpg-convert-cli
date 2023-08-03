package dev.ebullient.convert.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;

import dev.ebullient.convert.config.TtrpgConfig;
import dev.ebullient.convert.qute.SourceAndPage;
import dev.ebullient.convert.tools.JsonTextConverter.SourceField;
import dev.ebullient.convert.tools.dnd5e.Tools5eIndexType;
import dev.ebullient.convert.tools.pf2e.Pf2eIndexType;
import io.quarkus.qute.TemplateData;

@TemplateData
public abstract class CompendiumSources {
    protected final IndexType type;
    protected final String key;
    protected final String name;

    // sources will only appear once, iterate by insertion order
    protected final Set<String> sources = new LinkedHashSet<>();
    protected final Set<SourceAndPage> bookRef = new LinkedHashSet<>();
    protected final String sourceText;

    public CompendiumSources(IndexType type, String key, JsonNode jsonElement) {
        this.type = type;
        this.key = key;
        this.name = findName(type, jsonElement);
        this.sourceText = findSourceText(type, jsonElement);
    }

    public String getSourceText() {
        return sourceText;
    }

    public Collection<String> getSources() {
        return sources;
    }

    /** Protected: used by Tags.addSourceTags(sources) */
    List<String> primarySourceTag() {
        return List.of(
                String.format("compendium/src/%s/%s",
                        TtrpgConfig.getConfig().datasource().shortName(),
                        isSynthetic() ? "" : primarySource().toLowerCase()));
    }

    public abstract JsonNode findNode();

    protected abstract String findName(IndexType type, JsonNode jsonElement);

    protected String findSourceText(IndexType type, JsonNode jsonElement) {
        List<String> srcText = new ArrayList<>();

        // add the primary source...
        SourceAndPage primary = new SourceAndPage(jsonElement);
        if (primary.source != null) {
            srcText.add(primary.toString());
            this.sources.add(primary.source);
            this.bookRef.add(primary);
        } else {
            this.sources.add(type.defaultSourceString());
        }

        JsonNode copyElement = Fields._copy.getFrom(jsonElement);
        String copyOf = SourceField.name.getTextOrNull(copyElement);
        String copySrc = SourceField.source.getTextOrNull(copyElement);

        if (copyOf != null) {
            srcText.add(String.format("Derived from %s (%s)", copyOf, copySrc));
        }

        // find/add additional sources
        if (Fields.additionalSources.existsIn(jsonElement)) { // Additional information from...
            srcText.addAll(Fields.additionalSources.streamOf(jsonElement)
                    .map(SourceAndPage::new)
                    .filter(sp -> sp.source != null)
                    .filter(sp -> !sp.source.equals(copySrc))
                    .filter(sp -> datasourceFilter(sp.source)) // eliminate common sources, e.g.
                    .peek(this.bookRef::add)
                    .peek(sp -> this.sources.add(sp.source))
                    .map(sp -> sp.toString())
                    .collect(Collectors.toList()));
        }
        if (Fields.otherSources.existsIn(jsonElement)) { // Also found in...
            srcText.addAll(Fields.otherSources.streamOf(jsonElement)
                    .map(SourceAndPage::new)
                    .filter(sp -> sp.source != null)
                    .filter(sp -> !sp.source.equals(copySrc))
                    .filter(sp -> datasourceFilter(sp.source))
                    .peek(this.bookRef::add)
                    .peek(sp -> this.sources.add(sp.source))
                    .filter(sp -> TtrpgConfig.getConfig().sourceIncluded(sp.source))
                    .map(sp -> sp.toString())
                    .collect(Collectors.toList()));
        }

        return String.join(", ", srcText);
    }

    protected boolean datasourceFilter(String source) {
        return true;
    }

    public boolean isPrimarySource(String source) {
        return source.equals(primarySource());
    }

    public String primarySource() {
        if (sources.isEmpty()) {
            return type.defaultSourceString();
        }
        return sources.iterator().next();
    }

    public String mapPrimarySource() {
        String primary = primarySource();
        return TtrpgConfig.sourceToAbbreviation(primary);
    }

    public String alternateSource() {
        if (sources.size() > 1) {
            Iterator<String> i = sources.iterator();
            i.next();
            return i.next();
        }
        return null;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public IndexType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "sources[" + key + ']';
    }

    public void checkKnown() {
        TtrpgConfig.checkKnown(this.sources);
    }

    boolean isSynthetic() {
        return type == Pf2eIndexType.syntheticGroup || type == Tools5eIndexType.syntheticGroup;
    }

    protected enum Fields implements JsonNodeReader {
        additionalSources,
        _copy,
        otherSources,
    }
}
