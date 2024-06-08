package de.zebrajaeger.phserver.data;

public record Version(String version,
                      String commitId,
                      String commitIdAbbrev,
                      String commitTime,
                      String buildTime) {

}
