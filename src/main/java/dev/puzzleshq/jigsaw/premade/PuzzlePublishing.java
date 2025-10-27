package dev.puzzleshq.jigsaw.premade;

import dev.puzzleshq.jigsaw.publishing.JigsawPublishingExtension;
import dev.puzzleshq.jigsaw.publishing.Publishing;
import dev.puzzleshq.jigsaw.publishing.config.MavenLicense;
import dev.puzzleshq.jigsaw.util.AbstractJigsawExtension;
import dev.puzzleshq.jigsaw.util.AbstractJigsawPlugin;
import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;

public class PuzzlePublishing extends AbstractJigsawPlugin {

    PuzzlePublishingExtension puzzlePublishingExtension;

    JigsawPublishingExtension extension;

    String defaultGroup = "dev.puzzleshq";
    String defaultGit = "https://github.com/PuzzlesHq/";

    @Override
    public void apply(Project project) {

        this.puzzlePublishingExtension = project.getExtensions().create(
                "puzzlePublishing",
                PuzzlePublishingExtension.class,
                project,
                project.getObjects()
        );

        if (project.getExtensions().findByName("jigsawPublishing") != null) {
            extension = project.getExtensions().getByType(JigsawPublishingExtension.class);
        } else {
            extension = project.getExtensions().create(
                    "jigsawPublishing",
                    JigsawPublishingExtension.class,
                    project,
                    project.getObjects()
            );
        }

        Object groupObj = project.findProperty("p_group");
        if (groupObj != null) {
            extension.setPublishingGroup(groupObj.toString());
        } else {
            extension.setPublishingGroup(defaultGroup);
        }

        Object idObj = project.findProperty("p_id");
        if (idObj != null) {
            extension.setPublishingId(idObj.toString());
        }

        Object nameObj = project.findProperty("p_name");
        if (nameObj != null) {
            extension.setPublishingName(nameObj.toString());
        }

        Object descObj = project.findProperty("p_desc");
        if (descObj != null) {
            extension.setPublishingDesc(descObj.toString());
        }

        Object gitObj = project.findProperty("p_git");
        if (gitObj != null) {
            extension.setPublishingUrl(gitObj.toString());
        } else {
            extension.setPublishingUrl(defaultGit);
        }

        String ref = System.getenv("GITHUB_REF");

        if (ref != null){
            extension.setSigning(true);
        }

        Object backupVersionObj = project.findProperty("p_version");
        String backupVersion;
        if (backupVersionObj == null){
            backupVersion = "refs/tags/0.0.0-alpha";
        } else {
            backupVersion = "refs/tags/" + backupVersionObj.toString();
        }

        String versions = ((ref == null) ? backupVersion : ref).replaceAll("refs/tags/", "");
        extension.setPublishingVersion(versions);

        String mavenUrl = System.getenv("MAVEN_URL");

        String envRepo = System.getenv("MAVEN_REPO");
        String mavenRepo = envRepo != null ? envRepo : "releases";

        extension.getRepos().register("PuzzleHQsMaven", repo -> {
            repo.setRepo(mavenRepo);
            repo.setUrl(mavenUrl);
            repo.setUsername(System.getenv("MAVEN_NAME"));
            repo.setPassword(System.getenv("MAVEN_SECRET"));
        });


        project.getPlugins().apply(Publishing.class);

    }

    @Override
    public void afterEvaluate(Project project) {
        super.afterEvaluate(project);

        if (this.puzzlePublishingExtension.getLicenses().isEmpty()){
            extension.getLicenses().register("LGPL", license -> {
                license.setName("GNU Lesser General Public License");
                license.setUrl("https://www.gnu.org/licenses/lgpl-3.0.html");
                license.setDistribution("LGPL");
                license.setComments("This project includes LGPL-licensed components.");
            });
        }

        if (this.puzzlePublishingExtension.mrzombii){
            extension.getDevelopers().register("mrzombii", dev -> {
                System.out.println("mrzombii");
                dev.setId("mrzombii");
                dev.setName("Mr Zombii");
                dev.setEmail("thezombiigames@gmail.com");
            });
        }

        if (this.puzzlePublishingExtension.crabking){
            System.out.println("K1ng");
            extension.getDevelopers().register("Crab-K1ng", dev -> {
                dev.setId("Crab-K1ng");
                dev.setName("Crab King");
            });
        }

        if (this.puzzlePublishingExtension.replet){
            System.out.println("repletsin5");
            extension.getDevelopers().register("repletsin5", dev -> {
                dev.setId("repletsin5");
                dev.setName("repletsin5");
            });
        }

        if (this.puzzlePublishingExtension.spicylemon){

            System.out.println("spicylemon");
            extension.getDevelopers().register("spicylemon", dev -> {
                dev.setId("spicylemon");
                dev.setName("spicylemon");
            });
        }

        System.out.println(this.puzzlePublishingExtension.mrzombii);
        System.out.println(this.puzzlePublishingExtension.crabking);
        System.out.println(this.puzzlePublishingExtension.replet);
        System.out.println(this.puzzlePublishingExtension.spicylemon);
    }

    @Override
    public String getName() {
        return "Jigsaw Puzzle Publishing";
    }

    @Override
    public int getPriority() {
        return 1;
    }

    public static class PuzzlePublishingExtension extends AbstractJigsawExtension {

        public boolean mrzombii;
        public boolean crabking;
        public boolean replet;
        public boolean spicylemon;


        private final NamedDomainObjectContainer<MavenLicense> licenses;

        public PuzzlePublishingExtension(Project project, ObjectFactory objectFactory) {
            super(project, objectFactory);

            this.licenses = objectFactory.domainObjectContainer(MavenLicense.class, name -> new MavenLicense(objectFactory));
        }

        public void zombii(boolean mrzombii) {
            this.mrzombii = mrzombii;
        }

        public void crabking(boolean crabking) {
            this.crabking = crabking;
        }

        public void replet(boolean replet) {
            this.replet = replet;
        }

        public void spicylemon(boolean spicylemon) {
            this.spicylemon = spicylemon;
        }

        public NamedDomainObjectContainer<MavenLicense> getLicenses() {
            return licenses;
        }

        public void licenses(Action<? super NamedDomainObjectContainer<MavenLicense>> action) {
            action.execute(licenses);
        }
    }

}
