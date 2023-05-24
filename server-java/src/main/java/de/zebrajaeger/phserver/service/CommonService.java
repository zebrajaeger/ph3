package de.zebrajaeger.phserver.service;

import de.zebrajaeger.phserver.data.Version;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Getter
public class CommonService {
    @Value("${project.version}")
    private String projectVersion;
    @Value("${git.commit.id}")
    private String gitCommitId;
    @Value("${git.commit.id.abbrev}")
    private String gitCommitIdAbbrev;
    @Value("${git.build.time}")
    private String gitBuildTime;
    @Value("${git.commit.time}")
    private String gitCommitTime;
    private Version version;

    @PostConstruct
    public void init() {
        version = new Version(projectVersion, gitCommitId, gitCommitIdAbbrev, gitCommitTime,
                gitBuildTime);
    }
}
