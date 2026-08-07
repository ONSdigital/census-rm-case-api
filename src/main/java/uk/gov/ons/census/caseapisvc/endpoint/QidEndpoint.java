package uk.gov.ons.census.caseapisvc.endpoint;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.ons.census.caseapisvc.model.dto.NewQidLink;
import uk.gov.ons.census.caseapisvc.model.dto.QidLink;
import uk.gov.ons.census.caseapisvc.service.CaseService;
import uk.gov.ons.census.caseapisvc.service.UacQidService;
import uk.gov.ons.census.common.model.entity.UacQidLink;

@RestController
@RequestMapping(value = "/qids")
@Timed
@Tag(
    name = "QID Endpoint",
    description =
        "Services for retrieving and managing questionnaire ID (QID) and UAC link information")
public class QidEndpoint {
  private final UacQidService uacQidService;
  private final CaseService caseService;

  @Autowired
  public QidEndpoint(UacQidService uacQidService, CaseService caseService) {
    this.uacQidService = uacQidService;
    this.caseService = caseService;
  }

  @GetMapping(value = "/{qid}")
  @Operation(
      summary = "Retrieve QID link details",
      description =
          "Retrieves the UAC-QID link information for a specified questionnaire ID, including associated case ID if available.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "QID link details retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = QidLink.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Questionnaire ID not found",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error occurred",
            content = @Content)
      })
  public QidLink getUacQidLinkByQid(
      @Parameter(description = "Questionnaire Identifier (QID) to retrieve", required = true)
          @PathVariable("qid")
          String qid) {
    UacQidLink uacQidLink = uacQidService.findUacQidLinkByQid(qid);
    QidLink qidDetails = new QidLink();
    qidDetails.setQuestionnaireId(uacQidLink.getQid());
    if (uacQidLink.getCaze() != null) {
      qidDetails.setCaseId(uacQidLink.getCaze().getId());
    }
    return qidDetails;
  }

  // As we don't have a subscription for the questionnaire links, it is not possible to test this.
  @PutMapping(value = "/link")
  @Operation(
      summary = "Link QID to case",
      description =
          "Links a questionnaire ID to a case. This endpoint is currently not implemented as the subscription for questionnaire links is not available.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "501",
            description = "Not Implemented - Questionnaire link subscription not available",
            content = @Content)
      })
  public void putQidLinkToCase(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "QID link information to be created",
              required = true,
              content = @Content(schema = @Schema(implementation = NewQidLink.class)))
          @RequestBody
          NewQidLink newQidLink) {
    // Below commented shall be uncommented when the subscription for the questionnaire link is
    // available
    //    UacQidLink uacQidLink =
    //        uacQidService.findUacQidLinkByQid(newQidLink.getQidLink().getQuestionnaireId());
    //    Case caseToLink = caseService.findById(newQidLink.getQidLink().getCaseId());
    //
    //    uacQidService.buildAndSendQuestionnaireLinkedEvent(uacQidLink, caseToLink, newQidLink);
    throw new ResponseStatusException(
        HttpStatus.NOT_IMPLEMENTED,
        "Questionnaire Id Link is not available, request cannot be fulfilled");
  }
}
