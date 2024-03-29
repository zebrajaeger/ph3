package de.zebrajaeger.phserver.rest;

import de.zebrajaeger.phserver.data.Border;
import de.zebrajaeger.phserver.data.FieldOfView;
import de.zebrajaeger.phserver.service.PanoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class PanoRESTController {

  private final PanoService panoService;

  @Autowired
  public PanoRESTController(PanoService panoService) {
    this.panoService = panoService;
  }

  @GetMapping("/camera/fov")
  public FieldOfView cameraFov() {
    return panoService.getPictureFOV();
  }

  @PutMapping("/camera/border")
  public void cameraBorder(Border[] borders) {
    panoService.setCurrentPositionAsPictureBorder(borders);
    panoService.publishPictureFOVChange();
  }

  @GetMapping("/pano/fov")
  public FieldOfView panoFov() {
    return panoService.getPanoFOV();
  }

  @PutMapping("/pano/border")
  public void panoBorder(Border[] borders) {
    panoService.setCurrentPositionAsPanoBorder(borders);
    panoService.publishPanoFOVChange();
  }
}
