package de.zebrajaeger.phserver.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Shots extends HashMap<String, List<Shot>> {

  @JsonIgnore
  public Shot getShot(String shotsName, int shotIndex) {
    List<Shot> shotList = get(shotsName);
    if (shotList == null || shotIndex >= shotList.size()) {
      return null;
    }
    return shotList.get(shotIndex);
  }

  @JsonIgnore
  public void add(String shotsName, Shot shot) {
    getCreateShotList(shotsName).add(shot);
  }

  @JsonIgnore
  public List<Shot> getCreateShotList(String shotsName) {
    List<Shot> shotList = get(shotsName);
    if (shotList == null) {
      shotList = new ArrayList<>();
      put(shotsName, shotList);
    }
    return shotList;
  }
}
