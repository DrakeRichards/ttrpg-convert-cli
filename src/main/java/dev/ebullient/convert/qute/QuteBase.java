package dev.ebullient.convert.qute;

import java.util.Collection;
import java.util.List;

import dev.ebullient.convert.tools.CompendiumSources;
import dev.ebullient.convert.tools.IndexType;
import dev.ebullient.convert.tools.Tags;
import io.quarkus.qute.TemplateData;
import io.quarkus.runtime.annotations.RegisterForReflection;

@TemplateData
@RegisterForReflection
public class QuteBase {
    protected final String name;
    protected final CompendiumSources sources;
    public final String sourceText;
    public final String text;
    public final Collection<String> tags;

    private String vaultPath;

    public QuteBase(CompendiumSources sources, String name, String source, String text, Tags tags) {
        this.sources = sources;
        this.name = name;
        this.sourceText = source;
        this.text = text;
        this.tags = tags == null ? List.of() : tags.build();
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return sourceText;
    }

    public boolean getHasSections() {
        return text.contains("\n## ");
    }

    public void vaultPath(String vaultPath) {
        this.vaultPath = vaultPath;
    }

    public String getVaultPath() {
        if (vaultPath != null) {
            return vaultPath;
        }
        return targetPath() + '/' + targetFile();
    }

    public List<ImageRef> images() {
        return List.of();
    }

    public CompendiumSources sources() {
        return sources;
    }

    public String title() {
        return name;
    }

    public String targetFile() {
        return name;
    }

    public String targetPath() {
        return ".";
    }

    public IndexType type() {
        return sources.getType();
    }

    public String key() {
        return sources.getKey();
    }

    public boolean createIndex() {
        return true;
    }

    public String template() {
        IndexType type = type();
        return String.format("%s2md.txt", type.templateName());
    }
}
