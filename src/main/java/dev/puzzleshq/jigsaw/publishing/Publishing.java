package dev.puzzleshq.jigsaw.publishing;

import dev.puzzleshq.jigsaw.publishing.config.MavenLicense;
import dev.puzzleshq.jigsaw.publishing.config.MavenRepo;
import dev.puzzleshq.jigsaw.publishing.tasks.DependenciesJson;
import dev.puzzleshq.jigsaw.abstracts.AbstractJigsawPlugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.plugins.signing.SigningExtension;

public class Publishing extends AbstractJigsawPlugin {

    public final static String GROUP = "jigsaw/publishing";

    JigsawPublishingExtension jigsawPublishingExtension;

    @Override
    public void apply(Project project) {
        super.apply(project);

        SourceSetContainer sourceSetContainer = project.getExtensions().getByType(SourceSetContainer.class);

        ConfigurationContainer configurations = project.getConfigurations();
        sourceSetContainer.all(sourceSet -> {
            if (sourceSet.getName().equals("main")) {
                configurations.register("includedDependency").get();
                return;
            }
            configurations.register(sourceSet.getName() + "IncludedDependency").get();
        });

        project.getPlugins().apply("maven-publish");
        project.getPlugins().apply("io.github.sgtsilvio.gradle.maven-central-publishing");

        TaskContainer container = project.getTasks();
        container.register("mkDeps", DependenciesJson.class);

        if (project.getExtensions().findByName("jigsawPublishing") != null) {
            this.jigsawPublishingExtension = project.getExtensions().getByType(JigsawPublishingExtension.class);
        } else {
            this.jigsawPublishingExtension = project.getExtensions().create(
                    "jigsawPublishing",
                    JigsawPublishingExtension.class,
                    project,
                    project.getObjects()
            );
        }
    }

    @Override
    public void afterEvaluate(Project project) {
        super.afterEvaluate(project);

        //TODO look at this
//        if (this.jigsawPublishingExtension.getPublishingVersion() != null) {
//            project.setVersion(this.jigsawPublishingExtension.getPublishingVersion());
//        }

        setupPublishing(project);
        setupSigning(project);
    }

    public void setupPublishing(Project project){
        PublishingExtension publishing = project.getExtensions().findByType(PublishingExtension.class);
        if (publishing == null) return;

        for (MavenRepo mavenRepo : this.jigsawPublishingExtension.getRepos()) {
            publishing.getRepositories().maven(repo -> {
                repo.setName(mavenRepo.getName());

//                repo.setUrl(URI.create(mavenRepo.getUrl().get() + "/" + mavenRepo.getRepo().get()));

                repo.setAllowInsecureProtocol(true);
                repo.credentials(credentials -> {
                    credentials.setUsername(mavenRepo.getUsername().getOrElse(""));
                    credentials.setPassword(mavenRepo.getPassword().getOrElse(""));
                });
                repo.authentication(auths -> {
                    auths.create("basic", org.gradle.authentication.http.BasicAuthentication.class);
                });
            });
        }

        publishing.getRepositories().mavenLocal();

        // === Publication ===
        System.out.println(publishing.getPublications());
        publishing.getPublications().create("maven", MavenPublication.class, pub -> {
            //TODO look at this
            pub.from(project.getComponents().getByName("java"));

            String group = this.jigsawPublishingExtension.getPublishingGroup();
            String id = this.jigsawPublishingExtension.getPublishingId();
            String name = this.jigsawPublishingExtension.getPublishingName();
            String desc = this.jigsawPublishingExtension.getPublishingDesc();
            String url = this.jigsawPublishingExtension.getPublishingUrl();


            pub.setGroupId(group);
            pub.setArtifactId(id);

            pub.pom(pom -> {

                pom.getName().set(name);
                if (desc != null) pom.getDescription().set(desc);
                if (url != null) pom.getUrl().set(url);

                for (MavenLicense mavenLicense : this.jigsawPublishingExtension.getLicenses()){
                    pom.licenses(licensesContainer -> {
                        licensesContainer.license(license -> {
                            license.getName().set(mavenLicense.getName());
                            license.getUrl().set(mavenLicense.getUrl());
                            license.getDistribution().set(mavenLicense.getDistribution());
                            license.getComments().set(mavenLicense.getComments());
                        });
                    });
                }

                pom.developers(devContainer -> {
                    this.jigsawPublishingExtension.getDevelopers().all(developer -> {
                        String devId = developer.getId().getOrElse("undefined");
                        String devName = developer.getName().getOrElse("undefined");
                        String email = developer.getEmail().getOrNull();

                        System.out.println("Adding developer: " + devId + " (" + devName + ")");

                        devContainer.developer(dev -> {
                            dev.getId().set(devId);
                            dev.getName().set(devName);
                            if (email != null) {
                                dev.getEmail().set(email);
                            }
                        });
                    });
                });


                // SCM
                if (url != null){
                    pom.scm(scm -> {
                        scm.getConnection().set(url + ".git");
                        scm.getDeveloperConnection().set(url + ".git");
                        scm.getUrl().set(url + ".git");
                    });
                }
            });

        });
    }

    public void setupSigning(Project project) {
        if (this.jigsawPublishingExtension.getSigning()){
            SigningExtension signing = project.getExtensions().findByType(SigningExtension.class);
            PublishingExtension publishing = project.getExtensions().findByType(PublishingExtension.class);

            if (signing == null || publishing == null) return;

            signing.useGpgCmd();
            if (!publishing.getPublications().isEmpty()) {
                publishing.getPublications().withType(MavenPublication.class).forEach(signing::sign);
            }
        }
    }

    @Override
    public String getName() {
        return "Jigsaw Publishing";
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
