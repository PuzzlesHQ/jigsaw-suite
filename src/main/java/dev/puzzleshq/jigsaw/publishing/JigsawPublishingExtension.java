package dev.puzzleshq.jigsaw.publishing;

import dev.puzzleshq.jigsaw.publishing.config.MavenDeveloper;
import dev.puzzleshq.jigsaw.publishing.config.MavenLicense;
import dev.puzzleshq.jigsaw.publishing.config.MavenRepo;
import dev.puzzleshq.jigsaw.abstracts.AbstractJigsawExtension;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.NamedDomainObjectContainer;

import javax.inject.Inject;

public class JigsawPublishingExtension extends AbstractJigsawExtension {

    private String version;
    private String group;
    private String id;

    private String name;
    private String desc;
    private String url;

    private boolean signing = false;

    private final NamedDomainObjectContainer<MavenRepo> repos;
    private final NamedDomainObjectContainer<MavenLicense> licenses;
    private final NamedDomainObjectContainer<MavenDeveloper> developers;

    @Inject
    public JigsawPublishingExtension(Project project, ObjectFactory objectFactory) {
        super(project, objectFactory);

        this.repos = objectFactory.domainObjectContainer(MavenRepo.class, name -> new MavenRepo(name, project, objectFactory));
        this.licenses = objectFactory.domainObjectContainer(MavenLicense.class, name -> new MavenLicense(objectFactory));
        this.developers = objectFactory.domainObjectContainer(MavenDeveloper.class, name -> new MavenDeveloper(objectFactory));
    }

    public void setPublishingVersion(String version) { this.version = version; }
    public void setPublishingGroup(String group) { this.group = group; }
    public void setPublishingId(String id) { this.id = id; }
    public void setPublishingName(String name) { this.name = name; }
    public void setPublishingDesc(String desc) { this.desc = desc; }
    public void setPublishingUrl(String url) { this.url = url; }
    public void setSigning(boolean signing) { this.signing = signing; }

    public String getPublishingVersion() {
        return (this.version != null) ? this.version : getProject().getVersion().toString();
    }

    public String getPublishingGroup() {
        return (this.group != null) ? this.group : getProject().getGroup().toString();
    }

    public String getPublishingId() {
        return (this.id != null) ? this.id : getProject().getName();
    }

    public String getPublishingName() {
        return (this.name != null) ? this.name : getProject().getName();
    }

    public String getPublishingDesc() {
        return this.desc;
    }

    public String getPublishingUrl() {
        return this.url;
    }

    public boolean getSigning(){
        return this.signing;
    }

    public NamedDomainObjectContainer<MavenRepo> getRepos() {
        return repos;
    }

    public void repos(Action<? super NamedDomainObjectContainer<MavenRepo>> action) {
        action.execute(repos);
    }

    public NamedDomainObjectContainer<MavenLicense> getLicenses() {
        return licenses;
    }

    public void licenses(Action<? super NamedDomainObjectContainer<MavenLicense>> action) {
        action.execute(licenses);
    }

    public NamedDomainObjectContainer<MavenDeveloper> getDevelopers() {
        return developers;
    }

    public void developers(Action<? super NamedDomainObjectContainer<MavenDeveloper>> action) {
        action.execute(developers);
    }

}
